package Gestion_mission_backend.demo.dto;

import lombok.Data;

@Data
public class ParticipantDTO {
    private Long idAgent;
    private String role; // "CHEF" ou "PARTICIPANT"
}
