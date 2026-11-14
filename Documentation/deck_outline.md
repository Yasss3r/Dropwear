# DropFlow Hub – Validation 1 | Plan de présentation restructuré (18 slides)
> **Durée cible :** 10 minutes | **Format :** équipe (slides 1-8, 15-18) + individuel (slides 9-14, ~1 min chacun)

---

## Slide 1 – TITRE : DropFlow Hub

**Contenu :**
- Titre principal : **DropFlow Hub**
- Sous-titre : *Plateforme Microservices de Dropshipping & Fulfillment*
- Module : Architecture Web Distribuée (AWD) – ESPRIT
- Année académique : 2026-2027
- Validation : **Validation 1**
- Membres de l'équipe : [Prénom Nom ×6] — Groupe : [X]
- Logo ESPRIT en haut à droite

**Notes présentateur :**
> Bonjour à tous. Nous vous présentons DropFlow Hub, notre plateforme microservices conçue pour unifier la gestion du dropshipping, du call center et du fulfillment en un seul écosystème distribué. Notre équipe est composée de six membres, chacun responsable d'un microservice dédié.

**Transition :** « Commençons par comprendre le problème métier que nous résolvons. »

---

## Slide 2 – PROBLÉMATIQUE & ACTEURS

**Contenu :**
- **Problème** : La gestion e-commerce actuelle est **fragmentée** — outils séparés pour la boutique, le call center, la logistique et les finances, entraînant des pertes de données, des délais et des erreurs de stock.
- **Notre réponse** : Une plateforme microservices unifiée avec 7 acteurs interconnectés.
- **Diagramme des 7 acteurs** (à inclure visuellement) :

| Acteur | Rôle |
|--------|------|
| Administrateur | Gestion globale de la plateforme |
| Fournisseur | Alimentation du catalogue produits |
| E-commerçant | Gestion de boutiques et campagnes |
| Agent Confirmation | Validation des commandes client |
| Agent Logistique | Préparation & expédition des colis |
| Agent Financier | Gestion des transactions COD |
| Agent Technique/Marketing | Intégration boutiques & analytics |

**Notes présentateur :**
> Le marché du e-commerce tunisien souffre d'une absence d'outil centralisé : les e-commerçants jonglent entre leur boutique Shopify, un Excel pour la logistique et WhatsApp pour le call center. DropFlow Hub adresse directement ce problème avec une architecture événementielle qui connecte tous ces acteurs en temps réel.

**Transition :** « Ces acteurs interagissent via un ensemble de fonctionnalités que nous allons maintenant décrire. »

---

## Slide 3 – FONCTIONNALITÉS & DIAGRAMME DE CAS D'UTILISATION

**Contenu :**
- **Diagramme de cas d'utilisation simplifié** (image UML, version épurée)
- Regroupement par acteur :
  - *Administrateur* → Gérer utilisateurs, configurer la plateforme, consulter logs
  - *Fournisseur* → Publier produits, mettre à jour stock/prix
  - *E-commerçant* → Connecter boutique, créer campagne, suivre commandes
  - *Agent Confirmation* → Confirmer/annuler commandes, contacter client
  - *Agent Logistique* → Préparer colis, enregistrer expédition, gérer dépôt
  - *Agent Financier* → Encaisser COD, réconcilier transactions, gérer wallet
  - *Agent Support* → Traiter échanges/retours, créer tickets SAV

**Notes présentateur :**
> Le diagramme de cas d'utilisation révèle la richesse fonctionnelle de la plateforme tout en maintenant une séparation claire des responsabilités. Chaque acteur dispose de son propre périmètre d'action, ce qui justifie notre découpage en microservices.

**Transition :** « Pour rendre cela concret, prenons un scénario réel : une campagne TikTok Ads. »

---

## Slide 4 – SCÉNARIO MÉTIER & CAS ALTERNATIFS

**Contenu :**
- **Scénario principal : Campagne TikTok Ads** *(flux complet)*
  1. E-commerçant lance une campagne → 100 vues produit (montres)
  2. 80 commandes importées via webhook (M2)
  3. Agents confirmation traitent les 80 commandes (M3)
  4. 60 confirmées → M4 prépare les colis
  5. M5 encaisse le COD à la livraison
  6. 5 retours → M6 ouvre tickets SAV
  7. Toutes les actions loguées dans M7

- **Tableau des cas alternatifs :**

