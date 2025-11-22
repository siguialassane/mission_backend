package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GM_BAREME")
@IdClass(GmBaremePK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmBareme {

    @Id
    @Column(name = "ID_CATEGORIE_FRAIS")
    private Long idCategorieFrais;

    @Id
    @Column(name = "ID_FONCTION")
    private Long idFonction;

    @Column(name = "MONTANT_UNITAIRE")
    private Long montantUnitaire;

    // Relations pour faciliter les jointures
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEGORIE_FRAIS", insertable = false, updatable = false)
    private GmCategorieFrais categorieFrais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FONCTION", insertable = false, updatable = false)
    private GmFonction fonction;
}
