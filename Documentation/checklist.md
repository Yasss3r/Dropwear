# ✅ Checklist de Validation — DropFlow Hub (AWD ESPRIT)
> **Validation 1** | Module AWD | ESPRIT 2026-2027
> Groupe : `dropflow-hub-<classe>-<groupe>` | Remplir les cases ✅/❌ lors de la revue

---

## 1. 🗂️ Structure du Dépôt Git

| # | Élément à vérifier | Statut |
|---|---|---|
| 1.1 | Le dépôt est nommé exactement `dropflow-hub-<classe>-<groupe>` (minuscules, tirets) | [ ] |
| 1.2 | Le dossier racine contient `Backend/`, `Frontend/`, `Documentation/`, `README.md` | [ ] |
| 1.3 | `Backend/` contient `eureka-server/`, `api-gateway/`, `config-server/` | [ ] |
| 1.4 | `Backend/` contient `m1-product-service/` à `m7-support-logging/` (noms exacts) | [ ] |
| 1.5 | `Frontend/` présent à la racine du dépôt | [ ] |
| 1.6 | `Documentation/Presentation/` présent | [ ] |
| 1.7 | `Documentation/Diagrams/` présent | [ ] |
| 1.8 | Aucun dossier `src/` ou `main/` directement à la racine (structure respectée) | [ ] |

---

## 2. 🔧 Microservices Backend

### Infrastructure Spring Cloud

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.1 | `eureka-server/` : compile (`mvn package -DskipTests` réussit) | [ ] |
| 2.2 | `eureka-server/` : démarre correctement (port 8761 par défaut) | [ ] |
| 2.3 | `eureka-server/` : possède `pom.xml` avec dépendance `spring-cloud-starter-netflix-eureka-server` | [ ] |
| 2.4 | `eureka-server/` : possède `application.yml` ou `application.properties` | [ ] |
| 2.5 | `api-gateway/` : compile et démarre | [ ] |
| 2.6 | `api-gateway/` : routes configurées vers les microservices | [ ] |
| 2.7 | `api-gateway/` : intégration Keycloak / JWT visible dans la config | [ ] |
| 2.8 | `api-gateway/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.9 | `config-server/` : compile et démarre | [ ] |
| 2.10 | `config-server/` : possède `pom.xml` et `application.yml` | [ ] |

### M1 — Product Service (MongoDB)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.11 | `m1-product-service/` : compile sans erreur | [ ] |
| 2.12 | `m1-product-service/` : démarre et se connecte à MongoDB | [ ] |
| 2.13 | `m1-product-service/` : possède `pom.xml` (dépendance `spring-boot-starter-data-mongodb`) | [ ] |
| 2.14 | `m1-product-service/` : possède `application.yml` avec config MongoDB | [ ] |
| 2.15 | `m1-product-service/` : endpoints REST présents (produits, catalogue, stock) | [ ] |
| 2.16 | `m1-product-service/` : s'enregistre sur Eureka | [ ] |

### M2 — Integration Service (PostgreSQL — Technologie Avancée)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.17 | `m2-integration-service/` : compile sans erreur | [ ] |
| 2.18 | `m2-integration-service/` : démarre et se connecte à PostgreSQL | [ ] |
| 2.19 | `m2-integration-service/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.20 | `m2-integration-service/` : **technologie avancée** identifiée (ex. Webhooks, JSONB, OpenFeign avancé, event sourcing…) | [ ] |
| 2.21 | `m2-integration-service/` : endpoints REST d'intégration boutiques présents | [ ] |
| 2.22 | `m2-integration-service/` : publie sur RabbitMQ (`ORDER_IMPORTED`) | [ ] |

