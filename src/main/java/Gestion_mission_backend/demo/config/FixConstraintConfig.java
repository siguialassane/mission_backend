package Gestion_mission_backend.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FixConstraintConfig {

    @Bean
    public CommandLineRunner disableFkConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                log.info("🔧 [FIX] Tentative de désactivation de la contrainte FK_GM_FRAISMISSION_AGENT...");
                // On essaie de désactiver la contrainte pour permettre les ID négatifs (Ressources)
                jdbcTemplate.execute("ALTER TABLE GM_FRAISMISSION DISABLE CONSTRAINT FK_GM_FRAISMISSION_AGENT");
                log.info("✅ [FIX] Contrainte FK_GM_FRAISMISSION_AGENT désactivée avec succès !");
            } catch (Exception e) {
                // On log en warning car elle peut déjà être désactivée ou ne pas exister
                log.warn("⚠️ [FIX] Note sur la contrainte : {}", e.getMessage());
            }
        };
    }
}
