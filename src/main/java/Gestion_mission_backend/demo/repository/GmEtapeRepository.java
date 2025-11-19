package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmEtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmEtapeRepository extends JpaRepository<GmEtape, Long> {
    
    List<GmEtape> findByOrdreMission_IdOrdreMissionOrderByOrdreEtapeAsc(Long idOrdreMission);
    
    void deleteByOrdreMission_IdOrdreMission(Long idOrdreMission);
}
