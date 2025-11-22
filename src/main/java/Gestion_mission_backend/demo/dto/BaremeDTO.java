package Gestion_mission_backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaremeDTO {
    private Long idCategorieFrais;
    private Long idFonction;
    private String nomCategorie;
    private String nomFonction;
    private Long montantUnitaire;
}
