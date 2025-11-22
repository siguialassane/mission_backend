package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.ViewMgFraisAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewMgFraisAgentRepository extends JpaRepository<ViewMgFraisAgent, Long> {
    
    List<ViewMgFraisAgent> findByIdOrdreMission(Long idOrdreMission);
}
