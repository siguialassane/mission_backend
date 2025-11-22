-- ============================================================================
-- Fonction : FN_MG_CALCULER_FRAIS
-- Description : Calcule automatiquement les frais d'une mission selon le barème
-- Paramètres : p_id_mission - ID de la mission
-- Retour : Nombre de lignes de frais créées
-- ============================================================================

CREATE OR REPLACE FUNCTION FN_MG_CALCULER_FRAIS(p_id_mission IN NUMBER)
RETURN NUMBER
IS
    v_count NUMBER := 0;
    v_duree_jours NUMBER;
    v_duree_nuits NUMBER;
    v_montant_repas NUMBER;
    v_montant_hebergement NUMBER;
    v_montant_indemnite NUMBER;
    v_montant_carburant NUMBER;
    v_total_ligne NUMBER;
BEGIN
    -- Supprimer les anciens frais si existants
    DELETE FROM GM_FRAISMISSION WHERE ID_ORDRE_MISSION = p_id_mission;
    
    -- Calculer la durée de la mission
    SELECT 
        (DATE_FIN_PREVUE_ORDRE_MISSION - DATE_DEBUT_PREVUE_ORDRE_MISSION + 1),
        (DATE_FIN_PREVUE_ORDRE_MISSION - DATE_DEBUT_PREVUE_ORDRE_MISSION)
    INTO v_duree_jours, v_duree_nuits
    FROM GM_ORDREMISSION
    WHERE ID_ORDRE_MISSION = p_id_mission;
    
    -- Boucle sur chaque participant
    FOR participant IN (
        SELECT p.ID_AGENT, a.ID_FONCTION
        FROM GM_PARTICIPER p
        INNER JOIN GM_AGENT a ON p.ID_AGENT = a.ID_AGENT
        WHERE p.ID_ORDRE_MISSION = p_id_mission
    ) LOOP
        
        -- 1. REPAS (ID_CATEGORIE_FRAIS = 1)
        BEGIN
            SELECT MONTANT_UNITAIRE INTO v_montant_repas
            FROM GM_BAREME
            WHERE ID_FONCTION = participant.ID_FONCTION
            AND ID_CATEGORIE_FRAIS = 1;
            
            IF v_montant_repas > 0 THEN
                v_total_ligne := v_montant_repas * v_duree_jours;
                
                INSERT INTO GM_FRAISMISSION (
                    ID_ORDRE_MISSION, ID_AGENT, ID_CATEGORIE_FRAIS,
                    QUANTITE_FRAIS_MISSION, PRIX_UNITAIRE_FRAIS_MISSION,
                    MONTANT_PREVU_FRAIS_MISSION, STATUT_VALIDATION_FRAIS_MISSION,
                    DATE_CRE_FRAIS_MISSION
                ) VALUES (
                    p_id_mission, participant.ID_AGENT, 1,
                    v_duree_jours, v_montant_repas, v_total_ligne,
                    'CALCULE', SYSDATE
                );
                v_count := v_count + 1;
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN NULL;
        END;
        
        -- 2. HÉBERGEMENT (ID_CATEGORIE_FRAIS = 2)
        IF v_duree_nuits > 0 THEN
            BEGIN
                SELECT MONTANT_UNITAIRE INTO v_montant_hebergement
                FROM GM_BAREME
                WHERE ID_FONCTION = participant.ID_FONCTION
                AND ID_CATEGORIE_FRAIS = 2;
                
                IF v_montant_hebergement > 0 THEN
                    v_total_ligne := v_montant_hebergement * v_duree_nuits;
                    
                    INSERT INTO GM_FRAISMISSION (
                        ID_ORDRE_MISSION, ID_AGENT, ID_CATEGORIE_FRAIS,
                        QUANTITE_FRAIS_MISSION, PRIX_UNITAIRE_FRAIS_MISSION,
                        MONTANT_PREVU_FRAIS_MISSION, STATUT_VALIDATION_FRAIS_MISSION,
                        DATE_CRE_FRAIS_MISSION
                    ) VALUES (
                        p_id_mission, participant.ID_AGENT, 2,
                        v_duree_nuits, v_montant_hebergement, v_total_ligne,
                        'CALCULE', SYSDATE
                    );
                    v_count := v_count + 1;
                END IF;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN NULL;
            END;
        END IF;
        
        -- 3. INDEMNITÉ (ID_CATEGORIE_FRAIS = 3)
        BEGIN
            SELECT MONTANT_UNITAIRE INTO v_montant_indemnite
            FROM GM_BAREME
            WHERE ID_FONCTION = participant.ID_FONCTION
            AND ID_CATEGORIE_FRAIS = 3;
            
            IF v_montant_indemnite > 0 THEN
                v_total_ligne := v_montant_indemnite * v_duree_jours;
                
                INSERT INTO GM_FRAISMISSION (
                    ID_ORDRE_MISSION, ID_AGENT, ID_CATEGORIE_FRAIS,
                    QUANTITE_FRAIS_MISSION, PRIX_UNITAIRE_FRAIS_MISSION,
                    MONTANT_PREVU_FRAIS_MISSION, STATUT_VALIDATION_FRAIS_MISSION,
                    DATE_CRE_FRAIS_MISSION
                ) VALUES (
                    p_id_mission, participant.ID_AGENT, 3,
                    v_duree_jours, v_montant_indemnite, v_total_ligne,
                    'CALCULE', SYSDATE
                );
                v_count := v_count + 1;
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN NULL;
        END;
        
        -- 4. CARBURANT (ID_CATEGORIE_FRAIS = 4) - Uniquement pour ID_FONCTION = 6 (Chauffeur)
        IF participant.ID_FONCTION = 6 THEN
            BEGIN
                SELECT MONTANT_UNITAIRE INTO v_montant_carburant
                FROM GM_BAREME
                WHERE ID_FONCTION = 6
                AND ID_CATEGORIE_FRAIS = 4;
                
                IF v_montant_carburant > 0 THEN
                    INSERT INTO GM_FRAISMISSION (
                        ID_ORDRE_MISSION, ID_AGENT, ID_CATEGORIE_FRAIS,
                        QUANTITE_FRAIS_MISSION, PRIX_UNITAIRE_FRAIS_MISSION,
                        MONTANT_PREVU_FRAIS_MISSION, STATUT_VALIDATION_FRAIS_MISSION,
                        DATE_CRE_FRAIS_MISSION
                    ) VALUES (
                        p_id_mission, participant.ID_AGENT, 4,
                        1, v_montant_carburant, v_montant_carburant,
                        'CALCULE', SYSDATE
                    );
                    v_count := v_count + 1;
                END IF;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN NULL;
            END;
        END IF;
        
    END LOOP;
    
    COMMIT;
    RETURN v_count;
    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20001, 'Erreur calcul frais: ' || SQLERRM);
END FN_MG_CALCULER_FRAIS;
/

-- Test de la fonction
-- SELECT FN_MG_CALCULER_FRAIS(4) FROM DUAL;
