package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.TemplateMotifDTO;
import Gestion_mission_backend.demo.service.TemplateMotifService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
public class TemplateMotifController {

    private final TemplateMotifService templateService;

    @GetMapping
    public ResponseEntity<List<TemplateMotifDTO>> getMyTemplates(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[TEMPLATE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[TEMPLATE_API] GET /api/templates - userId: {}", userId);
        List<TemplateMotifDTO> templates = templateService.getMyTemplates(userId);
        return ResponseEntity.ok(templates);
    }

    @PostMapping
    public ResponseEntity<TemplateMotifDTO> createTemplate(
            @RequestBody TemplateMotifDTO dto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[TEMPLATE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[TEMPLATE_API] POST /api/templates - userId: {}, nom: {}", userId, dto.getNomTemplate());
        TemplateMotifDTO created = templateService.createTemplate(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateMotifDTO> updateTemplate(
            @PathVariable Long id,
            @RequestBody TemplateMotifDTO dto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[TEMPLATE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[TEMPLATE_API] PUT /api/templates/{} - userId: {}", id, userId);
        try {
            TemplateMotifDTO updated = templateService.updateTemplate(id, userId, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("[TEMPLATE_API] Erreur: {}", e.getMessage());
            return ResponseEntity.status(403).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            log.error("[TEMPLATE_API] Utilisateur non authentifié");
            return ResponseEntity.status(401).build();
        }
        
        log.info("[TEMPLATE_API] DELETE /api/templates/{} - userId: {}", id, userId);
        try {
            templateService.deleteTemplate(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("[TEMPLATE_API] Erreur: {}", e.getMessage());
            return ResponseEntity.status(403).build();
        }
    }
}
