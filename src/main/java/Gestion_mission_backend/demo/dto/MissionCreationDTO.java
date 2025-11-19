package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MissionCreationDTO {
    private String objetOrdreMission;
    private LocalDate dateDebutMission;
    private LocalDate dateFinMission;
    private Long idNatureMission;
    private String codEntite;
    private Long idService;
    private Long idUtilisateurCreateur;
    private List<ParticipantDTO> participants;
    private List<EtapeDTO> etapes;
    private List<RessourceDTO> ressources;
}
