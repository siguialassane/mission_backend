package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "AC_ENTITE")
public class AcEntite {

    @Id
    @Column(name = "ENTITE_CODE", length = 2)
    private String entiteCode;

    @Column(name = "ENTITE_LIB", length = 100)
    private String entiteLib;

    @Column(name = "ENTITE_LIB_LONG", length = 100)
    private String entiteLibLong;

    @Column(name = "ENTITE_RESP", length = 50)
    private String entiteResp;

    @Column(name = "ENTITE_ASSIGN", length = 200)
    private String entiteAssign;

    @Column(name = "ENTITE_LIB_MINUSC", length = 50)
    private String entiteLibMinusc;

    // Constructeurs
    public AcEntite() {
    }

    public AcEntite(String entiteCode, String entiteLib) {
        this.entiteCode = entiteCode;
        this.entiteLib = entiteLib;
    }

    // Getters et Setters
    public String getEntiteCode() {
        return entiteCode;
    }

    public void setEntiteCode(String entiteCode) {
        this.entiteCode = entiteCode;
    }

    public String getEntiteLib() {
        return entiteLib;
    }

    public void setEntiteLib(String entiteLib) {
        this.entiteLib = entiteLib;
    }

    public String getEntiteLibLong() {
        return entiteLibLong;
    }

    public void setEntiteLibLong(String entiteLibLong) {
        this.entiteLibLong = entiteLibLong;
    }

    public String getEntiteResp() {
        return entiteResp;
    }

    public void setEntiteResp(String entiteResp) {
        this.entiteResp = entiteResp;
    }

    public String getEntiteAssign() {
        return entiteAssign;
    }

    public void setEntiteAssign(String entiteAssign) {
        this.entiteAssign = entiteAssign;
    }

    public String getEntiteLibMinusc() {
        return entiteLibMinusc;
    }

    public void setEntiteLibMinusc(String entiteLibMinusc) {
        this.entiteLibMinusc = entiteLibMinusc;
    }
}
