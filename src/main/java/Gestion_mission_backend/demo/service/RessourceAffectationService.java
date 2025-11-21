package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.entity.GmUtiliserRessour;
import Gestion_mission_backend.demo.entity.GmUtiliserRessourPK;
import Gestion_mission_backend.demo.repository.GmUtiliserRessourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RessourceAffectationService {

    private final GmUtiliserRessourRepository utiliserRessourRepository;

    @Transactional
    public void assignerRessource(Long idMission, Long idRessource) {
        log.info("[RESSOURCE_AFFECTATION] Assignation ressource {} à mission {}", idRessource, idMission);
        
        // Vérifier si l'assignation existe déjà
        GmUtiliserRessourPK id = new GmUtiliserRessourPK(idRessource, idMission);
        
        if (utiliserRessourRepository.existsById(id)) {
            log.info("[RESSOURCE_AFFECTATION] Ressource {} déjà assignée à mission {}", idRessource, idMission);
            return;
        }
        
        GmUtiliserRessour utilisation = new GmUtiliserRessour();
        utilisation.setIdRessource(idRessource);
        utilisation.setIdOrdreMission(idMission);
        utilisation.setQuantite(1L);
        
        utiliserRessourRepository.save(utilisation);
        log.info("[RESSOURCE_AFFECTATION] ✓ Ressource {} assignée à mission {}", idRessource, idMission);
    }

    @Transactional
    public void retirerRessource(Long idMission, Long idRessource) {
        log.info("[RESSOURCE_AFFECTATION] Retrait ressource {} de mission {}", idRessource, idMission);
        
        GmUtiliserRessourPK id = new GmUtiliserRessourPK(idRessource, idMission);
        
        utiliserRessourRepository.deleteById(id);
        log.info("[RESSOURCE_AFFECTATION] ✓ Ressource {} retirée de mission {}", idRessource, idMission);
    }
}
