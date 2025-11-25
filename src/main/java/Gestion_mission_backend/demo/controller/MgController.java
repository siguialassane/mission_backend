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
    private final Gestion_mission_backend.demo.repository.ViewMgFraisCompletRepository viewFraisCompletRepository;

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
                
                // Vérifier si cet agent est le Chef de Mission
                boolean estChefMission = "CHEF_MISSION".equals(participant.getRole());
                
                FraisAgentDTO fraisAgent = calculerEtCreerFraisAvecChef(
                    id, agent.getIdAgent(), idFonction, dureeJours, dureeNuits,
                    agent.getNomAgent(), agent.getPrenomAgent(), 
                    agent.getNomAgent() + " " + agent.getPrenomAgent(),
                    false, // Pas une ressource
                    estChefMission // Flag Chef de Mission pour le bonus carburant
                );
                fraisParAgent.add(fraisAgent);
                
                if (estChefMission) {
                    log.info("👑 [MG] Chef de Mission détecté : {} {} (Fonction: {}) - Bonus Carburant appliqué", 
                        agent.getPrenomAgent(), agent.getNomAgent(), idFonction);
                }
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
                
                // Utiliser un ID fictif positif (900000 + ID) pour éviter les IDs négatifs
                // Cela permet d'avoir un code unique "propre" pour les ressources
                Long idAgentFictif = 900000 + ressource.getIdRessource();
                
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
     * GET /api/mg/missions/{id}/frais - Détails des frais (Via Vue SQL Optimisée)
     */
    @GetMapping("/missions/{id}/frais")
    @Transactional
    public ResponseEntity<?> getFraisDetails(@PathVariable Long id) {
        log.info("📊 [MG] Récupération détails frais mission {} (Vue SQL)", id);

        try {
            // 1. Récupérer la mission
            GmOrdreMission mission = missionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

            // 2. Récupérer les frais via la Vue SQL Optimisée
            List<Gestion_mission_backend.demo.entity.ViewMgFraisComplet> fraisList = viewFraisCompletRepository.findByIdMission(id);
            
            if (fraisList.isEmpty()) {
                // Si vide, tenter un calcul automatique
                log.warn("⚠️ [MG] Aucun frais trouvé pour mission {}, tentative de calcul...", id);
                calculerFraisAutomatique(mission);
                fraisList = viewFraisCompletRepository.findByIdMission(id);
                
                if (fraisList.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Frais non calculés",
                        "message", "Aucun frais trouvé pour cette mission"
                    ));
                }
            }

            // 3. Grouper par Agent (ou Ressource)
            Map<Long, FraisAgentDTO> agentsMap = new LinkedHashMap<>();
            long totalGeneral = 0;
            long totalRepas = 0;
            long totalHebergement = 0;
            long totalIndemnite = 0;
            long totalCarburant = 0;

            for (Gestion_mission_backend.demo.entity.ViewMgFraisComplet frais : fraisList) {
                Long idAgent = frais.getIdParticipant();
                
                // Créer l'entrée agent si n'existe pas
                if (!agentsMap.containsKey(idAgent)) {
                    FraisAgentDTO agentDTO = new FraisAgentDTO();
                    agentDTO.setIdAgent(idAgent);
                    agentDTO.setLignesFrais(new ArrayList<>());
                    agentDTO.setTotalAgent(0L);
                    
                    // Nom et fonction viennent directement de la Vue !
                    agentDTO.setNomCompletAgent(frais.getNomComplet());
                    
                    // Séparer Nom/Prénom si possible (pour compatibilité frontend)
                    String[] parts = frais.getNomComplet().split(" ", 2);
                    agentDTO.setNomAgent(parts[0]);
                    agentDTO.setPrenomAgent(parts.length > 1 ? parts[1] : "");
                    
                    agentDTO.setLibelleFonction(frais.getLibFonction());
                    
                    agentsMap.put(idAgent, agentDTO);
                }

                // Ajouter la ligne de frais
                FraisAgentDTO agentDTO = agentsMap.get(idAgent);
                FraisLigneDTO ligne = new FraisLigneDTO();
                ligne.setIdCategorieFrais(frais.getIdCategorieFrais());
                ligne.setLibelleCategorie(frais.getLibCategorie());
                ligne.setQuantite(frais.getQuantite());
                ligne.setPrixUnitaire(frais.getPrixUnitaire());
                ligne.setMontant(frais.getMontantTotal());

                agentDTO.getLignesFrais().add(ligne);
                agentDTO.setTotalAgent(agentDTO.getTotalAgent() + ligne.getMontant());

                // Totaux globaux
                totalGeneral += ligne.getMontant();
                if (frais.getIdCategorieFrais() == 1L) totalRepas += ligne.getMontant();
                else if (frais.getIdCategorieFrais() == 2L) totalHebergement += ligne.getMontant();
                else if (frais.getIdCategorieFrais() == 3L) totalIndemnite += ligne.getMontant();
                else if (frais.getIdCategorieFrais() == 4L) totalCarburant += ligne.getMontant();
            }

            // Tri des lignes de frais pour chaque agent : Repas (1) -> Hébergement (2) -> Indemnité (3) -> Carburant (4)
            for (FraisAgentDTO agent : agentsMap.values()) {
                agent.getLignesFrais().sort(Comparator.comparingLong(FraisLigneDTO::getIdCategorieFrais));
            }

            // 4. Construire le récapitulatif
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
            
            recap.setNombreAgents(agentsMap.size());
            recap.setFraisParAgent(new ArrayList<>(agentsMap.values()));
            recap.setTotalRepas(totalRepas);
            recap.setTotalHebergement(totalHebergement);
            recap.setTotalIndemnite(totalIndemnite);
            recap.setTotalCarburant(totalCarburant);
            recap.setTotalGeneral(totalGeneral);
            
            recap.setValidationComplete("BUDGET_VALIDE".equals(mission.getStatutOrdreMission()));

            log.info("✅ [MG] Détails récupérés (Vue SQL) : {} FCFA pour {} personnes", totalGeneral, agentsMap.size());
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

            // Calcul du budget total validé
            List<GmFraisMission> frais = fraisMissionRepository.findByIdOrdreMission(id);
            long totalBudget = frais.stream()
                .mapToLong(f -> (f.getMontantPrevuFraisMission() != null ? f.getMontantPrevuFraisMission().longValue() : 0))
                .sum();

            mission.setBudgetAlloueOrdreMission(totalBudget);
            mission.setStatutOrdreMission("BUDGET_VALIDE");
            missionRepository.save(mission);

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

    /**
     * GET /api/mg/missions/{id}/compare - Compare les résultats Java vs Vue SQL
     */
    @GetMapping("/missions/{id}/compare")
    public ResponseEntity<?> compareCalculationMethods(@PathVariable Long id) {
        log.info("⚖️ [MG] Comparaison Java vs Vue SQL pour mission {}", id);
        
        // 1. Résultat Java (Méthode actuelle)
        ResponseEntity<?> responseJava = getFraisDetails(id);
        Object resultJava = responseJava.getBody();
        
        // 2. Résultat Vue SQL (Nouvelle méthode)
        List<Gestion_mission_backend.demo.entity.ViewMgFraisComplet> resultVue = viewFraisCompletRepository.findByIdMission(id);
        
        // Calculer le total via la Vue
        long totalVue = resultVue.stream().mapToLong(v -> v.getMontantTotal() != null ? v.getMontantTotal() : 0).sum();
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("java_result", resultJava);
        comparison.put("vue_result", resultVue);
        comparison.put("vue_total", totalVue);
        
        // Extraction du total Java pour comparaison facile (si possible)
        if (resultJava instanceof RecapitulatifFraisDTO) {
            comparison.put("java_total", ((RecapitulatifFraisDTO) resultJava).getTotalGeneral());
            comparison.put("match", totalVue == ((RecapitulatifFraisDTO) resultJava).getTotalGeneral());
        }
        
        return ResponseEntity.ok(comparison);
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
                
                // Règle Spéciale : Si l'agent est CHEF_MISSION dans cette mission, il reçoit le carburant
                // Mais attention : il garde sa fonction d'origine (ex: Chef de Service) pour les repas/hôtel
                boolean estChefMission = "CHEF_MISSION".equals(participant.getRole());
                
                calculerFraisParFonction(id, agent.getIdAgent(), idFonction, dureeJours, dureeNuits, 
                    agent.getNomAgent() + " " + agent.getPrenomAgent(), estChefMission);
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
                
                // Utiliser un ID fictif POSITIF (900000 + ID) pour éviter les IDs négatifs
                // Plage réservée : 900000+ pour les ressources (Police, Chauffeur)
                Long idAgentFictif = 900000L + ressource.getIdRessource();
                
                log.info("👮 [MG] Ressource {} ({}), fonction {}, ID fictif: {}", 
                    ressource.getLibRessource(), idTypeRessource == 2L ? "Chauffeur" : "Police", idFonction, idAgentFictif);
                
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
     * 
     * NOUVELLE RÈGLE MÉTIER (25/11/2025) :
     * - TOUS les participants reçoivent Repas + Hébergement + Indemnité selon leur FONCTION D'ORIGINE
     * - Le Chef de Mission reçoit EN PLUS un bonus Carburant de 400.000 FCFA
     */
    private void calculerFraisParFonction(Long idMission, Long idAgent, Long idFonction, 
                                          long dureeJours, long dureeNuits, String nomComplet, boolean estChefMission) {
        try {
            // ========== FRAIS NORMAUX (pour TOUS les participants, y compris le Chef de Mission) ==========
            // On utilise la fonction d'origine de l'agent (ex: Fondé de pouvoir, Chef de service, Agent)
            
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

            // ========== BONUS CARBURANT (UNIQUEMENT pour le Chef de Mission) ==========
            if (estChefMission) {
                // On utilise la Fonction 6 (Chef de Mission) et la Catégorie 4 (Carburant) pour récupérer le forfait
                Long montantCarburant = baremeRepository.findMontantByFonctionAndCategorie(6L, 4L).orElse(400000L);
                if (montantCarburant > 0) {
                    saveFrais(idMission, idAgent, 4L, 1L, montantCarburant, montantCarburant);
                    log.info("   ⛽ {} - BONUS Carburant (Chef de Mission) : {} FCFA", nomComplet, montantCarburant);
                }
            }

        } catch (Exception e) {
            log.error("❌ [MG] Erreur calcul frais pour {} (fonction {}) : {}", nomComplet, idFonction, e.getMessage());
        }
    }

    // Surcharge pour compatibilité avec l'ancien appel (Ressources)
    private void calculerFraisParFonction(Long idMission, Long idAgent, Long idFonction, 
                                          long dureeJours, long dureeNuits, String nomComplet) {
        calculerFraisParFonction(idMission, idAgent, idFonction, dureeJours, dureeNuits, nomComplet, false);
    }

    /**
     * Calcule et crée les frais pour un agent/ressource, retourne le DTO avec détails
     * 
     * NOUVELLE RÈGLE MÉTIER (25/11/2025) :
     * - Tous les participants reçoivent les frais normaux selon leur fonction d'origine
     * - Le Chef de Mission reçoit EN PLUS le bonus Carburant 400.000 FCFA
     */
    private FraisAgentDTO calculerEtCreerFrais(Long idMission, Long idAgent, Long idFonction,
                                               long dureeJours, long dureeNuits,
                                               String nom, String prenom, String nomComplet,
                                               boolean estRessource) {
        return calculerEtCreerFraisAvecChef(idMission, idAgent, idFonction, dureeJours, dureeNuits,
                                            nom, prenom, nomComplet, estRessource, false);
    }

    /**
     * Calcule et crée les frais pour un agent/ressource, avec gestion du Chef de Mission
     */
    private FraisAgentDTO calculerEtCreerFraisAvecChef(Long idMission, Long idAgent, Long idFonction,
                                               long dureeJours, long dureeNuits,
                                               String nom, String prenom, String nomComplet,
                                               boolean estRessource, boolean estChefMission) {
        List<FraisLigneDTO> lignes = new ArrayList<>();
        long totalAgent = 0;

        // ========== FRAIS NORMAUX (pour TOUS, y compris le Chef de Mission) ==========
        
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

        // ========== BONUS CARBURANT (UNIQUEMENT pour le Chef de Mission) ==========
        if (estChefMission) {
            Long montantCarburant = baremeRepository.findMontantByFonctionAndCategorie(6L, 4L).orElse(400000L);
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
