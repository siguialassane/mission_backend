package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GM_CATEGORIEFRAIS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmCategorieFrais {

    @Id
    @Column(name = "ID_CATEGORIE_FRAIS")
    private Long idCategorieFrais;

    @Column(name = "LIBELLE_CATEGORIE_FRAIS", length = 100)
    private String libelleCategorieFrais;

    @Column(name = "DESCRIPTION_CATEGORIE_FRAIS", length = 500)
    private String descriptionCategorieFrais;

    @Column(name = "MONTANT_PLAFOND_CATEGORIE_FRAI")
    private Long montantPlafondCategorieFrais;

    @Column(name = "STATUT_ACTIF_CATEGORIE_FRAIS", length = 10)
    private String statutActifCategorieFrais;
}