### M3 — Order / Call Center (MySQL)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.23 | `m3-order-callcenter/` : compile et démarre | [ ] |
| 2.24 | `m3-order-callcenter/` : se connecte à MySQL | [ ] |
| 2.25 | `m3-order-callcenter/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.26 | `m3-order-callcenter/` : endpoints de gestion des commandes présents | [ ] |
| 2.27 | `m3-order-callcenter/` : consomme `ORDER_IMPORTED`, publie `ORDER_CONFIRMED` | [ ] |

### M4 — Logistics Service (PostgreSQL)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.28 | `m4-logistics-service/` : compile et démarre | [ ] |
| 2.29 | `m4-logistics-service/` : se connecte à PostgreSQL | [ ] |
| 2.30 | `m4-logistics-service/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.31 | `m4-logistics-service/` : gestion des colis / livraisons présente | [ ] |
| 2.32 | `m4-logistics-service/` : consomme `ORDER_CONFIRMED`, publie `PARCEL_DELIVERED` | [ ] |

### M5 — Transaction / Wallet (PostgreSQL)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.33 | `m5-transaction-wallet/` : compile et démarre | [ ] |
| 2.34 | `m5-transaction-wallet/` : se connecte à PostgreSQL | [ ] |
| 2.35 | `m5-transaction-wallet/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.36 | `m5-transaction-wallet/` : gestion des transactions COD présente | [ ] |
| 2.37 | `m5-transaction-wallet/` : consomme `PARCEL_DELIVERED` | [ ] |

### M6 — Exchange / SAV (MySQL)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.38 | `m6-exchange-service/` : compile et démarre | [ ] |
| 2.39 | `m6-exchange-service/` : se connecte à MySQL | [ ] |
| 2.40 | `m6-exchange-service/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.41 | `m6-exchange-service/` : gestion des échanges et SAV présente | [ ] |

### M7 — Support / Logging (MongoDB)

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.42 | `m7-support-logging/` : compile et démarre | [ ] |
| 2.43 | `m7-support-logging/` : se connecte à MongoDB | [ ] |
| 2.44 | `m7-support-logging/` : possède `pom.xml` et `application.yml` | [ ] |
| 2.45 | `m7-support-logging/` : reçoit les logs de tous les autres microservices via RabbitMQ | [ ] |

### Vérification inter-services

| # | Élément à vérifier | Statut |
|---|---|---|
| 2.46 | Chaque microservice possède son **propre schéma/base de données** (aucune BD partagée) | [ ] |
| 2.47 | Chaque microservice s'enregistre sur Eureka | [ ] |
| 2.48 | Les appels OpenFeign synchrones sont présents (M2↔M1, M3↔M1, M5↔M1, M6↔M3) | [ ] |
| 2.49 | La configuration RabbitMQ (exchange `dropflow.events`, queues durables) est présente | [ ] |
| 2.50 | Circuit breaker / retry (Resilience4j) présent sur au moins un appel synchrone | [ ] |

---

## 3. 🌐 Frontend (React)

| # | Élément à vérifier | Statut |
|---|---|---|
| 3.1 | Dossier `Frontend/` contient un projet React (présence de `package.json`) | [ ] |
| 3.2 | `npm install` s'exécute sans erreur fatale | [ ] |
| 3.3 | `npm start` ou `npm run dev` démarre l'application | [ ] |
| 3.4 | L'application est intégrée au `docker-compose.yml` | [ ] |
| 3.5 | Le frontend communique avec l'API Gateway (URL configurable) | [ ] |

---

## 4. 📊 Documentation / Présentation

| # | Élément à vérifier | Statut |
|---|---|---|
| 4.1 | `Documentation/Presentation/` contient le fichier PPTX (présentation Validation 1) | [ ] |
| 4.2 | La présentation comporte bien les slides équipe (slides 03-05 ou équivalent) | [ ] |
| 4.3 | La présentation comporte les slides individuelles (4 slides par membre = 24 slides) | [ ] |
| 4.4 | La présentation est en version finale (pas un brouillon) | [ ] |
| 4.5 | Le titre inclut le nom du module AWD, l'école ESPRIT, l'année 2026-2027 | [ ] |

