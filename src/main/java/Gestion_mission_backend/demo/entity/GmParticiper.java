package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GM_PARTICIPER")
@IdClass(GmParticiperPK.class)
public class GmParticiper {

    @Id
    @Column(name = "ID_ORDRE_MISSION", nullable = false)
    private Long idOrdreMission;

    @Id
    @Column(name = "ID_AGENT", nullable = false)
    private Long idAgent;

    @Column(name = "ROLE", length = 20)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORDRE_MISSION", insertable = false, updatable = false)
    private GmOrdreMission ordreMission;

    // Constructeurs
    public GmParticiper() {
    }

    public GmParticiper(Long idOrdreMission, Long idAgent, String role) {
        this.idOrdreMission = idOrdreMission;
        this.idAgent = idAgent;
        this.role = role;
    }

    // Getters et Setters
    public Long getIdOrdreMission() {
        return idOrdreMission;
    }

    public void setIdOrdreMission(Long idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public Long getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(Long idAgent) {
        this.idAgent = idAgent;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public GmOrdreMission getOrdreMission() {
        return ordreMission;
    }

    public void setOrdreMission(GmOrdreMission ordreMission) {
        this.ordreMission = ordreMission;
    }
}
