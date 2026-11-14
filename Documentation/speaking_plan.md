# Plan de Prise de Parole — DropFlow Hub
### Validation 1 · Module AWD · ESPRIT 2026-2027
> **Contrainte : 10 minutes exactes (600 secondes) · 1 min max par étudiant pour son microservice**

---

## 1. Budget-temps global

| # | Slide | Présentateur | Durée (s) | Durée (mm:ss) | Cumul |
|---|-------|-------------|-----------|---------------|-------|
| 1 | Titre & Présentation équipe | **Membre 1** | 10 s | 0:10 | 0:10 |
| 2 | Problème & Acteurs | **Membre 2** | 43 s | 0:43 | 0:53 |
| 3 | Fonctionnalités principales & Diagramme use-case | **Membre 3** | 35 s | 0:35 | 1:28 |
| 4 | Scénario métier + cas alternatifs | **Membre 4** | 45 s | 0:45 | 2:13 |
| 5 | Découpage microservices & responsabilités | **Membre 5** | 45 s | 0:45 | 2:58 |
| 6 | Bases de données + communications sync/async | **Membre 6** | 45 s | 0:45 | 3:43 |
| 7 | Architecture globale | **Membre 1** | 35 s | 0:35 | 4:18 |
| 8 | Sécurité — Keycloak / JWT / Gateway | **Membre 2** | 30 s | 0:30 | 4:48 |
| 9 | M1 – Produits & Catalogue *(individuel)* | **Membre 1** | 42 s | 0:42 | 5:30 |
| 10 | M2 – Intégration Boutiques *(individuel)* | **Membre 2** | 42 s | 0:42 | 6:12 |
| 11 | M3 – Commandes & Call Center *(individuel)* | **Membre 3** | 42 s | 0:42 | 6:54 |
| 12 | M4 – Logistique & Fulfillment *(individuel)* | **Membre 4** | 42 s | 0:42 | 7:36 |
| 13 | M5 – Transactions COD *(individuel)* | **Membre 5** | 42 s | 0:42 | 8:18 |
| 14 | M6 – Échanges & SAV *(individuel)* | **Membre 6** | 42 s | 0:42 | 9:00 |
| 15 | M7 – Service commun (IAM + Logging) | **Membre 1** | 25 s | 0:25 | 9:25 |
| 16 | Stack technique | **Membre 3** | 10 s | 0:10 | 9:35 |
| 17 | Repo Git & gestion de projet | **Membre 4** | 10 s | 0:10 | 9:45 |
| 18 | Planning, risques & conclusion | **Membre 5** | 15 s | 0:15 | **10:00** |

**✅ Total : 600 secondes — exactement 10 minutes**

> **Note sur la coupe** : La slide "Value Proposition" (slide 10 du deck actuel) est absorbée dans le scénario métier (slide 4). La proposition de valeur est mentionnée en une phrase par Membre 4, ce qui économise ~30 s sans perdre de contenu E1.

---

## 2. Script détaillé par section

---

### Slide 1 — Titre *(Membre 1 · 10 s)*

**Points clés :**
- Énoncer le nom du projet et la problématique en une phrase d'accroche.

**Script indicatif :**
> « Bonjour, nous sommes l'équipe DropFlow Hub. Notre projet répond à un défi concret : simplifier et automatiser toute la chaîne dropshipping, du catalogue au remboursement. »

**Transition vers Membre 2 :**
> « Pour comprendre pourquoi ce projet existe, [Membre 2] va vous exposer le problème et les acteurs concernés. »

---

### Slide 2 — Problème & Acteurs *(Membre 2 · 43 s)*

**Points clés :**
- Fragmentations actuelles : gestion dispersée (commandes, stock, livraison, SAV dans des outils séparés).
- 7 acteurs : Administrateur, Fournisseur, E-commerçant, Agent confirmation, Agent logistique, Agent financier, Agent technique/marketing.
- Impact concret : erreurs, délais, perte de visibilité.

