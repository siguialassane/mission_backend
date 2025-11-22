package Gestion_mission_backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO représentant tous les frais d'un agent participant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraisAgentDTO {
    private Long idAgent;
    private String nomAgent;
    private String prenomAgent;
    private String nomCompletAgent;
    private Long idFonction;
    private String libelleFonction;
    
    private List<FraisLigneDTO> lignesFrais;  // Liste des frais (Repas, Hébergement, etc.)
    private Long totalAgent;                   // Total de tous les frais de cet agent
}
