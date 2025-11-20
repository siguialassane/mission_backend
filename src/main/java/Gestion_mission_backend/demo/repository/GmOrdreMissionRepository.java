package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmOrdreMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GmOrdreMissionRepository extends JpaRepository<GmOrdreMission, Long> {
    
    Optional<GmOrdreMission> findByNumeroOrdreMission(String numeroOrdreMission);
    
    List<GmOrdreMission> findByStatutOrdreMission(String statut);
    
    List<GmOrdreMission> findByStatutOrdreMissionIn(List<String> statuts);
    
    List<GmOrdreMission> findByIdUtilisateurCreateur(Long idUtilisateur);
    
    @Query("SELECT m FROM GmOrdreMission m WHERE m.statutOrdreMission IN :statuts")
    List<GmOrdreMission> findByStatutIn(@Param("statuts") List<String> statuts);
    
    @Query("SELECT m FROM GmOrdreMission m WHERE m.numeroOrdreMission LIKE :prefix ORDER BY m.numeroOrdreMission DESC")
    Optional<GmOrdreMission> findTopByNumeroOrdreMissionStartingWithOrderByNumeroOrdreMissionDesc(@Param("prefix") String prefix);
}
