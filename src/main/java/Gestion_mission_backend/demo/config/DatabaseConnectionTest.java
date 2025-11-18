package Gestion_mission_backend.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Classe de test pour vérifier la connexion à la base de données Oracle
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseConnectionTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ SUCCÈS: Connexion à la base de données Oracle établie!");
                System.out.println("📊 Database Product: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("📊 Database Version: " + connection.getMetaData().getDatabaseProductVersion());
                System.out.println("👤 Utilisateur: " + connection.getMetaData().getUserName());
                System.out.println("✨ La connexion fonctionne parfaitement!");
            } else {
                System.out.println("❌ ERREUR: Impossible de se connecter à la base de données");
            }
        } catch (Exception e) {
            System.out.println("❌ ERREUR de connexion:");
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
