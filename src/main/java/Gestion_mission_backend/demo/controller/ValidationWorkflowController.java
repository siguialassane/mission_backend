package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.ValidationWorkflowDTO;
import Gestion_mission_backend.demo.dto.RhTraitementMissionDTO;
import Gestion_mission_backend.demo.service.ValidationWorkflowService;
import Gestion_mission_backend.demo.service.RessourceAffectationService;
import Gestion_mission_backend.demo.service.MissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validations")
@RequiredArgsConstructor
@Slf4j
public class ValidationWorkflowController {

    private final ValidationWorkflowService validationService;
    private final RessourceAffectationService ressourceAffectationService;
    private final MissionService missionService;

    @GetMapping("/mission/{idMission}")
    public ResponseEntity<List<ValidationWorkflowDTO>> getValidationsByMission(@PathVariable Long idMission) {
        log.info("[VALIDATION_API] GET /api/validations/mission/{}", idMission);
        List<ValidationWorkflowDTO> validations = validationService.getValidationsByMission(idMission);
        return ResponseEntity.ok(validations);
    }

    @GetMapping("/mission/{idMission}/complete")
    public ResponseEntity<Map<String, Boolean>> checkMissionFullyValidated(@PathVariable Long idMission) {
        log.info("[VALIDATION_API] GET /api/validations/mission/{}/complete", idMission);
        boolean isComplete = validationService.isMissionFullyValidated(idMission);
        return ResponseEntity.ok(Map.of("isFullyValidated", isComplete));
    }

    @PostMapping
    public ResponseEntity<ValidationWorkflowDTO> saveValidation(
            @RequestBody ValidationWorkflowDTO dto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[VALIDATION_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[VALIDATION_API] POST /api/validations - userId: {}, type: {}", userId, dto.getTypeValidation());
        ValidationWorkflowDTO created = validationService.saveValidation(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/mission/{idMission}/traiter")
    public ResponseEntity<Map<String, String>> traiterMission(
            @PathVariable Long idMission,
            @RequestBody RhTraitementMissionDTO dto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[VALIDATION_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }

        log.info("[VALIDATION_API] ========== DÉBUT TRAITEMENT MISSION {} ==========", idMission);
        log.info("[VALIDATION_API] UserId: {}", userId);
        log.info("[VALIDATION_API] Payload reçu: {}", dto);
        log.info("[VALIDATION_API] - Validation Fondé: {}", dto.getValidationFonde());
        log.info("[VALIDATION_API] - Validation Comptable: {}", dto.getValidationComptable());
        log.info("[VALIDATION_API] - ID Véhicule: {}", dto.getIdVehicule());
        log.info("[VALIDATION_API] - ID Chauffeur: {}", dto.getIdChauffeur());
        log.info("[VALIDATION_API] - ID Police: {}", dto.getIdPolice());

        try {
            // 1. Enregistrer validation Fondé de Pouvoir
            log.info("[VALIDATION_API] ÉTAPE 1: Validation Fondé de Pouvoir");
            if (dto.getValidationFonde() != null && dto.getValidationFonde()) {
                ValidationWorkflowDTO fondeDTO = new ValidationWorkflowDTO();
                fondeDTO.setIdOrdreMission(idMission);
                fondeDTO.setTypeValidation("Fondé");
                fondeDTO.setStatutValidation("VALIDE");
                fondeDTO.setCommentairesValidation(dto.getCommentaireFonde());
                validationService.saveValidation(userId, fondeDTO);
                log.info("[VALIDATION_API] ✓ Validation Fondé de Pouvoir enregistrée");
            } else {
                log.warn("[VALIDATION_API] ⚠ Validation Fondé de Pouvoir non cochée");
            }

            // 2. Enregistrer validation Agent Comptable
            log.info("[VALIDATION_API] ÉTAPE 2: Validation Agent Comptable");
            if (dto.getValidationComptable() != null && dto.getValidationComptable()) {
                ValidationWorkflowDTO comptableDTO = new ValidationWorkflowDTO();
                comptableDTO.setIdOrdreMission(idMission);
                comptableDTO.setTypeValidation("Agent comptable");
                comptableDTO.setStatutValidation("VALIDE");
                comptableDTO.setCommentairesValidation(dto.getCommentaireComptable());
                validationService.saveValidation(userId, comptableDTO);
                log.info("[VALIDATION_API] ✓ Validation Agent Comptable enregistrée");
            } else {
                log.warn("[VALIDATION_API] ⚠ Validation Agent Comptable non cochée");
            }

            // 3. Assigner les ressources si fournies (support multi-ressources)
            log.info("[VALIDATION_API] ÉTAPE 3: Assignation des ressources");
            
            // Véhicules (nouveau format multi + ancien format single)
            java.util.List<Long> allVehicules = dto.getAllVehicules();
            if (!allVehicules.isEmpty()) {
                for (Long idVehicule : allVehicules) {
                    if (idVehicule != null && idVehicule > 0) {
                        log.info("[VALIDATION_API] Tentative assignation véhicule ID: {}", idVehicule);
                        ressourceAffectationService.assignerRessource(idMission, idVehicule);
                        log.info("[VALIDATION_API] ✓ Véhicule {} assigné", idVehicule);
                    }
                }
            } else {
                log.info("[VALIDATION_API] Aucun véhicule à assigner");
            }

            // Chauffeurs (nouveau format multi + ancien format single)
            java.util.List<Long> allChauffeurs = dto.getAllChauffeurs();
            if (!allChauffeurs.isEmpty()) {
                for (Long idChauffeur : allChauffeurs) {
                    if (idChauffeur != null && idChauffeur > 0) {
                        log.info("[VALIDATION_API] Tentative assignation chauffeur ID: {}", idChauffeur);
                        ressourceAffectationService.assignerRessource(idMission, idChauffeur);
                        log.info("[VALIDATION_API] ✓ Chauffeur {} assigné", idChauffeur);
                    }
                }
            } else {
                log.info("[VALIDATION_API] Aucun chauffeur à assigner");
            }

            // Polices (nouveau format multi + ancien format single)
            java.util.List<Long> allPolices = dto.getAllPolices();
            if (!allPolices.isEmpty()) {
                for (Long idPolice : allPolices) {
                    if (idPolice != null && idPolice > 0) {
                        log.info("[VALIDATION_API] Tentative assignation police ID: {}", idPolice);
                        ressourceAffectationService.assignerRessource(idMission, idPolice);
                        log.info("[VALIDATION_API] ✓ Police {} assigné", idPolice);
                    }
                }
            } else {
                log.info("[VALIDATION_API] Aucun adjudant à assigner");
            }

            // 4. Mettre à jour le statut de la mission
            log.info("[VALIDATION_API] ÉTAPE 4: Mise à jour du statut de la mission");
            missionService.updateMissionStatus(idMission, "VALIDEE_RH");
            log.info("[VALIDATION_API] ✓ Statut mission mis à jour: VALIDEE_RH");

            log.info("[VALIDATION_API] ========== ✅ Mission {} traitée avec succès ==========", idMission);
            return ResponseEntity.ok(Map.of("message", "Mission traitée avec succès"));
            
        } catch (Exception e) {
            log.error("[VALIDATION_API] ❌ ERREUR lors du traitement de la mission {}", idMission, e);
            log.error("[VALIDATION_API] Type d'erreur: {}", e.getClass().getName());
            log.error("[VALIDATION_API] Message: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("[VALIDATION_API] Cause: {}", e.getCause().getMessage());
            }
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Erreur inconnue"));
        }
    }
}
