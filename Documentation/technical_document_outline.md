# DropFlow Hub — Plan Détaillé du Dossier Technique (Word / LaTeX)
### Module AWD · Validation 1 (Semaine 4) · Année Universitaire 2026–2027
**Équipe DropFlow Hub — 6 Membres · ESPRIT**

---

## Guide d'Utilisation du Template
Ce document fournit la structure complète et détaillée du **Dossier Technique** exigé pour la Validation 1 sur Blackboard et dans le répertoire `Documentation/`. Il peut être directement transcrit vers Microsoft Word (`.docx`) ou LaTeX (`main.tex`).

---

# TABLE DES MATIÈRES PROPOSÉE

1. **Introduction et Contexte du Projet**
   - 1.1 Contexte métier du dropshipping et du cash-on-delivery (COD)
   - 1.2 Problématique identifiée (fragmentation, ressaisies, litiges)
   - 1.3 Objectifs de la plateforme DropFlow Hub
   - 1.4 Périmètre fonctionnel de la Validation 1

2. **Écosystème Métier et Analyse Fonctionnelle**
   - 2.1 Identification et responsabilités des acteurs
     - 2.1.1 Administrateur Plateforme
     - 2.1.2 Fournisseur B2B
     - 2.1.3 E-commerçant (Marchand)
     - 2.1.4 Agent de Confirmation (Call Center)
     - 2.1.5 Agent Logistique (Entrepôt & Hub)
     - 2.1.6 Agent Financier
     - 2.1.7 Agent Technique & Coaching Marketing
   - 2.2 Diagramme global des cas d'utilisation (UML Use Case)
   - 2.3 Matrice de traçabilité Acteurs ↔ Cas d'utilisation ↔ Microservices ↔ Entités
   - 2.4 Scénario nominal de bout en bout (Du clic publicitaire au virement COD)
   - 2.5 Scénarios alternatifs et gestion des exceptions :
     - 2.5.1 Client injoignable (politique de relance 3 appels max)
     - 2.5.2 Commande annulée lors de la qualification
     - 2.5.3 Rupture de stock lors du ramassage fournisseur
     - 2.5.4 Colis refusé ou livraison échouée par le transporteur
     - 2.5.5 Demande d'échange SAV et réintégration stock

3. **Architecture Métier : Découpage en Microservices (Bounded Contexts)**
   - 3.1 Principes de découpage DDD (Domain-Driven Design)
   - 3.2 Justification de l'indépendance des contextes (non-recouvrement)
   - 3.3 Description détaillée des 7 Microservices :
     - 3.3.1 M1 — Service Produits & Catalogue B2B
     - 3.3.2 M2 — Service Intégration Boutiques (*Microservice à Technologie Avancée*)
     - 3.3.3 M3 — Service Commandes & Call Center
     - 3.3.4 M4 — Service Logistique & Fulfillment (WMS)
     - 3.3.5 M5 — Service Transactions Financières & Portefeuilles COD
     - 3.3.6 M6 — Service Demandes d'Échange & SAV
     - 3.3.7 M7 — Service Commun (Identity & Access Management, Coaching & Audit Logs)

