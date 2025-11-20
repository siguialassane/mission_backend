package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmTemplateMotif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateMotifRepository extends JpaRepository<GmTemplateMotif, Long> {
    
    List<GmTemplateMotif> findByIdUtilisateurCreateurOrderByDateCreationDesc(Long idUtilisateurCreateur);
}
