package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.AcVille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcVilleRepository extends JpaRepository<AcVille, String> {
    
    List<AcVille> findByVilleLibContainingIgnoreCase(String libelle);
}
