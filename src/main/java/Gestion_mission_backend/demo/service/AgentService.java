package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.AgentDTO;
import Gestion_mission_backend.demo.entity.GmAgent;
import Gestion_mission_backend.demo.entity.GmFonction;
import Gestion_mission_backend.demo.entity.GmService;
import Gestion_mission_backend.demo.repository.GmAgentRepository;
import Gestion_mission_backend.demo.repository.GmFonctionRepository;
import Gestion_mission_backend.demo.repository.GmServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    @Autowired
    private GmAgentRepository agentRepository;
    
    @Autowired
    private GmFonctionRepository fonctionRepository;
    
    @Autowired
    private GmServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<AgentDTO> getAllAgents() {
        return agentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgentDTO> getAgentsActifs() {
        return agentRepository.findByStatutActifAgent("A").stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgentDTO> getMyAgents(Long idUtilisateur) {
        return agentRepository.findByIdUtilisateurCreateur(idUtilisateur).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgentDTO> searchAgents(String query) {
        return agentRepository.searchAgents(query).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgentDTO getAgentById(Long id) {
        return agentRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + id));
    }

    @Transactional
    public AgentDTO createAgent(AgentDTO dto) {
        log.info("[AGENT_CREATE] Début création agent - Nom: {} {}", dto.getNomAgent(), dto.getPrenomAgent());
        log.debug("[AGENT_CREATE] Payload reçu: Matricule={}, Email={}, IdFonction={}, IdService={}", 
                  dto.getMatriculeAgent(), dto.getEmailAgent(), dto.getIdFonction(), dto.getIdService());
        
        if (dto.getMatriculeAgent() != null && !dto.getMatriculeAgent().isEmpty()) {
            if (agentRepository.existsByMatriculeAgent(dto.getMatriculeAgent())) {
                log.error("[AGENT_CREATE] ERREUR: Matricule {} déjà existant", dto.getMatriculeAgent());
                throw new IllegalArgumentException("Un agent avec ce matricule existe déjà");
            }
        }
        if (dto.getEmailAgent() != null && !dto.getEmailAgent().isEmpty()) {
            if (agentRepository.existsByEmailAgent(dto.getEmailAgent())) {
                log.error("[AGENT_CREATE] ERREUR: Email {} déjà existant", dto.getEmailAgent());
                throw new IllegalArgumentException("Un agent avec cet email existe déjà");
            }
        }

        if (dto.getIdFonction() == null) {
            log.error("[AGENT_CREATE] ERREUR: Fonction manquante");
            throw new IllegalArgumentException("La fonction est obligatoire");
        }
        if (dto.getIdService() == null) {
            log.error("[AGENT_CREATE] ERREUR: Service manquant");
            throw new IllegalArgumentException("Le service est obligatoire");
        }
        log.info("[AGENT_CREATE] Validations OK - Fonction: {}, Service: {}", dto.getIdFonction(), dto.getIdService());

        GmAgent agent = new GmAgent();
        agent.setMatriculeAgent(dto.getMatriculeAgent());
        agent.setNomAgent(dto.getNomAgent());
        agent.setPrenomAgent(dto.getPrenomAgent());
        agent.setEmailAgent(dto.getEmailAgent());
        agent.setTelephoneAgent(dto.getTelephoneAgent());
        agent.setStatutActifAgent(dto.getStatutActifAgent() != null ? dto.getStatutActifAgent() : "A");
        agent.setIdFonction(dto.getIdFonction());
        agent.setIdService(dto.getIdService());
        agent.setIdUtilisateurCreateur(dto.getIdUtilisateurCreateur());
        agent.setDateCreationAgent(LocalDate.now());

        log.debug("[AGENT_CREATE] Sauvegarde de l'entité GmAgent...");
        agent = agentRepository.save(agent);
        log.info("[AGENT_CREATE] ✓ Agent créé avec succès - ID: {}, Matricule: {}", 
                 agent.getIdAgent(), agent.getMatriculeAgent());
        return toDTO(agent);
    }

    @Transactional
    public AgentDTO updateAgent(Long id, AgentDTO dto) {
        GmAgent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + id));

        agent.setNomAgent(dto.getNomAgent());
        agent.setPrenomAgent(dto.getPrenomAgent());
        agent.setEmailAgent(dto.getEmailAgent());
        agent.setTelephoneAgent(dto.getTelephoneAgent());
        agent.setStatutActifAgent(dto.getStatutActifAgent());

        agent = agentRepository.save(agent);
        return toDTO(agent);
    }

    private AgentDTO toDTO(GmAgent agent) {
        AgentDTO dto = new AgentDTO();
        dto.setIdAgent(agent.getIdAgent());
        dto.setMatriculeAgent(agent.getMatriculeAgent());
        dto.setNomAgent(agent.getNomAgent());
        dto.setPrenomAgent(agent.getPrenomAgent());
        dto.setEmailAgent(agent.getEmailAgent());
        dto.setTelephoneAgent(agent.getTelephoneAgent());
        dto.setStatutActifAgent(agent.getStatutActifAgent());
        dto.setIdFonction(agent.getIdFonction());
        dto.setIdService(agent.getIdService());
        dto.setIdUtilisateurCreateur(agent.getIdUtilisateurCreateur());
        
        // Récupérer libellés fonction et service si disponibles
        if (agent.getIdFonction() != null) {
            fonctionRepository.findById(agent.getIdFonction())
                .ifPresent(f -> dto.setLibelleFonction(f.getLibFonction()));
        }
        if (agent.getIdService() != null) {
            serviceRepository.findById(agent.getIdService())
                .ifPresent(s -> dto.setLibelleService(s.getLibelleService()));
        }
        
        return dto;
    }
}
