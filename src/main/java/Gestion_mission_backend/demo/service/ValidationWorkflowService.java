package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.ValidationWorkflowDTO;
import Gestion_mission_backend.demo.entity.GmValidationWorkflow;
import Gestion_mission_backend.demo.entity.GmUtilisateur;
import Gestion_mission_backend.demo.repository.ValidationWorkflowRepository;
import Gestion_mission_backend.demo.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationWorkflowService {

    private final ValidationWorkflowRepository validationRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<ValidationWorkflowDTO> getValidationsByMission(Long idOrdreMission) {
        log.info("[VALIDATION] Récupération validations pour mission ID: {}", idOrdreMission);
        List<GmValidationWorkflow> validations = validationRepository.findByIdOrdreMission(idOrdreMission);
        return validations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ValidationWorkflowDTO saveValidation(Long idUtilisateur, ValidationWorkflowDTO dto) {
        log.info("[VALIDATION] Enregistrement validation type={} pour mission ID={} par utilisateur ID={}", 
                dto.getTypeValidation(), dto.getIdOrdreMission(), idUtilisateur);
        
        // Vérifier si validation existe déjà
        GmValidationWorkflow validation = validationRepository
                .findByIdOrdreMissionAndTypeValidation(dto.getIdOrdreMission(), dto.getTypeValidation())
                .orElse(new GmValidationWorkflow());
        
        validation.setIdOrdreMission(dto.getIdOrdreMission());
        validation.setTypeValidation(dto.getTypeValidation());
        validation.setIdUtilisateurValidateur(idUtilisateur);
        validation.setDateValidation(new java.util.Date());
        validation.setStatutValidation(dto.getStatutValidation());
        validation.setCommentairesValidation(dto.getCommentairesValidation());
        
        validation = validationRepository.save(validation);
        log.info("[VALIDATION] ✓ Validation ID: {} enregistrée", validation.getIdValidation());
        return toDTO(validation);
    }

    @Transactional(readOnly = true)
    public boolean isMissionFullyValidated(Long idOrdreMission) {
        List<GmValidationWorkflow> validations = validationRepository.findByIdOrdreMission(idOrdreMission);
        
        boolean fondePouvoirValide = validations.stream()
                .anyMatch(v -> "FONDE_POUVOIR".equals(v.getTypeValidation()) && "VALIDE".equals(v.getStatutValidation()));
        
        boolean agentComptableValide = validations.stream()
                .anyMatch(v -> "AGENT_COMPTABLE".equals(v.getTypeValidation()) && "VALIDE".equals(v.getStatutValidation()));
        
        boolean result = fondePouvoirValide && agentComptableValide;
        log.info("[VALIDATION] Mission {} - Fondé Pouvoir: {}, Agent Comptable: {}, Complète: {}", 
                idOrdreMission, fondePouvoirValide, agentComptableValide, result);
        
        return result;
    }

    private ValidationWorkflowDTO toDTO(GmValidationWorkflow validation) {
        ValidationWorkflowDTO dto = new ValidationWorkflowDTO();
        dto.setIdValidation(validation.getIdValidation());
        dto.setIdOrdreMission(validation.getIdOrdreMission());
        dto.setTypeValidation(validation.getTypeValidation());
        dto.setIdUtilisateurValidateur(validation.getIdUtilisateurValidateur());
        dto.setDateValidation(validation.getDateValidation());
        dto.setStatutValidation(validation.getStatutValidation());
        dto.setCommentairesValidation(validation.getCommentairesValidation());
        
        // Récupérer nom validateur
        if (validation.getIdUtilisateurValidateur() != null) {
            utilisateurRepository.findById(validation.getIdUtilisateurValidateur())
                    .ifPresent(user -> {
                        String nom = String.format("%s %s", 
                                user.getPrenomUtilisateur() != null ? user.getPrenomUtilisateur() : "",
                                user.getNomUtilisateur() != null ? user.getNomUtilisateur() : ""
                        ).trim();
                        dto.setNomValidateur(nom.isEmpty() ? "Système" : nom);
                    });
        }
        
        return dto;
    }
}
