package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.AgentDTO;
import Gestion_mission_backend.demo.service.AgentService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @GetMapping
    public ResponseEntity<List<AgentDTO>> getAllAgents(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false, defaultValue = "false") boolean all,
            HttpSession session) {
        log.info("[API] GET /api/agents - statut={}, all={}", statut, all);
        
        // Par défaut, retourner uniquement les agents de l'utilisateur connecté
        if (!all) {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                log.warn("[API] GET /api/agents - Utilisateur non authentifié");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            log.info("[API] GET /api/agents - Retour des agents de userId={}", userId);
            List<AgentDTO> agents = agentService.getMyAgents(userId);
            log.info("[API] GET /api/agents - {} agents trouvés", agents.size());
            return ResponseEntity.ok(agents);
        }
        
        // Si all=true, retourner tous les agents (pour les administrateurs RH par exemple)
        log.info("[API] GET /api/agents - Mode ALL activé - retour de tous les agents");
        List<AgentDTO> agents;
        if ("A".equals(statut)) {
            agents = agentService.getAgentsActifs();
        } else {
            agents = agentService.getAllAgents();
        }
        log.info("[API] GET /api/agents - {} agents trouvés (mode ALL)", agents.size());
        return ResponseEntity.ok(agents);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<AgentDTO>> getMyAgents(HttpSession session) {
        log.info("[API] GET /api/agents/mine - Récupération des agents de l'utilisateur");
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.warn("[API] GET /api/agents/mine - Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("[API] ✅ userId depuis session: {}", userId);
        List<AgentDTO> agents = agentService.getMyAgents(userId);
        log.info("[API] GET /api/agents/mine - {} agents trouvés pour userId={}", agents.size(), userId);
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
        log.info("[API] POST /api/agents - Requête reçue");
        try {
            // Validation de l'ID créateur
            if (dto.getIdUtilisateurCreateur() == null) {
                log.warn("[API] POST /api/agents - ID Utilisateur créateur manquant");
                // On pourrait renvoyer une erreur, mais pour l'instant on log juste
                // return ResponseEntity.badRequest().build();
            }
            
            log.debug("[API] POST /api/agents - UserId: {}", dto.getIdUtilisateurCreateur());
            
            AgentDTO agent = agentService.createAgent(dto);
            log.info("[API] POST /api/agents - Succès (201)");
            return ResponseEntity.status(HttpStatus.CREATED).body(agent);
        } catch (IllegalArgumentException e) {
            log.error("[API] POST /api/agents - Erreur validation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[API] POST /api/agents - Erreur serveur: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id, HttpSession session) {
        log.info("[API] DELETE /api/agents/{} - Requ\u00eate re\u00e7ue", id);
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                log.warn("[API] DELETE /api/agents/{} - Utilisateur non authentifi\u00e9", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            log.debug("[API] DELETE /api/agents/{} - UserId: {}", id, userId);
            agentService.deleteAgent(id, userId);
            log.info("[API] DELETE /api/agents/{} - Succ\u00e8s (204)", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[API] DELETE /api/agents/{} - Erreur autorisation: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            log.error("[API] DELETE /api/agents/{} - Erreur: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
