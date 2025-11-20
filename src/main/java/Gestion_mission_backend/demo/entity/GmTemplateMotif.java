package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "GM_TEMPLATE_MOTIF")
@Data
public class GmTemplateMotif {

    @Id
    @Column(name = "ID_TEMPLATE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTemplate;

    @Column(name = "NOM_TEMPLATE", nullable = false, length = 200)
    private String nomTemplate;

    @Lob
    @Column(name = "CONTENU_TEMPLATE", nullable = false)
    private String contenuTemplate;

    @Column(name = "ID_UTILISATEUR_CREATEUR", nullable = false)
    private Long idUtilisateurCreateur;

    @Column(name = "DATE_CREATION", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "DATE_MODIFICATION")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