**Script indicatif :**
> « Aujourd'hui, un e-commerçant jongle avec de multiples outils déconnectés — catalogues fournisseurs, boutiques Shopify ou WooCommerce, suivi logistique, remboursements. Résultat : des erreurs, des délais et une expérience client dégradée. DropFlow Hub centralise ces flux autour de sept acteurs métier clairement identifiés : l'administrateur, le fournisseur, l'e-commerçant, l'agent de confirmation, l'agent logistique, l'agent financier et l'agent technique. »

**Transition vers Membre 3 :**
> « Pour voir comment nous répondons à ces besoins fonctionnellement, [Membre 3] présente les grandes fonctionnalités et le diagramme de cas d'usage. »

---

### Slide 3 — Fonctionnalités principales & Use-case *(Membre 3 · 35 s)*

**Points clés :**
- 6 grandes familles fonctionnelles (catalogue, intégration boutiques, gestion commandes/call, fulfillment, transactions, SAV).
- Diagramme use-case : chaque acteur est lié à ses cas d'usage.

**Script indicatif :**
> « Le diagramme de cas d'usage illustre six grandes familles fonctionnelles : gestion du catalogue B2B, intégration multi-boutiques, qualification des commandes via call center, fulfillment logistique, règlement COD et gestion des échanges SAV. Chaque acteur dispose d'un périmètre clair sans chevauchement. »

**Transition vers Membre 4 :**
> « Pour ancrer cela dans le réel, [Membre 4] va dérouler un scénario métier concret avec ses cas alternatifs. »

---

### Slide 4 — Scénario métier + cas alternatifs *(Membre 4 · 45 s)*

**Points clés :**
- Scénario fil rouge : campagne TikTok Ads → 100 montres, 80 commandes → qualification → livraison → encaissement COD.
- Proposition de valeur en une phrase (absorbe la slide VP supprimée).
- 4 cas alternatifs : client injoignable, commande annulée, fournisseur manquant, remboursement.

**Script indicatif :**
> « Imaginons une campagne TikTok : 100 montres en stock, 80 commandes reçues. Le flux nominal va de l'importation automatique de la commande jusqu'au règlement COD du livreur — DropFlow Hub automatise chaque étape et élimine les saisies manuelles. Mais tout ne se passe pas toujours comme prévu : si un client est injoignable, la commande passe en attente après trois tentatives ; en cas de fournisseur manquant, la livraison est bloquée et l'e-commerçant est alerté ; un remboursement déclenche un flux SAV structuré. »

**Transition vers Membre 5 :**
> « Pour gérer toute cette complexité, nous avons découpé le système en microservices autonomes. [Membre 5] vous explique ce découpage. »

---

### Slide 5 — Découpage microservices & responsabilités *(Membre 5 · 45 s)*

**Points clés :**
- 7 microservices, chacun sur son bounded context.
- Matrice responsabilités : M1 Produits-Stock, M2 Intégration Boutiques, M3 Commandes & Call, M4 Logistique & Dépôt, M5 Transactions & Wallet, M6 Échanges & SAV, M7 Commun.
- Pas de chevauchement de responsabilités (critère E2).
- M2 est identifié comme la "technologie avancée" (webhooks Shopify/WooCommerce).

**Script indicatif :**
> « Notre système est découpé en sept microservices alignés sur sept bounded contexts métier. Chacun possède ses propres responsabilités sans chevauchement : M1 gère le catalogue et les réservations de stock, M2 — notre microservice à technologie avancée — intègre les boutiques Shopify et WooCommerce via des webhooks en temps réel, M3 qualifie les commandes via le call center, M4 pilote la logistique et les dépôts, M5 gère les transactions COD et les wallets, M6 traite les échanges et le SAV, et M7 est le service commun d'identité et de logs. »

**Transition vers Membre 6 :**
> « Ces microservices ne sont pas isolés — ils communiquent selon des règles précises. [Membre 6] détaille les bases de données et les modes de communication. »

---

### Slide 6 — Bases de données + communications sync/async *(Membre 6 · 45 s)*

