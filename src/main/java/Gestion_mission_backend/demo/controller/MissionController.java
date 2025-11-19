package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.MissionCreationDTO;
import Gestion_mission_backend.demo.dto.MissionResponseDTO;
import Gestion_mission_backend.demo.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class MissionController {

    @Autowired
    private MissionService missionService;

    @PostMapping
    public ResponseEntity<MissionResponseDTO> createMission(@RequestBody MissionCreationDTO dto) {
        try {
            MissionResponseDTO response = missionService.createMission(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
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
        List<MissionResponseDTO> missions = missionService.getAllMissions();
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
