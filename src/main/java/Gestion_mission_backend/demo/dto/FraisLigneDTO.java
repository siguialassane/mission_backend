package Gestion_mission_backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un frais individuel (une ligne de frais)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraisLigneDTO {
    private Long idCategorieFrais;
    private String libelleCategorie;  // Repas, Hébergement, Indemnité, Carburant
    private Long quantite;            // Nombre de jours ou nuits
    private Long prixUnitaire;        // Tarif du barème
    private Long montant;             // quantite × prixUnitaire
}
