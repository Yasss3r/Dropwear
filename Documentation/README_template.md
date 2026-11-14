# 🚀 DropFlow Hub — Plateforme Dropshipping & Call Center

<!-- Badges -->
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![React](https://img.shields.io/badge/React-18-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![License](https://img.shields.io/badge/license-ESPRIT%20AWD-lightgrey)

---

## 📋 Description du Projet

> **Module :** Architecture Web Distribuée (AWD) | **Validation 1**
> **École :** ESPRIT — École Supérieure Privée d'Ingénierie et de Technologies
> **Année académique :** 2026 – 2027
> **Classe :** `À COMPLÉTER` | **Groupe :** `À COMPLÉTER`

**DropFlow Hub** est une plateforme complète de **dropshipping**, de **gestion des commandes call center** et de **fulfillment logistique**, construite selon une architecture **microservices** avec Spring Boot et Spring Cloud.

La plateforme permet à des e-commerçants de gérer leurs boutiques en ligne, de suivre les commandes depuis la réception jusqu'à la livraison, et d'assurer le service après-vente, le tout au travers de microservices indépendants communiquant via RabbitMQ et REST (OpenFeign).

---

## 👥 Équipe

| Membre | Microservice Responsable | Pseudo GitHub |
|---|---|---|
| À COMPLÉTER | M1 — Product Service (Produits & Catalogue) | À COMPLÉTER |
| À COMPLÉTER | M2 — Integration Service (Intégration Boutiques) | À COMPLÉTER |
| À COMPLÉTER | M3 — Order / Call Center (Commandes & Qualification) | À COMPLÉTER |
| À COMPLÉTER | M4 — Logistics Service (Logistique & Fulfillment) | À COMPLÉTER |
| À COMPLÉTER | M5 — Transaction Wallet (Transactions COD) | À COMPLÉTER |
| À COMPLÉTER | M6 — Exchange Service (Échanges & SAV) | À COMPLÉTER |
| Toute l'équipe | M7 — Support Logging (Service Commun) | — |

---

## 🏗️ Architecture Globale

> Le diagramme d'architecture est disponible dans [`Documentation/Diagrams/architecture-globale.png`](Documentation/Diagrams/architecture-globale.png)

La plateforme repose sur **7 microservices métier** (M1–M7) orchestrés par un **API Gateway** Spring Cloud, avec découverte de services via **Eureka**, configuration centralisée via **Config Server**, sécurité assurée par **Keycloak** (JWT), et communication asynchrone via **RabbitMQ**.

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Host                          │
│                                                         │
│  [React Frontend]                                       │
│       │                                                 │
│       ▼                                                 │
│  [API Gateway :8080] ◄── [Keycloak :8181] (JWT)        │
│       │                                                 │
│       ├── [Eureka Server :8761]                         │
│       ├── [Config Server :8888]                         │
│       ├── [M1 :8081] ── MongoDB                         │
│       ├── [M2 :8082] ── PostgreSQL  ◄── RabbitMQ        │
│       ├── [M3 :8083] ── MySQL                           │
│       ├── [M4 :8084] ── PostgreSQL                      │
│       ├── [M5 :8085] ── PostgreSQL                      │
│       ├── [M6 :8086] ── MySQL                           │
│       └── [M7 :8087] ── MongoDB    ◄── RabbitMQ (logs) │
│                                                         │
│  [RabbitMQ :5672/:15672]                                │
└─────────────────────────────────────────────────────────┘
```

---

## 🧩 Microservices

| Microservice | Technologie | Base de Données | Port | Description |
|---|---|---|---|---|
| `eureka-server` | Spring Cloud Netflix Eureka | — | 8761 | Registre de services (service discovery) |
| `api-gateway` | Spring Cloud Gateway | — | 8080 | Point d'entrée unique, routage, sécurité JWT |
| `config-server` | Spring Cloud Config | — | 8888 | Configuration centralisée |
| `m1-product-service` | Spring Boot | MongoDB | 8081 | Catalogue produits B2B, gestion du stock |
| `m2-integration-service` | Spring Boot ⭐ Tech Avancée | PostgreSQL (JSONB) | 8082 | Intégration boutiques, webhooks, import commandes |
| `m3-order-callcenter` | Spring Boot | MySQL | 8083 | Gestion des commandes, qualification call center |
| `m4-logistics-service` | Spring Boot | PostgreSQL | 8084 | Logistique, suivi colis, dépôt fulfillment |
| `m5-transaction-wallet` | Spring Boot | PostgreSQL | 8085 | Transactions COD, wallet financier (ACID) |
| `m6-exchange-service` | Spring Boot | MySQL | 8086 | Échanges produits, service après-vente |
| `m7-support-logging` | Spring Boot | MongoDB | 8087 | Logging centralisé, IAM support, coaching |
| `frontend` | React 18 | — | 3000 | Interface utilisateur web |

> ⭐ M2 est le microservice **Technologie Avancée** du groupe.

---

## ⚙️ Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

- **Docker Desktop** ≥ 24.x ([Télécharger](https://www.docker.com/products/docker-desktop/))
- **Docker Compose** ≥ 2.x (inclus avec Docker Desktop)
- *(Pour le développement local uniquement)* **Java 17** (JDK) + **Maven 3.8+** + **Node.js 18+**

---

## 🚀 Lancement du Projet

### 1. Cloner le dépôt

```bash
git clone https://github.com/À-COMPLÉTER/dropflow-hub-<classe>-<groupe>.git
cd dropflow-hub-<classe>-<groupe>
```

### 2. Configurer les variables d'environnement

Copiez le fichier exemple et adaptez-le si nécessaire :

```bash
cp .env.example .env
# Éditez .env si vous souhaitez changer les mots de passe par défaut
```

### 3. Lancer tous les services

```bash
docker-compose up --build
```

> 💡 Premier lancement : environ 3-5 minutes (téléchargement des images Docker)
> Les services démarrent dans l'ordre grâce aux `depends_on` et `healthcheck`.

### 4. Vérifier que tout fonctionne

Attendez que tous les conteneurs soient `healthy`, puis accédez aux URLs ci-dessous.

---

## 🌐 URLs d'Accès

| Service | URL | Description |
|---|---|---|
| **Frontend React** | http://localhost:3000 | Interface utilisateur principale |
| **API Gateway** | http://localhost:8080 | Point d'entrée de toutes les APIs |
| **Eureka Dashboard** | http://localhost:8761 | Tableau de bord de découverte de services |
| **RabbitMQ Management** | http://localhost:15672 | Console RabbitMQ (guest/guest par défaut) |
| **Keycloak Admin** | http://localhost:8181 | Console d'administration Keycloak |
| **Config Server** | http://localhost:8888 | Vérification de la configuration centralisée |
| **M1 Product Service** | http://localhost:8080/api/products | Via Gateway |
| **M2 Integration Service** | http://localhost:8080/api/integrations | Via Gateway |
| **M3 Order / Call Center** | http://localhost:8080/api/orders | Via Gateway |
| **M4 Logistics Service** | http://localhost:8080/api/logistics | Via Gateway |
| **M5 Transaction Wallet** | http://localhost:8080/api/transactions | Via Gateway |
| **M6 Exchange / SAV** | http://localhost:8080/api/exchanges | Via Gateway |
| **M7 Support Logging** | http://localhost:8080/api/logs | Via Gateway |

---

## 🔐 Configuration Keycloak

### Realm & Client

| Paramètre | Valeur |
|---|---|
| **Realm** | `dropflow` |
| **Client ID** | `dropflow-gateway` |
| **Grant Type** | `Authorization Code` / `Client Credentials` |
| **Admin console** | http://localhost:8181/admin |
| **Admin login** | `admin` / `À COMPLÉTER` |

### Rôles définis

| Rôle | Description | Microservices concernés |
|---|---|---|
| `ADMIN` | Administrateur système | Tous |
| `SUPPLIER` | Fournisseur | M1, M2 |
| `MERCHANT` | E-commerçant | M1, M2, M3 |
| `CONFIRMATION_AGENT` | Agent call center | M3 |
| `LOGISTICS_AGENT` | Agent logistique | M4 |
| `FINANCE_AGENT` | Agent financier | M5 |
| `SUPPORT_AGENT` | Agent technique/marketing | M6, M7 |

### Import du Realm

Le fichier d'export du realm est disponible dans :
```
Documentation/keycloak-realm-dropflow.json
```

Pour l'importer manuellement (si non monté automatiquement via Docker) :
1. Accédez à http://localhost:8181/admin
2. Sélectionnez **Create Realm**
3. Importez `keycloak-realm-dropflow.json`

---

## 📊 Flux de Communication

### Synchrone (OpenFeign)

| Appelant | Appelé | Données échangées |
|---|---|---|
| M2 (Integration) → M1 (Product) | Vérification SKU produit |
| M3 (Order) → M1 (Product) | Vérification stock disponible |
| M5 (Transaction) → M1 (Product) | Récupération prix |
| M6 (Exchange) → M3 (Order) | Statut de la commande originale |

### Asynchrone (RabbitMQ — Exchange `dropflow.events`)

| Événement | Émetteur | Consommateur |
|---|---|---|
| `ORDER_IMPORTED` | M2 | M3 |
| `ORDER_CONFIRMED` | M3 | M4 |
| `PARCEL_DELIVERED` | M4 | M5 |
| `LOG_EVENT` | M1–M6 | M7 |

---

## 📁 Structure du Dépôt

```
dropflow-hub-<classe>-<groupe>/
├── Backend/
│   ├── eureka-server/
│   ├── api-gateway/
│   ├── config-server/
│   ├── m1-product-service/
│   ├── m2-integration-service/
│   ├── m3-order-callcenter/
│   ├── m4-logistics-service/
│   ├── m5-transaction-wallet/
│   ├── m6-exchange-service/
│   └── m7-support-logging/
├── Frontend/
├── Documentation/
│   ├── Presentation/
│   │   └── DropFlowHub-Validation1.pptx
│   ├── Diagrams/
│   │   ├── architecture-globale.png
│   │   ├── uml-m1-product.png
│   │   ├── uml-m2-integration.png
│   │   ├── uml-m3-order.png
│   │   ├── uml-m4-logistics.png
│   │   ├── uml-m5-transaction.png
│   │   ├── uml-m6-exchange.png
│   │   ├── uml-m7-logging.png
│   │   ├── seq-webhook-callcenter.png
│   │   ├── seq-logistics-shipment.png
│   │   └── seq-cod-sav.png
│   └── keycloak-realm-dropflow.json
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## 🗺️ Diagrammes

| Diagramme | Fichier | Description |
|---|---|---|
| Architecture globale | [`Documentation/Diagrams/architecture-globale.png`](Documentation/Diagrams/architecture-globale.png) | Vue d'ensemble de tous les services |
| Classe M1 | [`Documentation/Diagrams/uml-m1-product.png`](Documentation/Diagrams/uml-m1-product.png) | Modèle de données Product Service |
| Classe M2 | [`Documentation/Diagrams/uml-m2-integration.png`](Documentation/Diagrams/uml-m2-integration.png) | Modèle de données Integration Service |
| Classe M3 | [`Documentation/Diagrams/uml-m3-order.png`](Documentation/Diagrams/uml-m3-order.png) | Modèle de données Order / Call Center |
| Classe M4 | [`Documentation/Diagrams/uml-m4-logistics.png`](Documentation/Diagrams/uml-m4-logistics.png) | Modèle de données Logistics Service |
| Classe M5 | [`Documentation/Diagrams/uml-m5-transaction.png`](Documentation/Diagrams/uml-m5-transaction.png) | Modèle de données Transaction Wallet |
| Classe M6 | [`Documentation/Diagrams/uml-m6-exchange.png`](Documentation/Diagrams/uml-m6-exchange.png) | Modèle de données Exchange / SAV |
| Classe M7 | [`Documentation/Diagrams/uml-m7-logging.png`](Documentation/Diagrams/uml-m7-logging.png) | Modèle de données Support Logging |
| Séquence 1 | [`Documentation/Diagrams/seq-webhook-callcenter.png`](Documentation/Diagrams/seq-webhook-callcenter.png) | Webhook → Call Center |
| Séquence 2 | [`Documentation/Diagrams/seq-logistics-shipment.png`](Documentation/Diagrams/seq-logistics-shipment.png) | Logistics → Shipment |
| Séquence 3 | [`Documentation/Diagrams/seq-cod-sav.png`](Documentation/Diagrams/seq-cod-sav.png) | COD Settlement + SAV Exchange |

---

## 📌 Gestion de Tâches

Nous utilisons **[GitHub Projects / Jira / Trello — À COMPLÉTER]** pour le suivi des tâches.

🔗 **Lien vers le tableau :** [À COMPLÉTER](https://À-COMPLÉTER)

> *Capture d'écran du tableau (si lien privé) :*
> ![Tableau de gestion des tâches](Documentation/Diagrams/task-board-screenshot.png)

---

## 👨‍🏫 Accès Enseignant

L'enseignant **[Nom de l'enseignant — À COMPLÉTER]** a été ajouté comme **collaborateur** du dépôt GitHub avec les droits de lecture.

> GitHub username de l'enseignant : `À COMPLÉTER`

Pour accéder au projet localement :
```bash
git clone https://github.com/À-COMPLÉTER/dropflow-hub-<classe>-<groupe>.git
cd dropflow-hub-<classe>-<groupe>
docker-compose up --build
# Accéder à http://localhost:3000 (Frontend) et http://localhost:8761 (Eureka)
```

---

## 🛠️ Stack Technique

| Catégorie | Technologies |
|---|---|
| Backend | Java 17, Spring Boot 3.x, Spring Cloud, OpenFeign, Resilience4j |
| Messaging | RabbitMQ (topic exchange, durable queues, ACK manuel, dead-letter queue) |
| Bases de données | MongoDB 6, PostgreSQL 15, MySQL 8 |
| Sécurité | Keycloak 23, JWT (Bearer Token) |
| Infrastructure | Docker, Docker Compose, Spring Cloud Gateway, Eureka, Config Server |
| Frontend | React 18, Axios |
| CI/CD | À COMPLÉTER (GitHub Actions / autre) |

---

## 📅 Planning

| Sprint | Semaine | Objectif |
|---|---|---|
| S1 | S4 | Initialisation du dépôt, structure, configuration Spring Cloud |
| S2 | S5-S6 | Développement M1, M2, M3 (MVP) |
| S3 | S7-S8 | Développement M4, M5, M6, M7 |
| S4 | S9 | Intégration, Docker, tests end-to-end |
| S5 | S10 | Finalisation, documentation, Validation 1 |

---

*README généré pour le projet DropFlow Hub — Validation 1 — Module AWD — ESPRIT 2026-2027*
*Dernière mise à jour : À COMPLÉTER*
