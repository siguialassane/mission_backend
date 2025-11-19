package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GM_NATUREMISSION")
public class GmNatureMission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nature_mission_seq")
    @SequenceGenerator(name = "nature_mission_seq", sequenceName = "SEQ_GM_NATURE_MISSION", allocationSize = 1)
    @Column(name = "ID_NATURE_MISSION")
    private Long idNatureMission;

    @Column(name = "LIBELLE_NATURE_MISSION", length = 200)
    private String libelleNatureMission;

    @Column(name = "DESCRIPTION_NATURE_MISSION", length = 500)
    private String descriptionNatureMission;

    // Constructeurs
    public GmNatureMission() {
    }

    public GmNatureMission(String libelleNatureMission) {
        this.libelleNatureMission = libelleNatureMission;
    }

    // Getters et Setters
    public Long getIdNatureMission() {
        return idNatureMission;
    }

    public void setIdNatureMission(Long idNatureMission) {
        this.idNatureMission = idNatureMission;
    }

    public String getLibelleNatureMission() {
        return libelleNatureMission;
    }

    public void setLibelleNatureMission(String libelleNatureMission) {
        this.libelleNatureMission = libelleNatureMission;
    }

    public String getDescriptionNatureMission() {
        return descriptionNatureMission;
    }

    public void setDescriptionNatureMission(String descriptionNatureMission) {
        this.descriptionNatureMission = descriptionNatureMission;
    }
}
