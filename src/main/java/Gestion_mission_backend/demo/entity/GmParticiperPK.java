package Gestion_mission_backend.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class GmParticiperPK implements Serializable {
    private Long idOrdreMission;
    private Long idAgent;

    public GmParticiperPK() {
    }

    public GmParticiperPK(Long idOrdreMission, Long idAgent) {
        this.idOrdreMission = idOrdreMission;
        this.idAgent = idAgent;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GmParticiperPK that = (GmParticiperPK) o;
        return Objects.equals(idOrdreMission, that.idOrdreMission) &&
                Objects.equals(idAgent, that.idAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrdreMission, idAgent);
    }
}
