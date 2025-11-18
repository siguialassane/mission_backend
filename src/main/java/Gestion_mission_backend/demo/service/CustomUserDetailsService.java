package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.entity.Utilisateur;
import Gestion_mission_backend.demo.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService() {
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmailUtilisateur(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        // Vérifier que l'utilisateur est actif
        if (!"ACTIF".equalsIgnoreCase(utilisateur.getStatutActif())) {
            throw new UsernameNotFoundException("Utilisateur inactif");
        }

        return User.builder()
                .username(utilisateur.getEmailUtilisateur())
                .password(utilisateur.getMotDePasse())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilisateur.getProfilUtilisateur())))
                .build();
    }
}
