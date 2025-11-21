package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ValidationInfoDTO {
    private Long idValidation;
    private String typeValidation;
    private String statutValidation;
    private Date dateValidation;
    private String commentairesValidation;
    private Long idUtilisateurValidateur;
    private String validateurNom;
    private String validateurPrenom;
    private String validateurNomComplet;
}
