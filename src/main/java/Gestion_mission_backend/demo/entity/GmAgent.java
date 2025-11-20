package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "GM_AGENT")
public class GmAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_seq")
    @SequenceGenerator(name = "agent_seq", sequenceName = "SEQ_GM_AGENT", allocationSize = 1)
    @Column(name = "ID_AGENT")
    private Long idAgent;

    @Column(name = "ID_FONCTION", nullable = false)
    private Long idFonction;

    @Column(name = "ID_HISTORIQUE")
    private Long idHistorique;

    @Column(name = "MATRICULE_AGENT", length = 20)
    private String matriculeAgent;

    @Column(name = "NOM_AGENT", length = 100)
    private String nomAgent;

    @Column(name = "PRENOM_AGENT", length = 100)
    private String prenomAgent;

    @Column(name = "EMAIL_AGENT", length = 100)
    private String emailAgent;

    @Column(name = "TELEPHONE_AGENT", length = 20)
    private String telephoneAgent;

    @Column(name = "ID_STRUCTURE_AGENT")
    private Long idStructureAgent;

    @Column(name = "DATE_CREATION_AGENT")
    private LocalDate dateCreationAgent;

    @Column(name = "STATUT_ACTIF_AGENT", length = 10)
    private String statutActifAgent;

    @Column(name = "ID_UTILISATEUR_CREATEUR")
    private Long idUtilisateurCreateur;

    @Column(name = "ID_SERVICE")
    private Long idService;

    // Constructeurs
    public GmAgent() {
    }

    // Getters et Setters
    public Long getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(Long idAgent) {
        this.idAgent = idAgent;
    }

    public Long getIdFonction() {
        return idFonction;
    }

    public void setIdFonction(Long idFonction) {
        this.idFonction = idFonction;
    }

    public Long getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }

    public String getMatriculeAgent() {
        return matriculeAgent;
    }

    public void setMatriculeAgent(String matriculeAgent) {
        this.matriculeAgent = matriculeAgent;
    }

    public String getNomAgent() {
        return nomAgent;
    }

    public void setNomAgent(String nomAgent) {
        this.nomAgent = nomAgent;
    }

    public String getPrenomAgent() {
        return prenomAgent;
    }

    public void setPrenomAgent(String prenomAgent) {
        this.prenomAgent = prenomAgent;
    }

    public String getEmailAgent() {
        return emailAgent;
    }

    public void setEmailAgent(String emailAgent) {
        this.emailAgent = emailAgent;
    }

    public String getTelephoneAgent() {
        return telephoneAgent;
    }

    public void setTelephoneAgent(String telephoneAgent) {
        this.telephoneAgent = telephoneAgent;
    }

    public Long getIdStructureAgent() {
        return idStructureAgent;
    }

    public void setIdStructureAgent(Long idStructureAgent) {
        this.idStructureAgent = idStructureAgent;
    }

    public LocalDate getDateCreationAgent() {
        return dateCreationAgent;
    }

    public void setDateCreationAgent(LocalDate dateCreationAgent) {
        this.dateCreationAgent = dateCreationAgent;
    }

    public String getStatutActifAgent() {
        return statutActifAgent;
    }

    public void setStatutActifAgent(String statutActifAgent) {
        this.statutActifAgent = statutActifAgent;
    }

    public Long getIdUtilisateurCreateur() {
        return idUtilisateurCreateur;
    }

    public void setIdUtilisateurCreateur(Long idUtilisateurCreateur) {
        this.idUtilisateurCreateur = idUtilisateurCreateur;
    }

    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }
}
