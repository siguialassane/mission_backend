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
        log.info("[AGENT_SERVICE] getMyAgents appelé pour userId={}", idUtilisateur);
        List<GmAgent> agents = agentRepository.findByIdUtilisateurCreateur(idUtilisateur);
        log.info("[AGENT_SERVICE] {} agents trouvés pour userId={}", agents.size(), idUtilisateur);
        return agents.stream()
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
        log.info("[AGENT_UPDATE] Début modification agent ID: {}", id);
        GmAgent agent = agentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[AGENT_UPDATE] Agent {} non trouvé", id);
                    return new RuntimeException("Agent non trouvé : " + id);
                });

        log.debug("[AGENT_UPDATE] Mise à jour des champs pour agent {}", id);
        agent.setNomAgent(dto.getNomAgent());
        agent.setPrenomAgent(dto.getPrenomAgent());
        agent.setEmailAgent(dto.getEmailAgent());
        agent.setTelephoneAgent(dto.getTelephoneAgent());
        agent.setStatutActifAgent(dto.getStatutActifAgent());
        
        // Mettre à jour fonction et service également
        if (dto.getIdFonction() != null) {
            agent.setIdFonction(dto.getIdFonction());
        }
        if (dto.getIdService() != null) {
            agent.setIdService(dto.getIdService());
        }

        agent = agentRepository.save(agent);
        log.info("[AGENT_UPDATE] ✓ Agent {} modifié avec succès", id);
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

    @Transactional
    public void deleteAgent(Long idAgent, Long idUtilisateurConnecte) {
        log.info("[AGENT_DELETE] Tentative de suppression agent ID: {} par utilisateur ID: {}", idAgent, idUtilisateurConnecte);
        
        GmAgent agent = agentRepository.findById(idAgent)
                .orElseThrow(() -> {
                    log.error("[AGENT_DELETE] Agent {} non trouv\u00e9", idAgent);
                    return new RuntimeException("Agent non trouv\u00e9 : " + idAgent);
                });
        
        // V\u00e9rifier que l'utilisateur connect\u00e9 est le cr\u00e9ateur de l'agent
        if (agent.getIdUtilisateurCreateur() == null || !agent.getIdUtilisateurCreateur().equals(idUtilisateurConnecte)) {
            log.error("[AGENT_DELETE] ERREUR: L'utilisateur {} n'est pas le créateur de l'agent {}", idUtilisateurConnecte, idAgent);
            throw new IllegalArgumentException("Vous ne pouvez supprimer que les agents que vous avez créés");
        }
        
        try {
            log.info("[AGENT_DELETE] Suppression autorisée - Agent {} créé par utilisateur {}", idAgent, idUtilisateurConnecte);
            agentRepository.deleteById(idAgent);
            log.info("[AGENT_DELETE] ✓ Agent {} supprimé avec succès", idAgent);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("[AGENT_DELETE] ERREUR: Impossible de supprimer l'agent {} - Utilisé dans des missions", idAgent);
            String message = String.format(
                "❌ SUPPRESSION IMPOSSIBLE\n\n" +
                "L'agent %s %s (Matricule: %s) ne peut pas être supprimé car il participe actuellement à une ou plusieurs missions.\n\n" +
                "🔹 Actions possibles :\n" +
                "  • Terminer ou clôturer les missions concernées\n" +
                "  • Retirer cet agent de la liste des participants\n" +
                "  • Remplacer l'agent par un autre participant\n\n" +
                "Pour des raisons de traçabilité et d'intégrité des données, les agents affectés à des missions actives ne peuvent pas être supprimés.",
                agent.getPrenomAgent(), 
                agent.getNomAgent(), 
                agent.getMatriculeAgent()
            );
            throw new IllegalArgumentException(message);
        }
    }
}
