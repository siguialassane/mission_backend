package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.AgentDTO;
import Gestion_mission_backend.demo.entity.GmAgent;
import Gestion_mission_backend.demo.repository.GmAgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Autowired
    private GmAgentRepository agentRepository;

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
        if (agentRepository.existsByMatriculeAgent(dto.getMatriculeAgent())) {
            throw new IllegalArgumentException("Un agent avec ce matricule existe déjà");
        }
        if (agentRepository.existsByEmailAgent(dto.getEmailAgent())) {
            throw new IllegalArgumentException("Un agent avec cet email existe déjà");
        }

        GmAgent agent = new GmAgent();
        agent.setMatriculeAgent(dto.getMatriculeAgent());
        agent.setNomAgent(dto.getNomAgent());
        agent.setPrenomAgent(dto.getPrenomAgent());
        agent.setEmailAgent(dto.getEmailAgent());
        agent.setTelephoneAgent(dto.getTelephoneAgent());
        agent.setStatutActifAgent(dto.getStatutActifAgent() != null ? dto.getStatutActifAgent() : "A");

        agent = agentRepository.save(agent);
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
        return dto;
    }
}
