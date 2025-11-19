package Gestion_mission_backend.demo.dto;

import lombok.Data;

@Data
public class ParticipantResponseDTO {
    private Long idParticiper;
    private Long idAgent;
    private String matriculeAgent;
    private String nomAgent;
    private String prenomAgent;
    private String role;
}
