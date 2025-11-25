# Audit Technique - Backend (Spring Boot)
**Date:** 24 Novembre 2025
**Statut:** À TRAITER APRÈS LA DÉMO (Ne rien supprimer maintenant)

Ce document recense les dettes techniques, duplications et fichiers inutiles identifiés dans le projet `Backend_mission`.

## 1. Duplication des Entités (Risque Élevé)
**Fichiers concernés :**
- `src/.../entity/Utilisateur.java`
- `src/.../entity/GmUtilisateur.java`

**Problème :**
Deux classes Java mappent la même table de base de données (`GM_UTILISATEUR`).
**Risque :** Si on ajoute une colonne en base, on risque de mettre à jour une seule des deux classes, ce qui causera des bugs incohérents selon le service appelé.
**Action future :** Choisir une seule entité (ex: `Utilisateur`) et refactoriser tout le code pour l'utiliser. Supprimer l'autre.

## 2. Duplication des Repositories (Risque Moyen)
**Fichiers concernés :**
- `src/.../repository/UtilisateurRepository.java`
- `src/.../repository/GmUtilisateurRepository.java`
- `src/.../repository/RessourceRepository.java`
- `src/.../repository/GmRessourceRepository.java`

**Problème :**
Conséquence du point 1. On a deux couches d'accès aux données pour les mêmes tables.
**Risque :** La logique métier (ex: "trouver les actifs") peut être implémentée dans l'un et oubliée dans l'autre.
**Action future :** Fusionner les méthodes dans un seul Repository par entité.

## 3. Configuration de Test en Production (Pollution)
**Fichier concerné :**
- `src/.../config/DatabaseConnectionTest.java`

**Problème :**
Ce fichier exécute un test de connexion au démarrage de l'application via `@PostConstruct`.
**Risque :** Ce n'est pas une bonne pratique de garder du code de "debug" dans le dossier `src/main`. Cela ralentit le démarrage et pollue les logs.
**Action future :** Déplacer dans `src/test` ou supprimer une fois la stabilité confirmée.

## 4. Gestion des IDs Négatifs (Fragilité)
**Fichiers concernés :**
- `src/.../config/FixConstraintConfig.java`
- Vues SQL (`V_MG_FRAIS_COMPLET_NEW.sql`)

**Problème :**
On utilise des IDs négatifs pour gérer les ressources (Police/Chauffeur) et on désactive une contrainte de clé étrangère au démarrage pour éviter les erreurs `ORA-02291`.
**Risque :** C'est un "hack" intelligent mais fragile. Si la base est migrée ou si la contrainte est réactivée par un DBA, l'appli plante.
**Action future :** Créer une vraie structure de données pour les ressources (table de liaison propre) pour ne plus dépendre d'IDs fictifs.

## 5. Contrôleurs de Debug (Code Mort)
**Fichier concerné :**
- `src/.../controller/HealthController.java`

**Problème :**
Contrôleur basique souvent utilisé pour vérifier si l'appli tourne.
**Risque :** Aucun risque majeur, mais c'est du code inutile à maintenir. Spring Boot Actuator fait déjà ça mieux.
**Action future :** Supprimer.
