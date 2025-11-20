package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmRessource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RessourceRepository extends JpaRepository<GmRessource, Long> {
    
    // Tous les RH voient toutes les ressources
    List<GmRessource> findAllByOrderByIdTypeRessourceAscLibRessourceAsc();
    
    // Filtrer par type (Véhicule=1, Chauffeur=2, Police=3)
    List<GmRessource> findByIdTypeRessource(Long idTypeRessource);
    
    // Ressources disponibles uniquement
    List<GmRessource> findByDispoRessource(Long dispoRessource);
}
