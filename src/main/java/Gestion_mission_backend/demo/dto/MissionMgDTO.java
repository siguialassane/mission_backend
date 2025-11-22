package Gestion_mission_backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO représentant une mission pour le dashboard Moyens Généraux
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissionMgDTO {
    private Long idOrdreMission;
    private String numeroOrdreMission;
    private String objetOrdreMission;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long dureeJours;
    private Integer nombreAgents;
    private String statutOrdreMission;
    
    // Validation workflow
    private Boolean validationComplete;  // true si Fondé + Agent Comptable ont validé
    private Boolean fraisCalcules;       // true si frais déjà calculés
    private Long totalFrais;             // montant total si déjà calculé
    private String messageValidation;    // message si validations incomplètes
}
