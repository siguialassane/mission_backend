package Gestion_mission_backend.demo.controller;

import Gestion_mission_backend.demo.dto.RhStatsDTO;
import Gestion_mission_backend.demo.repository.RhStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rh")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:8017", "http://localhost:5173", "http://localhost:3000"})
public class RhController {
    
    private final RhStatsRepository rhStatsRepository;
    
    @GetMapping("/stats")
    public ResponseEntity<RhStatsDTO> getStats() {
        log.info("[RH_API] GET /api/rh/stats");
        RhStatsDTO stats = rhStatsRepository.getStatistics();
        log.info("[RH_API] Stats: {}", stats);
        return ResponseEntity.ok(stats);
    }
}
