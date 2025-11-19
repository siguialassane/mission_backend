package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.*;
import Gestion_mission_backend.demo.entity.*;
import Gestion_mission_backend.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private GmOrdreMissionRepository missionRepository;

    @Autowired
    private GmParticiperRepository participerRepository;

    @Autowired
    private GmEtapeRepository etapeRepository;

    @Autowired
    private GmUtiliserRessourRepository ressourceRepository;

    @Autowired
    private GmAgentRepository agentRepository;

    @Autowired
    private AcVilleRepository villeRepository;

    @Autowired
    private GmRessourceRepository gmRessourceRepository;

    @Autowired
    private GmNatureMissionRepository natureMissionRepository;

    @Autowired
    private AcEntiteRepository entiteRepository;

    @Autowired
    private GmServiceRepository serviceRepository;

    @Transactional
    public MissionResponseDTO createMission(MissionCreationDTO dto) {
        // Validation : au moins un chef de mission
        long nbChefs = dto.getParticipants().stream()
                .filter(p -> "CHEF".equals(p.getRole()))
                .count();
        if (nbChefs == 0) {
            throw new IllegalArgumentException("Une mission doit avoir au moins un chef de mission");
        }

        // Validation : dates cohérentes
        if (dto.getDateFinMission().isBefore(dto.getDateDebutMission())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        // Créer l'ordre de mission
        GmOrdreMission mission = new GmOrdreMission();
        mission.setObjetOrdreMission(dto.getObjetOrdreMission());
        mission.setDateDebutPrevueOrdreMission(dto.getDateDebutMission());
        mission.setDateFinPrevueOrdreMission(dto.getDateFinMission());
        mission.setIdNatureMission(dto.getIdNatureMission());
        mission.setEntiteCode(dto.getCodEntite());
        mission.setIdUtilisateurCreateur(dto.getIdUtilisateurCreateur());
        // Le numeroOrdreMission et le statut sont définis par @PrePersist

        // Sauvegarder la mission (déclenche @PrePersist)
        mission = missionRepository.save(mission);

        // Ajouter les participants
        for (ParticipantDTO pDto : dto.getParticipants()) {
            GmParticiper participant = new GmParticiper();
            participant.setIdOrdreMission(mission.getIdOrdreMission());
            participant.setIdAgent(pDto.getIdAgent());
            participant.setRole(pDto.getRole());
            participant.setOrdreMission(mission);
            mission.getParticipants().add(participant);
        }

        // Ajouter les étapes
        for (EtapeDTO eDto : dto.getEtapes()) {
            GmEtape etape = new GmEtape();
            etape.setOrdreMission(mission);
            etape.setVilleCode(eDto.getVilleCode());
            etape.setOrdreEtape(eDto.getOrdreEtape());
            mission.getEtapes().add(etape);
        }

        // Ajouter les ressources
        for (RessourceDTO rDto : dto.getRessources()) {
            GmUtiliserRessour ressource = new GmUtiliserRessour();
            ressource.setIdOrdreMission(mission.getIdOrdreMission());
            ressource.setIdRessource(rDto.getIdRessource());
            ressource.setQuantite(rDto.getQuantite());
            ressource.setOrdreMission(mission);
            mission.getRessources().add(ressource);
        }

        // Sauvegarder avec cascade
        mission = missionRepository.save(mission);

        return toResponseDTO(mission);
    }

    @Transactional(readOnly = true)
    public MissionResponseDTO getMissionById(Long id) {
        GmOrdreMission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée : " + id));
        return toResponseDTO(mission);
    }

    @Transactional(readOnly = true)
    public List<MissionResponseDTO> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MissionResponseDTO> getMissionsByStatut(String statut) {
        return missionRepository.findByStatutOrdreMission(statut).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MissionResponseDTO> getMissionsByCreateur(Long idUtilisateur) {
        return missionRepository.findByIdUtilisateurCreateur(idUtilisateur).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MissionResponseDTO updateStatut(Long id, String nouveauStatut) {
        GmOrdreMission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée : " + id));
        mission.setStatutOrdreMission(nouveauStatut);
        mission = missionRepository.save(mission);
        return toResponseDTO(mission);
    }

    private MissionResponseDTO toResponseDTO(GmOrdreMission mission) {
        MissionResponseDTO dto = new MissionResponseDTO();
        dto.setIdOrdreMission(mission.getIdOrdreMission());
        dto.setNumeroOrdreMission(mission.getNumeroOrdreMission());
        dto.setObjetOrdreMission(mission.getObjetOrdreMission());
        dto.setDateDebutMission(mission.getDateDebutPrevueOrdreMission());
        dto.setDateFinMission(mission.getDateFinPrevueOrdreMission());
        dto.setStatutOrdreMission(mission.getStatutOrdreMission());
        dto.setIdNatureMission(mission.getIdNatureMission());
        dto.setCodEntite(mission.getEntiteCode());
        dto.setIdService(dto.getIdService()); // Garder la valeur du DTO
        dto.setIdUtilisateurCreateur(mission.getIdUtilisateurCreateur());

        // Enrichir avec les libellés
        natureMissionRepository.findById(mission.getIdNatureMission())
                .ifPresent(n -> dto.setNatureMissionLib(n.getLibelleNatureMission()));
        entiteRepository.findById(mission.getEntiteCode())
                .ifPresent(e -> dto.setEntiteLib(e.getEntiteLib()));
        if (dto.getIdService() != null) {
            serviceRepository.findById(dto.getIdService())
                    .ifPresent(s -> dto.setServiceLib(s.getLibelleService()));
        }

        // Mapper les participants
        dto.setParticipants(mission.getParticipants().stream()
                .map(this::toParticipantResponseDTO)
                .collect(Collectors.toList()));

        // Mapper les étapes
        dto.setEtapes(mission.getEtapes().stream()
                .map(this::toEtapeResponseDTO)
                .collect(Collectors.toList()));

        // Mapper les ressources
        dto.setRessources(mission.getRessources().stream()
                .map(this::toRessourceResponseDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private ParticipantResponseDTO toParticipantResponseDTO(GmParticiper participant) {
        ParticipantResponseDTO dto = new ParticipantResponseDTO();
        // Utiliser l'ID composite comme identifiant unique
        dto.setIdParticiper(participant.getIdAgent()); // Stocké temporairement
        dto.setIdAgent(participant.getIdAgent());
        dto.setRole(participant.getRole());

        agentRepository.findById(participant.getIdAgent()).ifPresent(agent -> {
            dto.setMatriculeAgent(agent.getMatriculeAgent());
            dto.setNomAgent(agent.getNomAgent());
            dto.setPrenomAgent(agent.getPrenomAgent());
        });

        return dto;
    }

    private EtapeResponseDTO toEtapeResponseDTO(GmEtape etape) {
        EtapeResponseDTO dto = new EtapeResponseDTO();
        dto.setIdEtape(etape.getIdEtape());
        dto.setVilleCode(etape.getVilleCode());
        dto.setOrdreEtape(etape.getOrdreEtape());

        villeRepository.findById(etape.getVilleCode())
                .ifPresent(ville -> dto.setVilleLib(ville.getVilleLib()));

        return dto;
    }

    private RessourceResponseDTO toRessourceResponseDTO(GmUtiliserRessour ressource) {
        RessourceResponseDTO dto = new RessourceResponseDTO();
        dto.setIdUtiliserRessour(ressource.getIdRessource()); // Utiliser idRessource comme identifiant composite
        dto.setIdRessource(ressource.getIdRessource());
        dto.setQuantite(ressource.getQuantite());

        gmRessourceRepository.findById(ressource.getIdRessource())
                .ifPresent(r -> dto.setRessourceLib(r.getLibRessource()));

        return dto;
    }
}
