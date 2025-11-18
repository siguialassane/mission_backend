package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.AuthResponse;
import Gestion_mission_backend.demo.dto.RegisterRequest;
import Gestion_mission_backend.demo.entity.Utilisateur;
import Gestion_mission_backend.demo.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;

    public AuthService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmailUtilisateur(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNomUtilisateur(request.getNom());
        utilisateur.setPrenomUtilisateur(request.getPrenom());
        utilisateur.setEmailUtilisateur(request.getEmail());
        utilisateur.setMotDePasse(request.getPassword()); // Mot de passe en clair
        utilisateur.setProfilUtilisateur(request.getProfil());
        utilisateur.setStatutActif("ACTIF");
        utilisateur.setDateCreation(LocalDate.now());
        utilisateur.setEntiteCode(request.getEntiteCode());
        utilisateur.setIdService(request.getIdService());

        // Sauvegarder l'utilisateur
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Retourner la réponse
        return new AuthResponse(
                savedUser.getIdUtilisateur(),
                savedUser.getNomUtilisateur(),
                savedUser.getPrenomUtilisateur(),
                savedUser.getEmailUtilisateur(),
                savedUser.getProfilUtilisateur(),
                "Utilisateur créé avec succès"
        );
    }

    public Utilisateur getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmailUtilisateur(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
