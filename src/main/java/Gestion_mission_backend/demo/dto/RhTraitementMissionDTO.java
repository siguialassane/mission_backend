package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class RhTraitementMissionDTO {
    
    // Validations administratives
    private Boolean validationFonde;
    private String commentaireFonde;
    private Boolean validationComptable;
    private String commentaireComptable;
    
    // Ressources assignées (optionnel)
    private Long idVehicule;
    private Long idChauffeur;
    private Long idPolice;
    
    // Info utilisateur qui valide
    private Long idUtilisateur;
}
