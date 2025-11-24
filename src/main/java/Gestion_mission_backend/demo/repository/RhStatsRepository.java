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
        String sql = "SELECT " +
            "(SELECT COUNT(*) FROM GM_ORDREMISSION WHERE STATUT_ORDRE_MISSION = 'EN_ATTENTE_VALIDATION_RH') as NB_EN_ATTENTE_RH, " +
            "(SELECT COUNT(*) FROM GM_ORDREMISSION WHERE STATUT_ORDRE_MISSION = 'VALIDEE_RH') as NB_VALIDEES_RH, " +
            "(SELECT COUNT(*) FROM GM_ORDREMISSION WHERE STATUT_ORDRE_MISSION = 'REFUSEE_RH') as NB_REFUSEES_RH, " +
            "(SELECT COUNT(*) FROM GM_ORDREMISSION WHERE STATUT_ORDRE_MISSION = 'EN_ATTENTE_VALIDATION_RH' " +
            "AND DATE_DEBUT_PREVUE_ORDRE_MISSIO <= SYSDATE + 7 " +
            "AND DATE_DEBUT_PREVUE_ORDRE_MISSIO >= TRUNC(SYSDATE)) as NB_URGENTES, " +
            "(SELECT COUNT(*) FROM GM_ORDREMISSION) as TOTAL_MISSIONS_RH, " +
            "(SELECT COUNT(DISTINCT m.ID_ORDRE_MISSION) FROM GM_ORDREMISSION m " +
            "JOIN GM_UTILISER_RESSOUR r ON m.ID_ORDRE_MISSION = r.ID_ORDRE_MISSION " +
            "WHERE m.STATUT_ORDRE_MISSION = 'VALIDEE_RH') as NB_AVEC_RESSOURCES " +
            "FROM DUAL";
        
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
