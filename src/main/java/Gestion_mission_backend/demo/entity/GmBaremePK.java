package Gestion_mission_backend.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GmBaremePK implements Serializable {
    
    private Long idCategorieFrais;
    private Long idFonction;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GmBaremePK that = (GmBaremePK) o;
        return Objects.equals(idCategorieFrais, that.idCategorieFrais) &&
               Objects.equals(idFonction, that.idFonction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategorieFrais, idFonction);
    }
}
