package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "GM_FRAISMISSION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmFraisMission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "frais_mission_seq")
    @SequenceGenerator(name = "frais_mission_seq", sequenceName = "SEQ_GM_FRAIS_MISSION", allocationSize = 1)
    @Column(name = "ID_FRAIS_MISSION")
    private Long idFraisMission;

    @Column(name = "ID_CATEGORIE_FRAIS", nullable = false)
    private Long idCategorieFrais;

    @Column(name = "ID_ORDRE_MISSION")
    private Long idOrdreMission;

    @Column(name = "ID_AGENT", nullable = false)
    private Long idAgent;

    @Column(name = "MONTANT_PREVU__FRAIS_MISSION")
    private Long montantPrevuFraisMission;

    @Column(name = "MONTANT_REEL__FRAIS_MISSION")
    private Long montantReelFraisMission;

    @Column(name = "QUANTITE__FRAIS_MISSION")
    private Long quantiteFraisMission;

    @Column(name = "PRIX_UNITAIRE__FRAIS_MISSION")
    private Long prixUnitaireFraisMission;

    @Column(name = "DATE_FRAIS__FRAIS_MISSION")
    private LocalDate dateFraisFraisMission;

    @Column(name = "NUMERO_FACTURE__FRAIS_MISSION", length = 50)
    private String numeroFactureFraisMission;

    @Column(name = "STATUT_VALIDATION__FRAIS_MISSI", length = 20)
    private String statutValidationFraisMission;

    @Column(name = "JUSTIFICATIF_URL_FRAIS_MISSION", length = 500)
    private String justificatifUrlFraisMission;

    @Column(name = "OBSERVATIONS_FRAIS_MISSION", length = 500)
    private String observationsFraisMission;

    @Column(name = "DATE_CRE_FRAIS_MISSION")
    private LocalDate dateCreFraisMission;

    @Column(name = "DATE_VALID_FAIS_MISSION")
    private LocalDate dateValidFaisMission;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEGORIE_FRAIS", insertable = false, updatable = false)
    private GmCategorieFrais categorieFrais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDRE_MISSION", insertable = false, updatable = false)
    private GmOrdreMission ordreMission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AGENT", insertable = false, updatable = false)
    private GmAgent agent;
}
