package Gestion_mission_backend.demo.dto;

import lombok.Data;

@Data
public class RhStatsDTO {
    private Long nbEnAttenteRh;
    private Long nbValideesRh;
    private Long nbRefuseesRh;
    private Long nbUrgentes;
    private Long totalMissionsRh;
    private Long nbAvecRessources;
}
