# DropFlow Hub – Fiche de préparation aux questions (Validation 1 AWD ESPRIT)

> 25+ questions probables avec réponses modèles. Utilisez-la pour préparer vos réponses individuelles et d'équipe.

---

## 🏗️ Architecture générale

### Q1 : Pourquoi une architecture microservices plutôt qu'un monolithe pour ce projet ?
**Réponse modèle :** DropFlow Hub couvre des domaines métiers très hétérogènes (catalogue, intégration boutiques, call center, logistique, finance, SAV), chacun avec son propre cycle de déploiement, sa charge et ses besoins en base de données différents. Une architecture microservices permet de déployer, scaler et faire évoluer chaque service indépendamment ; par exemple, M3 (Call Center) peut être scalé horizontalement pendant une campagne TikTok sans toucher M5 (Transactions). En cas de panne de M6 (SAV), les autres services continuent de fonctionner. Un monolithe aurait couplé ces domaines, rendant chaque déploiement risqué et imposant une technologie unique de base de données alors que chaque service a des besoins différents (MongoDB pour le catalogue, PostgreSQL ACID pour la finance).  
**Qui peut répondre :** Toute l'équipe (question d'architecture globale)

---

### Q2 : Quelle est la différence entre API Gateway et Eureka ? Pourquoi les deux ?
**Réponse modèle :** Eureka est un registre de découverte de services (Service Registry) : chaque microservice s'enregistre au démarrage avec son IP et port, et peut interroger Eureka pour connaître l'adresse des autres services. L'API Gateway (Spring Cloud Gateway) est le point d'entrée unique des clients externes : il route les requêtes, valide le JWT Keycloak et applique les règles cross-cutting (rate limiting, CORS). Sans Eureka, l'API Gateway devrait avoir les adresses des microservices en dur, ce qui est incompatible avec Docker où les IPs sont dynamiques. Les deux sont complémentaires : Eureka fournit la résolution de noms, Gateway utilise ces noms pour router et sécuriser le trafic entrant.  
**Qui peut répondre :** Toute l'équipe

---

### Q3 : À quoi sert Config Server ? Peut-on s'en passer ?
**Réponse modèle :** Spring Cloud Config Server centralise toutes les propriétés de configuration (URLs de bases de données, credentials RabbitMQ, paramètres Keycloak, niveaux de log) dans un dépôt Git unique. Chaque microservice charge sa configuration au démarrage depuis le Config Server, ce qui évite d'avoir 7 fichiers `application.yml` différents à maintenir et déployer séparément. On pourrait techniquement s'en passer en embarquant la config dans chaque image Docker, mais cela rendrait les changements de configuration très coûteux (rebuild + redeploy de toutes les images). En production, le Config Server permet aussi de changer un paramètre à chaud sans redémarrer les services (via `/actuator/refresh`).  
**Qui peut répondre :** Toute l'équipe

---

### Q4 : Expliquez le rôle de Keycloak et comment il s'articule avec le JWT et l'API Gateway.
**Réponse modèle :** Keycloak est un serveur d'identité et d'accès (IAM) open-source qui joue le rôle d'Authorization Server OAuth 2.0 / OIDC. Lorsqu'un utilisateur (MERCHANT, ADMIN, etc.) se connecte, Keycloak émet un JWT signé contenant ses rôles (claims). Le client envoie ce token dans l'en-tête `Authorization: Bearer <token>` à chaque requête vers l'API Gateway, qui valide la signature du token (clé publique Keycloak) et rejette les requêtes non authentifiées avant même qu'elles n'atteignent les microservices. Chaque microservice effectue ensuite une double vérification des rôles (`@PreAuthorize`) pour respecter le principe de défense en profondeur. Cette architecture évite de stocker des sessions côté serveur et est naturellement stateless et scalable.  
**Qui peut répondre :** Toute l'équipe / M7 (IAM)

---

### Q5 : Pourquoi RabbitMQ et pas Kafka ? Pourquoi pas des appels REST directs pour tout ?
**Réponse modèle :** Kafka est conçu pour des flux de données à très haut débit et la rétention longue durée de messages (event sourcing) ; RabbitMQ est plus léger, plus simple à opérer et parfaitement adapté à des workflows transactionnels à faible volume comme le nôtre (commandes e-commerce). Pour les appels REST synchrones : ils introduisent un couplage fort entre services — si M3 appelle M4 en REST et que M4 est temporairement indisponible, la commande est perdue. Avec RabbitMQ, le message `ORDER_CONFIRMED` est persisté dans la queue jusqu'à ce que M4 soit disponible pour le consommer, garantissant la résilience. Les appels REST synchrones sont conservés uniquement pour les opérations qui nécessitent une réponse immédiate (vérification de stock, de prix, de statut livraison).  
**Qui peut répondre :** Toute l'équipe

