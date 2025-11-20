package Gestion_mission_backend.demo.dto;

import lombok.Data;

@Data
public class RessourceDTO {
    private Long idRessource;
    private Long idTypeRessource;
    private String libTypeRessource; // Pour affichage (Véhicule/Chauffeur/Police)
    private String libRessource;
    private Integer dispoRessource;
    private Long quantite; // Pour affectation à mission
    private Long idUtilisateurCreateur;
    private String nomCreateur; // Pour affichage
}
