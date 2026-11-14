# DropFlow Hub — plateforme de dropshipping, call center & fulfillment logistique

> Mini-projet AWD 2026-2027 · Architecture microservices (Spring Boot, Spring Cloud) · Classe : `CLASSE` · Groupe : `GROUPE`

## 1. Sujet

DropFlow Hub réunit les intervenants du e-commerce dans un seul environnement et automatise le cycle d’une commande, de la boutique jusqu’à la livraison et au règlement financier.

La plateforme doit permettre de :

- centraliser les **produits, commandes, livraisons, échanges et transactions** ;
- importer automatiquement les commandes depuis les boutiques e-commerce via **Webhooks** ;
- organiser la **qualification téléphonique** des clients par un call center ;
- gérer la **collecte fournisseur, l’emballage, les bordereaux et l’expédition** ;
- automatiser le **paiement à la livraison (COD)**, les commissions et les virements ;
- traiter les **retours, échanges et demandes SAV** ;
- centraliser les journaux d’événements et le support marketing.

Acteurs : **Administrateur**, **Fournisseur**, **E-commerçant**, **Agent de confirmation**, **Agent logistique**, **Agent financier**, **Agent technique / marketing** et **Client final**.

## 2. Membres et responsabilités

|Membre|Microservice|Rôle transverse|
|--|--|--|
|Membre 1|Product & Stock Service (M1)|Catalogue B2B, variantes, réservation de stock|
|Membre 2|Integration Service (M2)|Webhooks, synchronisation boutiques, import commandes|
|Membre 3|Order & Call Center Service (M3)|File d’appels, qualification client, confirmation commande|
|Membre 4|Logistics & Fulfillment Service (M4)|Collecte, emballage, bordereaux, expédition|
|Membre 5|Transaction & Wallet Service (M5)|Commissions, répartition COD, portefeuilles, virements|
|Membre 6|Exchange Service (M6)|Retours, inspections, échanges et réexpédition|
|**Toute l’équipe**|**Support & Logging Service (M7)**|Identity & Access Management, support Ads, journalisation RabbitMQ|

## 3. Architecture

|Composant|Port|Base de données|Rôle|
|--|--:|--|--|
|frontend (React)|3000|–|Interfaces des acteurs de la plateforme|
|api-gateway|8080|–|Point d’entrée unique, validation JWT, routage|
|eureka-server|8761|–|Découverte des services|
|config-server|8888|–|Configuration centralisée|
|keycloak|8180|PostgreSQL|OAuth2 / OIDC, jetons JWT, rôles|
|rabbitmq|5672 (15672)|–|Événements métier asynchrones, files durables, DLT|
|m1-product-service|8081|MongoDB|Catalogue B2B, produits, variantes, réservations|
|m2-integration-service|8082|PostgreSQL|Boutiques, Webhooks, payloads JSONB|
|m3-order-callcenter|8083|MySQL|Commandes, files d’appels, tentatives de confirmation|
|m4-logistics-service|8084|PostgreSQL|Collectes, colis, bordereaux, expéditions|
|m5-transaction-wallet|8085|PostgreSQL|Portefeuilles, règlements COD, commissions|
|m6-exchange-service|8086|MySQL|Demandes d’échange, inspections, SAV|
|m7-support-logging|8087|MongoDB|Tickets marketing, journal d’audit, métriques|

### Communication

- **Synchrone (OpenFeign + Resilience4j)** : vérification de produit / SKU, lecture du prix fournisseur, décrément de stock, contrôle de commande.
- **Asynchrone (RabbitMQ)** : `EXTERNAL_ORDER_IMPORTED`, `ORDER_CONFIRMED`, `ORDER_CANCELLED`, `PARCEL_DELIVERED`, `SHIPMENT_STATUS_UPDATED`, `EXCHANGE_APPROVED`, `PROFIT_CREDITED`, `STOCK_LOW_ALERT`, `WEBHOOK_SYNC_FAILED` et `EXCHANGE_LOGGED`.
- **Échange RabbitMQ** : `dropflow.events.topic`, avec files durables par service, ACK manuel, idempotence et dead-letter queue.

## 4. Lancement

### Prérequis

- Docker Desktop
- Java 17
- Maven 3.9
- Node.js et npm pour le frontend React

### Option A — développement

```bash
cp .env.example .env
docker compose --profile infra up -d
```

```bash
mvn -f Backend/pom.xml package -DskipTests
```

Lancer ensuite, dans cet ordre : `config-server`, `eureka-server`, `api-gateway`, puis les microservices souhaités.

### Option B — démonstration complète

```bash
docker compose --profile all up -d --build
```

### URL utiles

|URL|Contenu|
|--|--|
|`http://localhost:3000`|Frontend React|
|`http://localhost:8080/api/...`|API via l’API Gateway|
|`http://localhost:8761`|Tableau de bord Eureka|
|`http://localhost:8180`|Console Keycloak|
|`http://localhost:15672`|Interface RabbitMQ|
|`http://localhost:8888/m1-product-service/default`|Configuration servie au service M1|

### Comptes de test

|Utilisateur|Mot de passe|Rôle|
|--|--|--|
|`admin1`|`Passw0rd!`|ADMIN|
|`fournisseur1`|`Passw0rd!`|SUPPLIER|
|`marchand1`|`Passw0rd!`|MERCHANT|
|`agent_confirmation`|`Passw0rd!`|CONFIRMATION_AGENT|
|`agent_logistique`|`Passw0rd!`|LOGISTICS_AGENT|
|`agent_financier`|`Passw0rd!`|FINANCE_AGENT|
|`agent_support`|`Passw0rd!`|SUPPORT_AGENT|

> Ces comptes sont destinés uniquement au développement et à la démonstration locale.

## 5. État à la Validation 1

Les projets sont créés et démarrent. Les contrats d’API, le découpage en microservices, les bases de données dédiées, les communications OpenFeign / RabbitMQ et la sécurité JWT sont définis.

|Vérification|Résultat attendu|
|--|--|
|`GET /api/products` sans jeton|`501` ou `401`, selon la configuration|
|`POST /api/webhooks/{storeId}/orders`|`202 Accepted` et payload sauvegardé|
|`PUT /api/orders/{id}/confirm`|Confirmation de commande et décrément de stock|
|`PUT /api/logistics/parcels/{id}/ship`|Remise du colis au transporteur|
|`POST /api/settlements/calculate`|Calcul de la répartition financière COD|
|`POST /api/exchanges`|Création d’une demande d’échange|
|Message RabbitMQ invalide|Retry puis dead-letter queue|

## 6. Arborescence

```text
Backend/
├── eureka-server/
├── api-gateway/
├── config-server/
├── m1-product-service/
├── m2-integration-service/
├── m3-order-callcenter/
├── m4-logistics-service/
├── m5-transaction-wallet/
├── m6-exchange-service/
└── m7-support-logging/

Frontend/
Documentation/
├── Presentation/
├── Diagrams/
└── Fiches/

README.md
```

## 7. Conventions Git

- Branches : `main`, `develop`, `feature/<service>/<sujet>`, `docs/<sujet>` et `fix/<service>/<sujet>`.
- Pull requests vers `develop` avec relecture obligatoire.
- Commits : *Conventional Commits*, par exemple `feat(order): add call queue endpoints`.
- Suivi du projet : GitHub Projects.
- Documentation : diagrammes UML, contrats d’API, flux métier et présentation dans `Documentation/`.
