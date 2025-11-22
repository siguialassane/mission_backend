package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmCategorieFrais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmCategorieFraisRepository extends JpaRepository<GmCategorieFrais, Long> {
    
    /**
     * Récupère toutes les catégories actives
     */
    List<GmCategorieFrais> findByStatutActifCategorieFrais(String statut);
}
