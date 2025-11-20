package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.MissionCreationDTO;
import Gestion_mission_backend.demo.dto.MissionResponseDTO;
import Gestion_mission_backend.demo.service.MissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class MissionController {

    private static final Logger log = LoggerFactory.getLogger(MissionController.class);

    @Autowired
    private MissionService missionService;

    @PostMapping
    public ResponseEntity<MissionResponseDTO> createMission(@RequestBody MissionCreationDTO dto) {
        log.info("[API] POST /api/missions - Requête reçue");
        try {
            MissionResponseDTO response = missionService.createMission(dto);
            log.info("[API] POST /api/missions - Succès (201)");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("[API] POST /api/missions - Erreur validation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[API] POST /api/missions - Erreur serveur: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionResponseDTO> getMissionById(@PathVariable Long id) {
        try {
            MissionResponseDTO response = missionService.getMissionById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MissionResponseDTO>> getAllMissions() {
        log.info("[API] GET /api/missions - Récupération de toutes les missions");
        List<MissionResponseDTO> missions = missionService.getAllMissions();
        log.info("[API] GET /api/missions - {} missions trouvées", missions.size());
        if (!missions.isEmpty()) {
            log.info("[API] Première mission: ID={}, Code={}, Statut={}", 
                missions.get(0).getIdOrdreMission(), 
                missions.get(0).getCodeMission(),
                missions.get(0).getStatutOrdreMission());
        }
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MissionResponseDTO>> getMyMissions() {
        log.info("[API] GET /api/missions/mine - Récupération des missions de l'utilisateur");
        Long currentUserId = 1L; // Temporaire - à remplacer par l'utilisateur authentifié
        log.info("[API] userId temporaire: {}", currentUserId);
        List<MissionResponseDTO> missions = missionService.getMissionsByCreateur(currentUserId);
        log.info("[API] GET /api/missions/mine - {} missions trouvées pour userId={}", missions.size(), currentUserId);
        if (!missions.isEmpty()) {
            log.info("[API] Première mission: ID={}, Code={}, Statut={}", 
                missions.get(0).getIdOrdreMission(), 
                missions.get(0).getCodeMission(),
                missions.get(0).getStatutOrdreMission());
        }
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<MissionResponseDTO>> getMissionsByStatut(@PathVariable String statut) {
        List<MissionResponseDTO> missions = missionService.getMissionsByStatut(statut);
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/createur/{idUtilisateur}")
    public ResponseEntity<List<MissionResponseDTO>> getMissionsByCreateur(@PathVariable Long idUtilisateur) {
        List<MissionResponseDTO> missions = missionService.getMissionsByCreateur(idUtilisateur);
        return ResponseEntity.ok(missions);
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<MissionResponseDTO> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        try {
            MissionResponseDTO response = missionService.updateStatut(id, statut);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
