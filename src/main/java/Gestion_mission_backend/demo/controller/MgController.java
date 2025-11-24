package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.*;
import Gestion_mission_backend.demo.entity.*;
import Gestion_mission_backend.demo.repository.GmOrdreMissionRepository;
import Gestion_mission_backend.demo.repository.GmParticiperRepository;
import Gestion_mission_backend.demo.repository.GmValidationWorkflowRepository;
import Gestion_mission_backend.demo.repository.GmFraisMissionRepository;
import Gestion_mission_backend.demo.repository.GmBaremeRepository;
import Gestion_mission_backend.demo.repository.GmAgentRepository;
import Gestion_mission_backend.demo.repository.GmCategorieFraisRepository;
import Gestion_mission_backend.demo.repository.GmFonctionRepository;
import Gestion_mission_backend.demo.repository.ViewMgFraisAgentRepository;
import Gestion_mission_backend.demo.repository.ViewMgRecapRepository;
import Gestion_mission_backend.demo.repository.GmUtiliserRessourRepository;
import Gestion_mission_backend.demo.repository.RessourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/mg")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class MgController {

    private final GmOrdreMissionRepository missionRepository;
    private final GmParticiperRepository participerRepository;
    private final GmValidationWorkflowRepository validationRepository;
    private final GmFraisMissionRepository fraisMissionRepository;
    private final GmBaremeRepository baremeRepository;
    private final GmAgentRepository agentRepository;
    private final GmCategorieFraisRepository categorieFraisRepository;
    private final GmFonctionRepository fonctionRepository;
    private final ViewMgFraisAgentRepository viewFraisAgentRepository;
    private final ViewMgRecapRepository viewRecapRepository;
    private final GmUtiliserRessourRepository utiliserRessourRepository;
    private final RessourceRepository ressourceRepository;

    /**
     * GET /api/mg/missions - Liste des missions VALIDEE_RH avec calcul automatique
     */
    @GetMapping("/missions")
    @Transactional
    public ResponseEntity<List<MissionMgDTO>> getMissionsValidees() {
        log.info("📋 [MG] Récupération des missions validées RH");

        List<GmOrdreMission> missions = missionRepository.findByStatutOrdreMission("VALIDEE_RH");
        
        List<MissionMgDTO> dtos = missions.stream().map(mission -> {
            Long idMission = mission.getIdOrdreMission();
            
            // Compter les agents
            int nombreAgents = participerRepository.countByIdOrdreMission(idMission);
            
            // Vérifier les validations (Fondé + Agent comptable)
            List<GmValidationWorkflow> validations = validationRepository.findByIdOrdreMission(idMission);
            boolean hasFonde = validations.stream()
                    .anyMatch(v -> "Fondé".equalsIgnoreCase(v.getTypeValidation()) 
                            && "VALIDE".equalsIgnoreCase(v.getStatutValidation()));
            boolean hasAgentComptable = validations.stream()
                    .anyMatch(v -> "Agent comptable".equalsIgnoreCase(v.getTypeValidation()) 
                            && "VALIDE".equalsIgnoreCase(v.getStatutValidation()));
            boolean validationComplete = hasFonde && hasAgentComptable;
            
            // 🔥 CALCUL AUTOMATIQUE si validations complètes et pas encore calculé
            boolean fraisCalcules = fraisMissionRepository.existsByIdOrdreMission(idMission);
            if (validationComplete && !fraisCalcules) {
                log.info("🔄 [MG] Calcul automatique pour mission {}", idMission);
                calculerFraisAutomatique(mission);
                fraisCalcules = true;
            }
            
            // Calculer total si frais existent
            Long totalFrais = fraisCalcules ? fraisMissionRepository.sumMontantByIdOrdreMission(idMission) : 0L;
            
            // Calculer durée
            long dureeJours = 0;
            if (mission.getDateDebutPrevueOrdreMission() != null && mission.getDateFinPrevueOrdreMission() != null) {
                dureeJours = ChronoUnit.DAYS.between(
                        mission.getDateDebutPrevueOrdreMission(),
                        mission.getDateFinPrevueOrdreMission()
                ) + 1;
            }
            
            MissionMgDTO dto = new MissionMgDTO();
            dto.setIdOrdreMission(idMission);
            dto.setNumeroOrdreMission(mission.getNumeroOrdreMission());
            dto.setObjetOrdreMission(mission.getObjetOrdreMission());
            dto.setDateDebut(mission.getDateDebutPrevueOrdreMission());
            dto.setDateFin(mission.getDateFinPrevueOrdreMission());
            dto.setDureeJours(dureeJours);
            dto.setNombreAgents(nombreAgents);
            dto.setStatutOrdreMission(mission.getStatutOrdreMission());
            dto.setValidationComplete(validationComplete);
            dto.setFraisCalcules(fraisCalcules);
            dto.setTotalFrais(totalFrais);
            
            return dto;
        }).collect(Collectors.toList());
        
        log.info("✅ [MG] {} missions trouvées", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    /**
     * POST /api/mg/missions/{id}/calculer - Calcule les frais
     */
    @PostMapping("/missions/{id}/calculer")
    @Transactional
    public ResponseEntity<?> calculerFrais(@PathVariable Long id) {
        log.info("🧮 [MG] Calcul des frais pour mission {}", id);

        try {
            // Vérifier la mission
            GmOrdreMission mission = missionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mission introuvable"));

            // Vérifier validations
            List<GmValidationWorkflow> validations = validationRepository.findByIdOrdreMission(id);
            boolean hasFonde = validations.stream()
                    .anyMatch(v -> "Fondé".equalsIgnoreCase(v.getTypeValidation()) && "VALIDE".equalsIgnoreCase(v.getStatutValidation()));
            boolean hasAgentComptable = validations.stream()
                    .anyMatch(v -> "Agent comptable".equalsIgnoreCase(v.getTypeValidation()) && "VALIDE".equalsIgnoreCase(v.getStatutValidation()));

            if (!hasFonde || !hasAgentComptable) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Validations incomplètes",
                        "message", "La mission doit être validée par le Fondé de Pouvoir ET l'Agent Comptable"
                ));
            }

            // Supprimer anciens frais
            fraisMissionRepository.deleteByIdOrdreMission(id);

            // Récupérer participants (agents)
            List<GmParticiper> participants = participerRepository.findByIdOrdreMission(id);
            
            // Récupérer ressources (Police et Chauffeur)
            List<GmUtiliserRessour> ressources = utiliserRessourRepository.findByIdOrdreMission(id);
            
            if (participants.isEmpty() && ressources.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun participant ni ressource"));
            }

            // Calculer durée
            long dureeJours = ChronoUnit.DAYS.between(
                    mission.getDateDebutPrevueOrdreMission(),
                    mission.getDateFinPrevueOrdreMission()
            ) + 1;
            long dureeNuits = dureeJours - 1;

            List<FraisAgentDTO> fraisParAgent = new ArrayList<>();
            
            // ========== 1. CALCULER FRAIS POUR LES PARTICIPANTS (Agents) ==========
            log.info("🔄 [MG] Calcul frais pour {} participants", participants.size());
            for (GmParticiper participant : participants) {
                GmAgent agent = agentRepository.findById(participant.getIdAgent()).orElseThrow();
                Long idFonction = agent.getIdFonction();
                
                FraisAgentDTO fraisAgent = calculerEtCreerFrais(
                    id, agent.getIdAgent(), idFonction, dureeJours, dureeNuits,
                    agent.getNomAgent(), agent.getPrenomAgent(), 
                    agent.getNomAgent() + " " + agent.getPrenomAgent(),
                    false // Pas une ressource
                );
                fraisParAgent.add(fraisAgent);
            }

            // ========== 2. CALCULER FRAIS POUR LES RESSOURCES (Police et Chauffeur) ==========
            log.info("🚗 [MG] Calcul frais pour {} ressources", ressources.size());
            for (GmUtiliserRessour utiliserRessour : ressources) {
                GmRessource ressource = ressourceRepository.findById(utiliserRessour.getIdRessource()).orElse(null);
                if (ressource == null) continue;
                
                Long idTypeRessource = ressource.getIdTypeRessource();
                
                // Type 2 = Chauffeur → Fonction 5
                // Type 3 = Police → Fonction 4
                // Type 1 = Véhicule → Pas de frais
                Long idFonction = null;
                String libelleFonction = "";
                if (idTypeRessource == 2L) {
                    idFonction = 5L; // Chauffeur
                    libelleFonction = "Chauffeur";
                } else if (idTypeRessource == 3L) {
                    idFonction = 4L; // Police
                    libelleFonction = "Police";
                } else {
                    continue; // Véhicule, pas de frais
                }
                
                // Utiliser l'ID de la ressource comme ID d'agent fictif (négatif pour éviter conflits)
                Long idAgentFictif = -ressource.getIdRessource();
                
                FraisAgentDTO fraisRessource = calculerEtCreerFrais(
                    id, idAgentFictif, idFonction, dureeJours, dureeNuits,
                    ressource.getLibRessource(), "", // Nom complet dans le premier paramètre
                    ressource.getLibRessource() + " (" + libelleFonction + ")",
                    true // C'est une ressource
                );
                fraisRessource.setLibelleFonction(libelleFonction);
                fraisParAgent.add(fraisRessource);
            }

            long totalGlobal = fraisParAgent.stream().mapToLong(FraisAgentDTO::getTotalAgent).sum();

            RecapitulatifFraisDTO recap = new RecapitulatifFraisDTO();
            recap.setIdOrdreMission(id);
            recap.setNumeroOrdreMission(mission.getNumeroOrdreMission());
            recap.setObjetOrdreMission(mission.getObjetOrdreMission());
            recap.setDateDebut(mission.getDateDebutPrevueOrdreMission());
            recap.setDateFin(mission.getDateFinPrevueOrdreMission());
            recap.setDureeJours(dureeJours);
            recap.setDureeNuits(dureeNuits);
            
            // Compter les ressources Police/Chauffeur
            long nbRessources = ressources.stream()
                .filter(r -> {
                    GmRessource res = ressourceRepository.findById(r.getIdRessource()).orElse(null);
                    return res != null && (res.getIdTypeRessource() == 2L || res.getIdTypeRessource() == 3L);
                })
                .count();
            
            recap.setNombreAgents(participants.size() + (int) nbRessources);
            recap.setFraisParAgent(fraisParAgent);
            recap.setTotalGeneral(totalGlobal);
            recap.setValidationComplete(true);

            log.info("✅ [MG] Calcul terminé : {} FCFA ({} agents + {} ressources)", 
                totalGlobal, participants.size(), nbRessources);
            return ResponseEntity.ok(recap);

        } catch (Exception e) {
            log.error("❌ [MG] Erreur : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/mg/missions/{id}/frais - Détails des frais (Calcul manuel pour inclure les ressources)
     */
    @GetMapping("/missions/{id}/frais")
    @Transactional
    public ResponseEntity<?> getFraisDetails(@PathVariable Long id) {
        log.info("📊 [MG] Récupération détails frais mission {}", id);

        try {
            // 1. Récupérer la mission
            GmOrdreMission mission = missionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

            // 2. Récupérer tous les frais calculés
            List<GmFraisMission> fraisList = fraisMissionRepository.findByIdOrdreMission(id);
            if (fraisList.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Frais non calculés",
                    "message", "Aucun frais trouvé pour cette mission"
                ));
            }

            // 3. Préparer les maps pour les libellés
            Map<Long, String> categoriesMap = categorieFraisRepository.findAll().stream()
                    .collect(Collectors.toMap(GmCategorieFrais::getIdCategorieFrais, GmCategorieFrais::getLibelleCategorieFrais));

            // 4. Grouper par Agent (ou Ressource)
            Map<Long, FraisAgentDTO> agentsMap = new LinkedHashMap<>();
            long totalGeneral = 0;
            long totalRepas = 0;
            long totalHebergement = 0;
            long totalIndemnite = 0;
            long totalCarburant = 0;

            for (GmFraisMission frais : fraisList) {
                Long idAgent = frais.getIdAgent();
                
                // Créer l'entrée agent si n'existe pas
                if (!agentsMap.containsKey(idAgent)) {
                    FraisAgentDTO agentDTO = new FraisAgentDTO();
                    agentDTO.setIdAgent(idAgent);
                    agentDTO.setLignesFrais(new ArrayList<>());
                    agentDTO.setTotalAgent(0L);

                    // Résoudre le nom et la fonction
                    if (idAgent > 0) {
                        // C'est un agent classique
                        agentRepository.findById(idAgent).ifPresent(agent -> {
                            agentDTO.setNomAgent(agent.getNomAgent());
                            agentDTO.setPrenomAgent(agent.getPrenomAgent());
                            agentDTO.setNomCompletAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
                            agentDTO.setIdFonction(agent.getIdFonction());
                            
                            fonctionRepository.findById(agent.getIdFonction()).ifPresent(f -> 
                                agentDTO.setLibelleFonction(f.getLibFonction())
                            );
                        });
                    } else {
                        // C'est une ressource (ID négatif)
                        Long idRessource = -idAgent;
                        ressourceRepository.findById(idRessource).ifPresent(res -> {
                            String type = "";
                            if (res.getIdTypeRessource() == 2L) type = "Chauffeur";
                            else if (res.getIdTypeRessource() == 3L) type = "Police";
                            
                            agentDTO.setNomAgent(res.getLibRessource());
                            agentDTO.setPrenomAgent("");
                            agentDTO.setNomCompletAgent(res.getLibRessource() + " (" + type + ")");
                            agentDTO.setLibelleFonction(type);
                        });
                    }
                    agentsMap.put(idAgent, agentDTO);
                }

                // Ajouter la ligne de frais
                FraisAgentDTO agentDTO = agentsMap.get(idAgent);
                FraisLigneDTO ligne = new FraisLigneDTO();
                ligne.setIdCategorieFrais(frais.getIdCategorieFrais());
                ligne.setLibelleCategorie(categoriesMap.getOrDefault(frais.getIdCategorieFrais(), "Inconnu"));
                ligne.setQuantite(frais.getQuantiteFraisMission());
                ligne.setPrixUnitaire(frais.getPrixUnitaireFraisMission());
                ligne.setMontant(frais.getMontantPrevuFraisMission());

                agentDTO.getLignesFrais().add(ligne);
                agentDTO.setTotalAgent(agentDTO.getTotalAgent() + ligne.getMontant());

                // Totaux globaux
                totalGeneral += ligne.getMontant();
                if (frais.getIdCategorieFrais() == 1L) totalRepas += ligne.getMontant();
                else if (frais.getIdCategorieFrais() == 2L) totalHebergement += ligne.getMontant();
                else if (frais.getIdCategorieFrais() == 3L) totalIndemnite += ligne.getMontant();
                else if (frais.getIdCategorieFrais() == 4L) totalCarburant += ligne.getMontant();
            }

            // 5. Construire le récapitulatif
            RecapitulatifFraisDTO recap = new RecapitulatifFraisDTO();
            recap.setIdOrdreMission(mission.getIdOrdreMission());
            recap.setNumeroOrdreMission(mission.getNumeroOrdreMission());
            recap.setObjetOrdreMission(mission.getObjetOrdreMission());
            recap.setDateDebut(mission.getDateDebutPrevueOrdreMission());
            recap.setDateFin(mission.getDateFinPrevueOrdreMission());
            
            // Calcul durées
            long dureeJours = 0;
            if (mission.getDateDebutPrevueOrdreMission() != null && mission.getDateFinPrevueOrdreMission() != null) {
                dureeJours = ChronoUnit.DAYS.between(mission.getDateDebutPrevueOrdreMission(), mission.getDateFinPrevueOrdreMission()) + 1;
            }
            recap.setDureeJours(dureeJours);
            recap.setDureeNuits(Math.max(0, dureeJours - 1));
            
            // Vérifier si le nombre d'agents calculés correspond au nombre attendu (Participants + Ressources Humaines)
            int nbParticipants = participerRepository.countByIdOrdreMission(id);
            long nbRessourcesHumaines = utiliserRessourRepository.findByIdOrdreMission(id).stream()
                .filter(ur -> {
                    return ressourceRepository.findById(ur.getIdRessource())
                        .map(r -> r.getIdTypeRessource() == 2L || r.getIdTypeRessource() == 3L)
                        .orElse(false);
                })
                .count();
            
            int nbAttendu = nbParticipants + (int) nbRessourcesHumaines;
            int nbCalcules = agentsMap.size();
            
            // Si incohérence, on recalcule UNE SEULE FOIS et on recharge les données
            if (nbCalcules < nbAttendu) {
                log.warn("⚠️ [MG] Incohérence détectée : {} calculés vs {} attendus. Recalcul forcé...", nbCalcules, nbAttendu);
                
                // 1. Supprimer les anciens frais pour éviter les doublons
                fraisMissionRepository.deleteByIdOrdreMission(id);
                
                // 2. Recalculer
                calculerFraisAutomatique(mission);
                
                // 3. Recharger les frais fraîchement calculés
                fraisList = fraisMissionRepository.findByIdOrdreMission(id);
                
                // 4. Reconstruire la map (copier-coller de la logique ci-dessus, ou refactoriser)
                // Pour faire simple et éviter la récursion infinie, on refait la boucle de mapping ici
                agentsMap.clear();
                totalGeneral = 0;
                totalRepas = 0;
                totalHebergement = 0;
                totalIndemnite = 0;
                totalCarburant = 0;
                
                for (GmFraisMission frais : fraisList) {
                    Long idAgent = frais.getIdAgent();
                    if (!agentsMap.containsKey(idAgent)) {
                        FraisAgentDTO agentDTO = new FraisAgentDTO();
                        agentDTO.setIdAgent(idAgent);
                        agentDTO.setLignesFrais(new ArrayList<>());
                        agentDTO.setTotalAgent(0L);

                        if (idAgent > 0) {
                            agentRepository.findById(idAgent).ifPresent(agent -> {
                                agentDTO.setNomAgent(agent.getNomAgent());
                                agentDTO.setPrenomAgent(agent.getPrenomAgent());
                                agentDTO.setNomCompletAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
                                agentDTO.setIdFonction(agent.getIdFonction());
                                fonctionRepository.findById(agent.getIdFonction()).ifPresent(f -> 
                                    agentDTO.setLibelleFonction(f.getLibFonction())
                                );
                            });
                        } else {
                            Long idRessource = -idAgent;
                            ressourceRepository.findById(idRessource).ifPresent(res -> {
                                String type = "";
                                if (res.getIdTypeRessource() == 2L) type = "Chauffeur";
                                else if (res.getIdTypeRessource() == 3L) type = "Police";
                                agentDTO.setNomAgent(res.getLibRessource());
                                agentDTO.setPrenomAgent("");
                                agentDTO.setNomCompletAgent(res.getLibRessource() + " (" + type + ")");
                                agentDTO.setLibelleFonction(type);
                            });
                        }
                        agentsMap.put(idAgent, agentDTO);
                    }
                    
                    FraisAgentDTO agentDTO = agentsMap.get(idAgent);
                    FraisLigneDTO ligne = new FraisLigneDTO();
                    ligne.setIdCategorieFrais(frais.getIdCategorieFrais());
                    ligne.setLibelleCategorie(categoriesMap.getOrDefault(frais.getIdCategorieFrais(), "Inconnu"));
                    ligne.setQuantite(frais.getQuantiteFraisMission());
                    ligne.setPrixUnitaire(frais.getPrixUnitaireFraisMission());
                    ligne.setMontant(frais.getMontantPrevuFraisMission());

                    agentDTO.getLignesFrais().add(ligne);
                    agentDTO.setTotalAgent(agentDTO.getTotalAgent() + ligne.getMontant());

                    totalGeneral += ligne.getMontant();
                    if (frais.getIdCategorieFrais() == 1L) totalRepas += ligne.getMontant();
                    else if (frais.getIdCategorieFrais() == 2L) totalHebergement += ligne.getMontant();
                    else if (frais.getIdCategorieFrais() == 3L) totalIndemnite += ligne.getMontant();
                    else if (frais.getIdCategorieFrais() == 4L) totalCarburant += ligne.getMontant();
                }
            }
            
            recap.setNombreAgents(agentsMap.size());
            recap.setFraisParAgent(new ArrayList<>(agentsMap.values()));
            recap.setTotalRepas(totalRepas);
            recap.setTotalHebergement(totalHebergement);
            recap.setTotalIndemnite(totalIndemnite);
            recap.setTotalCarburant(totalCarburant);
            recap.setTotalGeneral(totalGeneral);
            
            recap.setValidationComplete("BUDGET_VALIDE".equals(mission.getStatutOrdreMission()));

            log.info("✅ [MG] Détails récupérés (Manuel) : {} FCFA pour {} personnes", totalGeneral, agentsMap.size());
            return ResponseEntity.ok(recap);

        } catch (Exception e) {
            log.error("❌ [MG] Erreur : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/mg/missions/{id}/valider - Valide le budget
     */
    @PostMapping("/missions/{id}/valider")
    public ResponseEntity<?> validerBudget(@PathVariable Long id) {
        log.info("✅ [MG] Validation budget mission {}", id);

        try {
            GmOrdreMission mission = missionRepository.findById(id).orElseThrow();

            if (!fraisMissionRepository.existsByIdOrdreMission(id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Frais non calculés"));
            }

            mission.setStatutOrdreMission("BUDGET_VALIDE");
            missionRepository.save(mission);

            List<GmFraisMission> frais = fraisMissionRepository.findByIdOrdreMission(id);
            frais.forEach(f -> {
                f.setStatutValidationFraisMission("VALIDE");
                f.setDateValidFaisMission(LocalDate.now());
            });
            fraisMissionRepository.saveAll(frais);

            log.info("✅ [MG] Mission {} validée", id);
            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            log.error("❌ [MG] Erreur : {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/mg/bareme - Liste le barème
     */
    @GetMapping("/bareme")
    public ResponseEntity<List<BaremeDTO>> getBareme() {
        log.info("📋 [MG] Récupération barème");

        List<GmBareme> baremes = baremeRepository.findAll();
        
        List<BaremeDTO> dtos = baremes.stream().map(b -> {
            GmCategorieFrais categorie = categorieFraisRepository.findById(b.getIdCategorieFrais()).orElse(null);
            GmFonction fonction = fonctionRepository.findById(b.getIdFonction()).orElse(null);

            BaremeDTO dto = new BaremeDTO();
            dto.setIdCategorieFrais(b.getIdCategorieFrais());
            dto.setIdFonction(b.getIdFonction());
            dto.setNomCategorie(categorie != null ? categorie.getLibelleCategorieFrais() : "");
            dto.setNomFonction(fonction != null ? fonction.getLibFonction() : "");
            dto.setMontantUnitaire(b.getMontantUnitaire());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/mg/missions/validees - Liste des missions BUDGET_VALIDE
     */
    @GetMapping("/missions/validees")
    public ResponseEntity<List<MissionMgDTO>> getMissionsAvecBudgetValide() {
        log.info("📋 [MG] Récupération des missions avec budget validé");

        List<GmOrdreMission> missions = missionRepository.findByStatutOrdreMission("BUDGET_VALIDE");
        
        List<MissionMgDTO> dtos = missions.stream().map(mission -> {
            Long idMission = mission.getIdOrdreMission();
            
            int nombreAgents = participerRepository.countByIdOrdreMission(idMission);
            Long totalFrais = fraisMissionRepository.sumMontantByIdOrdreMission(idMission);
            
            long dureeJours = 0;
            if (mission.getDateDebutPrevueOrdreMission() != null && mission.getDateFinPrevueOrdreMission() != null) {
                dureeJours = ChronoUnit.DAYS.between(
                        mission.getDateDebutPrevueOrdreMission(),
                        mission.getDateFinPrevueOrdreMission()
                ) + 1;
            }
            
            MissionMgDTO dto = new MissionMgDTO();
            dto.setIdOrdreMission(idMission);
            dto.setNumeroOrdreMission(mission.getNumeroOrdreMission());
            dto.setObjetOrdreMission(mission.getObjetOrdreMission());
            dto.setDateDebut(mission.getDateDebutPrevueOrdreMission());
            dto.setDateFin(mission.getDateFinPrevueOrdreMission());
            dto.setDureeJours(dureeJours);
            dto.setNombreAgents(nombreAgents);
            dto.setStatutOrdreMission(mission.getStatutOrdreMission());
            dto.setValidationComplete(true);
            dto.setFraisCalcules(true);
            dto.setTotalFrais(totalFrais);
            
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Helper
    private void saveFrais(Long idMission, Long idAgent, Long idCategorie, long quantite, long prixUnitaire, long montantTotal) {
        GmFraisMission frais = new GmFraisMission();
        frais.setIdOrdreMission(idMission);
        frais.setIdAgent(idAgent);
        frais.setIdCategorieFrais(idCategorie);
        frais.setQuantiteFraisMission(quantite);
        frais.setPrixUnitaireFraisMission(prixUnitaire);
        frais.setMontantPrevuFraisMission(montantTotal);
        frais.setStatutValidationFraisMission("CALCULE");
        frais.setDateCreFraisMission(LocalDate.now());
        fraisMissionRepository.save(frais);
    }

    /**
     * Calcul automatique des frais pour une mission (appelé depuis GET /missions)
     */
    private void calculerFraisAutomatique(GmOrdreMission mission) {
        try {
            Long id = mission.getIdOrdreMission();
            
            // 🧹 Nettoyage préalable pour éviter les doublons
            fraisMissionRepository.deleteByIdOrdreMission(id);
            
            // Récupérer participants (agents)
            List<GmParticiper> participants = participerRepository.findByIdOrdreMission(id);
            
            // Récupérer ressources (Police et Chauffeur)
            List<GmUtiliserRessour> ressources = utiliserRessourRepository.findByIdOrdreMission(id);
            
            if (participants.isEmpty() && ressources.isEmpty()) {
                log.warn("⚠️ [MG] Mission {} : aucun participant ni ressource", id);
                return;
            }

            // Calculer durée
            long dureeJours = ChronoUnit.DAYS.between(
                    mission.getDateDebutPrevueOrdreMission(),
                    mission.getDateFinPrevueOrdreMission()
            ) + 1;
            long dureeNuits = dureeJours - 1;

            // ========== 1. CALCULER FRAIS POUR LES PARTICIPANTS (Agents) ==========
            log.info("🔄 [MG] Mission {} : calcul frais pour {} participants", id, participants.size());
            for (GmParticiper participant : participants) {
                GmAgent agent = agentRepository.findById(participant.getIdAgent()).orElse(null);
                if (agent == null) continue;
                
                Long idFonction = agent.getIdFonction();
                calculerFraisParFonction(id, agent.getIdAgent(), idFonction, dureeJours, dureeNuits, 
                    agent.getNomAgent() + " " + agent.getPrenomAgent());
            }

            // ========== 2. CALCULER FRAIS POUR LES RESSOURCES (Police et Chauffeur) ==========
            log.info("🚗 [MG] Mission {} : calcul frais pour {} ressources", id, ressources.size());
            for (GmUtiliserRessour utiliserRessour : ressources) {
                GmRessource ressource = ressourceRepository.findById(utiliserRessour.getIdRessource()).orElse(null);
                if (ressource == null) continue;
                
                Long idTypeRessource = ressource.getIdTypeRessource();
                
                // Type 2 = Chauffeur → Fonction 5
                // Type 3 = Police → Fonction 4
                // Type 1 = Véhicule → Pas de frais
                Long idFonction = null;
                if (idTypeRessource == 2L) {
                    idFonction = 5L; // Chauffeur
                } else if (idTypeRessource == 3L) {
                    idFonction = 4L; // Police
                } else {
                    log.debug("⏭️ [MG] Ressource {} (type {}) : pas de frais (véhicule)", 
                        ressource.getLibRessource(), idTypeRessource);
                    continue; // Véhicule, pas de frais
                }
                
                // Utiliser l'ID de la ressource comme ID d'agent fictif (négatif pour éviter conflits)
                Long idAgentFictif = -ressource.getIdRessource();
                
                log.info("👮 [MG] Ressource {} ({}), fonction {}", 
                    ressource.getLibRessource(), idTypeRessource == 2L ? "Chauffeur" : "Police", idFonction);
                
                calculerFraisParFonction(id, idAgentFictif, idFonction, dureeJours, dureeNuits, 
                    ressource.getLibRessource());
            }
            
            log.info("✅ [MG] Calcul auto terminé pour mission {}", id);
        } catch (Exception e) {
            log.error("❌ [MG] Erreur calcul auto : {}", e.getMessage(), e);
        }
    }

    /**
     * Calcule les frais pour une fonction donnée (utilisé par participants ET ressources)
     */
    private void calculerFraisParFonction(Long idMission, Long idAgent, Long idFonction, 
                                          long dureeJours, long dureeNuits, String nomComplet) {
        try {
            // Repas (ID=1)
            Long montantRepas = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 1L).orElse(0L);
            if (montantRepas > 0) {
                saveFrais(idMission, idAgent, 1L, dureeJours, montantRepas, montantRepas * dureeJours);
                log.debug("   💰 {} - Repas : {} × {} = {}", nomComplet, montantRepas, dureeJours, montantRepas * dureeJours);
            }

            // Hébergement (ID=2)
            if (dureeNuits > 0) {
                Long montantHebergement = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 2L).orElse(0L);
                if (montantHebergement > 0) {
                    saveFrais(idMission, idAgent, 2L, dureeNuits, montantHebergement, montantHebergement * dureeNuits);
                    log.debug("   🏨 {} - Hébergement : {} × {} = {}", nomComplet, montantHebergement, dureeNuits, montantHebergement * dureeNuits);
                }
            }

            // Indemnité (ID=3)
            Long montantIndemnite = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 3L).orElse(0L);
            if (montantIndemnite > 0) {
                saveFrais(idMission, idAgent, 3L, dureeJours, montantIndemnite, montantIndemnite * dureeJours);
                log.debug("   📋 {} - Indemnité : {} × {} = {}", nomComplet, montantIndemnite, dureeJours, montantIndemnite * dureeJours);
            }

            // Carburant (ID=4, Fonction=6 uniquement - Chef de mission)
            if (idFonction == 6L) {
                Long montantCarburant = baremeRepository.findMontantByFonctionAndCategorie(6L, 4L).orElse(0L);
                if (montantCarburant > 0) {
                    saveFrais(idMission, idAgent, 4L, 1L, montantCarburant, montantCarburant);
                    log.debug("   ⛽ {} - Carburant : {}", nomComplet, montantCarburant);
                }
            }
        } catch (Exception e) {
            log.error("❌ [MG] Erreur calcul frais pour {} (fonction {}) : {}", nomComplet, idFonction, e.getMessage());
        }
    }

    /**
     * Calcule et crée les frais pour un agent/ressource, retourne le DTO avec détails
     */
    private FraisAgentDTO calculerEtCreerFrais(Long idMission, Long idAgent, Long idFonction,
                                               long dureeJours, long dureeNuits,
                                               String nom, String prenom, String nomComplet,
                                               boolean estRessource) {
        List<FraisLigneDTO> lignes = new ArrayList<>();
        long totalAgent = 0;

        // Repas (ID=1)
        Long montantRepas = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 1L).orElse(0L);
        if (montantRepas > 0) {
            long montantTotal = montantRepas * dureeJours;
            saveFrais(idMission, idAgent, 1L, dureeJours, montantRepas, montantTotal);
            lignes.add(new FraisLigneDTO(1L, "Repas", dureeJours, montantRepas, montantTotal));
            totalAgent += montantTotal;
        }

        // Hébergement (ID=2)
        if (dureeNuits > 0) {
            Long montantHebergement = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 2L).orElse(0L);
            if (montantHebergement > 0) {
                long montantTotal = montantHebergement * dureeNuits;
                saveFrais(idMission, idAgent, 2L, dureeNuits, montantHebergement, montantTotal);
                lignes.add(new FraisLigneDTO(2L, "Hébergement", dureeNuits, montantHebergement, montantTotal));
                totalAgent += montantTotal;
            }
        }

        // Indemnité (ID=3)
        Long montantIndemnite = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 3L).orElse(0L);
        if (montantIndemnite > 0) {
            long montantTotal = montantIndemnite * dureeJours;
            saveFrais(idMission, idAgent, 3L, dureeJours, montantIndemnite, montantTotal);
            lignes.add(new FraisLigneDTO(3L, "Indemnité", dureeJours, montantIndemnite, montantTotal));
            totalAgent += montantTotal;
        }

        // Carburant (ID=4, Fonction=6 uniquement)
        if (idFonction == 6L) {
            Long montantCarburant = baremeRepository.findMontantByFonctionAndCategorie(6L, 4L).orElse(0L);
            if (montantCarburant > 0) {
                saveFrais(idMission, idAgent, 4L, 1L, montantCarburant, montantCarburant);
                lignes.add(new FraisLigneDTO(4L, "Carburant", 1L, montantCarburant, montantCarburant));
                totalAgent += montantCarburant;
            }
        }

        FraisAgentDTO fraisAgent = new FraisAgentDTO();
        fraisAgent.setIdAgent(idAgent);
        fraisAgent.setNomAgent(nom);
        fraisAgent.setPrenomAgent(prenom);
        fraisAgent.setNomCompletAgent(nomComplet);
        fraisAgent.setIdFonction(idFonction);
        
        if (!estRessource) {
            GmFonction fonction = fonctionRepository.findById(idFonction).orElse(null);
            fraisAgent.setLibelleFonction(fonction != null ? fonction.getLibFonction() : "");
        }
        
        fraisAgent.setLignesFrais(lignes);
        fraisAgent.setTotalAgent(totalAgent);
        
        return fraisAgent;
    }
}
