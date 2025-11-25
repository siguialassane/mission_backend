package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.ViewMgFraisComplet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewMgFraisCompletRepository extends JpaRepository<ViewMgFraisComplet, Long> {
    List<ViewMgFraisComplet> findByIdMission(Long idMission);
}
