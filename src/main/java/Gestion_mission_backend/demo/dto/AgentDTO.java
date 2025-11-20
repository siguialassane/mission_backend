package Gestion_mission_backend.demo.dto;

import lombok.Data;

@Data
public class AgentDTO {
    private Long idAgent;
    private String matriculeAgent;
    private String nomAgent;
    private String prenomAgent;
    private String emailAgent;
    private String telephoneAgent;
    private String statutActifAgent;
    private Long idFonction;
    private String libelleFonction;
    private Long idService;
    private String libelleService;
    private Long idUtilisateurCreateur;
}
