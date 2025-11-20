package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "GM_TYPE_RESSOURCE")
@Data
public class GmTypeRessource {
    
    @Id
    @Column(name = "ID_TYPE_RESSOURCE")
    private Long idTypeRessource;
    
    @Column(name = "LIB_TYPE_RESSOURCE", length = 30)
    private String libTypeRessource;
}
