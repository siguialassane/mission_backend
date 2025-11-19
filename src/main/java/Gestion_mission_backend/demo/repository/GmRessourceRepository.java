package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmRessource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmRessourceRepository extends JpaRepository<GmRessource, Long> {
    
    List<GmRessource> findByDispoRessource(Integer disponible);
}
