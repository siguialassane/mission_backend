package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmFraisMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GmFraisMissionRepository extends JpaRepository<GmFraisMission, Long> {
    
    /**
     * Récupère tous les frais d'une mission
     */
    List<GmFraisMission> findByIdOrdreMission(Long idOrdreMission);
    
    /**
     * Récupère tous les frais d'un agent pour une mission
     */
    List<GmFraisMission> findByIdOrdreMissionAndIdAgent(Long idOrdreMission, Long idAgent);
    
    /**
     * Vérifie si des frais ont déjà été calculés pour une mission
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM GmFraisMission f " +
           "WHERE f.idOrdreMission = :idOrdreMission")
    boolean existsByIdOrdreMission(@Param("idOrdreMission") Long idOrdreMission);
    
    /**
     * Supprime tous les frais d'une mission (pour recalcul)
     */
    void deleteByIdOrdreMission(Long idOrdreMission);
    
    /**
     * Calcule le total des frais d'une mission
     */
    @Query("SELECT COALESCE(SUM(f.montantPrevuFraisMission), 0) FROM GmFraisMission f " +
           "WHERE f.idOrdreMission = :idOrdreMission")
    Long sumMontantByMission(@Param("idOrdreMission") Long idOrdreMission);
    
    /**
     * Calcule le total des frais d'une mission (alias)
     */
    @Query("SELECT COALESCE(SUM(f.montantPrevuFraisMission), 0) FROM GmFraisMission f " +
           "WHERE f.idOrdreMission = :idOrdreMission")
    Long sumMontantByIdOrdreMission(@Param("idOrdreMission") Long idOrdreMission);
}
