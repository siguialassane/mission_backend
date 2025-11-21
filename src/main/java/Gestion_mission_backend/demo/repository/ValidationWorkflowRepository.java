package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmValidationWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValidationWorkflowRepository extends JpaRepository<GmValidationWorkflow, Long> {
    
    List<GmValidationWorkflow> findByIdOrdreMission(Long idOrdreMission);
    
    Optional<GmValidationWorkflow> findByIdOrdreMissionAndTypeValidation(Long idOrdreMission, String typeValidation);
    
    List<GmValidationWorkflow> findByIdOrdreMissionAndStatutValidation(Long idOrdreMission, String statutValidation);
}
