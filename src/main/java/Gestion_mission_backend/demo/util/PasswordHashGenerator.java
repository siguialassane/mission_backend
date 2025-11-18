package Gestion_mission_backend.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Classe utilitaire pour générer des mots de passe hashés
 * Utilisez cette classe pour créer des hash BCrypt pour vos utilisateurs
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash des mots de passe existants
        String[] passwords = {
            "ChefService2025",
            "RH2025",
            "Moyens2025"
        };
        
        System.out.println("=== MOTS DE PASSE HASHÉS ===\n");
        for (String password : passwords) {
            String hashed = encoder.encode(password);
            System.out.println("Mot de passe: " + password);
            System.out.println("Hash BCrypt: " + hashed);
            System.out.println();
        }
    }
}
