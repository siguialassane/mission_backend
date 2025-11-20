package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GM_FONCTION")
public class GmFonction {

    @Id
    @Column(name = "ID_FONCTION")
    private Long idFonction;

    @Column(name = "LIB_FONCTION", length = 255, nullable = false)
    private String libFonction;

    // Constructeurs
    public GmFonction() {
    }

    public GmFonction(String libFonction) {
        this.libFonction = libFonction;
    }

    // Getters et Setters
    public Long getIdFonction() {
        return idFonction;
    }

    public void setIdFonction(Long idFonction) {
        this.idFonction = idFonction;
    }

    public String getLibFonction() {
        return libFonction;
    }

    public void setLibFonction(String libFonction) {
        this.libFonction = libFonction;
    }
}