| Cas alternatif | Déclencheur | Comportement système |
|----------------|------------|----------------------|
| Client injoignable | 3 tentatives échouées | Commande mise en attente, notification e-commerçant |
| Commande annulée | Client refuse à la livraison | M5 annule transaction, M4 met à jour stock |
| Fournisseur manquant | SKU introuvable | M1 déclenche alerte, M3 bloque confirmation |
| Remboursement | Retour validé | M6 notifie M5 via RabbitMQ → remboursement wallet |

**Notes présentateur :**
> Ce scénario illustre l'intégralité du cycle de vie d'une commande. Les cas alternatifs sont particulièrement importants pour démontrer la robustesse de notre architecture événementielle : un événement RabbitMQ déclenche le comportement approprié dans le microservice concerné, sans couplage fort.

**Transition :** « Voyons maintenant comment nous avons découpé ce système en microservices. »

---

## Slide 5 – DÉCOUPAGE EN MICROSERVICES

**Contenu :**
- **Principe** : Un microservice = un bounded context métier = une équipe = un dépôt
- **Diagramme visuel** (hexagones ou blocs colorés M1→M7) :

| ID | Microservice | Domaine métier | Port |
|----|-------------|----------------|------|
| M1 | Produits & Catalogue | Catalogue B2B, Stock | 8081 |
| M2 | Intégration Boutiques | Webhooks, Synchronisation | 8082 |
| M3 | Commandes & Call Center | Qualification, Confirmation | 8083 |
| M4 | Logistique & Fulfillment | Expédition, Dépôt | 8084 |
| M5 | Transactions COD | Finances, Wallet | 8085 |
| M6 | Échanges & SAV | Support, Retours | 8086 |
| M7 | Service Commun | IAM, Logging centralisé | 8087 |

- **Microservice avancé** : M2 – *Intégration Boutiques* (webhooks multi-plateformes : Shopify, WooCommerce, TikTok Shop)
- Zéro chevauchement de responsabilités — validé par la matrice acteurs-entités

**Notes présentateur :**
> Notre découpage respecte strictement les principes DDD : chaque bounded context possède ses propres entités, sa propre base de données, et communique via des événements. M2 est notre microservice techniquement avancé : il implémente un système de webhooks universel capable d'ingérer des commandes depuis n'importe quelle boutique e-commerce.

**Transition :** « Chaque microservice a sa propre base de données. Voici comment nous avons fait ce choix et comment ils communiquent. »

---

## Slide 6 – BASES DE DONNÉES & COMMUNICATIONS

**Contenu :**
**Tableau des bases de données :**

| Microservice | DB | Justification |
|-------------|-----|---------------|
| M1 – Produits | MongoDB | Schéma flexible pour attributs produits variables |
| M2 – Intégration | PostgreSQL + JSONB | Stockage semi-structuré des payloads webhook |
| M3 – Commandes | MySQL | Relations fortes commande-client-produit, transactions |
| M4 – Logistique | PostgreSQL | Gestion des états d'expédition (machine à états) |
| M5 – Transactions | PostgreSQL | ACID obligatoire pour toutes les opérations financières |
| M6 – SAV | MySQL | Données structurées tickets + échanges |
| M7 – Commun | MongoDB | Logs structurés haute volumétrie (time-series) |

**Communications :**
- **Synchrone (OpenFeign)** : M2↔M1 (validation SKU), M3↔M1 (vérif. stock), M5↔M1 (prix), M6↔M3 (statut commande)
- **Asynchrone (RabbitMQ)** : Topic exchange `dropflow.events` | Queues durables | ACK manuel | Dead-Letter Queue
  - M2 → M3 : `ORDER_IMPORTED`
  - M3 → M4 : `ORDER_CONFIRMED`
  - M4 → M5 : `PARCEL_DELIVERED`
  - M1-M6 → M7 : `*.LOG`

**Notes présentateur :**
> La règle d'or : zéro base de données partagée. Chaque choix de technologie est justifié par les contraintes métier du microservice. Les communications synchrones sont réservées aux opérations qui nécessitent une réponse immédiate — validation de stock avant confirmation. Tout le reste est asynchrone via RabbitMQ pour garantir le découplage.

**Transition :** « Ces composants s'assemblent dans une architecture technique globale que voici. »

---

## Slide 7 – ARCHITECTURE TECHNIQUE GLOBALE

