-- ============================================================================
-- Vue : V_MG_FRAIS_PAR_AGENT
-- Description : Détail des frais calculés par agent pour chaque mission
-- Utilisation : Affichage des tableaux individuels par agent
-- ============================================================================

CREATE OR REPLACE VIEW V_MG_FRAIS_PAR_AGENT AS
SELECT 
    f.ID_ORDRE_MISSION,
    f.ID_AGENT,
    a.NOM_AGENT,
    a.PRENOM_AGENT,
    (a.NOM_AGENT || ' ' || a.PRENOM_AGENT) AS NOM_COMPLET_AGENT,
    a.ID_FONCTION,
    fn.LIB_FONCTION AS LIBELLE_FONCTION,
    f.ID_CATEGORIE_FRAIS,
    cf.LIBELLE_CATEGORIE_FRAIS,
    f.QUANTITE_FRAIS_MISSION AS QUANTITE,
    f.PRIX_UNITAIRE_FRAIS_MISSION AS PRIX_UNITAIRE,
    f.MONTANT_PREVU_FRAIS_MISSION AS MONTANT,
    f.STATUT_VALIDATION_FRAIS_MISSION AS STATUT,
    f.DATE_CRE_FRAIS_MISSION AS DATE_CREATION,
    f.DATE_VALID_FAIS_MISSION AS DATE_VALIDATION
FROM 
    GM_FRAISMISSION f
    INNER JOIN GM_AGENT a ON f.ID_AGENT = a.ID_AGENT
    INNER JOIN GM_FONCTION fn ON a.ID_FONCTION = fn.ID_FONCTION
    INNER JOIN GM_CATEGORIE_FRAIS cf ON f.ID_CATEGORIE_FRAIS = cf.ID_CATEGORIE_FRAIS
WHERE 
    f.MONTANT_PREVU_FRAIS_MISSION > 0
ORDER BY 
    f.ID_ORDRE_MISSION,
    a.NOM_AGENT,
    cf.ORDRE_AFFICHAGE;

-- Index pour améliorer les performances
CREATE INDEX IDX_V_FRAIS_AGENT_MISSION ON GM_FRAISMISSION(ID_ORDRE_MISSION, ID_AGENT);

-- Commentaires
COMMENT ON TABLE V_MG_FRAIS_PAR_AGENT IS 'Vue détaillée des frais par agent et par catégorie pour chaque mission';
