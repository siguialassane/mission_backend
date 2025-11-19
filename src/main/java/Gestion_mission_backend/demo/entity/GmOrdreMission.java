package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GM_ORDREMISSION")
public class GmOrdreMission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ordre_mission_seq")
    @SequenceGenerator(name = "ordre_mission_seq", sequenceName = "SEQ_GM_ORDREMISSION", allocationSize = 1)
    @Column(name = "ID_ORDRE_MISSION")
    private Long idOrdreMission;

    @Column(name = "NUMERO_ORDRE_MISSION", length = 50, unique = true)
    private String numeroOrdreMission;

    @Column(name = "OBJET_ORDRE_MISSION", length = 500, nullable = false)
    private String objetOrdreMission;

    @Column(name = "OBSERVATIONS_ORDRE_MISSION", length = 1000)
    private String observationsOrdreMission;

    @Column(name = "NOM_DESTINATAIRE", length = 200)
    private String nomDestinataire;

    @Column(name = "DATE_DEBUT_PREVUE_ORDRE_MISSIO")
    private LocalDate dateDebutPrevueOrdreMission;

    @Column(name = "DATE_FIN_PREVUE_ORDRE_MISSION")
    private LocalDate dateFinPrevueOrdreMission;

    @Column(name = "DATE_DEBUT_EFFECTIVE_ORDRE_MIS")
    private LocalDate dateDebutEffectiveOrdreMission;

    @Column(name = "DATE_FIN_EFFECTIVE_ORDRE_MISSI")
    private LocalDate dateFinEffectiveOrdreMission;

    @Column(name = "DUREE_PREVUE_JOURS_ORDRE_MISSI")
    private Integer dureePrevueJoursOrdreMission;

    @Column(name = "DUREE_EFFECTIVE_JOURS_ORDRE_MI")
    private Integer dureeEffectiveJoursOrdreMission;

    @Column(name = "URGENCE_ORDRE_MISSION", length = 30)
    private String urgenceOrdreMission;

    @Column(name = "STATUT_ORDRE_MISSION", length = 30)
    private String statutOrdreMission;

    @Column(name = "DATE_DEMANDE_ORDRE_MISSION")
    private LocalDate dateDemandeMission;

    @Column(name = "BUDGET_ALLOUE_ORDRE_MISSION")
    private Long budgetAlloueOrdreMission;

    @Column(name = "DATE_CREATION_ORDRE_MISSION")
    private LocalDate dateCreationOrdreMission;

    @Column(name = "LIEU_DEPART_ORDRE_MISSION", length = 200)
    private String lieuDepartOrdreMission;

    @Column(name = "LIEU_DESTINATION_ORDRE_MISSION", length = 200)
    private String lieuDestinationOrdreMission;

    @Column(name = "ID_UTILISATEUR_CREATEUR")
    private Long idUtilisateurCreateur;

    @Column(name = "ENTITE_CODE", length = 2)
    private String entiteCode;

    @Column(name = "ID_NATURE_MISSION")
    private Long idNatureMission;

    // Relations bidirectionnelles avec cascade
    @OneToMany(mappedBy = "ordreMission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GmParticiper> participants = new ArrayList<>();

    @OneToMany(mappedBy = "ordreMission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GmEtape> etapes = new ArrayList<>();

    @OneToMany(mappedBy = "ordreMission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GmUtiliserRessour> ressources = new ArrayList<>();

    // Constructeurs
    public GmOrdreMission() {
    }

    // Callbacks JPA
    @PrePersist
    public void prePersist() {
        if (this.statutOrdreMission == null) {
            this.statutOrdreMission = "BROUILLON";
        }
        if (this.dateCreationOrdreMission == null) {
            this.dateCreationOrdreMission = LocalDate.now();
        }
        if (this.dateDemandeMission == null) {
            this.dateDemandeMission = LocalDate.now();
        }
    }

    // Méthodes helper pour gérer les relations bidirectionnelles
    public void addParticipant(GmParticiper participant) {
        participants.add(participant);
        participant.setOrdreMission(this);
    }

    public void removeParticipant(GmParticiper participant) {
        participants.remove(participant);
        participant.setOrdreMission(null);
    }

    public void addEtape(GmEtape etape) {
        etapes.add(etape);
        etape.setOrdreMission(this);
    }

    public void removeEtape(GmEtape etape) {
        etapes.remove(etape);
        etape.setOrdreMission(null);
    }

    public void addRessource(GmUtiliserRessour ressource) {
        ressources.add(ressource);
        ressource.setOrdreMission(this);
    }

    public void removeRessource(GmUtiliserRessour ressource) {
        ressources.remove(ressource);
        ressource.setOrdreMission(null);
    }

    // Getters et Setters
    public Long getIdOrdreMission() {
        return idOrdreMission;
    }

    public void setIdOrdreMission(Long idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public String getNumeroOrdreMission() {
        return numeroOrdreMission;
    }

    public void setNumeroOrdreMission(String numeroOrdreMission) {
        this.numeroOrdreMission = numeroOrdreMission;
    }

    public String getObjetOrdreMission() {
        return objetOrdreMission;
    }

    public void setObjetOrdreMission(String objetOrdreMission) {
        this.objetOrdreMission = objetOrdreMission;
    }

    public String getObservationsOrdreMission() {
        return observationsOrdreMission;
    }

    public void setObservationsOrdreMission(String observationsOrdreMission) {
        this.observationsOrdreMission = observationsOrdreMission;
    }

    public String getNomDestinataire() {
        return nomDestinataire;
    }

    public void setNomDestinataire(String nomDestinataire) {
        this.nomDestinataire = nomDestinataire;
    }

    public LocalDate getDateDebutPrevueOrdreMission() {
        return dateDebutPrevueOrdreMission;
    }

    public void setDateDebutPrevueOrdreMission(LocalDate dateDebutPrevueOrdreMission) {
        this.dateDebutPrevueOrdreMission = dateDebutPrevueOrdreMission;
    }

    public LocalDate getDateFinPrevueOrdreMission() {
        return dateFinPrevueOrdreMission;
    }

    public void setDateFinPrevueOrdreMission(LocalDate dateFinPrevueOrdreMission) {
        this.dateFinPrevueOrdreMission = dateFinPrevueOrdreMission;
    }

    public LocalDate getDateDebutEffectiveOrdreMission() {
        return dateDebutEffectiveOrdreMission;
    }

    public void setDateDebutEffectiveOrdreMission(LocalDate dateDebutEffectiveOrdreMission) {
        this.dateDebutEffectiveOrdreMission = dateDebutEffectiveOrdreMission;
    }

    public LocalDate getDateFinEffectiveOrdreMission() {
        return dateFinEffectiveOrdreMission;
    }

    public void setDateFinEffectiveOrdreMission(LocalDate dateFinEffectiveOrdreMission) {
        this.dateFinEffectiveOrdreMission = dateFinEffectiveOrdreMission;
    }

    public Integer getDureePrevueJoursOrdreMission() {
        return dureePrevueJoursOrdreMission;
    }

    public void setDureePrevueJoursOrdreMission(Integer dureePrevueJoursOrdreMission) {
        this.dureePrevueJoursOrdreMission = dureePrevueJoursOrdreMission;
    }

    public Integer getDureeEffectiveJoursOrdreMission() {
        return dureeEffectiveJoursOrdreMission;
    }

    public void setDureeEffectiveJoursOrdreMission(Integer dureeEffectiveJoursOrdreMission) {
        this.dureeEffectiveJoursOrdreMission = dureeEffectiveJoursOrdreMission;
    }

    public String getUrgenceOrdreMission() {
        return urgenceOrdreMission;
    }

    public void setUrgenceOrdreMission(String urgenceOrdreMission) {
        this.urgenceOrdreMission = urgenceOrdreMission;
    }

    public String getStatutOrdreMission() {
        return statutOrdreMission;
    }

    public void setStatutOrdreMission(String statutOrdreMission) {
        this.statutOrdreMission = statutOrdreMission;
    }

    public LocalDate getDateDemandeMission() {
        return dateDemandeMission;
    }

    public void setDateDemandeMission(LocalDate dateDemandeMission) {
        this.dateDemandeMission = dateDemandeMission;
    }

    public Double getBudgetAlloueOrdreMission() {
        return budgetAlloueOrdreMission;
    }

    public void setBudgetAlloueOrdreMission(Double budgetAlloueOrdreMission) {
        this.budgetAlloueOrdreMission = budgetAlloueOrdreMission;
    }

    public LocalDate getDateCreationOrdreMission() {
        return dateCreationOrdreMission;
    }

    public void setDateCreationOrdreMission(LocalDate dateCreationOrdreMission) {
        this.dateCreationOrdreMission = dateCreationOrdreMission;
    }

    public String getLieuDepartOrdreMission() {
        return lieuDepartOrdreMission;
    }

    public void setLieuDepartOrdreMission(String lieuDepartOrdreMission) {
        this.lieuDepartOrdreMission = lieuDepartOrdreMission;
    }

    public String getLieuDestinationOrdreMission() {
        return lieuDestinationOrdreMission;
    }

    public void setLieuDestinationOrdreMission(String lieuDestinationOrdreMission) {
        this.lieuDestinationOrdreMission = lieuDestinationOrdreMission;
    }

    public Long getIdUtilisateurCreateur() {
        return idUtilisateurCreateur;
    }

    public void setIdUtilisateurCreateur(Long idUtilisateurCreateur) {
        this.idUtilisateurCreateur = idUtilisateurCreateur;
    }

    public String getEntiteCode() {
        return entiteCode;
    }

    public void setEntiteCode(String entiteCode) {
        this.entiteCode = entiteCode;
    }

    public Long getIdNatureMission() {
        return idNatureMission;
    }

    public void setIdNatureMission(Long idNatureMission) {
        this.idNatureMission = idNatureMission;
    }

    public List<GmParticiper> getParticipants() {
        return participants;
    }

    public void setParticipants(List<GmParticiper> participants) {
        this.participants = participants;
    }

    public List<GmEtape> getEtapes() {
        return etapes;
    }

    public void setEtapes(List<GmEtape> etapes) {
        this.etapes = etapes;
    }

    public List<GmUtiliserRessour> getRessources() {
        return ressources;
    }

    public void setRessources(List<GmUtiliserRessour> ressources) {
        this.ressources = ressources;
    }
}