**Contenu :**
- **Diagramme d'architecture complet** (image haute résolution)
- Composants présents dans le diagramme :
  - 🌐 **Frontend** : React.js (port 3000)
  - 🔀 **API Gateway** : Spring Cloud Gateway (port 8080)
  - 🔍 **Service Discovery** : Eureka Server (port 8761)
  - ⚙️ **Config Server** : Spring Cloud Config (port 8888)
  - 🔐 **Keycloak** : IAM & JWT (port 8180)
  - 📦 **7 Microservices** (M1-M7) avec leurs DBs respectives
  - 📨 **RabbitMQ** : Message broker (port 5672)
  - 🐳 **Docker** : Containerisation de l'ensemble (docker-compose)

**Notes présentateur :**
> L'architecture suit le pattern Spring Cloud complet. Toutes les requêtes passent par l'API Gateway qui valide le JWT auprès de Keycloak avant de router vers le microservice cible. Eureka assure la découverte dynamique des services. La configuration centralisée via Config Server permet de modifier les paramètres sans redéploiement.

**Transition :** « Parlons maintenant de la sécurité, qui est transversale à toute l'architecture. »

---

## Slide 8 – SÉCURITÉ : KEYCLOAK & JWT

**Contenu :**
- **Chaîne de sécurité** (diagramme de séquence simplifié) :
  1. Client s'authentifie → **Keycloak** émet un JWT signé
  2. JWT transmis dans le header `Authorization: Bearer <token>`
  3. **API Gateway** valide la signature + expiration
  4. Claims JWT (roles) propagés aux microservices
  5. Chaque microservice vérifie l'autorisation locale via `@PreAuthorize`

- **Tableau des rôles :**

| Rôle | Microservices accessibles | Permissions |
|------|--------------------------|-------------|
| ADMIN | Tous | CRUD complet |
| SUPPLIER | M1 | Publier/modifier produits |
| MERCHANT | M2, M3 | Boutiques, suivi commandes |
| CONFIRMATION_AGENT | M3 | Confirmer/annuler commandes |
| LOGISTICS_AGENT | M4 | Expédition, dépôt |
| FINANCE_AGENT | M5 | Transactions, wallet |
| SUPPORT_AGENT | M6 | Tickets SAV, échanges |

**Notes présentateur :**
> La sécurité est centralisée dans Keycloak — aucun microservice ne gère ses propres utilisateurs. Le JWT contient les roles claim que chaque service lit pour appliquer un contrôle d'accès fin. En cas de token invalide, l'API Gateway rejette la requête avec un 401 avant même d'atteindre le microservice.

**Transition :** « Maintenant, chaque membre de l'équipe va présenter son microservice en 1 minute. Je commence avec M1. »

---

## Slide 9 – M1 : PRODUITS & CATALOGUE *(Membre 1)*

**Contenu :**

**Responsabilité :**
- Gérer le catalogue produits B2B (création, modification, suppression)
- Maintenir les niveaux de stock en temps réel
- Exposer les prix et disponibilités aux autres microservices

**Entités clés :** `Product`, `Category`, `StockEntry`, `PriceHistory`, `Supplier`

**Base de données :** MongoDB — schéma flexible pour attributs produits variables (couleur, taille, poids selon catégorie)

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| GET | `/api/v1/products` | 200, 404 |
| POST | `/api/v1/products` | 201, 400, 403 |
| PUT | `/api/v1/products/{id}/stock` | 200, 404, 409 |
| GET | `/api/v1/products/{id}/price` | 200, 404 |

**Communications :**
- *Sync (appelé par)* : M2 (validation SKU), M3 (vérif. stock), M5 (prix)
- *Async (émet vers)* : M7 → `PRODUCT_UPDATED`, `STOCK_LOW`

**Comportement en échec :**
- Circuit breaker Resilience4j : fallback retourne le dernier stock connu (cache Redis)
- Si MongoDB indisponible : réponse 503 + log `PRODUCT_SERVICE_DOWN` dans M7

**Notes présentateur :**
> M1 est le référentiel central des produits. Sa disponibilité est critique car trois autres microservices en dépendent en synchrone. C'est pourquoi nous avons mis en place un circuit breaker avec cache Redis pour absorber les pics de charge et éviter les cascades de pannes.

**Transition :** « [Membre 2], tu peux présenter M2. »

---

## Slide 10 – M2 : INTÉGRATION BOUTIQUES — Tech Avancée *(Membre 2)*

**Contenu :**

**Responsabilité :**
- Recevoir et normaliser les webhooks de Shopify, WooCommerce, TikTok Shop
- Transformer les commandes sources en format interne DropFlow
- Publier les commandes importées vers M3 via RabbitMQ

**Entités clés :** `Shop`, `WebhookConfig`, `RawOrder`, `NormalizedOrder`, `IntegrationLog`

