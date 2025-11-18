package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "GM_UTILISATEUR")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "utilisateur_seq")
    @SequenceGenerator(name = "utilisateur_seq", sequenceName = "GM_UTILISATEUR_SEQ", allocationSize = 1)
    @Column(name = "ID_UTILISATEUR")
    private Long idUtilisateur;

    @Column(name = "NOM_UTILISATEUR", nullable = false, length = 100)
    private String nomUtilisateur;

    @Column(name = "PRENOM_UTILISATEUR", nullable = false, length = 100)
    private String prenomUtilisateur;

    @Column(name = "EMAIL_UTILISATEUR", nullable = false, unique = true, length = 100)
    private String emailUtilisateur;

    @Column(name = "MOT_DE_PASSE", nullable = false, length = 255)
    private String motDePasse;

    @Column(name = "PROFIL_UTILISATEUR", nullable = false, length = 30)
    private String profilUtilisateur;

    @Column(name = "STATUT_ACTIF", length = 10)
    private String statutActif;

    @Column(name = "DATE_CREATION")
    private LocalDate dateCreation;

    @Column(name = "ENTITE_CODE", length = 2)
    private String entiteCode;

    @Column(name = "ID_SERVICE")
    private Long idService;

    // Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(String nomUtilisateur, String prenomUtilisateur, String emailUtilisateur, 
                       String motDePasse, String profilUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
        this.prenomUtilisateur = prenomUtilisateur;
        this.emailUtilisateur = emailUtilisateur;
        this.motDePasse = motDePasse;
        this.profilUtilisateur = profilUtilisateur;
        this.statutActif = "ACTIF";
        this.dateCreation = LocalDate.now();
    }

    // Getters et Setters
    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getPrenomUtilisateur() {
        return prenomUtilisateur;
    }

    public void setPrenomUtilisateur(String prenomUtilisateur) {
        this.prenomUtilisateur = prenomUtilisateur;
    }

    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }

    public void setEmailUtilisateur(String emailUtilisateur) {
        this.emailUtilisateur = emailUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getProfilUtilisateur() {
        return profilUtilisateur;
    }

    public void setProfilUtilisateur(String profilUtilisateur) {
        this.profilUtilisateur = profilUtilisateur;
    }

    public String getStatutActif() {
        return statutActif;
    }

    public void setStatutActif(String statutActif) {
        this.statutActif = statutActif;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getEntiteCode() {
        return entiteCode;
    }

    public void setEntiteCode(String entiteCode) {
        this.entiteCode = entiteCode;
    }

    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }
}
