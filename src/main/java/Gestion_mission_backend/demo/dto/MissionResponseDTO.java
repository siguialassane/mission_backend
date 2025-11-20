package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MissionResponseDTO {
    private Long idOrdreMission;
    private String numeroOrdreMission;
    private String codeMission;
    private String objetOrdreMission;
    private String objetMission; // Alias pour compatibilité frontend
    private LocalDate dateDebutMission;
    private LocalDate dateFinMission;
    private String lieuDepart;
    private String lieuDestination;
    private String statutOrdreMission;
    private Long idNatureMission;
    private String natureMissionLib;
    private String codEntite;
    private String entiteLib;
    private Long idService;
    private String serviceLib;
    private Long idUtilisateurCreateur;
    private String createurNom;
    private List<ParticipantResponseDTO> participants;
    private List<EtapeResponseDTO> etapes;
    private List<RessourceResponseDTO> ressources;
}
