package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "GM_UTILISATEUR")
@Data
public class GmUtilisateur {
    
    @Id
    @Column(name = "ID_UTILISATEUR")
    private Long idUtilisateur;
    
    @Column(name = "NOM_UTILISATEUR")
    private String nomUtilisateur;
    
    @Column(name = "PRENOM_UTILISATEUR")
    private String prenomUtilisateur;
    
    @Column(name = "EMAIL_UTILISATEUR")
    private String emailUtilisateur;
}
