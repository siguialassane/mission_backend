package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.AgentDTO;
import Gestion_mission_backend.demo.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class AgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping
    public ResponseEntity<List<AgentDTO>> getAllAgents(@RequestParam(required = false) String statut) {
        List<AgentDTO> agents;
        if ("A".equals(statut)) {
            agents = agentService.getAgentsActifs();
        } else {
            agents = agentService.getAllAgents();
        }
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AgentDTO>> searchAgents(@RequestParam String query) {
        List<AgentDTO> agents = agentService.searchAgents(query);
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentDTO> getAgentById(@PathVariable Long id) {
        try {
            AgentDTO agent = agentService.getAgentById(id);
            return ResponseEntity.ok(agent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<AgentDTO> createAgent(@RequestBody AgentDTO dto) {
        try {
            AgentDTO agent = agentService.createAgent(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(agent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentDTO> updateAgent(@PathVariable Long id, @RequestBody AgentDTO dto) {
        try {
            AgentDTO agent = agentService.updateAgent(id, dto);
            return ResponseEntity.ok(agent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
