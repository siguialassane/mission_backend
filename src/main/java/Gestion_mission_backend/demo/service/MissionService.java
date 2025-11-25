package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.*;
import Gestion_mission_backend.demo.entity.*;
import Gestion_mission_backend.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionService {

    private static final Logger log = LoggerFactory.getLogger(MissionService.class);

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

    @Autowired
    private GmValidationWorkflowRepository validationRepository;

    @Autowired
    private GmUtilisateurRepository utilisateurRepository;

    @Transactional
    public MissionResponseDTO createMission(MissionCreationDTO dto) {
        log.info("[MISSION_CREATE] Début création mission - Objet: {}", dto.getObjetOrdreMission());
        log.debug("[MISSION_CREATE] Payload reçu: Participants={}, Etapes={}, Ressources={}", 
                  dto.getParticipants().size(), dto.getEtapes().size(), dto.getRessources().size());
        
        // Validation : au moins un chef de mission
        long nbChefs = dto.getParticipants().stream()
                .filter(p -> "CHEF_MISSION".equals(p.getRole()))
                .count();
        if (nbChefs == 0) {
            log.error("[MISSION_CREATE] ERREUR: Aucun chef de mission trouvé");
            throw new IllegalArgumentException("Une mission doit avoir au moins un chef de mission");
        }

        // Validation : dates cohérentes
        if (dto.getDateFinMission().isBefore(dto.getDateDebutMission())) {
            log.error("[MISSION_CREATE] ERREUR: Dates incohérentes - Début: {}, Fin: {}", 
                      dto.getDateDebutMission(), dto.getDateFinMission());
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        log.info("[MISSION_CREATE] Validations OK - NbChefs: {}, Dates valides", nbChefs);

        // Créer l'ordre de mission
        GmOrdreMission mission = new GmOrdreMission();
        mission.setObjetOrdreMission(dto.getObjetOrdreMission());
        
        // Mappage du Motif vers Observations (ou un champ dédié si existant, ici on utilise Observations pour ne pas perdre l'info)
        if (dto.getMotifMission() != null && !dto.getMotifMission().isEmpty()) {
            mission.setObservationsOrdreMission(dto.getMotifMission());
        }

        // Mappage de l'Urgence
        if (Boolean.TRUE.equals(dto.getUrgence())) {
            mission.setUrgenceOrdreMission("URGENT");
        } else {
            mission.setUrgenceOrdreMission("NORMAL");
        }

        mission.setDateDebutPrevueOrdreMission(dto.getDateDebutMission());
        mission.setDateFinPrevueOrdreMission(dto.getDateFinMission());
        
        // Calcul automatique de la durée
        if (dto.getDateDebutMission() != null && dto.getDateFinMission() != null) {
            long jours = ChronoUnit.DAYS.between(dto.getDateDebutMission(), dto.getDateFinMission()) + 1;
            mission.setDureePrevueJoursOrdreMission(jours);
        }

        // Valeurs par défaut pour les lieux (Règle métier : Départ/Retour Abidjan par défaut)
        mission.setLieuDepartOrdreMission("Abidjan");
        mission.setLieuDestinationOrdreMission("Abidjan"); // Par défaut, retour à Abidjan

        // Gestion de l'urgence
        if (Boolean.TRUE.equals(dto.getUrgence())) {
            mission.setUrgenceOrdreMission("URGENT");
        } else {
            mission.setUrgenceOrdreMission("NORMAL");
        }

        mission.setIdNatureMission(dto.getIdNatureMission());
        mission.setEntiteCode(dto.getCodEntite());
        mission.setIdUtilisateurCreateur(dto.getIdUtilisateurCreateur());
        mission.setIdService(dto.getIdService());
        // Définir le statut initial: EN_ATTENTE_VALIDATION_RH (soumise pour validation RH)
        mission.setStatutOrdreMission("EN_ATTENTE_VALIDATION_RH");

        // Sauvegarder la mission (déclenche @PrePersist)
        log.debug("[MISSION_CREATE] Sauvegarde de l'entité GmOrdreMission...");
        mission = missionRepository.save(mission);
        log.info("[MISSION_CREATE] Mission créée - ID: {}, Numéro: {}", 
                 mission.getIdOrdreMission(), mission.getNumeroOrdreMission());

        // Ajouter les participants
        log.debug("[MISSION_CREATE] Ajout de {} participants", dto.getParticipants().size());
        for (ParticipantDTO pDto : dto.getParticipants()) {
            GmParticiper participant = new GmParticiper();
            participant.setIdOrdreMission(mission.getIdOrdreMission());
            participant.setIdAgent(pDto.getIdAgent());
            participant.setRole(pDto.getRole());
            mission.addParticipant(participant);
        }

        // Ajouter les étapes
        log.debug("[MISSION_CREATE] Ajout de {} étapes", dto.getEtapes().size());
        for (EtapeDTO eDto : dto.getEtapes()) {
            GmEtape etape = new GmEtape();
            etape.setVilleCode(eDto.getVilleCode());
            etape.setOrdreEtape(eDto.getOrdreEtape());
            mission.addEtape(etape);
        }

        // Ajouter les ressources
        log.debug("[MISSION_CREATE] Ajout de {} ressources", dto.getRessources().size());
        for (RessourceDTO rDto : dto.getRessources()) {
            GmUtiliserRessour ressource = new GmUtiliserRessour();
            ressource.setIdOrdreMission(mission.getIdOrdreMission());
            ressource.setIdRessource(rDto.getIdRessource());
            ressource.setQuantite(rDto.getQuantite());
            mission.addRessource(ressource);
        }

        // Sauvegarder avec cascade
        log.debug("[MISSION_CREATE] Sauvegarde finale avec cascade...");
        mission = missionRepository.save(mission);
        log.info("[MISSION_CREATE] ✓ Mission créée avec succès - ID: {}", mission.getIdOrdreMission());

        return toResponseDTO(mission);
    }

    @Transactional(readOnly = true)
    public MissionResponseDTO getMissionById(Long id) {
        log.info("[MISSION_DETAIL] Récupération mission ID: {}", id);
        GmOrdreMission mission = missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée : " + id));
        return toResponseDTO(mission);
    }

    @Transactional(readOnly = true)
    public List<MissionResponseDTO> getAllMissions() {
        log.info("[MISSION_SERVICE] getAllMissions appelé");
        List<GmOrdreMission> missions = missionRepository.findAll();
        log.info("[MISSION_SERVICE] {} missions trouvées", missions.size());
        return missions.stream()
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
    public List<MissionResponseDTO> getMissionsByStatuts(List<String> statuts) {
        log.info("[MISSION_SERVICE] getMissionsByStatuts appelé avec {} statuts", statuts.size());
        List<GmOrdreMission> missions = missionRepository.findByStatutOrdreMissionIn(statuts);
        log.info("[MISSION_SERVICE] {} missions trouvées pour statuts: {}", missions.size(), statuts);
        return missions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MissionResponseDTO> getMissionsByCreateur(Long idUtilisateur) {
        log.info("[MISSION_SERVICE] getMissionsByCreateur appelé pour userId={}", idUtilisateur);
        List<GmOrdreMission> missions = missionRepository.findByIdUtilisateurCreateur(idUtilisateur);
        log.info("[MISSION_SERVICE] {} missions trouvées pour userId={}", missions.size(), idUtilisateur);
        return missions.stream()
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

    @Transactional
    public void updateMissionStatus(Long id, String nouveauStatut) {
        log.info("[MISSION_SERVICE] Mise à jour statut mission {} vers {}", id, nouveauStatut);
        updateStatut(id, nouveauStatut);
    }

    private MissionResponseDTO toResponseDTO(GmOrdreMission mission) {
        MissionResponseDTO dto = new MissionResponseDTO();
        dto.setIdOrdreMission(mission.getIdOrdreMission());
        dto.setNumeroOrdreMission(mission.getNumeroOrdreMission());
        
        // Générer codeMission
        dto.setCodeMission(mission.getNumeroOrdreMission() != null 
            ? mission.getNumeroOrdreMission() 
            : "MISS-" + mission.getIdOrdreMission());
        
        dto.setObjetOrdreMission(mission.getObjetOrdreMission());
        dto.setObjetMission(mission.getObjetOrdreMission()); // Alias pour frontend
        dto.setDateDebutMission(mission.getDateDebutPrevueOrdreMission());
        dto.setDateFinMission(mission.getDateFinPrevueOrdreMission());
        dto.setStatutOrdreMission(mission.getStatutOrdreMission());
        dto.setIdNatureMission(mission.getIdNatureMission());
        dto.setCodEntite(mission.getEntiteCode());
        dto.setIdService(mission.getIdService());
        dto.setIdUtilisateurCreateur(mission.getIdUtilisateurCreateur());

        // Enrichir avec les libellés
        if (mission.getIdNatureMission() != null) {
            natureMissionRepository.findById(mission.getIdNatureMission())
                    .ifPresent(n -> dto.setNatureMissionLib(n.getLibelleNatureMission()));
        }
        if (mission.getEntiteCode() != null) {
            entiteRepository.findById(mission.getEntiteCode())
                    .ifPresent(e -> dto.setEntiteLib(e.getEntiteLib()));
        }
        // Enrichir avec le service si présent
        if (mission.getIdService() != null) {
            serviceRepository.findById(mission.getIdService())
                    .ifPresent(s -> dto.setServiceLib(s.getLibelleService()));
        }
        if (mission.getIdUtilisateurCreateur() != null) {
            utilisateurRepository.findById(mission.getIdUtilisateurCreateur())
                    .ifPresent(u -> dto.setCreateurNom(u.getPrenomUtilisateur() + " " + u.getNomUtilisateur()));
        }

        // Mapper les participants
        dto.setParticipants(mission.getParticipants().stream()
                .map(this::toParticipantResponseDTO)
                .collect(Collectors.toList()));

        // Mapper les étapes
        List<EtapeResponseDTO> etapes = mission.getEtapes().stream()
                .sorted((e1, e2) -> Long.compare(e1.getOrdreEtape(), e2.getOrdreEtape()))
                .map(this::toEtapeResponseDTO)
                .collect(Collectors.toList());
        dto.setEtapes(etapes);
        
        // Calculer lieuDepart et lieuDestination
        // Toujours partir d'Abidjan et retourner à Abidjan
        dto.setLieuDepart("Abidjan");
        dto.setLieuDestination("Abidjan");

        // Mapper les ressources - Charger directement depuis le repository pour éviter les problèmes de LAZY loading
        List<GmUtiliserRessour> ressourcesMission = ressourceRepository.findByIdOrdreMission(mission.getIdOrdreMission());
        dto.setRessources(ressourcesMission.stream()
                .map(this::toRessourceResponseDTO)
                .collect(Collectors.toList()));

        // Mapper les validations avec les infos du validateur
        dto.setValidations(validationRepository.findByIdOrdreMissionOrderByDateValidationDesc(mission.getIdOrdreMission()).stream()
                .map(this::toValidationInfoDTO)
                .collect(Collectors.toList()));

        log.debug("[MISSION_DTO] Mission {} convertie - Participants: {}, Etapes: {}, Ressources: {}, Validations: {}",
                mission.getIdOrdreMission(), dto.getParticipants().size(), 
                dto.getEtapes().size(), dto.getRessources().size(), dto.getValidations().size());

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

    private ValidationInfoDTO toValidationInfoDTO(GmValidationWorkflow validation) {
        ValidationInfoDTO dto = new ValidationInfoDTO();
        dto.setIdValidation(validation.getIdValidation());
        dto.setTypeValidation(validation.getTypeValidation());
        dto.setStatutValidation(validation.getStatutValidation());
        dto.setDateValidation(validation.getDateValidation());
        dto.setCommentairesValidation(validation.getCommentairesValidation());
        dto.setIdUtilisateurValidateur(validation.getIdUtilisateurValidateur());

        // Récupérer le nom du validateur
        if (validation.getIdUtilisateurValidateur() != null) {
            utilisateurRepository.findById(validation.getIdUtilisateurValidateur())
                    .ifPresent(u -> {
                        dto.setValidateurNom(u.getNomUtilisateur());
                        dto.setValidateurPrenom(u.getPrenomUtilisateur());
                        dto.setValidateurNomComplet(u.getPrenomUtilisateur() + " " + u.getNomUtilisateur());
                    });
        }

        return dto;
    }
}