4. **Architecture des Données et Stratégie de Persistance**
   - 4.1 Principe *Database-per-Service* (Zéro base partagée)
   - 4.2 Justification technologique détaillée pour chaque microservice :
     - 4.2.1 M1 : MongoDB (modèle polymorphe, variantes dynamiques de produits)
     - 4.2.2 M2 : PostgreSQL avec JSONB (stockage brut des webhooks, flexibilité Shopify/WooCommerce)
     - 4.2.3 M3 : MySQL (transactions relationnelles, verrous de distribution d'appels)
     - 4.2.4 M4 : PostgreSQL (machine à états d'expédition, intégrité logistique)
     - 4.2.5 M5 : PostgreSQL (propriétés ACID strictes, isolation financière, non-répudiation)
     - 4.2.6 M6 : MySQL (gestion relationnelle des dossiers SAV et inspections)
     - 4.2.7 M7 : MongoDB (ingestion massive en écriture de logs et événements)
   - 4.3 Diagrammes de classes UML entités par microservice

5. **Modèle de Communication Inter-Services**
   - 5.1 Matrice globale des flux synchrones vs asynchrones
   - 5.2 Communications Synchrones REST (Spring Cloud OpenFeign) :
     - 5.2.1 Justification des cas d'usage (besoin de validation immédiate)
     - 5.2.2 Flux M2 ↔ M1 (Vérification existence SKU lors de l'import webhook)
     - 5.2.3 Flux M3 ↔ M1 (Vérification et réservation/décrémentation stock)
     - 5.2.4 Flux M5 ↔ M1 (Récupération prix gros fournisseur pour décompte net)
     - 5.2.5 Flux M6 ↔ M3 (Vérification statut livré de la commande d'origine)
   - 5.3 Communications Asynchrones Événementielles (RabbitMQ) :
     - 5.3.1 Topologie de messagerie : Topic Exchange `dropflow.events`
     - 5.3.2 Événement `ORDER_IMPORTED` (M2 → M3)
     - 5.3.3 Événement `ORDER_CONFIRMED` (M3 → M4, M7)
     - 5.3.4 Événement `PARCEL_DELIVERED` (M4 → M5, M7)
     - 5.3.5 Événement `EXCHANGE_APPROVED` (M6 → M4, M7)
     - 5.3.6 Ingestion des événements d'audit (M1..M6 → M7)
   - 5.4 Diagrammes de séquence UML :
     - 5.4.1 Ingestion Webhook & Confirmation Téléphonique
     - 5.4.2 Collecte, Emballage et Remise Transporteur
     - 5.4.3 Décompte Financier COD et Traitement SAV

6. **Architecture Technique Globale et Infrastructure**
   - 6.1 Diagramme d'architecture complet (Frontend, Gateway, Eureka, Config, Keycloak, Services, BD, RabbitMQ, Docker)
   - 6.2 Service Discovery : Netflix Eureka Server (enregistrement dynamique, heartbeat)
   - 6.3 Passerelle Applicative : Spring Cloud Gateway (routage, rate-limiting, CORS)
   - 6.4 Centralisation de Configuration : Spring Cloud Config Server (profils dev/docker)
   - 6.5 Sécurité et Gestion des Identités :
     - 6.5.1 Rôle de Keycloak (Identity Provider, émission jetons JWT signés RSA-256)
     - 6.5.2 Validation des jetons au niveau API Gateway
     - 6.5.3 Autorisation basée sur les rôles (RBAC) au niveau microservices (`@PreAuthorize`)
     - 6.5.4 Matrice Rôles ↔ Scopes d'accès aux microservices
   - 6.6 Conteneurisation et Orchestration locale :
     - 6.6.1 Structure du `docker-compose.yml`
     - 6.6.2 Dépendances au démarrage (`depends_on` avec `condition: service_healthy`)
     - 6.6.3 Gestion des réseaux (`dropflow-network`) et des volumes de persistance

7. **Résilience et Tolérance aux Pannes**
   - 7.1 Stratégie de Circuit Breaker avec Resilience4j (OpenFeign timeouts et fallbacks)
   - 7.2 Gestion de l'idempotence des messages RabbitMQ (déduplication par message ID)
   - 7.3 Dead Letter Queues (DLQ) et politique de réessai (Exponential Backoff)
   - 7.4 Transactional Outbox Pattern pour la cohérence base locale / message broker

8. **Contrats d'API REST par Microservice**
   - 8.1 Spécifications OpenAPI / Swagger
   - 8.2 Tableau complet des endpoints, verbes HTTP, codes de statut et schémas d'entrée/sortie (M1 à M7)

9. **Organisation du Projet, Démarche Qualité et Gestion des Risques**
   - 9.1 Organisation du dépôt Git (`dropflow-hub-<classe>-<groupe>`)
   - 9.2 Stratégie de branches (GitFlow : `main`, `develop`, `feature/*`)
   - 9.3 Répartition individuelle des tâches (Responsable par microservice + M7 commun)
   - 9.4 Outil de gestion de projet (GitHub Projects, kanban, user stories)
   - 9.5 Analyse et matrice des risques techniques / organisationnels avec plans de mitigation
   - 9.6 Planning prévisionnel d'avancement (Semaines S4 à S10)

10. **Conclusion et Perspectives pour la Validation 2**

---
*Document technique DropFlow Hub — Conforme aux exigences officielles AWD ESPRIT 2026–2027.*
