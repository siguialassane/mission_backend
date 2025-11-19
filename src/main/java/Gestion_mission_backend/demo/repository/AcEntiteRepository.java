package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.AcEntite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcEntiteRepository extends JpaRepository<AcEntite, String> {
    
    List<AcEntite> findByEntiteLibContainingIgnoreCase(String libelle);
}