---

## 🔲 Bounded Context & Découpage

### Q6 : Pourquoi M3 gère à la fois les commandes ET le call center ? N'est-ce pas deux responsabilités ?
**Réponse modèle :** Le call center dans notre contexte n'est pas une fonctionnalité générique de support téléphonique, mais une étape métier intrinsèque du processus de qualification des commandes COD (Cash on Delivery) : un agent appelle le client pour confirmer la commande avant de la transmettre à la logistique. Cette confirmation téléphonique est une sous-étape de la gestion de commande, non un domaine séparé. Les entités (commande, tentative d'appel, résultat de confirmation) sont naturellement liées et partagent le même cycle de vie. Séparer les deux créerait un couplage artificiel entre deux services qui travaillent sur la même agrégat « Commande ». Il s'agit donc d'un seul bounded context cohérent.  
**Qui peut répondre :** Membre responsable de M3

---

### Q7 : M2 est votre "technologie avancée" – qu'est-ce qui la justifie techniquement ?
**Réponse modèle :** M2 Intégration Boutiques implémente des webhooks Shopify et WooCommerce en temps réel : il reçoit des événements HTTP POST signés (HMAC-SHA256) de plateformes tierces, valide les signatures, mappe les SKUs externes vers les SKUs internes DropFlow via une table de mapping stockée en PostgreSQL/JSONB, et publie ensuite l'événement `ORDER_IMPORTED` sur RabbitMQ. L'utilisation de JSONB permet de stocker la charge utile brute du webhook (structure variable selon la plateforme) tout en indexant les champs critiques. La gestion des webhooks inclut également un mécanisme de retry côté Shopify (renvoi en cas d'erreur 5xx) que M2 gère en idempotence via un `webhook_id` unique. C'est la seule intégration avec des systèmes externes hors de notre infrastructure.  
**Qui peut répondre :** Membre responsable de M2

---

### Q8 : M7 fait IAM et logging – pourquoi regrouper deux rôles dans un seul service ?
**Réponse modèle :** M7 est appelé le service "Commun" car il regroupe les préoccupations transversales (cross-cutting concerns) qui concernent l'ensemble du système, et non un domaine métier spécifique. IAM (délégué à Keycloak) et logging centralisé sont deux fonctions d'infrastructure qui n'ont pas de sens à opérer indépendamment à ce stade du projet. M7 est également le seul service à recevoir des événements de tous les autres (M1 à M6) via le topic exchange RabbitMQ `dropflow.events`, ce qui en fait naturellement le point de centralisation des logs d'audit. Chaque membre de l'équipe contribue un `@RabbitListener` dans M7, ce qui assure une responsabilité partagée. En production, ce service pourrait être scindé, mais pour ce projet le regroupement est justifié par la cohérence des préoccupations transversales.  
**Qui peut répondre :** Membre responsable de M7 / Toute l'équipe

---

### Q9 : Pourquoi ne pas avoir fusionné M4 (Logistique) et M5 (Transactions) ?
**Réponse modèle :** M4 et M5 couvrent des bounded contexts fondamentalement différents : M4 gère l'état physique du colis (collecte fournisseur, emballage, expédition, tracking) tandis que M5 gère l'état financier de la transaction (encaissement COD, calcul du profit, crédit du wallet marchand). Leurs modèles de données, leurs acteurs (agent logistique vs agent financier) et leurs rythmes d'évolution sont distincts. De plus, M5 nécessite des garanties ACID strictes (transactions bancaires) alors que M4 peut tolérer une consistance éventuelle. Fusionner les deux créerait un service trop large avec des responsabilités contradictoires. L'événement `PARCEL_DELIVERED` que M4 émet et que M5 consomme est la frontière claire entre les deux domaines.  
**Qui peut répondre :** Membres responsables de M4 et M5

---

## 🗄️ Données & Base de données

### Q10 : Chaque microservice a sa propre base – comment gérez-vous la cohérence des données globalement ?
**Réponse modèle :** Nous appliquons le pattern de **consistance éventuelle** (eventual consistency) via la chorégraphie par événements RabbitMQ. Lorsqu'une commande est confirmée (M3), un événement `ORDER_CONFIRMED` est publié sur RabbitMQ et M4 le consomme pour créer l'entrée logistique — il y a un délai possible, mais le système converge vers un état cohérent. Pour les opérations qui nécessitent une cohérence immédiate (vérification de stock avant confirmation), nous utilisons des appels OpenFeign synchrones. Nous ne partageons jamais de base de données entre services ; chaque service est la source de vérité pour ses propres données. Les données dupliquées (ex: statut commande dans M3 ET M4) sont synchronisées via événements.  
**Qui peut répondre :** Toute l'équipe

---

### Q11 : Pourquoi MongoDB pour M1 et pas PostgreSQL ?
**Réponse modèle :** Le catalogue produits B2B est un domaine à **schéma flexible** : un article peut être une montre avec des attributs (taille, bracelet, couleur), un vêtement avec des tailles et matières, ou un produit électronique avec des spécifications techniques très différentes. Modéliser cela en SQL imposerait soit une table EAV complexe soit de nombreuses colonnes nulles. MongoDB permet de stocker chaque produit comme un document JSON avec son propre schéma, facilitant l'ajout de nouveaux types de produits sans migration de base de données. MongoDB offre également de bonnes performances pour les recherches de catalogue (indexation full-text, compound indexes). La fréquence de lecture élevée (lookups de prix, de stock) est bien servie par le modèle documentaire dénormalisé.  
**Qui peut répondre :** Membre responsable de M1

---

### Q12 : M5 utilise PostgreSQL pour l'ACID – expliquez une transaction typique de règlement COD.
**Réponse modèle :** Lorsque M5 reçoit l'événement `PARCEL_DELIVERED`, il démarre une transaction PostgreSQL qui effectue les opérations suivantes de manière atomique : (1) appel synchrone à M1 pour récupérer le prix fournisseur du produit, (2) calcul du profit = `prix_COD - coût_fournisseur - commission_plateforme`, (3) INSERT dans la table `transaction` avec statut `SETTLED`, (4) UPDATE du solde dans la table `wallet_marchand`. Si l'une de ces étapes échoue (ex: M1 indisponible), toute la transaction est annulée (ROLLBACK) et le message RabbitMQ n'est pas acquitté (NACK), déclenchant un retry. L'ACID de PostgreSQL garantit qu'un marchand ne peut pas être crédité deux fois pour la même livraison (contrainte d'unicité sur `parcel_id`).  
**Qui peut répondre :** Membre responsable de M5

