package Gestion_mission_backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO représentant le récapitulatif complet des frais de la mission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecapitulatifFraisDTO {
    
    // Informations mission
    private Long idOrdreMission;
    private String numeroOrdreMission;
    private String objetOrdreMission;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long dureeJours;
    private Long dureeNuits;
    private Integer nombreAgents;
    
    // Détails par agent
    private List<FraisAgentDTO> fraisParAgent;
    
    // Récapitulatif global par catégorie
    private Long totalRepas;
    private Long totalHebergement;
    private Long totalIndemnite;
    private Long totalCarburant;
    private Long totalGeneral;
    
    // Informations de validation (si mission validée par Fondé + Agent comptable)
    private Boolean validationComplete;  // true si les 2 validations sont OK
    private String messageValidation;
}
