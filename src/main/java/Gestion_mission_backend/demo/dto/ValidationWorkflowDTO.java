package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ValidationWorkflowDTO {
    private Long idValidation;
    private Long idOrdreMission;
    private String typeValidation; // FONDE_POUVOIR, AGENT_COMPTABLE
    private Long idUtilisateurValidateur;
    private String nomValidateur;
    private Date dateValidation;
    private String statutValidation; // VALIDE, REJETE, EN_ATTENTE
    private String commentairesValidation;
}
