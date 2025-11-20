package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDate;

/**
 * Entité read-only mappée sur la vue V_MISSION_DETAIL
 * Permet de récupérer tous les détails d'une mission en 1 seule requête
 */
@Entity
@Immutable
@Table(name = "V_MISSION_DETAIL")
public class VMissionDetail {

    @Id
    @Column(name = "ID_ORDRE_MISSION")
    private Long idOrdreMission;

    @Column(name = "NUMERO_ORDRE_MISSION", length = 50)
    private String numeroOrdreMission;

    @Column(name = "OBJET_ORDRE_MISSION", length = 500)
    private String objetOrdreMission;

    @Column(name = "DATE_DEBUT_MISSION")
    private LocalDate dateDebutMission;

    @Column(name = "DATE_FIN_MISSION")
    private LocalDate dateFinMission;

    @Column(name = "STATUT_ORDRE_MISSION", length = 30)
    private String statutOrdreMission;

    @Column(name = "DATE_CREATION_ORDRE_MISSION")
    private LocalDate dateCreationOrdreMission;

    @Column(name = "ID_NATURE_MISSION")
    private Long idNatureMission;

    @Column(name = "NATURE_MISSION_LIB", length = 200)
    private String natureMissionLib;

    @Column(name = "ENTITE_CODE", length = 2)
    private String entiteCode;

    @Column(name = "ENTITE_LIB", length = 200)
    private String entiteLib;

    @Column(name = "ID_SERVICE")
    private Long idService;

    @Column(name = "SERVICE_LIB", length = 200)
    private String serviceLib;

    @Column(name = "ID_UTILISATEUR_CREATEUR")
    private Long idUtilisateurCreateur;

    @Column(name = "CREATEUR_NOM", length = 200)
    private String createurNom;

    /**
     * Format: idAgent1:role1:nom1:prenom1:matricule1|idAgent2:role2:nom2:prenom2:matricule2
     */
    @Column(name = "PARTICIPANTS_DATA", length = 4000)
    private String participantsData;

    /**
     * Format: idEtape1:ordre1:villeCode1:villeLib1|idEtape2:ordre2:villeCode2:villeLib2
     */
    @Column(name = "ETAPES_DATA", length = 4000)
    private String etapesData;

    /**
     * Format: idRessource1:quantite1:libelle1|idRessource2:quantite2:libelle2
     */
    @Column(name = "RESSOURCES_DATA", length = 4000)
    private String ressourcesData;

    // Constructeur par défaut
    public VMissionDetail() {
    }

    // Getters
    public Long getIdOrdreMission() {
        return idOrdreMission;
    }

    public String getNumeroOrdreMission() {
        return numeroOrdreMission;
    }

    public String getObjetOrdreMission() {
        return objetOrdreMission;
    }

    public LocalDate getDateDebutMission() {
        return dateDebutMission;
    }

    public LocalDate getDateFinMission() {
        return dateFinMission;
    }

    public String getStatutOrdreMission() {
        return statutOrdreMission;
    }

    public LocalDate getDateCreationOrdreMission() {
        return dateCreationOrdreMission;
    }

    public Long getIdNatureMission() {
        return idNatureMission;
    }

    public String getNatureMissionLib() {
        return natureMissionLib;
    }

    public String getEntiteCode() {
        return entiteCode;
    }

    public String getEntiteLib() {
        return entiteLib;
    }

    public Long getIdService() {
        return idService;
    }

    public String getServiceLib() {
        return serviceLib;
    }

    public Long getIdUtilisateurCreateur() {
        return idUtilisateurCreateur;
    }

    public String getCreateurNom() {
        return createurNom;
    }

    public String getParticipantsData() {
        return participantsData;
    }

    public String getEtapesData() {
        return etapesData;
    }

    public String getRessourcesData() {
        return ressourcesData;
    }
}