---

### Q13 : Comment évitez-vous les doublons / idempotence avec RabbitMQ ?
**Réponse modèle :** Chaque message RabbitMQ porte un identifiant unique (`messageId` ou un `eventId` métier comme `orderId`). Côté consommateur, avant de traiter un message, le service vérifie dans sa base si cet `eventId` a déjà été traité (table `processed_events` ou contrainte UNIQUE). Si oui, le message est acquitté sans traitement (idempotent). Si non, le traitement est effectué et l'`eventId` est enregistré dans la même transaction atomique. Cette approche est particulièrement importante pour M5 (transactions financières) et M4 (créations d'expédition) où un doublon aurait des conséquences graves. Pour les webhooks Shopify reçus par M2, le `webhook_id` fourni par Shopify sert d'identifiant idempotent.  
**Qui peut répondre :** Membres M2, M3, M4, M5

---

## 📡 Communications & Résilience

### Q14 : Qu'est-ce qui se passe si M1 est down quand M3 veut vérifier le stock ?
**Réponse modèle :** M3 utilise OpenFeign avec Resilience4j pour appeler M1. Si M1 est indisponible, le circuit breaker s'ouvre après un certain seuil d'échecs consécutifs (ex: 5 échecs sur 10 appels en 10 secondes). En état **OPEN**, M3 n'essaie plus d'appeler M1 et exécute directement le **fallback** configuré — par exemple, refuser la confirmation de commande avec un message d'erreur « vérification de stock temporairement indisponible » ou utiliser la dernière valeur de stock mise en cache. Après un délai (ex: 30 secondes), le circuit passe en **HALF-OPEN** : un appel test est effectué ; si M1 répond, le circuit se referme. Cette approche protège M3 d'une cascade de timeouts et protège M1 d'une surcharge lors de sa reprise.  
**Qui peut répondre :** Membre responsable de M3

