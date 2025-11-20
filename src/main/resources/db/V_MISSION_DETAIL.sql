-- VUE ORACLE : V_MISSION_DETAIL
-- Cette vue regroupe toutes les informations d'une mission avec participants, étapes et ressources
-- Pour une lecture optimisée (1 seule requête au lieu de 10+)

CREATE OR REPLACE VIEW V_MISSION_DETAIL AS
SELECT 
    -- Infos mission principales
    om.ID_ORDRE_MISSION,
    om.NUMERO_ORDRE_MISSION,
    om.OBJET_ORDRE_MISSION,
    om.DATE_DEBUT_PREVUE_ORDRE_MISSIO AS DATE_DEBUT_MISSION,
    om.DATE_FIN_PREVUE_ORDRE_MISSION AS DATE_FIN_MISSION,
    om.STATUT_ORDRE_MISSION,
    om.DATE_CREATION_ORDRE_MISSION,
    om.ID_NATURE_MISSION,
    om.ENTITE_CODE,
    om.ID_UTILISATEUR_CREATEUR,
    
    -- Enrichissement Nature Mission
    nm.LIBELLE_NATURE_MISSION AS NATURE_MISSION_LIB,
    
    -- Enrichissement Entité
    e.ENTITE_LIB,
    
    -- Enrichissement Service (si présent dans GM_ORDREMISSION plus tard)
    CAST(NULL AS NUMBER) AS ID_SERVICE,
    CAST(NULL AS VARCHAR2(200)) AS SERVICE_LIB,
    
    -- Enrichissement Créateur
    u.NOM_UTILISATEUR || ' ' || u.PRENOM_UTILISATEUR AS CREATEUR_NOM,
    
    -- Participants concaténés (format: idAgent1:role1:nom1:prenom1|idAgent2:role2:nom2:prenom2)
    (
        SELECT LISTAGG(
            p.ID_AGENT || ':' || 
            p.ROLE || ':' || 
            NVL(a.NOM_AGENT, '') || ':' || 
            NVL(a.PRENOM_AGENT, '') || ':' ||
            NVL(a.MATRICULE_AGENT, ''),
            '|'
        ) WITHIN GROUP (ORDER BY p.ROLE DESC, a.NOM_AGENT)
        FROM GM_PARTICIPER p
        LEFT JOIN GM_AGENT a ON p.ID_AGENT = a.ID_AGENT
        WHERE p.ID_ORDRE_MISSION = om.ID_ORDRE_MISSION
    ) AS PARTICIPANTS_DATA,
    
    -- Étapes concaténées (format: idEtape1:ordre1:villeCode1:villeLib1|idEtape2:ordre2:villeCode2:villeLib2)
    (
        SELECT LISTAGG(
            et.ID_ETAPE || ':' || 
            et.ORDRE_ETAPE || ':' || 
            et.VILLE_CODE || ':' || 
            NVL(v.VILLE_LIB, et.VILLE_CODE),
            '|'
        ) WITHIN GROUP (ORDER BY et.ORDRE_ETAPE)
        FROM GM_ETAPE et
        LEFT JOIN AC_VILLE v ON et.VILLE_CODE = v.VILLE_CODE
        WHERE et.ID_ORDRE_MISSION = om.ID_ORDRE_MISSION
    ) AS ETAPES_DATA,
    
    -- Ressources concaténées (format: idRessource1:quantite1:libelle1|idRessource2:quantite2:libelle2)
    (
        SELECT LISTAGG(
            r.ID_RESSOURCE || ':' || 
            NVL(TO_CHAR(ur.QUANTITE), '0') || ':' || 
            NVL(r.LIB_RESSOURCE, ''),
            '|'
        ) WITHIN GROUP (ORDER BY r.LIB_RESSOURCE)
        FROM GM_UTILISER_RESSOUR ur
        LEFT JOIN GM_RESSOURCE r ON ur.ID_RESSOURCE = r.ID_RESSOURCE
        WHERE ur.ID_ORDRE_MISSION = om.ID_ORDRE_MISSION
    ) AS RESSOURCES_DATA
    
FROM GM_ORDREMISSION om
LEFT JOIN GM_NATUREMISSION nm ON om.ID_NATURE_MISSION = nm.ID_NATURE_MISSION
LEFT JOIN AC_ENTITE e ON om.ENTITE_CODE = e.ENTITE_CODE
LEFT JOIN GM_UTILISATEUR u ON om.ID_UTILISATEUR_CREATEUR = u.ID_UTILISATEUR;

-- Commentaire sur la vue
COMMENT ON TABLE V_MISSION_DETAIL IS 'Vue denormalisee pour afficher les details complets d une mission avec participants, etapes et ressources en 1 seule requete';


-- ===============================================
-- CORRECTION DES AUTRES VUES DÉFECTUEUSES
-- ===============================================

-- Vue des utilisateurs actifs
CREATE OR REPLACE VIEW V_UTILISATEUR_ACTIFS AS
SELECT 
    u.ID_UTILISATEUR,
    u.EMAIL_UTILISATEUR AS EMAIL,
    u.NOM_UTILISATEUR,
    u.PRENOM_UTILISATEUR,
    u.PROFIL_UTILISATEUR,
    u.ENTITE_CODE,
    e.ENTITE_LIB,
    u.ID_SERVICE,
    s.LIBELLE_SERVICE
FROM GM_UTILISATEUR u
LEFT JOIN AC_ENTITE e ON u.ENTITE_CODE = e.ENTITE_CODE
LEFT JOIN GM_SERVICE s ON u.ID_SERVICE = s.ID_SERVICE
WHERE u.STATUT_ACTIF = '1';

-- Vue des utilisateurs avec leurs agents
CREATE OR REPLACE VIEW V_UTILISATEUR_AVEC_AGENTS AS
SELECT 
    u.ID_UTILISATEUR,
    u.EMAIL_UTILISATEUR,
    u.NOM_UTILISATEUR,
    u.PRENOM_UTILISATEUR,
    u.PROFIL_UTILISATEUR,
    a.ID_AGENT,
    a.MATRICULE_AGENT,
    a.NOM_AGENT,
    a.PRENOM_AGENT,
    f.LIBELLE_FONCTION
FROM GM_UTILISATEUR u
LEFT JOIN GM_AGENT a ON u.EMAIL_UTILISATEUR = a.EMAIL_AGENT
LEFT JOIN GM_FONCTION f ON a.ID_FONCTION = f.ID_FONCTION
WHERE u.STATUT_ACTIF = '1';
