package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GmAgentRepository extends JpaRepository<GmAgent, Long> {
    
    Optional<GmAgent> findByMatriculeAgent(String matricule);
    
    boolean existsByMatriculeAgent(String matricule);
    
    boolean existsByEmailAgent(String email);
    
    @Query("SELECT a FROM GmAgent a WHERE " +
           "LOWER(a.nomAgent) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.prenomAgent) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.matriculeAgent) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<GmAgent> searchAgents(@Param("query") String query);
    
    List<GmAgent> findByStatutActifAgent(String statut);
}