---

### Q15 : Qu'est-ce qu'un circuit breaker Resilience4j ? Montrez un exemple sur M3→M1.
**Réponse modèle :** Un circuit breaker est un pattern de résilience qui surveille les appels entre services et « coupe le circuit » (arrête d'appeler) lorsque le service distant est défaillant, évitant les timeouts en cascade. Il a trois états : **CLOSED** (normal, tous les appels passent), **OPEN** (panne détectée, tous les appels vont directement au fallback), **HALF-OPEN** (en attente, quelques appels tests). Exemple M3→M1 en Spring Boot avec Resilience4j :  
```java
@CircuitBreaker(name = "m1-stock", fallbackMethod = "stockFallback")
public StockResponse checkStock(String sku) {
    return m1Client.checkStock(sku); // OpenFeign
}

public StockResponse stockFallback(String sku, Exception e) {
    return new StockResponse(sku, -1, "Stock indisponible - réessayez ultérieurement");
}
```
La configuration dans `application.yml` définit `slidingWindowSize`, `failureRateThreshold` et `waitDurationInOpenState`.  
**Qui peut répondre :** Membre responsable de M3 / Toute l'équipe

---

### Q16 : Saga pattern ou 2PC pour les transactions distribuées ? Qu'avez-vous choisi ?
**Réponse modèle :** Nous avons choisi le **Saga pattern en chorégraphie** (sans orchestrateur central). Le Two-Phase Commit (2PC) est impraticable dans une architecture microservices car il nécessite un coordinateur distribué et bloque les ressources pendant toute la durée de la transaction, ce qui dégrade les performances et crée un point de défaillance unique. Dans notre saga, chaque service écoute un événement, effectue son action locale, puis publie un nouvel événement : M2 publie `ORDER_IMPORTED` → M3 traite et publie `ORDER_CONFIRMED` → M4 traite et publie `PARCEL_DELIVERED` → M5 règle. En cas d'échec à une étape, chaque service publie un événement compensatoire (ex: `ORDER_CANCELLED`) que les services précédents consomment pour annuler leur action locale.  
**Qui peut répondre :** Toute l'équipe

---

### Q17 : Comment fonctionnent les dead-letter queues dans votre architecture RabbitMQ ?
**Réponse modèle :** Chaque queue principale est configurée avec un argument `x-dead-letter-exchange` pointant vers un exchange dédié aux messages morts (DLX). Lorsqu'un message est rejeté (NACK sans requeue) après le nombre maximum de retries configuré (ex: 3 tentatives), RabbitMQ le route automatiquement vers la Dead Letter Queue (DLQ) correspondante. Par exemple, `order-confirmation-queue` a pour DLQ `order-confirmation-dlq`. Un service de monitoring (ou M7) consomme les DLQs pour alerter l'équipe et permettre une inspection manuelle ou un replay. Sans DLQ, un message non traitable bloquerait indefiniment la queue ou serait perdu. Les DLQs permettent également d'analyser les causes d'échec en production.  
**Qui peut répondre :** Toute l'équipe / Membre responsable de M7

---

### Q18 : Pourquoi ACK manuel dans RabbitMQ et pas auto-ack ?
**Réponse modèle :** Avec l'auto-ack, RabbitMQ considère le message comme traité dès qu'il est livré au consommateur, avant même que le traitement métier ne soit terminé. Si le service plante pendant le traitement, le message est perdu définitivement. Avec l'ACK manuel, le service acquitte explicitement le message (`channel.basicAck`) uniquement après que le traitement est terminé avec succès et persiste en base. En cas de panne, RabbitMQ réemettra le message à un autre consommateur disponible. C'est critique pour des opérations comme la création d'expédition (M4) ou le règlement financier (M5) où la perte d'un message est inacceptable. La contrepartie est qu'il faut gérer l'idempotence car un même message peut être délivré plusieurs fois.  
**Qui peut répondre :** Toute l'équipe

---

## 📦 M1 – Produits & Catalogue

### Q19 : Quelles sont les principales entités de M1 et leurs responsabilités ?
**Réponse modèle :** M1 gère principalement trois entités : **Produit** (SKU interne, nom, description, images, attributs flexibles en document MongoDB), **FournisseurProduit** (association produit ↔ fournisseur avec coût d'achat, délai et quantité disponible), et **ReservationStock** (réservation temporaire d'unités lors d'une commande en cours de confirmation). M1 est la source de vérité pour tout ce qui concerne le catalogue B2B : prix fournisseur, disponibilité, SKU mapping. Il expose des endpoints REST pour la consultation de catalogue (MERCHANT, ADMIN) et la gestion des produits (SUPPLIER, ADMIN).  
**Qui peut répondre :** Membre responsable de M1

---

### Q20 : Comment M1 gère-t-il la réservation de stock et les conflits de concurrence ?
**Réponse modèle :** Lorsque M3 appelle M1 pour vérifier et réserver le stock avant confirmation, M1 utilise une opération atomique MongoDB (findAndModify ou session de transaction) pour décrémenter le stock disponible et créer une entrée `ReservationStock` avec un TTL (ex: 15 minutes). Si la commande est confirmée, la réservation est convertie en déduction définitive via un événement. Si la commande est annulée ou expire, la réservation est libérée. Pour éviter les sur-réservations concurrentes, M1 utilise une condition optimiste : la mise à jour n'est effectuée que si `quantiteDisponible >= quantiteDemandee` au moment de l'écriture (opération atomique). En cas d'échec, M1 retourne une réponse `STOCK_INSUFFICIENT` à M3.  
**Qui peut répondre :** Membre responsable de M1

---

### Q21 : Quelle est l'API REST exposée par M1 ? Donnez 3 exemples d'endpoints.
**Réponse modèle :**  
- `GET /api/v1/products?category={cat}&supplier={id}` → Liste filtrée du catalogue (rôle MERCHANT, ADMIN, SUPPLIER)  
- `GET /api/v1/products/{sku}/stock` → Vérifie la disponibilité d'un SKU (appelé par M3 via OpenFeign, rôle interne)  
- `GET /api/v1/products/{sku}/price?supplierId={id}` → Prix fournisseur d'un SKU (appelé par M5 via OpenFeign, rôle FINANCE_AGENT)  
- `POST /api/v1/products` → Création d'un produit au catalogue (rôle SUPPLIER, ADMIN)  
- `PUT /api/v1/products/{sku}/stock/reserve` → Réservation de stock (appelé par M3)  
Chaque endpoint retourne les codes HTTP appropriés : 200 OK, 201 Created, 404 Not Found (SKU inconnu), 409 Conflict (stock insuffisant), 403 Forbidden (rôle inadéquat).  
**Qui peut répondre :** Membre responsable de M1

---

## 🔗 M2 – Intégration Boutiques

### Q22 : Comment M2 valide-t-il un webhook Shopify entrant ?
**Réponse modèle :** Shopify signe chaque webhook avec une signature HMAC-SHA256 calculée sur le corps du message avec le secret partagé de l'application. M2 reçoit l'en-tête `X-Shopify-Hmac-SHA256` et recalcule la signature côté serveur avec le même secret. Si les deux signatures correspondent, le webhook est authentique et traité ; sinon, M2 retourne un 401 Unauthorized et log l'événement suspect dans M7. Cette validation est effectuée avant tout traitement métier, dans un filtre Spring ou un `@Aspect`. Le corps brut du webhook est stocké en PostgreSQL JSONB (table `webhook_raw`) pour auditabilité et possibilité de replay.  
**Qui peut répondre :** Membre responsable de M2

---

### Q23 : Comment M2 mappe-t-il les SKUs externes (Shopify/WooCommerce) vers les SKUs internes DropFlow ?
**Réponse modèle :** M2 maintient une table `sku_mapping` en PostgreSQL avec les colonnes `external_platform` (shopify/woocommerce), `external_sku`, `internal_sku`, et `store_id`. Lorsqu'un webhook arrive avec un `external_sku`, M2 cherche la correspondance dans cette table. Si trouvée, il appelle M1 en synchrone (OpenFeign) pour valider que le SKU interne existe toujours dans le catalogue. Si la correspondance n'existe pas, M2 crée une alerte (ou rejette le webhook avec un code d'erreur approprié) pour qu'un ADMIN configure le mapping. Le JSONB de PostgreSQL est utilisé pour stocker les métadonnées de la boutique (tokens OAuth, URLs webhook, paramètres de configuration) qui varient selon la plateforme.  
**Qui peut répondre :** Membre responsable de M2

---

## 📞 M3 – Commandes & Call Center

### Q24 : Décrivez le cycle de vie complet d'une commande dans M3.
**Réponse modèle :** M3 reçoit l'événement `ORDER_IMPORTED` de RabbitMQ (publié par M2). Il crée une entité `Commande` en base MySQL avec statut `PENDING` et l'enqueue dans la file des agents de confirmation. Un `CONFIRMATION_AGENT` prend en charge la commande et effectue jusqu'à N tentatives d'appel (`TentativeAppel` entity) au client. Si le client confirme : M3 appelle M1 (stock) et passe la commande en `CONFIRMED`, publie `ORDER_CONFIRMED` sur RabbitMQ pour M4. Si le client refuse ou est injoignable après N tentatives : statut `CANCELLED` ou `UNREACHABLE`, l'agent logistique est notifié. M3 peut également être appelé par M6 (SAV) pour vérifier qu'une commande est bien en statut `DELIVERED` avant d'accepter une demande d'échange.  
**Qui peut répondre :** Membre responsable de M3

---

### Q25 : Pourquoi MySQL pour M3 et pas PostgreSQL ?
**Réponse modèle :** M3 gère des données relationnelles bien structurées avec des relations claires entre `Commande`, `TentativeAppel`, `Agent`, et `HistoriqueStatut`. MySQL offre d'excellentes performances pour des requêtes de type OLTP avec des jointures simples, et est un choix mature et bien supporté par Spring Data JPA. La décision n'était pas dictée par une fonctionnalité exclusive de MySQL mais par la répartition des technologies dans l'équipe pour éviter que tous les services utilisent la même base (diversité justifiée pédagogiquement). En pratique, MySQL et PostgreSQL auraient tous deux répondu aux besoins de M3 ; ce qui compte est la cohérence interne du service et l'isolation par rapport aux autres bases.  
**Qui peut répondre :** Membre responsable de M3

---

## 🚚 M4 – Logistique & Fulfillment

### Q26 : Quelles entités M4 gère-t-il et quel est son workflow principal ?
**Réponse modèle :** M4 gère les entités **Expedition** (lien vers commande, statut, transporteur), **ColisTracking** (étapes de tracking avec timestamps), **EtiquetteExpedition** (PDF/URL du label), et **CollecteFournisseur** (planification du pickup). Workflow : réception de `ORDER_CONFIRMED` → création d'une `Expedition` en statut `PENDING_PICKUP` → notification au fournisseur pour collecte du colis → génération de l'étiquette d'expédition (intégration transporteur) → suivi du colis via tracking → passage au statut `DELIVERED` → publication de l'événement `PARCEL_DELIVERED` sur RabbitMQ pour déclencher le règlement financier dans M5. PostgreSQL est choisi pour la fiabilité des états du colis et la compatibilité avec des types avancés (tableaux pour le tracking).  
**Qui peut répondre :** Membre responsable de M4

---

### Q27 : Comment M4 génère-t-il et transmet-il les étiquettes d'expédition ?
**Réponse modèle :** M4 intègre une API de transporteur (ex: Aramex, DHL Express) via un client HTTP REST. Après confirmation de collecte, M4 appelle l'API du transporteur avec les données de l'expédition (adresse, poids, dimensions, valeur déclarée COD) et reçoit en retour un numéro de tracking et un PDF d'étiquette encodé en base64 (ou une URL). M4 stocke ces informations dans l'entité `EtiquetteExpedition` en PostgreSQL et expose un endpoint `GET /api/v1/expeditions/{id}/label` pour téléchargement. L'agent logistique (rôle LOGISTICS_AGENT) peut aussi être notifié via un webhook push ou consulter l'interface dédiée.  
**Qui peut répondre :** Membre responsable de M4

---

## 💰 M5 – Transactions COD

### Q28 : Comment M5 calcule-t-il le profit et met-il à jour le wallet marchand ?
**Réponse modèle :** À la réception de `PARCEL_DELIVERED`, M5 : (1) appelle M1 en synchrone pour obtenir le `coût_fournisseur` du SKU livré, (2) récupère le `prix_COD` depuis les données de la commande embarquées dans l'événement (ou via un appel M3), (3) calcule `profit = prix_COD - coût_fournisseur - commission_plateforme` (commission configurable en pourcentage ou montant fixe), (4) dans une **transaction PostgreSQL atomique**, insère un enregistrement dans `transaction_financiere` et crédite le wallet du marchand (`UPDATE wallet SET balance = balance + profit WHERE merchant_id = ?`). La contrainte d'unicité sur `parcel_id` dans `transaction_financiere` garantit l'idempotence : un même colis ne peut être réglé qu'une fois.  
**Qui peut répondre :** Membre responsable de M5

---

### Q29 : Quels sont les risques financiers si M5 n'utilise pas l'ACID ? Comment PostgreSQL les prévient ?
**Réponse modèle :** Sans ACID, si le serveur tombe après l'INSERT de la transaction mais avant l'UPDATE du wallet, le marchand ne serait pas crédité alors que la transaction est enregistrée — ou inversement, le wallet serait crédité sans trace dans les transactions (inconsistance comptable). PostgreSQL garantit l'**atomicité** : les deux opérations se font ensemble ou pas du tout (ROLLBACK en cas d'erreur). L'**isolation** (niveau READ COMMITTED par défaut) évite les lectures de données intermédiaires par d'autres transactions concurrentes. La **durabilité** (WAL - Write-Ahead Log) garantit qu'une transaction committée survit à un crash. Ces garanties sont non négociables pour un système de paiement, même simplifié.  
**Qui peut répondre :** Membre responsable de M5

---

## 🔄 M6 – Échanges & SAV

### Q30 : Décrivez le workflow d'une demande d'échange dans M6.
**Réponse modèle :** Un client ou marchand soumet une demande d'échange via `POST /api/v1/exchanges`. M6 appelle M3 en synchrone (OpenFeign) pour vérifier que la commande associée est bien en statut `DELIVERED` (échange refusé si commande non livrée). Un `SUPPORT_AGENT` effectue une inspection qualité du produit retourné (entité `InspectionQualite`) et documente les défauts. Si l'échange est approuvé, M6 crée une `CommandeRemplacement` (entité liée) et publie un événement d'échange sur RabbitMQ vers M7 pour logging et vers M3 pour créer une nouvelle commande de remplacement. Si rejeté, M6 met à jour le statut et notifie le marchand. MySQL est choisi pour la nature relationnelle structurée des données SAV.  
**Qui peut répondre :** Membre responsable de M6

---

### Q31 : Pourquoi M6 appelle M3 en synchrone plutôt qu'en asynchrone pour vérifier le statut commande ?
**Réponse modèle :** La vérification du statut de la commande dans M3 est une **précondition bloquante** pour accepter ou rejeter une demande d'échange : sans réponse immédiate et fiable, M6 ne peut pas décider si l'échange est éligible. Un appel asynchrone via événements ne donnerait pas de réponse immédiate et créerait une complexité de corrélation de messages inutile pour ce cas d'usage simple. La latence attendue de cet appel OpenFeign est faible (quelques millisecondes sur le réseau interne Docker), et il est protégé par un circuit breaker avec fallback (ex: retourner un statut « vérification impossible » et mettre la demande en attente manuelle si M3 est indisponible).  
**Qui peut répondre :** Membre responsable de M6

---

## 🌐 M7 – Commun (IAM + Logging)

### Q32 : Comment M7 centralise-t-il les logs de tous les microservices ?
**Réponse modèle :** Chaque microservice (M1 à M6) publie des événements métiers (et éventuellement des logs d'audit) sur le topic exchange RabbitMQ `dropflow.events` avec des routing keys typées (ex: `m1.product.created`, `m3.order.confirmed`). M7 déclare une binding queue pour chaque type d'événement et dispose d'un `@RabbitListener` par microservice (un par membre de l'équipe). Ces listeners consomment les événements et les persiste dans MongoDB (schéma flexible adapté aux structures variées des événements de chaque service). MongoDB est idéal pour le logging car les documents d'événements ont des structures variables et les logs sont essentiellement en écriture (append-only). M7 expose ensuite des endpoints de consultation de logs pour l'ADMIN.  
**Qui peut répondre :** Membre responsable de M7 / Toute l'équipe

---

## 🔧 Git & Organisation

### Q33 : Comment avez-vous organisé vos branches Git ?
**Réponse modèle :** Nous utilisons une stratégie de branches basée sur **Git Flow** adapté : une branche `main` stable (code fonctionnel uniquement), une branche `develop` d'intégration continue, et des branches de fonctionnalités nommées `feature/m{n}-{feature-name}` (ex: `feature/m3-order-confirmation`) créées par chaque développeur pour son microservice. Les Pull Requests (ou Merge Requests) vers `develop` doivent être approuvées par au moins un autre membre de l'équipe. Les commits de chaque membre sont visibles dans l'historique Git, et l'enseignant a été ajouté comme collaborateur (accès lecture) sur le dépôt GitHub/GitLab selon les exigences du module.  
**Qui peut répondre :** Toute l'équipe / Responsable dépôt

---

### Q34 : Quel outil de gestion de tâches utilisez-vous et comment l'avez-vous organisé ?
**Réponse modèle :** Nous utilisons **Jira** (ou Trello/GitHub Projects selon le choix de l'équipe) pour suivre l'avancement des tâches. Le board est organisé par colonnes Kanban (Backlog, In Progress, In Review, Done) avec des tickets correspondant aux user stories de chaque microservice. Chaque ticket est assigné à un membre et lié à sa branche Git via la référence dans les messages de commit (ex: `[M3-12] Implement call attempt retry logic`). Un sprint de 2 semaines couvre le développement principal (sprints S4-S10 selon le planning de la slide 48). Cette organisation démontre une gestion de projet structurée conforme aux critères E5.  
**Qui peut répondre :** Toute l'équipe

---

### Q35 : Comment un enseignant peut-il lancer votre projet localement en 5 minutes ?
**Réponse modèle :** Le `README.md` du dépôt documente les prérequis (Docker Desktop, Java 17+, Git) et les étapes suivantes :  
1. `git clone <repo-url> && cd dropflow-hub-awd`  
2. `docker-compose up -d` — démarre Keycloak, RabbitMQ, PostgreSQL (×3), MySQL (×2), MongoDB (×2), Config Server, Eureka, API Gateway et les 7 microservices  
3. Attendre ~90s que tous les services soient healthy (vérifiable via `docker-compose ps`)  
4. Accéder à `http://localhost:8080` (API Gateway) et importer la collection Postman fournie dans `/Documentation`  
5. Se connecter via Keycloak (`http://localhost:8180`) avec les comptes de démonstration documentés  
Tous les ports, credentials de démo et variables d'environnement sont documentés dans le README. Un script `start.sh` / `start.bat` automatise les étapes 1-3.  
**Qui peut répondre :** Toute l'équipe / Responsable infrastructure

---

## ⚡ Questions bonus (susceptibles d'être posées)

### Q36 : Que se passe-t-il si RabbitMQ tombe ? Votre système est-il résilient ?
**Réponse modèle :** Si RabbitMQ est indisponible, les opérations synchrones (OpenFeign) continuent de fonctionner. Les opérations asynchrones (publication d'événements) échouent temporairement : les services qui publient reçoivent une exception et doivent soit retenter, soit stocker le message localement (Outbox Pattern). Dans notre implémentation, nous avons configuré la durabilité des queues (`durable: true`) et des messages (`persistent: true`) de sorte que les messages survivent à un redémarrage de RabbitMQ. En production, RabbitMQ serait déployé en cluster avec mirroring de queues pour la haute disponibilité. Pour ce projet académique, le Docker Compose inclut un healthcheck sur RabbitMQ et les microservices dépendants attendent sa disponibilité via `depends_on: condition: service_healthy`.  
**Qui peut répondre :** Toute l'équipe

---

### Q37 : Comment sécurisez-vous les communications inter-services en interne ?
**Réponse modèle :** Dans notre architecture Docker, les microservices communiquent sur un réseau Docker bridge privé (`dropflow-network`) non exposé à l'extérieur. Les appels OpenFeign entre services internes transitent par Eureka (nom de service, pas d'IP) et ne passent pas par l'API Gateway — ils ne requièrent donc pas de JWT client. Pour les appels OpenFeign, les services utilisent un `@FeignClient` configuré pour propager automatiquement le token JWT entrant (via `RequestInterceptor`) lorsque l'appel est initié dans le contexte d'une requête utilisateur authentifiée (M2→M1, M3→M1, etc.), ou utilisent un token de service interne (client_credentials OAuth) pour les appels initiés par des événements RabbitMQ (sans contexte utilisateur).  
**Qui peut répondre :** Toute l'équipe / Membre responsable de M7

---

*Fiche générée pour la Validation 1 AWD ESPRIT 2026-2027 – DropFlow Hub*  
*37 questions couvrant les 10 critères de notation (E1-E6, I1-I4)*
