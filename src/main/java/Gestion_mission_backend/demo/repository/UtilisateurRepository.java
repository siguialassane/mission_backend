package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmailUtilisateur(String email);
    
    boolean existsByEmailUtilisateur(String email);
}
