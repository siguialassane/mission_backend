package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmValidationWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmValidationWorkflowRepository extends JpaRepository<GmValidationWorkflow, Long> {
    
    List<GmValidationWorkflow> findByIdOrdreMission(Long idOrdreMission);
    
    @Query("SELECT v FROM GmValidationWorkflow v WHERE v.idOrdreMission = :idMission ORDER BY v.dateValidation DESC")
    List<GmValidationWorkflow> findByIdOrdreMissionOrderByDateValidationDesc(@Param("idMission") Long idMission);
}