**Base de données :** PostgreSQL + JSONB — stockage des payloads bruts hétérogènes avec capacité de requêtage structuré

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| POST | `/webhook/shopify` | 200, 400, 401 |
| POST | `/webhook/woocommerce` | 200, 400, 401 |
| GET | `/api/v1/shops/{id}/orders` | 200, 404 |
| POST | `/api/v1/shops` | 201, 400, 409 |

**Communications :**
- *Sync (appelle)* : M1 → validation SKU avant import
- *Async (émet vers)* : M3 → `ORDER_IMPORTED` | M7 → `WEBHOOK_RECEIVED`

**Comportement en échec :**
- Webhook en échec : retry 3× avec backoff exponentiel → Dead-Letter Queue
- Si M1 indisponible (OpenFeign) : circuit breaker → commande mise en quarantaine + alerte

**Notes présentateur :**
> M2 est notre microservice techniquement avancé. L'enjeu principal est l'hétérogénéité des formats : un webhook Shopify n'a pas la même structure qu'un webhook TikTok Shop. Nous avons implémenté un pattern Adapter par plateforme, avec un normaliseur central qui produit toujours le même format interne. Le JSONB PostgreSQL nous permet de stocker le payload brut pour audit tout en indexant les champs critiques.

**Transition :** « [Membre 3], à toi pour les commandes et le call center. »

---

## Slide 11 – M3 : COMMANDES & CALL CENTER *(Membre 3)*

**Contenu :**

**Responsabilité :**
- Réceptionner les commandes importées par M2 et les qualifier
- Gérer le workflow de confirmation via les agents call center
- Coordonner le passage à la logistique après confirmation

**Entités clés :** `Order`, `OrderItem`, `CallAttempt`, `ConfirmationAgent`, `Customer`

**Base de données :** MySQL — relations fortes commande/client/produit, intégrité référentielle et transactions ACID

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| GET | `/api/v1/orders?status=PENDING` | 200 |
| PUT | `/api/v1/orders/{id}/confirm` | 200, 404, 409 |
| PUT | `/api/v1/orders/{id}/cancel` | 200, 404 |
| POST | `/api/v1/orders/{id}/call-attempt` | 201, 400 |

**Communications :**
- *Sync (appelle)* : M1 → vérification stock disponible ; M6 → statut commande pour SAV
- *Async (reçoit)* : M2 → `ORDER_IMPORTED` | *(émet)* M4 → `ORDER_CONFIRMED`

**Comportement en échec :**
- Client injoignable après 3 tentatives : statut `UNREACHABLE`, notification automatique à l'e-commerçant
- Si M4 refuse l'`ORDER_CONFIRMED` : event replay depuis la Dead-Letter Queue

**Notes présentateur :**
> M3 est le cœur opérationnel de la plateforme : c'est ici que la commande passe de brute à confirmée. Le workflow de call center est critique — chaque tentative est loguée avec horodatage et résultat. En cas d'échec de publication vers M4, le message est retenu dans la DLQ et rejoué automatiquement dès que M4 est disponible.

**Transition :** « [Membre 4], présente-nous la logistique. »

---

## Slide 12 – M4 : LOGISTIQUE & FULFILLMENT *(Membre 4)*

**Contenu :**

**Responsabilité :**
- Gérer la préparation et l'expédition des colis confirmés
- Maintenir l'état du dépôt (inventaire physique)
- Émettre les événements de livraison vers M5

**Entités clés :** `Shipment`, `Parcel`, `Warehouse`, `DeliveryAgent`, `TrackingEvent`

**Base de données :** PostgreSQL — machine à états pour shipment (PENDING→PREPARED→SHIPPED→DELIVERED), requêtes analytiques sur flux logistique

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| GET | `/api/v1/shipments?status=PREPARED` | 200 |
| PUT | `/api/v1/shipments/{id}/ship` | 200, 404, 409 |
| PUT | `/api/v1/shipments/{id}/deliver` | 200, 404 |
| GET | `/api/v1/warehouse/inventory` | 200 |

**Communications :**
- *Async (reçoit)* : M3 → `ORDER_CONFIRMED` | *(émet)* M5 → `PARCEL_DELIVERED` | M7 → `SHIPMENT_UPDATED`

**Comportement en échec :**
- Si M5 inaccessible lors d'une livraison : événement `PARCEL_DELIVERED` stocké localement et rejoué (outbox pattern)
- Circuit breaker sur tout appel sortant ; fallback : statut shipment conservé, alert M7

