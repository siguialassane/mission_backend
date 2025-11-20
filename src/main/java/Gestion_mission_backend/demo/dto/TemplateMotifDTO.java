package Gestion_mission_backend.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TemplateMotifDTO {
    private Long idTemplate;
    private String nomTemplate;
    private String contenuTemplate;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
