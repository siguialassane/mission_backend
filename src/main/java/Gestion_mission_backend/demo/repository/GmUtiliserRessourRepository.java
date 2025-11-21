package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmUtiliserRessour;
import Gestion_mission_backend.demo.entity.GmUtiliserRessourPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmUtiliserRessourRepository extends JpaRepository<GmUtiliserRessour, GmUtiliserRessourPK> {
    
    List<GmUtiliserRessour> findByIdOrdreMission(Long idOrdreMission);
    
    void deleteByIdOrdreMission(Long idOrdreMission);
}
