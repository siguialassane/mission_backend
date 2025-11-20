package Gestion_mission_backend.demo.service;

import Gestion_mission_backend.demo.dto.TemplateMotifDTO;
import Gestion_mission_backend.demo.entity.GmTemplateMotif;
import Gestion_mission_backend.demo.repository.TemplateMotifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateMotifService {

    private final TemplateMotifRepository templateRepository;

    @Transactional(readOnly = true)
    public List<TemplateMotifDTO> getMyTemplates(Long idUtilisateur) {
        log.info("[TEMPLATE_LIST] Récupération templates pour utilisateur ID: {}", idUtilisateur);
        List<GmTemplateMotif> templates = templateRepository.findByIdUtilisateurCreateurOrderByDateCreationDesc(idUtilisateur);
        log.info("[TEMPLATE_LIST] ✓ {} template(s) trouvé(s)", templates.size());
        return templates.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TemplateMotifDTO createTemplate(Long idUtilisateur, TemplateMotifDTO dto) {
        log.info("[TEMPLATE_CREATE] Création template '{}' pour utilisateur ID: {}", dto.getNomTemplate(), idUtilisateur);
        
        GmTemplateMotif template = new GmTemplateMotif();
        template.setNomTemplate(dto.getNomTemplate());
        template.setContenuTemplate(dto.getContenuTemplate());
        template.setIdUtilisateurCreateur(idUtilisateur);
        
        template = templateRepository.save(template);
        log.info("[TEMPLATE_CREATE] ✓ Template ID: {} créé avec succès", template.getIdTemplate());
        return toDTO(template);
    }

    @Transactional
    public TemplateMotifDTO updateTemplate(Long idTemplate, Long idUtilisateur, TemplateMotifDTO dto) {
        log.info("[TEMPLATE_UPDATE] Modification template ID: {} par utilisateur ID: {}", idTemplate, idUtilisateur);
        
        GmTemplateMotif template = templateRepository.findById(idTemplate)
                .orElseThrow(() -> {
                    log.error("[TEMPLATE_UPDATE] Template {} non trouvé", idTemplate);
                    return new RuntimeException("Template non trouvé : " + idTemplate);
                });
        
        // Vérifier que l'utilisateur est le créateur
        if (!template.getIdUtilisateurCreateur().equals(idUtilisateur)) {
            log.error("[TEMPLATE_UPDATE] ERREUR: Utilisateur {} n'est pas le créateur du template {}", idUtilisateur, idTemplate);
            throw new IllegalArgumentException("Vous ne pouvez modifier que vos propres templates");
        }
        
        template.setNomTemplate(dto.getNomTemplate());
        template.setContenuTemplate(dto.getContenuTemplate());
        
        template = templateRepository.save(template);
        log.info("[TEMPLATE_UPDATE] ✓ Template {} modifié avec succès", idTemplate);
        return toDTO(template);
    }

    @Transactional
    public void deleteTemplate(Long idTemplate, Long idUtilisateur) {
        log.info("[TEMPLATE_DELETE] Suppression template ID: {} par utilisateur ID: {}", idTemplate, idUtilisateur);
        
        GmTemplateMotif template = templateRepository.findById(idTemplate)
                .orElseThrow(() -> {
                    log.error("[TEMPLATE_DELETE] Template {} non trouvé", idTemplate);
                    return new RuntimeException("Template non trouvé : " + idTemplate);
                });
        
        // Vérifier que l'utilisateur est le créateur
        if (!template.getIdUtilisateurCreateur().equals(idUtilisateur)) {
            log.error("[TEMPLATE_DELETE] ERREUR: Utilisateur {} n'est pas le créateur du template {}", idUtilisateur, idTemplate);
            throw new IllegalArgumentException("Vous ne pouvez supprimer que vos propres templates");
        }
        
        templateRepository.deleteById(idTemplate);
        log.info("[TEMPLATE_DELETE] ✓ Template {} supprimé avec succès", idTemplate);
    }

    private TemplateMotifDTO toDTO(GmTemplateMotif template) {
        TemplateMotifDTO dto = new TemplateMotifDTO();
        dto.setIdTemplate(template.getIdTemplate());
        dto.setNomTemplate(template.getNomTemplate());
        dto.setContenuTemplate(template.getContenuTemplate());
        dto.setDateCreation(template.getDateCreation());
        dto.setDateModification(template.getDateModification());
        return dto;
    }
}