**Notes présentateur :**
> M4 utilise l'outbox pattern pour garantir que l'événement PARCEL_DELIVERED ne se perd jamais, même si RabbitMQ est temporairement indisponible. La livraison déclenche le paiement dans M5 — la fiabilité de cet événement est critique pour la réconciliation financière.

**Transition :** « [Membre 5], les transactions COD. »

---

## Slide 13 – M5 : TRANSACTIONS COD *(Membre 5)*

**Contenu :**

**Responsabilité :**
- Enregistrer et réconcilier les encaissements COD (Cash On Delivery)
- Gérer le wallet e-commerçant (crédit/débit après commission)
- Produire les rapports financiers par période

**Entités clés :** `Transaction`, `Wallet`, `Commission`, `Settlement`, `FinancialReport`

**Base de données :** PostgreSQL — ACID obligatoire, contraintes d'intégrité strictes sur toutes les opérations financières

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| GET | `/api/v1/transactions/{id}` | 200, 404 |
| POST | `/api/v1/transactions/cod` | 201, 400, 422 |
| GET | `/api/v1/wallet/{merchantId}` | 200, 404 |
| POST | `/api/v1/wallet/{merchantId}/refund` | 200, 400, 409 |

**Communications :**
- *Sync (appelle)* : M1 → vérification prix de référence
- *Async (reçoit)* : M4 → `PARCEL_DELIVERED` | M6 → `REFUND_REQUESTED` | *(émet)* M7 → `TRANSACTION_LOGGED`

**Comportement en échec :**
- Idempotence obligatoire : chaque transaction porte un `idempotencyKey` pour éviter les doubles encaissements
- En cas de panne : les événements `PARCEL_DELIVERED` sont rejoués depuis la DLQ → aucune perte financière

**Notes présentateur :**
> L'idempotence est notre principale protection contre les doublons financiers. Si RabbitMQ rejoue un événement PARCEL_DELIVERED, M5 détecte l'idempotencyKey déjà traité et ignore le doublon. Les opérations sur le wallet sont entourées de transactions PostgreSQL pour garantir la cohérence même en cas de crash mid-opération.

**Transition :** « [Membre 6], le SAV et les échanges. »

---

## Slide 14 – M6 : ÉCHANGES & SAV *(Membre 6)*

**Contenu :**

**Responsabilité :**
- Gérer les demandes de retour, d'échange et de remboursement
- Ouvrir et suivre les tickets SAV jusqu'à clôture
- Coordonner avec M3 (statut commande) et M5 (remboursement)

**Entités clés :** `ReturnRequest`, `ExchangeOrder`, `SupportTicket`, `Resolution`, `RefundRecord`

**Base de données :** MySQL — données structurées à relations fortes (ticket→commande→client), faible volumétrie mais requêtes complexes

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| POST | `/api/v1/returns` | 201, 400, 404 |
| PUT | `/api/v1/returns/{id}/approve` | 200, 404, 409 |
| POST | `/api/v1/tickets` | 201, 400 |
| GET | `/api/v1/tickets?status=OPEN` | 200 |

**Communications :**
- *Sync (appelle)* : M3 → vérification statut commande originale
- *Async (émet)* : M5 → `REFUND_REQUESTED` | M7 → `SAV_EVENT`

**Comportement en échec :**
- Si M3 inaccessible (OpenFeign) : circuit breaker → ticket créé en mode dégradé, validation asynchrone ultérieure
- Si M5 ne reçoit pas `REFUND_REQUESTED` : DLQ + retry automatique jusqu'à 5 fois

**Notes présentateur :**
> Le SAV est souvent sous-estimé dans les architectures microservices, mais c'est ici que les incohérences remontent : un retour validé doit absolument déclencher un remboursement dans M5. Nous avons donc privilégié la fiabilité de messaging sur la performance — l'événement REFUND_REQUESTED ne disparaît pas tant qu'il n'est pas acquitté.

**Transition :** « Toute l'équipe contribue à M7, notre service commun. »

---

## Slide 15 – M7 : SERVICE COMMUN *(Toute l'équipe)*

**Contenu :**

**Responsabilité :**
- **IAM centralisé** : Gestion des utilisateurs, rôles et permissions via Keycloak
- **Logging centralisé** : Agrégation de tous les logs applicatifs (ELK-ready)
- **Support marketing** : Coaching et analytics pour les e-commerçants

**Entités clés :** `UserProfile`, `Role`, `LogEntry`, `AuditTrail`, `MarketingInsight`

**Base de données :** MongoDB — logs structurés haute volumétrie, schéma variable selon le type d'événement

**API Endpoints :**

