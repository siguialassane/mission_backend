package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmNatureMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GmNatureMissionRepository extends JpaRepository<GmNatureMission, Long> {
}
