package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Vue Oracle: V_MG_FRAIS_AGENT
 * Détails des frais par agent et par catégorie
 */
@Entity
@Immutable
@Table(name = "V_MG_FRAIS_AGENT")
@IdClass(ViewMgFraisAgent.ViewMgFraisAgentId.class)
@Data
public class ViewMgFraisAgent {

    @Id
    @Column(name = "ID_ORDRE_MISSION")
    private Long idOrdreMission;

    @Id
    @Column(name = "ID_AGENT")
    private Long idAgent;

    @Id
    @Column(name = "ID_CATEGORIE_FRAIS")
    private Long idCategorieFrais;

    @Column(name = "NOM_AGENT")
    private String nomAgent;

    @Column(name = "PRENOM_AGENT")
    private String prenomAgent;

    @Column(name = "NOM_COMPLET")
    private String nomComplet;

    @Column(name = "ID_FONCTION")
    private Long idFonction;

    @Column(name = "LIB_FONCTION")
    private String libFonction;

    @Column(name = "LIBELLE_CATEGORIE_FRAIS")
    private String libelleCategorieFrais;

    @Column(name = "QTE")
    private BigDecimal qte;

    @Column(name = "PU")
    private BigDecimal pu;

    @Column(name = "MT")
    private BigDecimal mt;

    // Classe de clé composite
    @Data
    public static class ViewMgFraisAgentId implements Serializable {
        private Long idOrdreMission;
        private Long idAgent;
        private Long idCategorieFrais;
    }
}
