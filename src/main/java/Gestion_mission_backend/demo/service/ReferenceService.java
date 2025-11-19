package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.entity.AcEntite;
import Gestion_mission_backend.demo.entity.AcVille;
import Gestion_mission_backend.demo.entity.GmNatureMission;
import Gestion_mission_backend.demo.entity.GmRessource;
import Gestion_mission_backend.demo.entity.GmService;
import Gestion_mission_backend.demo.repository.AcEntiteRepository;
import Gestion_mission_backend.demo.repository.AcVilleRepository;
import Gestion_mission_backend.demo.repository.GmNatureMissionRepository;
import Gestion_mission_backend.demo.repository.GmRessourceRepository;
import Gestion_mission_backend.demo.repository.GmServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReferenceService {

    @Autowired
    private AcVilleRepository villeRepository;

    @Autowired
    private AcEntiteRepository entiteRepository;

    @Autowired
    private GmRessourceRepository ressourceRepository;

    @Autowired
    private GmNatureMissionRepository natureMissionRepository;

    @Autowired
    private GmServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<AcVille> getAllVilles() {
        return villeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AcEntite> getAllEntites() {
        return entiteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GmRessource> getAllRessources() {
        return ressourceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GmRessource> getRessourcesDisponibles() {
        return ressourceRepository.findByDispoRessource(1);
    }

    @Transactional(readOnly = true)
    public List<GmNatureMission> getAllNaturesMission() {
        return natureMissionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<GmService> getAllServices() {
        return serviceRepository.findAll();
    }
}
