package Gestion_mission_backend.demo.entity;

import java.io.Serializable;
import java.util.Objects;

public class GmUtiliserRessourPK implements Serializable {
    private Long idRessource;
    private Long idOrdreMission;

    public GmUtiliserRessourPK() {
    }

    public GmUtiliserRessourPK(Long idRessource, Long idOrdreMission) {
        this.idRessource = idRessource;
        this.idOrdreMission = idOrdreMission;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GmUtiliserRessourPK that = (GmUtiliserRessourPK) o;
        return Objects.equals(idRessource, that.idRessource) &&
                Objects.equals(idOrdreMission, that.idOrdreMission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRessource, idOrdreMission);
    }
}