| Méthode | URI | Codes HTTP |
|---------|-----|------------|
| POST | `/api/v1/logs` | 201, 400 |
| GET | `/api/v1/logs?service=M3` | 200 |
| GET | `/api/v1/users/{id}/profile` | 200, 404 |
| POST | `/api/v1/analytics/report` | 202, 400 |

**Communications :**
- *Async (reçoit)* : M1 à M6 → `*.LOG` events via RabbitMQ topic exchange `dropflow.events`
- *Sync (appelé par)* : API Gateway → validation token / récupération profil

**Comportement en échec :**
- M7 ne bloque jamais un service métier : les logs sont émis en fire-and-forget
- Si MongoDB indisponible : buffer en mémoire (max 10 000 entrées) puis flush à la reconnexion

**Notes présentateur :**
> M7 est transversal — il ne fait partie d'aucun flux métier critique mais agrège l'intelligence opérationnelle de toute la plateforme. Chaque microservice émet des logs structurés en JSON vers M7 via RabbitMQ, sans jamais attendre de réponse. Cette approche garantit que M7 ne peut pas devenir un point de défaillance unique.

**Transition :** « Voyons maintenant notre stack technique complète et l'organisation de notre dépôt Git. »

---

## Slide 16 – STACK TECHNIQUE & REPO GIT

**Contenu :**

**Stack Backend :**
- Java 17 + Spring Boot 3.x + Spring Cloud (Gateway, Eureka, Config, OpenFeign)
- RabbitMQ (messaging) | Resilience4j (circuit breaker)
- Bases : MongoDB, PostgreSQL, MySQL

**Stack Frontend :**
- React.js 18 + Vite | Axios | React Query

**Infrastructure :**
- Docker + Docker Compose (déploiement one-command)
- Keycloak 22 (IAM) | GitHub Actions (CI)

**Structure du dépôt :**
```
dropflow-hub-awd-<classe>-<groupe>/
├── Backend/
│   ├── m1-produits/
│   ├── m2-integration/
│   ├── m3-commandes/
│   ├── m4-logistique/
│   ├── m5-transactions/
│   ├── m6-sav/
│   └── m7-commun/
├── Frontend/
├── Documentation/
│   ├── Presentation/ (PPTX v2)
│   └── Diagrams/ (architecture, UML, séquences)
├── docker-compose.yml
└── README.md
```

- Tous les membres ont des commits dans le dépôt
- Enseignant ajouté comme collaborateur
- GitHub Projects utilisé pour la gestion des tâches

**Notes présentateur :**
> Notre dépôt suit exactement le template officiel. Chaque microservice est dans son propre dossier avec son pom.xml, son application.yml et ses tests. Le docker-compose.yml à la racine permet de démarrer toute la plateforme avec une seule commande : `docker-compose up --build`. L'enseignant est collaborateur et peut vérifier l'historique des commits de chaque membre.

**Transition :** « Voici notre planning de développement et les risques identifiés. »

---

## Slide 17 – PLANNING (S4-S10) & RISQUES

**Contenu :**

**Planning Sprint par Sprint :**

| Sprint | Semaine | Livrables |
|--------|---------|-----------|
| S4 | Setup | Architecture + Config Server + Keycloak + Docker Compose de base |
| S5 | M1 + M7 | Catalogue produits fonctionnel + logging centralisé |
| S6 | M2 + M3 | Intégration webhooks + gestion commandes call center |
| S7 | M4 + M5 | Logistique + transactions COD |
| S8 | M6 + Integration | SAV + tests d'intégration end-to-end |
| S9 | Frontend + Tests | Interface React + tests de performance |
| S10 | Préparation V1 | Finalisation PPTX + README + répétition présentation |

**Risques identifiés :**

| Risque | Impact | Probabilité | Mitigation |
|--------|--------|-------------|------------|
| Indisponibilité RabbitMQ | Élevé | Faible | Circuit breaker + DLQ |
| Incohérence des données inter-MS | Élevé | Moyen | Saga pattern + event sourcing |
| Dépendance circulaire entre MS | Moyen | Faible | Architecture review hebdomadaire |
| Retard d'un membre | Moyen | Moyen | Buffer S9 + pair programming |

**Notes présentateur :**
> Notre planning est réaliste et prévoit une semaine tampon en S9 pour absorber les imprévus. Les risques techniques ont tous une mitigation technique concrète — le plus critique, l'incohérence des données entre microservices, est géré via le pattern Saga chorégraphié par nos événements RabbitMQ.

