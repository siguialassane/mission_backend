package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class RhTraitementMissionDTO {
    
    // Validations administratives
    private Boolean validationFonde;
    private String commentaireFonde;
    private Boolean validationComptable;
    private String commentaireComptable;
    
    // Ressources assignées (optionnel) - Support ancien format (single)
    private Long idVehicule;
    private Long idChauffeur;
    private Long idPolice;
    
    // Ressources assignées (nouveau format - multiples)
    private List<Long> vehicules = new ArrayList<>();
    private List<Long> chauffeurs = new ArrayList<>();
    private List<Long> polices = new ArrayList<>();
    
    // Info utilisateur qui valide
    private Long idUtilisateur;
    
    /**
     * Retourne tous les IDs de véhicules (combine ancien et nouveau format)
     */
    public List<Long> getAllVehicules() {
        List<Long> all = new ArrayList<>(vehicules);
        if (idVehicule != null && !all.contains(idVehicule)) {
            all.add(idVehicule);
        }
        return all;
    }
    
    /**
     * Retourne tous les IDs de chauffeurs (combine ancien et nouveau format)
     */
    public List<Long> getAllChauffeurs() {
        List<Long> all = new ArrayList<>(chauffeurs);
        if (idChauffeur != null && !all.contains(idChauffeur)) {
            all.add(idChauffeur);
        }
        return all;
    }
    
    /**
     * Retourne tous les IDs de polices (combine ancien et nouveau format)
     */
    public List<Long> getAllPolices() {
        List<Long> all = new ArrayList<>(polices);
        if (idPolice != null && !all.contains(idPolice)) {
            all.add(idPolice);
        }
        return all;
    }
}