---

## 5. 🗺️ Documentation / Diagrammes

| # | Élément à vérifier | Statut |
|---|---|---|
| 5.1 | `Documentation/Diagrams/` contient le **diagramme d'architecture global** (PNG ou SVG) | [ ] |
| 5.2 | Le diagramme d'architecture montre : Frontend, API Gateway, Eureka, Config Server, Keycloak, tous les microservices (M1-M7), leurs BDs, RabbitMQ, Docker | [ ] |
| 5.3 | Diagramme de classes UML pour **M1** présent | [ ] |
| 5.4 | Diagramme de classes UML pour **M2** présent | [ ] |
| 5.5 | Diagramme de classes UML pour **M3** présent | [ ] |
| 5.6 | Diagramme de classes UML pour **M4** présent | [ ] |
| 5.7 | Diagramme de classes UML pour **M5** présent | [ ] |
| 5.8 | Diagramme de classes UML pour **M6** présent | [ ] |
| 5.9 | Diagramme de classes UML pour **M7** présent | [ ] |
| 5.10 | **Diagramme de séquence** : Webhook + Call Center (M2→M3) présent | [ ] |
| 5.11 | **Diagramme de séquence** : Logistics + Shipment (M3→M4) présent | [ ] |
| 5.12 | **Diagramme de séquence** : COD Settlement + SAV Exchange (M4→M5→M6) présent | [ ] |
| 5.13 | **Diagramme d'activité** : scénario métier complet (cycle de vie d'une commande) présent | [ ] |
| 5.14 | **Diagramme de cas d'utilisation** présent | [ ] |

---

## 6. 📄 README.md

| # | Élément à vérifier | Statut |
|---|---|---|
| 6.1 | `README.md` présent **à la racine** du dépôt | [ ] |
| 6.2 | Titre du projet, module, école, année académique clairement indiqués | [ ] |
| 6.3 | **Tableau des membres** (nom, MS responsable, pseudo GitHub) présent et complet | [ ] |
| 6.4 | **Description de l'architecture** avec lien vers le diagramme | [ ] |
| 6.5 | **Tableau des microservices** (MS, technologie, BD, port, description) présent | [ ] |
| 6.6 | **Instructions d'installation** (prérequis : Docker, Java 17) présentes | [ ] |
| 6.7 | Commande `docker-compose up --build` documentée | [ ] |
| 6.8 | **Tableau des URLs d'accès** (service, URL, description) présent | [ ] |
| 6.9 | **Configuration Keycloak** (realm, client, rôles) documentée | [ ] |
| 6.10 | **Liens vers les diagrammes** (architecture, UML, séquences) présents | [ ] |
| 6.11 | **Lien ou capture** de l'outil de gestion de tâches présent | [ ] |
| 6.12 | Note sur l'accès enseignant (collaborateur GitHub) présente | [ ] |
| 6.13 | Aucun mot de passe ou secret en clair dans le README | [ ] |

---

## 7. 🔀 Hygiène Git

