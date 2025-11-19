package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "AC_VILLE")
public class AcVille {

    @Id
    @Column(name = "VILLE_CODE", length = 25)
    private String villeCode;

    @Column(name = "VILLE_LIB", length = 250)
    private String villeLib;

    @Column(name = "VILLE_LIB_LONG", length = 250)
    private String villeLibLong;

    @Column(name = "VILLE_NUM", length = 25)
    private String villeNum;

    // Constructeurs
    public AcVille() {
    }

    public AcVille(String villeCode, String villeLib) {
        this.villeCode = villeCode;
        this.villeLib = villeLib;
    }

    // Getters et Setters
    public String getVilleCode() {
        return villeCode;
    }

    public void setVilleCode(String villeCode) {
        this.villeCode = villeCode;
    }

    public String getVilleLib() {
        return villeLib;
    }

    public void setVilleLib(String villeLib) {
        this.villeLib = villeLib;
    }

    public String getVilleLibLong() {
        return villeLibLong;
    }

    public void setVilleLibLong(String villeLibLong) {
        this.villeLibLong = villeLibLong;
    }

    public String getVilleNum() {
        return villeNum;
    }

    public void setVilleNum(String villeNum) {
        this.villeNum = villeNum;
    }
}
