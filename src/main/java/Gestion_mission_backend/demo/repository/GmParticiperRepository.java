package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmParticiper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmParticiperRepository extends JpaRepository<GmParticiper, Long> {
    
    List<GmParticiper> findByOrdreMission_IdOrdreMission(Long idOrdreMission);
    
    @Query("SELECT COUNT(p) FROM GmParticiper p WHERE p.ordreMission.idOrdreMission = :idMission AND p.role = 'CHEF'")
    long countChefByMission(@Param("idMission") Long idMission);
    
    boolean existsByOrdreMission_IdOrdreMissionAndIdAgent(Long idMission, Long idAgent);
    
    void deleteByOrdreMission_IdOrdreMission(Long idOrdreMission);
}
