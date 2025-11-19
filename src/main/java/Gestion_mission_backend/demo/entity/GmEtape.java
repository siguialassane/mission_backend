package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GM_ETAPE")
public class GmEtape {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etape_seq")
    @SequenceGenerator(name = "etape_seq", sequenceName = "SEQ_GM_ETAPE", allocationSize = 1)
    @Column(name = "ID_ETAPE")
    private Long idEtape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDRE_MISSION", nullable = false)
    private GmOrdreMission ordreMission;

    @Column(name = "TYPE_ETAPE", length = 10)
    private String typeEtape;

    @Column(name = "VILLE_CODE", length = 25)
    private String villeCode;

    @Column(name = "ORDRE_ETAPE")
    private Integer ordreEtape;

    // Constructeurs
    public GmEtape() {
    }

    public GmEtape(GmOrdreMission ordreMission, String villeCode, Integer ordreEtape) {
        this.ordreMission = ordreMission;
        this.villeCode = villeCode;
        this.ordreEtape = ordreEtape;
    }

    // Getters et Setters
    public Long getIdEtape() {
        return idEtape;
    }

    public void setIdEtape(Long idEtape) {
        this.idEtape = idEtape;
    }

    public GmOrdreMission getOrdreMission() {
        return ordreMission;
    }

    public void setOrdreMission(GmOrdreMission ordreMission) {
        this.ordreMission = ordreMission;
    }

    public String getTypeEtape() {
        return typeEtape;
    }

    public void setTypeEtape(String typeEtape) {
        this.typeEtape = typeEtape;
    }

    public String getVilleCode() {
        return villeCode;
    }

    public void setVilleCode(String villeCode) {
        this.villeCode = villeCode;
    }

    public Integer getOrdreEtape() {
        return ordreEtape;
    }

    public void setOrdreEtape(Integer ordreEtape) {
        this.ordreEtape = ordreEtape;
    }
}
