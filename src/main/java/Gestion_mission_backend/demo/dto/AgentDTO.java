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
}
