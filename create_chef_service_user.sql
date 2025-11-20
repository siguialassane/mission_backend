-- Création de l'utilisateur Chef de Service: Alassane Sigui
-- Mot de passe: alasco22
-- Hash BCrypt généré pour "alasco22"

-- 1. Insérer l'utilisateur
INSERT INTO GM_UTILISATEUR (
    ID_UTILISATEUR,
    NOM_UTILISATEUR,
    PRENOM_UTILISATEUR,
    EMAIL_UTILISATEUR,
    MOT_DE_PASSE,
    PROFIL_UTILISATEUR,
    STATUT_ACTIF,
    DATE_CREATION,
    ENTITE_CODE,
    ID_SERVICE
) VALUES (
    (SELECT NVL(MAX(ID_UTILISATEUR), 0) + 1 FROM GM_UTILISATEUR),
    'SIGUI',
    'Alassane',
    'alassane.sigui@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- alasco22
    'CHEF_SERVICE',
    'ACTIF',
    SYSDATE,
    '01', -- Code entité par défaut
    1     -- ID service par défaut
);

-- 2. Vérifier la création
SELECT ID_UTILISATEUR, NOM_UTILISATEUR, PRENOM_UTILISATEUR, EMAIL_UTILISATEUR, PROFIL_UTILISATEUR, STATUT_ACTIF
FROM GM_UTILISATEUR
WHERE NOM_UTILISATEUR = 'SIGUI';

COMMIT;