**Points clés :**
- Une BDD par microservice, aucune base partagée (critère E3).
- Choix justifiés : MongoDB (M1 — schéma flexible), PostgreSQL (M2, M4, M5 — JSONB/ACID/états), MySQL (M3, M6 — transactions relationnelles), MongoDB (M7 — logging souple).
- Sync (OpenFeign) : M2↔M1 SKU, M3↔M1 stock, M5↔M1 prix, M6↔M3 statut commande.
- Async (RabbitMQ) : ORDER_IMPORTED, ORDER_CONFIRMED, PARCEL_DELIVERED, et logs vers M7.

**Script indicatif :**
> « Chaque microservice possède sa propre base de données isolée : MongoDB pour le catalogue et les logs, PostgreSQL pour l'intégration boutiques, la logistique et les finances, MySQL pour les commandes et le SAV. Les communications synchrones via OpenFeign couvrent les lectures critiques — SKU, stock, prix, statut commande. Les flux asynchrones via RabbitMQ transportent les événements métier — ORDER_IMPORTED, ORDER_CONFIRMED, PARCEL_DELIVERED — avec dead-letter queue et retry pour la résilience. »

**Transition vers Membre 1 :**
> « Tout cela s'assemble dans une architecture globale. [Membre 1] vous la présente. »

---

### Slide 7 — Architecture globale *(Membre 1 · 35 s)*

**Points clés :**
- Front-end → API Gateway → Eureka (service discovery) → Config Server → Microservices → DBs → RabbitMQ.
- Keycloak en périphérie pour l'authentification.
- Conteneurisé sous Docker.

**Script indicatif :**
> « L'architecture Spring Cloud place un API Gateway en entrée unique, avec Eureka pour la découverte de services et un Config Server centralisé. Chaque microservice tourne dans son conteneur Docker avec sa base de données dédiée. Keycloak gère l'authentification en amont, et RabbitMQ assure le bus d'événements asynchrones. »

**Transition vers Membre 2 :**
> « La sécurité est un point critique. [Membre 2] détaille notre stratégie JWT et Keycloak. »

---

### Slide 8 — Sécurité — Keycloak / JWT / Gateway *(Membre 2 · 30 s)*

**Points clés :**
- Keycloak émet les tokens JWT ; le Gateway les valide.
- 7 rôles : ADMIN, SUPPLIER, MERCHANT, CONFIRMATION_AGENT, LOGISTICS_AGENT, FINANCE_AGENT, SUPPORT_AGENT.
- Aucun microservice n'expose d'endpoint public non sécurisé.

**Script indicatif :**
> « Keycloak centralise l'authentification et émet des JWT avec le rôle de l'utilisateur. Le Gateway valide chaque token avant de router la requête ; aucun microservice n'est accessible directement. Nous définissons sept rôles métier correspondant exactement aux acteurs identifiés en début de présentation. »

**Transition vers slides individuels :**
> « Chaque membre de l'équipe va maintenant présenter son microservice en détail. Membre 1, pour M1. »

---

## 3. Monologues individuels (42 secondes chacun — max 60 s)

> **Règle d'or :** Responsabilités → Entités & données → BDD justifiée → API clés → Interactions. Pas plus, pas moins.

---

### M1 — Produits & Catalogue *(Membre 1 · 42 s)*

**Points à couvrir :**
- Responsabilité : catalogue B2B, gestion des SKUs, réservation de stock.
- Entités : Produit, Variante, StockEntry.
- BDD : MongoDB — schéma de produit flexible (attributs variables selon le fournisseur).
- API clés : `GET /products`, `POST /products`, `PUT /products/{sku}/reserve-stock`.
- Appelé par M2 (SKU lookup), M3 (réservation stock), M5 (prix).

**Script indicatif (42 s) :**
> « M1 gère le catalogue B2B : produits, variantes et réservations de stock. J'utilise MongoDB car le schéma produit est flexible — les attributs varient selon le fournisseur. Mes entités principales sont Produit, Variante et StockEntry. L'API expose GET et POST /products pour la consultation et l'ajout, et PUT /products/{sku}/reserve-stock pour les réservations. M2 me consulte pour les SKUs, M3 pour réserver le stock avant confirmation, et M5 pour les prix. »