**Transition :** « Pour conclure, voici ce que DropFlow Hub apporte concrètement. »

---

## Slide 18 – CONCLUSION & QUESTIONS

**Contenu :**

**Ce que DropFlow Hub résout :**
- ✅ Fragmentation e-commerce → **Plateforme unifiée 7 microservices**
- ✅ Perte de commandes → **Architecture événementielle RabbitMQ + DLQ**
- ✅ Sécurité défaillante → **Keycloak + JWT + RBAC par microservice**
- ✅ Scalabilité limitée → **Spring Cloud + Docker + Eureka**

**Valeur ajoutée :**
- Réduction de 60% du temps de traitement d'une commande (estimation)
- Zéro perte de données grâce aux DLQ et event replay
- Onboarding d'une nouvelle boutique en < 5 minutes via webhooks M2

**Récapitulatif de l'équipe :**

| Membre | Microservice | Technologie signature |
|--------|-------------|----------------------|
| [Membre 1] | M1 – Produits | MongoDB + Circuit Breaker |
| [Membre 2] | M2 – Intégration | Webhooks + PostgreSQL JSONB |
| [Membre 3] | M3 – Commandes | MySQL + Workflow Call Center |
| [Membre 4] | M4 – Logistique | PostgreSQL + Outbox Pattern |
| [Membre 5] | M5 – Transactions | PostgreSQL ACID + Idempotence |
| [Membre 6] | M6 – SAV | MySQL + RabbitMQ Retry |

> **Merci pour votre attention. Nous sommes prêts pour vos questions.**

**Notes présentateur :**
> En dix minutes, nous avons présenté une plateforme microservices complète : du problème métier à l'architecture technique, en passant par chaque service individuellement. Nous avons fait des choix techniques délibérés et justifiés. Nous sommes disponibles pour approfondir n'importe quel aspect lors de la session de questions.

**Transition :** *(Fin de la présentation — session Q&A)*

---

---

# ANNEXE (Slides mis en réserve du deck original)

> Ces slides ne figurent pas dans la présentation principale de 10 minutes mais doivent être **préparés dans un fichier PPTX séparé** (ou en fin de deck principal après la slide 18) pour la session de questions.

| # Annexe | Slides originaux | Contenu | Utiliser quand... |
|----------|-----------------|---------|-------------------|
| A1 | Slide 5 | Diagramme d'activité UML complet du cycle de commande | Question sur le flux complet bout-en-bout |
| A2 | Slide 7 | Diagramme de cas d'utilisation complet (non simplifié) | Question sur la couverture fonctionnelle exacte |
| A3 | Slide 8 | Matrice acteurs-cas d'utilisation-microservice-entité | Question sur la traçabilité acteurs↔microservices |
| A4 | Slide 10 | Slide valeur ajoutée détaillée | Question sur la proposition de valeur ou le business model |
| A5 | Slide 15 | Diagramme RabbitMQ détaillé (topic exchange, queues, DLQ) | Question sur la configuration exacte du messaging |
| A6 | Slides 18-20 | 3 diagrammes de séquence (Webhook+CallCenter, Logistics, COD+SAV) | Question sur un flux précis inter-microservices |
| A7 | Slides 21-24 | M1 : diagramme de classe + schéma d'interaction + JSON exemples | Question approfondie sur M1 (jury membre 1) |
| A8 | Slides 25-28 | M2 : diagramme de classe + schéma d'interaction + JSON exemples | Question approfondie sur M2 (jury membre 2) |
| A9 | Slides 29-32 | M3 : diagramme de classe + schéma d'interaction + JSON exemples | Question approfondie sur M3 (jury membre 3) |
| A10 | Slides 33-36 | M4 : diagramme de classe + schéma d'interaction + JSON exemples | Question approfondie sur M4 (jury membre 4) |
| A11 | Slides 37-40 | M5 : diagramme de classe + schéma d'interaction + JSON exemples | Question approfondie sur M5 (jury membre 5) |
| A12 | Slides 41-44 | M6 : diagramme de classe + schéma d'interaction + JSON exemples | Question approfondie sur M6 (jury membre 6) |
| A13 | Slides 45-46 | M7 : API table + coaching marketing + IAM détaillé | Question sur M7 ou sur la sécurité transversale |
| A14 | Slide 12 | Matrice de responsabilités M1-M7 complète | Question sur le découpage ou les overlaps |

**Conseil :** Pendant la session Q&A, le présentateur désigné (ou chef de projet) projette la slide d'annexe correspondante lorsqu'une question précise est posée. Cela montre au jury que l'équipe maîtrise son sujet au-delà des 10 minutes de présentation.

