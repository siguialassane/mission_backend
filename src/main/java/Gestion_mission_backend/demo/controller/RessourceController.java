package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.RessourceDTO;
import Gestion_mission_backend.demo.service.RessourceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ressources")
@RequiredArgsConstructor
@Slf4j
public class RessourceController {

    private final RessourceService ressourceService;

    @GetMapping
    public ResponseEntity<List<RessourceDTO>> getAllRessources() {
        log.info("[RESSOURCE_API] GET /api/ressources - Toutes les ressources");
        List<RessourceDTO> ressources = ressourceService.getAllRessources();
        return ResponseEntity.ok(ressources);
    }

    @GetMapping("/type/{idType}")
    public ResponseEntity<List<RessourceDTO>> getRessourcesByType(@PathVariable Long idType) {
        log.info("[RESSOURCE_API] GET /api/ressources/type/{} - Filtrage par type", idType);
        List<RessourceDTO> ressources = ressourceService.getRessourcesByType(idType);
        return ResponseEntity.ok(ressources);
    }

    @PostMapping
    public ResponseEntity<RessourceDTO> createRessource(
            @RequestBody RessourceDTO dto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[RESSOURCE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[RESSOURCE_API] POST /api/ressources - userId: {}, libelle: {}", userId, dto.getLibRessource());
        RessourceDTO created = ressourceService.createRessource(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RessourceDTO> updateRessource(
            @PathVariable Long id,
            @RequestBody RessourceDTO dto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[RESSOURCE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[RESSOURCE_API] PUT /api/ressources/{} - userId: {}", id, userId);
        try {
            RessourceDTO updated = ressourceService.updateRessource(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            log.error("[RESSOURCE_API] Erreur: {}", e.getMessage());
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRessource(
            @PathVariable Long id,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[RESSOURCE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[RESSOURCE_API] DELETE /api/ressources/{} - userId: {}", id, userId);
        try {
            ressourceService.deleteRessource(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("[RESSOURCE_API] Erreur: {}", e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }
}