---

### M2 — Intégration Boutiques *(Membre 2 · 42 s)*

**Points à couvrir :**
- Responsabilité : réception webhooks Shopify et WooCommerce, normalisation, publication d'événements.
- Technologie avancée : webhooks temps réel + signature HMAC.
- Entités : ShopOrder, WebhookEvent, Shop.
- BDD : PostgreSQL avec JSONB (payload webhook variable).
- Publie `ORDER_IMPORTED` sur RabbitMQ → M3.

**Script indicatif (42 s) :**
> « M2 est notre microservice à technologie avancée : il reçoit et valide les webhooks Shopify et WooCommerce en temps réel via signature HMAC. J'utilise PostgreSQL avec JSONB pour stocker des payloads hétérogènes. Mes entités sont ShopOrder, WebhookEvent et Shop. À chaque nouvelle commande valide, je publie un événement ORDER_IMPORTED sur RabbitMQ que M3 consomme pour démarrer la qualification. »

---

### M3 — Commandes & Call Center *(Membre 3 · 42 s)*

**Points à couvrir :**
- Responsabilité : qualification commandes, gestion tentatives d'appel, statuts.
- Entités : Order, CallAttempt, Agent.
- BDD : MySQL — transactions relationnelles, historique d'appels structuré.
- Consomme `ORDER_IMPORTED` ; publie `ORDER_CONFIRMED` → M4.
- Appel synchrone vers M1 pour vérifier stock.

**Script indicatif (42 s) :**
> « M3 qualifie les commandes via le call center. Je consomme l'événement ORDER_IMPORTED de M2 et j'enregistre chaque tentative d'appel dans MySQL — base bien adaptée aux transactions relationnelles et à l'historique structuré. Mes entités : Order, CallAttempt, Agent. Après trois tentatives infructueuses, la commande est mise en attente ; en cas de confirmation, je vérifie le stock auprès de M1 par OpenFeign, puis je publie ORDER_CONFIRMED vers M4. »

---

### M4 — Logistique & Fulfillment *(Membre 4 · 42 s)*

