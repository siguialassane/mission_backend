package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmUtiliserRessour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmUtiliserRessourRepository extends JpaRepository<GmUtiliserRessour, Long> {
    
    List<GmUtiliserRessour> findByOrdreMission_IdOrdreMission(Long idOrdreMission);
    
    void deleteByOrdreMission_IdOrdreMission(Long idOrdreMission);
}
