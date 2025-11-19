package Gestion_mission_backend.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GM_SERVICE")
public class GmService {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_seq")
    @SequenceGenerator(name = "service_seq", sequenceName = "SEQ_GM_SERVICE", allocationSize = 1)
    @Column(name = "ID_SERVICE")
    private Long idService;

    @Column(name = "CODE_SERVICE", length = 20, unique = true)
    private String codeService;

    @Column(name = "LIBELLE_SERVICE", length = 255, nullable = false)
    private String libelleService;

    @Column(name = "DESCRIPTION_SERVICE", length = 1000)
    private String descriptionService;

    // Constructeurs
    public GmService() {
    }

    public GmService(String codeService, String libelleService) {
        this.codeService = codeService;
        this.libelleService = libelleService;
    }

    // Getters et Setters
    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }

    public String getCodeService() {
        return codeService;
    }

    public void setCodeService(String codeService) {
        this.codeService = codeService;
    }

    public String getLibelleService() {
        return libelleService;
    }

    public void setLibelleService(String libelleService) {
        this.libelleService = libelleService;
    }

    public String getDescriptionService() {
        return descriptionService;
    }

    public void setDescriptionService(String descriptionService) {
        this.descriptionService = descriptionService;
    }
}
