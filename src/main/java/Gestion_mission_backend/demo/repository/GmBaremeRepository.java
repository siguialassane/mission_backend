package Gestion_mission_backend.demo.repository;

import Gestion_mission_backend.demo.entity.GmBareme;
import Gestion_mission_backend.demo.entity.GmBaremePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GmBaremeRepository extends JpaRepository<GmBareme, GmBaremePK> {
    
    /**
     * Récupère le tarif du barème pour une fonction et une catégorie de frais donnée
     */
    @Query("SELECT b.montantUnitaire FROM GmBareme b " +
           "WHERE b.idFonction = :idFonction AND b.idCategorieFrais = :idCategorieFrais")
    Optional<Long> findMontantByFonctionAndCategorie(
        @Param("idFonction") Long idFonction,
        @Param("idCategorieFrais") Long idCategorieFrais
    );
}
