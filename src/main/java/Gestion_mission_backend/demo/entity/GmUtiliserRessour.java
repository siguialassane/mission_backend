package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "GM_UTILISER_RESSOUR")
@IdClass(GmUtiliserRessourPK.class)
public class GmUtiliserRessour {

    @Id
    @Column(name = "ID_RESSOURCE", nullable = false)
    private Long idRessource;

    @Id
    @Column(name = "ID_ORDRE_MISSION", nullable = false)
    private Long idOrdreMission;

    @Column(name = "QUANTITE")
    private Integer quantite;

    @Column(name = "DATE_DEBUT")
    private LocalDate dateDebut;

    @Column(name = "DATE_FIN")
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDRE_MISSION", insertable = false, updatable = false)
    private GmOrdreMission ordreMission;

    // Constructeurs
    public GmUtiliserRessour() {
    }

    public GmUtiliserRessour(Long idRessource, Long idOrdreMission, Integer quantite) {
        this.idRessource = idRessource;
        this.idOrdreMission = idOrdreMission;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getIdRessource() {
        return idRessource;
    }

    public void setIdRessource(Long idRessource) {
        this.idRessource = idRessource;
    }

    public Long getIdOrdreMission() {
        return idOrdreMission;
    }

    public void setIdOrdreMission(Long idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public GmOrdreMission getOrdreMission() {
        return ordreMission;
    }

    public void setOrdreMission(GmOrdreMission ordreMission) {
        this.ordreMission = ordreMission;
    }
}
