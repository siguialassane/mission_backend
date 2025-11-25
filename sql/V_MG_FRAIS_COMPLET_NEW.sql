CREATE OR REPLACE VIEW V_MG_FRAIS_COMPLET_NEW AS
SELECT 
    f.ID_FRAIS_MISSION AS ID_FRAIS,
    f.ID_ORDRE_MISSION AS ID_MISSION,
    f.ID_AGENT AS ID_PARTICIPANT,
    -- Détermination du Nom Complet
    CASE 
        WHEN f.ID_AGENT > 0 THEN a.NOM_AGENT || ' ' || a.PRENOM_AGENT
        ELSE r.LIB_RESSOURCE || ' (' || 
            CASE r.ID_TYPE_RESSOURCE 
                WHEN 2 THEN 'Chauffeur' 
                WHEN 3 THEN 'Police' 
                ELSE 'Autre' 
            END || ')'
    END AS NOM_COMPLET,
    -- Détermination de la Fonction
    CASE 
        WHEN f.ID_AGENT > 0 THEN fn.LIB_FONCTION
        ELSE 
            CASE r.ID_TYPE_RESSOURCE 
                WHEN 2 THEN 'Chauffeur' 
                WHEN 3 THEN 'Police' 
                ELSE 'Autre' 
            END
    END AS LIB_FONCTION,
    -- Détails Frais
    f.ID_CATEGORIE_FRAIS,
    c.LIBELLE_CATEGORIE_FRAIS AS LIB_CATEGORIE,
    f.QUANTITE__FRAIS_MISSION AS QUANTITE,
    f.PRIX_UNITAIRE__FRAIS_MISSION AS PRIX_UNITAIRE,
    f.MONTANT_PREVU__FRAIS_MISSION AS MONTANT_TOTAL
FROM GM_FRAISMISSION f
-- Jointure pour les Agents (ID > 0)
LEFT JOIN GM_AGENT a ON f.ID_AGENT = a.ID_AGENT AND f.ID_AGENT > 0
LEFT JOIN GM_FONCTION fn ON a.ID_FONCTION = fn.ID_FONCTION
-- Jointure pour les Ressources (ID < 0, on inverse le signe pour joindre)
LEFT JOIN GM_RESSOURCE r ON f.ID_AGENT = -r.ID_RESSOURCE AND f.ID_AGENT < 0
-- Jointure pour la catégorie de frais
LEFT JOIN GM_CATEGORIEFRAIS c ON f.ID_CATEGORIE_FRAIS = c.ID_CATEGORIE_FRAIS;
