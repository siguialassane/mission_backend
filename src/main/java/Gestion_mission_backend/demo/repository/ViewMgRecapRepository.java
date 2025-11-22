package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.ViewMgRecap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewMgRecapRepository extends JpaRepository<ViewMgRecap, Long> {
    
    Optional<ViewMgRecap> findByIdOrdreMission(Long idOrdreMission);
}
