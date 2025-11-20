package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmFonction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GmFonctionRepository extends JpaRepository<GmFonction, Long> {
}
