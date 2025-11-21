package Gestion_mission_backend.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import Gestion_mission_backend.demo.dto.RhStatsDTO;

@Repository
public class RhStatsRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public RhStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public RhStatsDTO getStatistics() {
        String sql = "SELECT "
            + "NB_EN_ATTENTE_RH, "
            + "NB_VALIDEES_RH, "
            + "NB_REFUSEES_RH, "
            + "NB_URGENTES, "
            + "TOTAL_MISSIONS_RH, "
            + "NB_AVEC_RESSOURCES "
            + "FROM V_RH_STATS_MISSIONS";
        
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            RhStatsDTO stats = new RhStatsDTO();
            stats.setNbEnAttenteRh(rs.getLong("NB_EN_ATTENTE_RH"));
            stats.setNbValideesRh(rs.getLong("NB_VALIDEES_RH"));
            stats.setNbRefuseesRh(rs.getLong("NB_REFUSEES_RH"));
            stats.setNbUrgentes(rs.getLong("NB_URGENTES"));
            stats.setTotalMissionsRh(rs.getLong("TOTAL_MISSIONS_RH"));
            stats.setNbAvecRessources(rs.getLong("NB_AVEC_RESSOURCES"));
            return stats;
        });
    }
}