---

---

# ESTIMATION DU SCORE

## Score AVANT restructuration : **12-13 / 20**

| Critère | Score estimé | Justification |
|---------|-------------|---------------|
| **E1** (Problème, acteurs, fonctionnalités, scénario) | 1.5 / 2 | Contenu présent mais dilué sur trop de slides (5→9), risque de dépassement du temps dédié |
| **E2** (Découpage microservices, MS avancé) | 1.5 / 2 | M2 identifié comme avancé ✅, mais dans le deck actuel la justification du bounded context n'est pas assez visuelle |
| **E3** (1 DB par MS, communications justifiées) | 1.5 / 2 | Tableau DB présent ✅, communications présentes ✅, mais justifications trop courtes dans les slides actuels |
| **E4** (Architecture complète) | 1.5 / 2 | Diagramme présent mais slide 16 image seulement — pas de légende ni de callouts |
| **E5** (README, Git, repo structure) | 1 / 2 | Repo mentionné mais pas de preuve README complet, commits tous membres non vérifiable depuis le deck |
| **E6** (Timing, équilibre) | 1 / 2 | Deck 49 slides → 10 min impossible → dépassement garanti → pénalité |
| **I1** (Responsabilités, entités, DB, API — par membre) | 1.2 / 2 | Slides individuels (21-49) existent mais 4 slides par MS en 1 min = impossible à présenter |
| **I2** (Callers/callees, circuit breaker — par membre) | 0.8 / 2 | Communications présentées côté équipe mais pas réitérées dans les slides individuels |
| **I3** (Communication, clarté) | 1.5 / 2 | Dépend des présentateurs, mais structure actuelle ne facilite pas la fluidité |
| **I4** (Q&A) | 1.5 / 2 | Contenu technique présent mais sans annexes organisées, les réponses seront moins fluides |
| **TOTAL** | **~12.5 / 20** | |

---

## Score APRÈS restructuration : **16-17 / 20**

| Critère | Score estimé | Justification |
|---------|-------------|---------------|
| **E1** (Problème, acteurs, fonctionnalités, scénario) | 2 / 2 | Slides 2, 3, 4 couvrent tout : acteurs (tableau), use-case (diagramme), scénario TikTok complet + cas alternatifs |
| **E2** (Découpage microservices, MS avancé) | 2 / 2 | Slide 5 avec tableau clair M1-M7 + mention explicite M2 = "microservice techniquement avancé" + justification webhooks multi-plateformes |
| **E3** (1 DB par MS, communications justifiées) | 1.5 / 2 | Slide 6 couvre les deux tables. ½ point de risque si le jury demande des justifications plus fines → annexes prêtes |
| **E4** (Architecture complète) | 2 / 2 | Slide 7 = diagramme complet avec tous les composants requis (front, gateway, eureka, config, keycloak, 7MS, DBs, RabbitMQ, Docker) |
| **E5** (README, Git, repo structure) | 1.5 / 2 | Slide 16 montre la structure du repo ✅. Le ½ point manquant dépend du vrai README.md et des commits réels |
| **E6** (Timing, équilibre) | 2 / 2 | 18 slides → ~33 sec/slide en moyenne ✅. 1 min par membre (slides 9-14) ✅. Timing respecté |
| **I1** (Responsabilités, entités, DB, API — par membre) | 2 / 2 | Chaque slide 9-14 contient exactement : responsabilité + entités + DB + endpoints API avec codes HTTP |
| **I2** (Callers/callees, circuit breaker — par membre) | 1.5 / 2 | Callers/callees et failure behavior inclus dans chaque slide individuel. ½ risque si détails insuffisants à l'oral |
| **I3** (Communication, clarté) | 1.5 / 2 | Structure fluide avec transitions explicites facilite la clarté. Dépend aussi des présentateurs individuels |
| **I4** (Q&A) | 1.5 / 2 | 14 annexes organisées par thème → réponses rapides et précises lors du Q&A |
| **TOTAL** | **~17 / 20** | *+4-5 points de gain attendu par la restructuration* |

### Leviers d'amélioration résiduelle pour atteindre 18-19/20
1. **E5** : S'assurer que le README.md est complet (template fourni séparément) et que l'enseignant est collaborateur GitHub
2. **I2/I3** : Chaque membre doit répéter sa minute exactement — utiliser un timer pendant les répétitions
3. **I4** : Chaque membre doit pouvoir répondre à des questions sur n'importe quel autre microservice (pas seulement le sien)
