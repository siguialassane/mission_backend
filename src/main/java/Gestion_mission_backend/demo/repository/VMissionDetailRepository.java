package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.VMissionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VMissionDetailRepository extends JpaRepository<VMissionDetail, Long> {
    
    /**
     * Trouve toutes les missions avec un statut donné
     */
    List<VMissionDetail> findByStatutOrdreMission(String statut);
    
    /**
     * Trouve toutes les missions créées par un utilisateur
     */
    List<VMissionDetail> findByIdUtilisateurCreateur(Long idUtilisateur);
    
    /**
     * Trouve toutes les missions d'une entité
     */
    List<VMissionDetail> findByEntiteCode(String entiteCode);
}