**Points à couvrir :**
- Responsabilité : ramassage fournisseur, emballage, expédition, suivi colis.
- Entités : Shipment, Parcel, SupplierPickup.
- BDD : PostgreSQL — états de livraison, transitions de statut (machine d'état).
- Consomme `ORDER_CONFIRMED` ; publie `PARCEL_DELIVERED` → M5.

**Script indicatif (42 s) :**
> « M4 pilote toute la chaîne physique : ramassage chez le fournisseur, emballage, expédition et suivi. J'utilise PostgreSQL car les transitions d'état du colis — de PICKUP_REQUESTED à DELIVERED — forment une machine d'état que PostgreSQL gère proprement avec contraintes. Mes entités sont Shipment, Parcel et SupplierPickup. Je consomme ORDER_CONFIRMED et, dès livraison confirmée, je publie PARCEL_DELIVERED vers M5 pour déclencher le règlement financier. »

---

### M5 — Transactions COD *(Membre 5 · 42 s)*

**Points à couvrir :**
- Responsabilité : encaissement COD, gestion wallets, réconciliation financière.
- Entités : Transaction, Wallet, Settlement.
- BDD : PostgreSQL — ACID indispensable pour les opérations financières.
- Consomme `PARCEL_DELIVERED` ; appel synchrone M1 pour le prix de référence.

**Script indicatif (42 s) :**
> « M5 gère l'encaissement Cash-On-Delivery et les wallets des agents. PostgreSQL s'impose ici : toute opération financière exige des garanties ACID strictes. Mes entités sont Transaction, Wallet et Settlement. Je consomme PARCEL_DELIVERED pour déclencher le règlement, je consulte M1 par OpenFeign pour le prix de référence, et je mets à jour le wallet de l'agent logistique. En cas d'échec, Resilience4j déclenche un circuit breaker pour éviter les doubles encaissements. »

---

### M6 — Échanges & SAV *(Membre 6 · 42 s)*

**Points à couvrir :**
- Responsabilité : retours, inspection, remplacement ou remboursement.
- Entités : ReturnRequest, Inspection, Replacement.
- BDD : MySQL — données SAV structurées, relations claires entre retour et commande d'origine.
- Appel synchrone M3 pour le statut de la commande d'origine.

**Script indicatif (42 s) :**
> « M6 gère les échanges et le SAV : retour produit, inspection qualité, remplacement ou remboursement. J'utilise MySQL car les données SAV sont très relationnelles — un retour référence toujours une commande et un produit existants. Mes entités : ReturnRequest, Inspection, Replacement. Je consulte M3 par OpenFeign pour vérifier le statut de la commande d'origine avant d'accepter un retour, et je log chaque événement vers M7. »

---

## 4. Slide 15 — M7 Service commun *(Membre 1 · 25 s)*

**Points à couvrir :**
- IAM centralisé (Keycloak-backed), logging centralisé.
- MongoDB pour logs (schéma flexible, volume élevé).
- Tous les microservices publient vers M7 en async.

**Script indicatif :**
> « M7 est notre socle commun : il centralise l'Identity & Access Management en complément de Keycloak, et agrège les logs de tous les microservices via RabbitMQ en MongoDB. Ce choix de MongoDB permet d'absorber des structures de logs hétérogènes à fort volume sans migration de schéma. »

**Transition vers Membre 3 :**
> « Pour récapituler notre environnement technique, [Membre 3]. »

---

## 5. Slides finaux (closing run)

### Slide 16 — Stack technique *(Membre 3 · 10 s)*
> « Notre stack : Spring Boot 3, Spring Cloud (Gateway, Eureka, Config), OpenFeign, RabbitMQ, Keycloak, Docker Compose, React pour le front-end, et les bases de données que vous avez vues. »
→ **Transition :** « [Membre 4] pour le repo et l'organisation. »

### Slide 17 — Repo Git & gestion de projet *(Membre 4 · 10 s)*
> « Notre repo GitHub `dropflow-hub-awd/` suit le template officiel — Backend, Frontend, Documentation. Nous utilisons un tableau Jira pour le suivi, et notre enseignant est collaborateur sur le repo. »
→ **Transition :** « [Membre 5] pour clore. »

### Slide 18 — Planning, risques & conclusion *(Membre 5 · 15 s)*
> « Nous avons suivi un planning en sept sprints, S4 à S10. Les risques principaux — disponibilité des APIs externes et cohérence éventuelle en async — sont couverts respectivement par des stubs et par la dead-letter queue RabbitMQ. DropFlow Hub est opérationnel et prêt pour vos questions. »

---

## 6. Vérification du total

```
Slide  1 (M1) :  10 s
Slide  2 (M2) :  43 s
Slide  3 (M3) :  35 s
Slide  4 (M4) :  45 s
Slide  5 (M5) :  45 s
Slide  6 (M6) :  45 s
Slide  7 (M1) :  35 s
Slide  8 (M2) :  30 s
Slides 9-14   : 252 s  (6 × 42 s)
Slide 15 (M1) :  25 s
Slide 16 (M3) :  10 s
Slide 17 (M4) :  10 s
Slide 18 (M5) :  15 s
─────────────────────
TOTAL         : 600 s = 10 min 00 s  ✅
```

---

## 7. Répartition du temps de parole par membre

| Membre | Slides présentées | Durée totale |
|--------|-------------------|-------------|
| Membre 1 | 1 + 7 + 9 (M1 indiv.) + 15 | 10+35+42+25 = **112 s** |
| Membre 2 | 2 + 8 + 10 (M2 indiv.) | 43+30+42 = **115 s** |
| Membre 3 | 3 + 11 (M3 indiv.) + 16 | 35+42+10 = **87 s** |
| Membre 4 | 4 + 12 (M4 indiv.) + 17 | 45+42+10 = **97 s** |
| Membre 5 | 5 + 13 (M5 indiv.) + 18 | 45+42+15 = **102 s** |
| Membre 6 | 6 + 14 (M6 indiv.) | 45+42 = **87 s** |
| **Total** | | **600 s** |

> **Écart min/max : 87 s – 115 s.** Acceptable pour 6 membres sur 10 minutes. Si l'enseignant exige un équilibre plus strict, Membre 3 et Membre 6 peuvent récupérer 5–10 s chacun depuis les slides groupe en développant légèrement leurs transitions.

---

## 8. Conseils pour tenir le temps

### Pendant la préparation
- **Chronométrez chaque segment séparément** au moins deux fois avant la soutenance.
- **Chaque membre prépare une version courte (−5 s) et une version longue (+5 s)** de son monologue individuel, pour s'adapter en direct si un précédent intervenant a pris du retard.
- Désignez un **"time keeper"** (par exemple Membre 3 ou Membre 4) qui surveille une montre et fait un signal discret (doigt levé) à 7 minutes pour les prévenir s'il faut accélérer les slides finaux.

### Pendant la présentation
- **Transitions soignées :** Ne pas improviser les transitions ; les phrases de transition ci-dessus sont à apprendre presque mot pour mot. Elles économisent du temps et donnent une image professionnelle.
- **Pas d'excuse, pas de préambule :** Commencer chaque monologue directement sur le fond (pas de "donc euh, pour ma part je vais vous parler de…").
- Si un membre est en retard, le slide 18 (conclusion) peut être réduit à 5 s (juste la phrase finale "DropFlow Hub est prêt pour vos questions.").

### Transition vers la Q&A
- Dès la fin du slide 18, Membre 5 dit :
  > « Voilà pour notre présentation. Toute l'équipe est disponible pour répondre à vos questions. »
- **Tous les membres restent debout** face au jury et se regardent brièvement pour signaler qu'ils sont prêts.
- En Q&A, le membre concerné répond **d'abord seul** (I4 est noté individuellement) ; les autres complètent seulement si nécessaire.
- Si la question porte sur une partie inter-microservices, le membre dont le service est **appelant** répond en premier.

---

## 9. Récapitulatif des coupures effectuées par rapport au budget initial

| Poste | Budget initial | Budget révisé | Économie |
|-------|---------------|---------------|----------|
| Slide 1 Titre | 20 s | 10 s | −10 s |
| Slide 2 Problème | 60 s | 43 s | −17 s |
| Slide 3 Use-cases | 50 s | 35 s | −15 s |
| Slide 4 Scénario | 60 s | 45 s | −15 s |
| Slide 5 Découpage | 60 s | 45 s | −15 s |
| Slide 6 BDD+Comms | 60 s | 45 s | −15 s |
| Slide 7 Architecture | 50 s | 35 s | −15 s |
| Slide 8 Sécurité | 40 s | 30 s | −10 s |
| Slides 9-14 Individuels | 6×55 s = 330 s | 6×42 s = 252 s | −78 s |
| Slide 15 M7 Commun | 40 s | 25 s | −15 s |
| Slide 16 Stack | 20 s | 10 s | −10 s |
| Slide 17 Repo Git | 20 s | 10 s | −10 s |
| Slide 18 Conclusion | 20 s | 15 s | −5 s |
| *Slide VP supprimée* | *30 s estimés* | *Absorbée dans slide 4* | −30 s |
| **Total** | **830 s** | **600 s** | **−230 s** |

> **Note importante :** Les monologues individuels restent bien **en dessous de la limite de 60 secondes** (42 s chacun), avec 18 secondes de marge de sécurité par membre. Cette marge absorbe les hésitations naturelles ou les questions du jury pendant la présentation.

---

*Document produit pour l'équipe DropFlow Hub — Validation 1, module AWD, ESPRIT 2026-2027.*
