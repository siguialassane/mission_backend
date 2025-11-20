package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.RessourceDTO;
import Gestion_mission_backend.demo.entity.GmRessource;
import Gestion_mission_backend.demo.entity.GmTypeRessource;
import Gestion_mission_backend.demo.entity.GmUtilisateur;
import Gestion_mission_backend.demo.repository.RessourceRepository;
import Gestion_mission_backend.demo.repository.TypeRessourceRepository;
import Gestion_mission_backend.demo.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RessourceService {

    private final RessourceRepository ressourceRepository;
    private final TypeRessourceRepository typeRessourceRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional(readOnly = true)
    public List<RessourceDTO> getAllRessources() {
        log.info("[RESSOURCE_LIST] Récupération de toutes les ressources");
        List<GmRessource> ressources = ressourceRepository.findAllByOrderByIdTypeRessourceAscLibRessourceAsc();
        log.info("[RESSOURCE_LIST] ✓ {} ressource(s) trouvée(s)", ressources.size());
        return ressources.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RessourceDTO> getRessourcesByType(Long idTypeRessource) {
        log.info("[RESSOURCE_LIST] Récupération ressources type ID: {}", idTypeRessource);
        List<GmRessource> ressources = ressourceRepository.findByIdTypeRessource(idTypeRessource);
        return ressources.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RessourceDTO createRessource(Long idUtilisateur, RessourceDTO dto) {
        log.info("[RESSOURCE_CREATE] Création ressource '{}' type {} par utilisateur ID: {}", 
                dto.getLibRessource(), dto.getIdTypeRessource(), idUtilisateur);
        
        GmRessource ressource = new GmRessource();
        ressource.setIdTypeRessource(dto.getIdTypeRessource());
        ressource.setLibRessource(dto.getLibRessource());
        ressource.setDispoRessource(1L); // Disponible par défaut
        ressource.setIdUtilisateurCreateur(idUtilisateur);
        
        ressource = ressourceRepository.save(ressource);
        log.info("[RESSOURCE_CREATE] ✓ Ressource ID: {} créée avec succès", ressource.getIdRessource());
        return toDTO(ressource);
    }

    @Transactional
    public RessourceDTO updateRessource(Long idRessource, RessourceDTO dto) {
        log.info("[RESSOURCE_UPDATE] Modification ressource ID: {}", idRessource);
        
        GmRessource ressource = ressourceRepository.findById(idRessource)
                .orElseThrow(() -> {
                    log.error("[RESSOURCE_UPDATE] Ressource {} non trouvée", idRessource);
                    return new RuntimeException("Ressource non trouvée : " + idRessource);
                });
        
        ressource.setLibRessource(dto.getLibRessource());
        ressource.setIdTypeRessource(dto.getIdTypeRessource());
        if (dto.getDispoRessource() != null) {
            ressource.setDispoRessource(dto.getDispoRessource().longValue());
        }
        
        ressource = ressourceRepository.save(ressource);
        log.info("[RESSOURCE_UPDATE] ✓ Ressource {} modifiée avec succès", idRessource);
        return toDTO(ressource);
    }

    @Transactional
    public void deleteRessource(Long idRessource) {
        log.info("[RESSOURCE_DELETE] Suppression ressource ID: {}", idRessource);
        
        if (!ressourceRepository.existsById(idRessource)) {
            log.error("[RESSOURCE_DELETE] Ressource {} non trouvée", idRessource);
            throw new RuntimeException("Ressource non trouvée : " + idRessource);
        }
        
        ressourceRepository.deleteById(idRessource);
        log.info("[RESSOURCE_DELETE] ✓ Ressource {} supprimée avec succès", idRessource);
    }

    private RessourceDTO toDTO(GmRessource ressource) {
        RessourceDTO dto = new RessourceDTO();
        dto.setIdRessource(ressource.getIdRessource());
        dto.setIdTypeRessource(ressource.getIdTypeRessource());
        dto.setLibRessource(ressource.getLibRessource());
        dto.setDispoRessource(ressource.getDispoRessource() != null ? ressource.getDispoRessource().intValue() : 1);
        dto.setIdUtilisateurCreateur(ressource.getIdUtilisateurCreateur());
        
        // Récupérer libellé type ressource
        if (ressource.getIdTypeRessource() != null) {
            typeRessourceRepository.findById(ressource.getIdTypeRessource())
                    .ifPresent(type -> dto.setLibTypeRessource(type.getLibTypeRessource()));
        }
        
        // Récupérer nom créateur
        if (ressource.getIdUtilisateurCreateur() != null) {
            utilisateurRepository.findById(ressource.getIdUtilisateurCreateur())
                    .ifPresent(user -> {
                        String nom = String.format("%s %s", 
                                user.getPrenomUtilisateur() != null ? user.getPrenomUtilisateur() : "",
                                user.getNomUtilisateur() != null ? user.getNomUtilisateur() : ""
                        ).trim();
                        dto.setNomCreateur(nom.isEmpty() ? "Inconnu" : nom);
                    });
        }
        
        return dto;
    }
}
