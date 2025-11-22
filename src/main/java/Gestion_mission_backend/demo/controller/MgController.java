package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.*;
import Gestion_mission_backend.demo.entity.*;
import Gestion_mission_backend.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    /**
     * GET /api/mg/missions - Liste des missions VALIDEE_RH avec calcul automatique
     */
    @GetMapping("/missions")
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

            // Récupérer participants
            List<GmParticiper> participants = participerRepository.findByIdOrdreMission(id);
            if (participants.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Aucun participant"));
            }

            // Calculer durée
            long dureeJours = ChronoUnit.DAYS.between(
                    mission.getDateDebutPrevueOrdreMission(),
                    mission.getDateFinPrevueOrdreMission()
            ) + 1;
            long dureeNuits = dureeJours - 1;

            // Calculer frais par agent
            List<FraisAgentDTO> fraisParAgent = new ArrayList<>();
            for (GmParticiper participant : participants) {
                GmAgent agent = agentRepository.findById(participant.getIdAgent()).orElseThrow();
                Long idFonction = agent.getIdFonction();
                
                List<FraisLigneDTO> lignes = new ArrayList<>();
                long totalAgent = 0;

                // Repas (ID=1)
                Long montantRepas = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 1L).orElse(0L);
                if (montantRepas > 0) {
                    long montantTotal = montantRepas * dureeJours;
                    saveFrais(id, agent.getIdAgent(), 1L, dureeJours, montantRepas, montantTotal);
                    lignes.add(new FraisLigneDTO(1L, "Repas", dureeJours, montantRepas, montantTotal));
                    totalAgent += montantTotal;
                }

                // Hébergement (ID=2)
                if (dureeNuits > 0) {
                    Long montantHebergement = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 2L).orElse(0L);
                    if (montantHebergement > 0) {
                        long montantTotal = montantHebergement * dureeNuits;
                        saveFrais(id, agent.getIdAgent(), 2L, dureeNuits, montantHebergement, montantTotal);
                        lignes.add(new FraisLigneDTO(2L, "Hébergement", dureeNuits, montantHebergement, montantTotal));
                        totalAgent += montantTotal;
                    }
                }

                // Indemnité (ID=3)
                Long montantIndemnite = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 3L).orElse(0L);
                if (montantIndemnite > 0) {
                    long montantTotal = montantIndemnite * dureeJours;
                    saveFrais(id, agent.getIdAgent(), 3L, dureeJours, montantIndemnite, montantTotal);
                    lignes.add(new FraisLigneDTO(3L, "Indemnité", dureeJours, montantIndemnite, montantTotal));
                    totalAgent += montantTotal;
                }

                // Carburant (ID=4, Fonction=6)
                if (idFonction == 6L) {
                    Long montantCarburant = baremeRepository.findMontantByFonctionAndCategorie(6L, 4L).orElse(0L);
                    if (montantCarburant > 0) {
                        saveFrais(id, agent.getIdAgent(), 4L, 1L, montantCarburant, montantCarburant);
                        lignes.add(new FraisLigneDTO(4L, "Carburant", 1L, montantCarburant, montantCarburant));
                        totalAgent += montantCarburant;
                    }
                }

                FraisAgentDTO fraisAgent = new FraisAgentDTO();
                fraisAgent.setIdAgent(agent.getIdAgent());
                fraisAgent.setNomAgent(agent.getNomAgent());
                fraisAgent.setPrenomAgent(agent.getPrenomAgent());
                fraisAgent.setNomCompletAgent(agent.getNomAgent() + " " + agent.getPrenomAgent());
                fraisAgent.setIdFonction(idFonction);
                
                GmFonction fonction = fonctionRepository.findById(idFonction).orElse(null);
                fraisAgent.setLibelleFonction(fonction != null ? fonction.getLibFonction() : "");
                
                fraisAgent.setLignesFrais(lignes);
                fraisAgent.setTotalAgent(totalAgent);
                fraisParAgent.add(fraisAgent);
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
            recap.setNombreAgents(participants.size());
            recap.setFraisParAgent(fraisParAgent);
            recap.setTotalGeneral(totalGlobal);
            recap.setValidationComplete(true);

            log.info("✅ [MG] Calcul terminé : {} FCFA", totalGlobal);
            return ResponseEntity.ok(recap);

        } catch (Exception e) {
            log.error("❌ [MG] Erreur : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/mg/missions/{id}/frais - Détails des frais avec vues Oracle
     */
    @GetMapping("/missions/{id}/frais")
    public ResponseEntity<?> getFraisDetails(@PathVariable Long id) {
        log.info("📊 [MG] Récupération détails frais mission {}", id);

        try {
            // Récupérer les détails depuis la vue V_MG_FRAIS_AGENT
            List<ViewMgFraisAgent> detailsFrais = viewFraisAgentRepository.findByIdOrdreMission(id);
            if (detailsFrais.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Frais non calculés",
                    "message", "Aucun frais trouvé pour cette mission"
                ));
            }

            // Récupérer le récapitulatif depuis la vue V_MG_RECAP
            ViewMgRecap recapView = viewRecapRepository.findByIdOrdreMission(id)
                    .orElseThrow(() -> new RuntimeException("Récapitulatif non trouvé"));

            // Grouper les frais par agent
            Map<Long, FraisAgentDTO> agentsMap = new LinkedHashMap<>();
            
            for (ViewMgFraisAgent detail : detailsFrais) {
                Long idAgent = detail.getIdAgent();
                
                if (!agentsMap.containsKey(idAgent)) {
                    FraisAgentDTO agentDTO = new FraisAgentDTO();
                    agentDTO.setIdAgent(idAgent);
                    agentDTO.setNomAgent(detail.getNomAgent());
                    agentDTO.setPrenomAgent(detail.getPrenomAgent());
                    agentDTO.setNomCompletAgent(detail.getNomComplet());
                    agentDTO.setIdFonction(detail.getIdFonction());
                    agentDTO.setLibelleFonction(detail.getLibFonction());
                    agentDTO.setLignesFrais(new ArrayList<>());
                    agentDTO.setTotalAgent(0L);
                    agentsMap.put(idAgent, agentDTO);
                }
                
                FraisAgentDTO agentDTO = agentsMap.get(idAgent);
                
                FraisLigneDTO ligne = new FraisLigneDTO();
                ligne.setIdCategorieFrais(detail.getIdCategorieFrais());
                ligne.setLibelleCategorie(detail.getLibelleCategorieFrais());
                ligne.setQuantite(detail.getQte().longValue());
                ligne.setPrixUnitaire(detail.getPu().longValue());
                ligne.setMontant(detail.getMt().longValue());
                
                agentDTO.getLignesFrais().add(ligne);
                agentDTO.setTotalAgent(agentDTO.getTotalAgent() + ligne.getMontant());
            }

            // Construire la réponse
            RecapitulatifFraisDTO recap = new RecapitulatifFraisDTO();
            recap.setIdOrdreMission(recapView.getIdOrdreMission());
            recap.setNumeroOrdreMission(recapView.getNumeroOrdreMission());
            recap.setObjetOrdreMission(recapView.getObjetOrdreMission());
            recap.setDateDebut(recapView.getDateDebut());
            recap.setDateFin(recapView.getDateFin());
            recap.setDureeJours(recapView.getDureeJours().longValue());
            recap.setDureeNuits(recapView.getDureeNuits().longValue());
            recap.setNombreAgents(recapView.getNombreAgents().intValue());
            recap.setFraisParAgent(new ArrayList<>(agentsMap.values()));
            recap.setTotalRepas(recapView.getTotalRepas().longValue());
            recap.setTotalHebergement(recapView.getTotalHebergement().longValue());
            recap.setTotalIndemnite(recapView.getTotalIndemnite().longValue());
            recap.setTotalCarburant(recapView.getTotalCarburant().longValue());
            recap.setTotalGeneral(recapView.getTotalGeneral().longValue());
            
            // Vérifier si déjà validé
            GmOrdreMission mission = missionRepository.findById(id).orElseThrow();
            recap.setValidationComplete("BUDGET_VALIDE".equals(mission.getStatutOrdreMission()));

            log.info("✅ [MG] Détails récupérés : {} FCFA", recap.getTotalGeneral());
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
            
            // Récupérer participants
            List<GmParticiper> participants = participerRepository.findByIdOrdreMission(id);
            if (participants.isEmpty()) {
                log.warn("⚠️ [MG] Mission {} : aucun participant", id);
                return;
            }

            // Calculer durée
            long dureeJours = ChronoUnit.DAYS.between(
                    mission.getDateDebutPrevueOrdreMission(),
                    mission.getDateFinPrevueOrdreMission()
            ) + 1;
            long dureeNuits = dureeJours - 1;

            // Calculer frais par agent
            for (GmParticiper participant : participants) {
                GmAgent agent = agentRepository.findById(participant.getIdAgent()).orElse(null);
                if (agent == null) continue;
                
                Long idFonction = agent.getIdFonction();

                // Repas (ID=1)
                Long montantRepas = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 1L).orElse(0L);
                if (montantRepas > 0) {
                    saveFrais(id, agent.getIdAgent(), 1L, dureeJours, montantRepas, montantRepas * dureeJours);
                }

                // Hébergement (ID=2)
                if (dureeNuits > 0) {
                    Long montantHebergement = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 2L).orElse(0L);
                    if (montantHebergement > 0) {
                        saveFrais(id, agent.getIdAgent(), 2L, dureeNuits, montantHebergement, montantHebergement * dureeNuits);
                    }
                }

                // Indemnité (ID=3)
                Long montantIndemnite = baremeRepository.findMontantByFonctionAndCategorie(idFonction, 3L).orElse(0L);
                if (montantIndemnite > 0) {
                    saveFrais(id, agent.getIdAgent(), 3L, dureeJours, montantIndemnite, montantIndemnite * dureeJours);
                }

                // Carburant (ID=4, Fonction=6 uniquement)
                if (idFonction == 6L) {
                    Long montantCarburant = baremeRepository.findMontantByFonctionAndCategorie(6L, 4L).orElse(0L);
                    if (montantCarburant > 0) {
                        saveFrais(id, agent.getIdAgent(), 4L, 1L, montantCarburant, montantCarburant);
                    }
                }
            }
            
            log.info("✅ [MG] Calcul auto terminé pour mission {}", id);
        } catch (Exception e) {
            log.error("❌ [MG] Erreur calcul auto : {}", e.getMessage());
        }
    }
}
