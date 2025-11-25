package Gestion_mission_backend.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "V_MG_FRAIS_COMPLET_NEW")
@Data
public class ViewMgFraisComplet {

    @Id
    @Column(name = "ID_FRAIS")
    private Long idFrais;

    @Column(name = "ID_MISSION")
    private Long idMission;

    @Column(name = "ID_PARTICIPANT")
    private Long idParticipant;

    @Column(name = "NOM_COMPLET")
    private String nomComplet;

    @Column(name = "LIB_FONCTION")
    private String libFonction;

    @Column(name = "ID_CATEGORIE_FRAIS")
    private Long idCategorieFrais;

    @Column(name = "LIB_CATEGORIE")
    private String libCategorie;

    @Column(name = "QUANTITE")
    private Long quantite;

    @Column(name = "PRIX_UNITAIRE")
    private Long prixUnitaire;

    @Column(name = "MONTANT_TOTAL")
    private Long montantTotal;
}
