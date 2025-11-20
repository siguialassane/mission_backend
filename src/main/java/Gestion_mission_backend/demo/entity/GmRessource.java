package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GM_RESSOURCE")
public class GmRessource {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ressource_seq")
    @SequenceGenerator(name = "ressource_seq", sequenceName = "SEQ_GM_RESSOURCE", allocationSize = 1)
    @Column(name = "ID_RESSOURCE")
    private Long idRessource;

    @Column(name = "ID_TYPE_RESSOURCE", nullable = false)
    private Long idTypeRessource;

    @Column(name = "LIB_RESSOURCE", length = 30)
    private String libRessource;

    @Column(name = "DISPO_RESSOURCE")
    private Long dispoRessource;

    @Column(name = "ID_UTILISATEUR_CREATEUR")
    private Long idUtilisateurCreateur;

    // Constructeurs
    public GmRessource() {
    }

    public GmRessource(Long idTypeRessource, String libRessource) {
        this.idTypeRessource = idTypeRessource;
        this.libRessource = libRessource;
    }

    // Getters et Setters
    public Long getIdRessource() {
        return idRessource;
    }

    public void setIdRessource(Long idRessource) {
        this.idRessource = idRessource;
    }

    public Long getIdTypeRessource() {
        return idTypeRessource;
    }

    public void setIdTypeRessource(Long idTypeRessource) {
        this.idTypeRessource = idTypeRessource;
    }

    public String getLibRessource() {
        return libRessource;
    }

    public void setLibRessource(String libRessource) {
        this.libRessource = libRessource;
    }

    public Long getDispoRessource() {
        return dispoRessource;
    }

    public void setDispoRessource(Long dispoRessource) {
        this.dispoRessource = dispoRessource;
    }

    public Long getIdUtilisateurCreateur() {
        return idUtilisateurCreateur;
    }

    public void setIdUtilisateurCreateur(Long idUtilisateurCreateur) {
        this.idUtilisateurCreateur = idUtilisateurCreateur;
    }
}
