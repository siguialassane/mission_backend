package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "GM_VALIDATIONWORKFLOW")
@Data
public class GmValidationWorkflow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "validation_seq")
    @SequenceGenerator(name = "validation_seq", sequenceName = "SEQ_GM_VALIDATION_WORKFLOW", allocationSize = 1)
    @Column(name = "ID_VALIDATION")
    private Long idValidation;
    
    @Column(name = "ID_ORDRE_MISSION")
    private Long idOrdreMission;
    
    @Column(name = "ID_AGENT", nullable = true)
    private Long idAgent;
    
    @Column(name = "NIVEAU_VALIDATION")
    private Long niveauValidation;
    
    @Column(name = "TYPE_VALIDATION", length = 30)
    private String typeValidation;
    
    @Column(name = "ID_UTILISATEUR_VALIDATEUR")
    private Long idUtilisateurValidateur;
    
    @Column(name = "DATE_VALIDATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValidation;
    
    @Column(name = "STATUT_VALIDATION", length = 20)
    private String statutValidation;
    
    @Column(name = "COMMENTAIRES_VALIDATION", length = 500)
    private String commentairesValidation;
}
