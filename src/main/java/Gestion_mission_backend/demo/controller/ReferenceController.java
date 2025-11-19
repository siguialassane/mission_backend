package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.entity.AcEntite;
import Gestion_mission_backend.demo.entity.AcVille;
import Gestion_mission_backend.demo.entity.GmNatureMission;
import Gestion_mission_backend.demo.entity.GmRessource;
import Gestion_mission_backend.demo.entity.GmService;
import Gestion_mission_backend.demo.service.ReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/references")
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class ReferenceController {

    @Autowired
    private ReferenceService referenceService;

    @GetMapping("/villes")
    public ResponseEntity<List<AcVille>> getAllVilles() {
        return ResponseEntity.ok(referenceService.getAllVilles());
    }

    @GetMapping("/entites")
    public ResponseEntity<List<AcEntite>> getAllEntites() {
        return ResponseEntity.ok(referenceService.getAllEntites());
    }

    @GetMapping("/ressources")
    public ResponseEntity<List<GmRessource>> getAllRessources() {
        return ResponseEntity.ok(referenceService.getAllRessources());
    }

    @GetMapping("/ressources/disponibles")
    public ResponseEntity<List<GmRessource>> getRessourcesDisponibles() {
        return ResponseEntity.ok(referenceService.getRessourcesDisponibles());
    }

    @GetMapping("/natures-mission")
    public ResponseEntity<List<GmNatureMission>> getAllNaturesMission() {
        return ResponseEntity.ok(referenceService.getAllNaturesMission());
    }

    @GetMapping("/services")
    public ResponseEntity<List<GmService>> getAllServices() {
        return ResponseEntity.ok(referenceService.getAllServices());
    }
}
