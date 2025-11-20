package Gestion_mission_backend.demo.dto;

import lombok.Data;

@Data
public class RessourceResponseDTO {
    private Long idUtiliserRessour;
    private Long idRessource;
    private String ressourceLib;
    private Long quantite;
}