| # | Élément à vérifier | Statut |
|---|---|---|
| 7.1 | Au moins **1 commit significatif par membre** (6 membres = 6 comptes différents dans l'historique) | [ ] |
| 7.2 | L'enseignant est ajouté comme **collaborateur** du dépôt GitHub | [ ] |
| 7.3 | Des **branches de fonctionnalités** sont visibles (ex. `feature/m1-product`, `feature/m3-order`) | [ ] |
| 7.4 | La branche principale (`main` ou `master`) est protégée ou bien organisée | [ ] |
| 7.5 | **Aucun mot de passe, clé secrète, ou credential** en dur dans le code ou les fichiers de config | [ ] |
| 7.6 | Fichier `.gitignore` présent et approprié (exclut `target/`, `node_modules/`, `.env`, etc.) | [ ] |
| 7.7 | Les fichiers `.env.example` ou variables d'environnement documentées dans le README | [ ] |
| 7.8 | Les messages de commit sont descriptifs (pas uniquement "fix", "update") | [ ] |

---

## 8. 🛠️ Outil de Gestion de Tâches

| # | Élément à vérifier | Statut |
|---|---|---|
| 8.1 | Un outil est utilisé : **GitHub Projects**, **Jira**, **Trello** ou équivalent | [ ] |
| 8.2 | Le lien vers le tableau est présent dans le README | [ ] |
| 8.3 | Une capture d'écran est présente dans le README OU le tableau est public/accessible | [ ] |
| 8.4 | Les tâches sont réparties entre les 6 membres | [ ] |
| 8.5 | Les tâches reflètent l'avancement réel du projet (pas vide ou factice) | [ ] |

---

## 9. 🐳 Docker

| # | Élément à vérifier | Statut |
|---|---|---|
| 9.1 | `docker-compose.yml` présent **à la racine** du dépôt | [ ] |
| 9.2 | Tous les microservices (M1-M7 + Gateway + Eureka + Config) sont définis dans `docker-compose.yml` | [ ] |
| 9.3 | Le Frontend (React) est défini dans `docker-compose.yml` | [ ] |
| 9.4 | Toutes les bases de données (MongoDB ×2, PostgreSQL ×3, MySQL ×2) sont définies | [ ] |
| 9.5 | RabbitMQ est défini dans `docker-compose.yml` | [ ] |
| 9.6 | Keycloak est défini dans `docker-compose.yml` | [ ] |
| 9.7 | Les réseaux Docker sont correctement configurés (tous les services sur le même réseau) | [ ] |
| 9.8 | Les **ports** sont exposés et documentés (aucun conflit) | [ ] |
| 9.9 | Les **volumes** sont définis pour la persistance des BDs | [ ] |
| 9.10 | Les **variables d'environnement** sont utilisées (pas de secrets en dur) | [ ] |
| 9.11 | La commande `docker-compose up --build` fonctionne sans intervention manuelle | [ ] |
| 9.12 | Chaque microservice a un `Dockerfile` (ou utilise une image définie dans le compose) | [ ] |

---

## 10. 🔐 Configuration Keycloak

| # | Élément à vérifier | Statut |
|---|---|---|
| 10.1 | Un fichier d'export du realm Keycloak (JSON) est présent dans `Documentation/` ou monté via `docker-compose.yml` | [ ] |
| 10.2 | Le realm `dropflow` (ou équivalent) est configuré avec les **rôles** : `ADMIN`, `SUPPLIER`, `MERCHANT`, `CONFIRMATION_AGENT`, `LOGISTICS_AGENT`, `FINANCE_AGENT`, `SUPPORT_AGENT` | [ ] |
| 10.3 | Un client Keycloak pour l'API Gateway est défini | [ ] |
| 10.4 | La validation JWT est configurée dans l'API Gateway | [ ] |
| 10.5 | La configuration Keycloak est documentée dans le README | [ ] |
| 10.6 | Le flow d'authentification est décrit (émission JWT → Gateway → microservices) | [ ] |

---

## 📊 Résumé de Validation

| Catégorie | Total items | ✅ Validés | ❌ Manquants |
|---|---|---|---|
| 1. Structure Git | 8 | | |
| 2. Backend Microservices | 40 | | |
| 3. Frontend | 5 | | |
| 4. Présentation | 5 | | |
| 5. Diagrammes | 14 | | |
| 6. README.md | 13 | | |
| 7. Hygiène Git | 8 | | |
| 8. Gestion de tâches | 5 | | |
| 9. Docker | 12 | | |
| 10. Keycloak | 6 | | |
| **TOTAL** | **116** | | |

---

> *Checklist générée pour la Validation 1 — Module AWD — ESPRIT 2026-2027*
> *À compléter par l'équipe avant la soutenance.*
