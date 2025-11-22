package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vue Oracle: V_MG_RECAP
 * Récapitulatif global d'une mission avec totaux par catégorie
 */
@Entity
@Immutable
@Table(name = "V_MG_RECAP")
@Data
public class ViewMgRecap {

    @Id
    @Column(name = "ID_ORDRE_MISSION")
    private Long idOrdreMission;

    @Column(name = "NUMERO_ORDRE_MISSION")
    private String numeroOrdreMission;

    @Column(name = "OBJET_ORDRE_MISSION")
    private String objetOrdreMission;

    @Column(name = "DATE_DEBUT")
    private LocalDate dateDebut;

    @Column(name = "DATE_FIN")
    private LocalDate dateFin;

    @Column(name = "DUREE_JOURS")
    private BigDecimal dureeJours;

    @Column(name = "DUREE_NUITS")
    private BigDecimal dureeNuits;

    @Column(name = "NOMBRE_AGENTS")
    private BigDecimal nombreAgents;

    @Column(name = "TOTAL_REPAS")
    private BigDecimal totalRepas;

    @Column(name = "TOTAL_HEBERGEMENT")
    private BigDecimal totalHebergement;

    @Column(name = "TOTAL_INDEMNITE")
    private BigDecimal totalIndemnite;

    @Column(name = "TOTAL_CARBURANT")
    private BigDecimal totalCarburant;

    @Column(name = "TOTAL_GENERAL")
    private BigDecimal totalGeneral;
}
