# DropFlow Hub – Validation 1 Audit Report
**Module:** ESPRIT AWD 2026-2027  
**Presentation:** 49 slides (French), 7 microservices (M1-M7)  
**Auditor:** audit agent  
**Date:** 2026-11-13  

---

## Executive Summary

The presentation demonstrates strong technical depth — the bounded-context split, DB choices, and sync/async communication map are well-thought-out. The main risks before the defense are: (1) the deck is far too long for the 10-minute slot; (2) failure-resilience patterns (Resilience4j) are declared in the tech stack but invisible in every individual microservice slide; (3) the GitHub repo slide does not prove team hygiene (commits, task tool, teacher collaborator). These are all fixable before presentation day.

---

## 1. Criterion-by-Criterion Assessment

### E1 — Problem, Actors, Business Scenario, Value Proposition (Team, 2 pts)

| Item | Evidence (slides) | Detail |
|---|---|---|
| Problem statement | 3 | Fragmented e-commerce management; clear motivation |
| Actors (7 roles) | 4 | Administrateur, Fournisseur, E-commerçant, Agent confirmation, Agent logistique, Agent financier, Agent technique/marketing — complete |
| Main features | 8 | Actor-use-case-MS-entity matrix provides implicit feature list |
| Business scenario | 5, 6, 9 | UML activity diagram (full order lifecycle) + alternative cases table + TikTok Ads concrete scenario |
| Value proposition | 10 | Dedicated slide |

**Estimated level:** ✅ **Atteint**

**Gaps:**
- Slide 7 is a use-case diagram rendered as an image with no extractable text; if the image is blurry or low-contrast on the projector, actors and use cases will be unreadable.
- Alternative cases (slide 6) cover only 4 scenarios. Edge cases around partial stock (commande partiellement disponible) are not listed.

**Fixes:**
1. Export slide 7 at ≥150 dpi; use high-contrast colors; add a text legend table below the image.
2. Add at least one additional alternative case (e.g., *paiement échoué* or *stock insuffisant partiel*) to show completeness.

---

### E2 — Microservice Split by Bounded Context (Team, 2 pts)

| Microservice | Declared responsibility | Concern |
|---|---|---|
| M1 Catalogue B2B | Produits, Stock | — |
| M2 Tech Avancée | Intégration Boutiques / Webhooks | ⚠️ See below |
| M3 Call Center | Commandes & Qualification | ⚠️ See below |
| M4 Fulfillment | Logistique & Dépôt | — |
| M5 Finances | Transactions COD, Wallet | — |
| M6 SAV | Support & Échange | — |
| M7 Commun | IAM + Centralized Logging | ⚠️ See below |

**"Advanced Technology" microservice identification:**  
M2 is explicitly labeled *"Technologie avancée"* on slide 25. ✅ Identified.

**Estimated level:** ⚠️ **Partiellement atteint**

**Gaps & flags:**

1. **M2 ↔ M3 order-creation overlap (critical):** M2 receives webhooks from e-commerce platforms and fires `ORDER_IMPORTED` to M3. This means M2 creates/imports order data before M3 processes it. The slide deck does not clearly state *where the Order aggregate is owned*. If M2 persists any `Order` entity (even partially), it violates single-ownership. The event name `ORDER_IMPORTED` (M2→M3, slide 14) suggests M2 is acting as an order ingestion boundary — this is acceptable only if M2 never stores a canonical Order record and immediately delegates ownership to M3.

2. **M7 dual responsibility (IAM + Logging):** M7 owns both Identity & Access Management and Centralized Logging. These are two distinct sub-domains. While grouping shared infrastructure into one "Common" service is pragmatic, the evaluator may flag it as a bounded-context violation (a microservice should be responsible for one thing). The slide deck does not justify this grouping.

3. **M3 "Commandes & Call Center":** The name bundles call-center operations (phone qualification) with order management. These could be separate bounded contexts. The deck does not explicitly justify the aggregation.

**Fixes:**
1. Add a one-sentence note on slide 11 or 12: *"M2 is a pure integration adapter — it does not own any Order aggregate. It transforms webhook payloads into ORDER_IMPORTED events and forwards them. M3 is the single owner of the Order domain."*
2. Add a justification box on slide 11 for M7: *"M7 is a shared infrastructure service, not a business domain. IAM and logging are both cross-cutting non-functional concerns bundled for team economy and to avoid a 9th microservice."*
3. Rename or clarify M3 label: e.g., *"M3 Order Qualification & Call Center"* and add a sentence explaining why call qualification and order state belong in the same context (they share the same agent and the same Order lifecycle transitions).

---

### E3 — One DB per Microservice, Justified, All Interactions Labelled (Team, 2 pts)

| Microservice | DB | Justification (slide 13) | Distinct? |
|---|---|---|---|
| M1 | MongoDB | Flexible product schema | ✅ |
| M2 | PostgreSQL (JSONB) | Webhook payload storage | ✅ |
| M3 | MySQL | Relational transactions | ✅ |
| M4 | PostgreSQL | Shipment state machine | ✅ |
| M5 | PostgreSQL | ACID financial integrity | ✅ |
| M6 | MySQL | SAV structured data | ✅ |
| M7 | MongoDB | Logging (document-oriented) | ✅ |

All 7 microservices have a declared database. No shared databases are stated. ✅

**Sync/Async labelling (slide 14):**

| Channel | Type | Services | Justification in deck |
|---|---|---|---|
| OpenFeign | Sync | M2↔M1, M3↔M1, M5↔M1, M6↔M3 | Implied by OpenFeign choice; not explicitly stated per call |
| RabbitMQ | Async | M2→M3, M3→M4, M4→M5, M1-M6→M7 | Slide 15 explains exchange/queue setup |

**Estimated level:** ⚠️ **Partiellement atteint**

**Gaps:**
1. **M7 DB isolation unverified:** Slide 13 declares M7=MongoDB for logging. It is not stated whether this is a separate MongoDB instance or a shared cluster with M1 (M1 also uses MongoDB). If they share the same MongoDB instance/cluster (even different collections or databases inside the same host), evaluators will flag a shared-infrastructure violation.
2. **Sync justifications per call not explicit:** Slide 14 lists OpenFeign calls but does not explain *why* each is synchronous (e.g., "M3↔M1 stock check is synchronous because order confirmation requires real-time stock validation"). The justification is implied but not stated.
3. **Async justifications per event not explicit:** Similarly, `ORDER_IMPORTED`, `ORDER_CONFIRMED`, `PARCEL_DELIVERED` are listed without per-event justification (why async? what is the tolerance for eventual consistency here?).
4. **M4 and M5 both use PostgreSQL** — evaluators might ask whether they share an instance. Not addressed.

**Fixes:**
1. Add a footnote on slide 13 or a new sub-bullet: *"M1 MongoDB and M7 MongoDB are deployed as separate containers/instances — no shared cluster."*
2. Add a "Why?" column to the sync/async table on slide 14. Example: `M3↔M1 (sync) — real-time stock reservation required at order confirmation`.
3. Add the same column for async events: `ORDER_CONFIRMED (async) — logistics processing is decoupled from call-center to allow independent scaling`.
4. Clarify M4 and M5 PostgreSQL: *"Separate PostgreSQL container per service — same technology, different instances."*

---

### E4 — Architecture Diagram Completeness (Team, 2 pts)

**Primary evidence:** Slide 16 (image, no text extraction possible)  
**Supporting evidence:** Slide 17 (JWT/Keycloak), slide 15 (RabbitMQ), slide 48 (tech stack)

**Estimated level:** ⚠️ **Partiellement atteint** *(cannot fully confirm — image not readable by text extraction)*

**Required elements checklist:**

| Required element | Status | Evidence |
|---|---|---|
| Front-end | ❓ Unverified | Not confirmed from text; slide 16 is image-only |
| API Gateway | ❓ Unverified | Slide 17 implies Gateway validates JWT; slide 48 mentions Spring Cloud Gateway |
| Eureka (service discovery) | ❓ Unverified | Slide 48 mentions Eureka in tech stack; not confirmed in diagram |
| Config Server | ❓ Unverified | Slide 48 mentions Spring Cloud Config; **not confirmed in diagram** |
| Keycloak | ❓ Unverified | Slide 17 describes Keycloak role; presence in diagram unconfirmed |
| All 7 microservices | ❓ Unverified | Declared in slides 11-12 |
| All 7 DBs | ❓ Unverified | Declared in slide 13 |
| RabbitMQ message broker | ❓ Unverified | Slide 15 describes it; presence in diagram unconfirmed |
| Docker context | ❓ Unverified | Slide 16 title mentions "Docker Host"; content unreadable |
| JWT/Keycloak reconciled | ✅ Confirmed | Slide 17: Keycloak issues JWT, Gateway validates — consistent |

**Gaps:**
1. The architecture diagram (slide 16) is a raster image with no extractable text. If it is low-resolution or uses small fonts, it will be illegible on a projector.
2. **Config Server presence is uncertain** — it appears in the tech stack (slide 48) but was not visible/confirmed in the diagram description. This is a mandatory element for E4.
3. Docker context scope is unclear — the title says "Docker Host" but it is not confirmed whether all services (Gateway, Keycloak, all microservices, all DBs, RabbitMQ, Eureka, Config Server) are shown inside a Docker boundary.
4. Front-end is not confirmed in diagram or in individual slides. If the team built only a backend, this needs to be explicitly stated and justified.

**Fixes:**
1. Redraw or export the architecture diagram at high resolution (minimum 1920×1080 px for a 16:9 slide).
2. Verify and add a **Config Server** node to the diagram if missing; add a brief arrow showing all microservices pulling config from it.
3. Draw a visible **Docker Compose boundary** rectangle enclosing all containers.
4. If the front-end exists, show it outside the Docker boundary calling the API Gateway. If it does not exist yet, add a note: *"Front-end: React.js — in progress, not in scope for Validation 1."*
5. Add a text legend or component list as a sidebar table on slide 16 so evaluators can confirm all elements without zooming in.

---

### E5 — Repository, README, Task Management (Team, 2 pts)

**Primary evidence:** Slide 47

| Check | Status | Detail |
|---|---|---|
| Repo structure matches official template | ⚠️ Partial | Backend/, Frontend/, Documentation/, README.md present |
| Repo name format `dropflow-hub-<class>-<group>/` | ❌ **Fail** | Slide 47 shows `dropflow-hub-awd/` — missing `<class>-<group>` suffix |
| README content | ❓ Unverified | Slide 47 shows file exists; content not shown in deck |
| Task management tool | ❌ **Not mentioned** | No mention of Jira, Trello, GitHub Projects, or equivalent in slides 47-48 |
| Commits from all members | ❓ Unverified | No screenshot of commit history in deck |
| Teacher as collaborator | ❓ Unverified | Not mentioned anywhere in deck |

**Estimated level:** ❌ **Non atteint / Partiellement atteint**

**Gaps:**
1. **Repo name non-compliant:** `dropflow-hub-awd` does not follow the `dropflow-hub-<class>-<group>` naming convention. The evaluator will deduct points immediately on inspection.
2. **Task management tool absent from presentation:** The deck contains no screenshot or mention of a task-management tool (Jira board, GitHub Projects kanban, Trello, etc.).
3. **No commit history screenshot:** The deck does not show evidence that all team members have committed code.
4. **Teacher collaborator not shown:** No screenshot or confirmation that the teacher's GitHub account has been added as a collaborator.
5. **README content unverified:** The slide shows `README.md` exists but does not display its structure (required sections: project overview, tech stack, setup instructions, team members, etc.).

**Fixes:**
1. **Rename the repo immediately** to `dropflow-hub-<actual-class>-<actual-group>` (e.g., `dropflow-hub-awd-g3`). Update slide 47 to reflect the new name.
2. Add a slide or sub-slide showing a **GitHub Projects / Jira board screenshot** with tasks assigned to team members.
3. Add a **GitHub contributors / commit graph screenshot** proving all members have pushed commits.
4. Add a **screenshot of the repo Settings → Collaborators** page showing the teacher's account.
5. Add a **README structure preview** (even a collapsed tree view) on slide 47.

---

### E6 — Presentation Timing & Speaking Balance (Team + Timing, 2 pts)

**Primary evidence:** Slide structure (49 slides total), slide 2 (table of contents)

**Slide count analysis:**

| Section | Slides | Count |
|---|---|---|
| Group slides (problem → architecture) | 3-20 | 18 slides |
| M1 individual | 21-24 | 4 slides |
| M2 individual | 25-28 | 4 slides |
| M3 individual | 29-32 | 4 slides |
| M4 individual | 33-36 | 4 slides |
| M5 individual | 37-40 | 4 slides |
| M6 individual | 41-44 | 4 slides |
| M7 team common | 45-46 | 2 slides |
| Repo + Risk | 47 | 1 slide |
| Tech stack + Planning | 48 | 1 slide |
| Conclusion | 49 | 1 slide |
| **Total** | | **49 slides** |

**Time budget:** 10 minutes = 600 seconds  
**At 49 slides:** ~12 seconds per slide on average — **physically impossible** to present meaningfully.  
**Individual per-student rule:** 1 minute max per student × (say) 7 students = 7 minutes individual + 3 minutes group = 10 min total. At 4 slides/student × 7 students = 28 individual slides → ~15 seconds per individual slide.

**Estimated level:** ❌ **Non atteint**

**Gaps:**
1. **Slide count is 4–5× too high** for a 10-minute defense. Even if each slide takes only 10 seconds, 49 slides require 8+ minutes leaving no room for group presentation, transitions, or Q&A buffer.
2. **No timing notes or speaker script** are visible in the deck — no indication the team has rehearsed or timed their delivery.
3. **M7 has only 2 slides** (45-46) while M1-M6 each have 4 — M7's presenter is under-represented, which may create imbalance.

**Fixes:**
1. **Drastically reduce slide count.** Target: ≤25 slides total.
   - Group slides: collapse slides 3-10 into 3 slides (problem+actors, scenario+value, use-case summary).
   - Architecture: keep slides 11-17 but merge DB and comms into one slide each.
   - Individual slides: **each student gets exactly 1 slide** (or 2 max). One slide per student showing: class diagram thumbnail, one key API endpoint, one interaction diagram thumbnail.
   - Drop JSON example slides entirely — have them ready as backup if asked.
2. **Add speaker notes** with timing markers (e.g., "0:00 group start → 3:00 → M1 student → 4:00 → M2 …").
3. **Rehearse with a stopwatch** — do at least two full dry runs before the defense.
4. **Give M7 parity:** either expand to 3-4 slides on the same template as other services, or note that M7 is team-shared and its presenter gets explicit time in the group section.

---

### I1 — Individual: Responsibilities, Data Model, API (Individual, 2 pts per student)

**Evidence:** Slides 21-46 (4 slides per MS: class diagram, interaction schema, API table, JSON examples)

**General assessment across all microservices:**

| MS | Responsibilities | Entities/Data Model | DB Justified | REST API | HTTP Methods | Status Codes | Resource Naming |
|---|---|---|---|---|---|---|---|
| M1 | ✅ | ✅ class diagram | ✅ slide 13 | ✅ table | ⚠️ see below | ⚠️ see below | ⚠️ |
| M2 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |
| M3 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |
| M4 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |
| M5 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |
| M6 | ✅ | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |
| M7 | ✅ (IAM+Logging) | ✅ | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ |

**Estimated level:** ⚠️ **Partiellement atteint**

**Specific gaps:**

1. **Status code completeness:** The brief confirms API tables exist per MS. However, typical student presentations list only `200 OK` and `404 Not Found`. Missing from expected tables:
   - `201 Created` (POST endpoints)
   - `400 Bad Request` (validation errors)
   - `401 Unauthorized` / `403 Forbidden` (all secured endpoints)
   - `409 Conflict` (duplicate resource)
   - `422 Unprocessable Entity` (business rule violations)
   - `500 Internal Server Error`
   - `503 Service Unavailable` (circuit breaker open)
   - There is **no mention of 503 / fallback codes** in any individual slide — this is a gap evaluators will probe.

2. **HTTP method variety:** Standard REST requires GET, POST, PUT/PATCH, DELETE. Financial (M5) and logistics (M4) services may only have GET/POST if state transitions are modeled as actions rather than resource updates — this should be explicitly justified.

3. **Resource naming conventions:** REST URIs should be plural nouns, kebab-case, versioned (e.g., `/api/v1/products`, `/api/v1/orders/{id}/confirm`). The slide deck does not confirm whether URIs follow these conventions.

4. **JSON examples (slides 24, 28, 32, 36, 40, 44):** These show real data shapes but do not demonstrate error response bodies — evaluators asking "what does your API return when stock is insufficient?" need a 422 or 409 JSON error body example.

**Fixes (apply to each student's 4 slides):**
1. Add a "Status Codes" column to the API table: list at minimum `200/201`, `400`, `401`, `403`, `404`, `409`/`422`, `500`, `503`.
2. Add one error JSON example per MS (e.g., `{"error": "INSUFFICIENT_STOCK", "code": 422, "message": "..."}`).
3. Verify all URIs follow `/api/v1/<resource>` pattern and are shown in the API table.
4. For M4 and M5 especially, justify why certain HTTP methods are (or are not) used (e.g., PATCH for partial state update of shipment status).

---

### I2 — Individual: Interactions, Failure Handling (Individual, 2 pts per student)

**Evidence:** Slide 14 (global comms map), slide 15 (RabbitMQ setup), slides 18-20 (sequence diagrams), individual interaction schema slides (22, 26, 30, 34, 38, 42, 46)

**Estimated level:** ❌ **Non atteint / Partiellement atteint**

**Gaps:**

1. **Resilience4j NOT shown per microservice (critical gap):**  
   Slide 48 (tech stack) lists Resilience4j. Slide 14 describes OpenFeign sync calls. However, **none of the 7 individual MS slides** (confirmed from extraction summary) show a circuit breaker, fallback method, retry policy, or timeout configuration. Evaluators will ask: *"What happens when M1 (catalogue) is down and M3 tries to check stock synchronously via OpenFeign?"* — and there is no answer in the deck.

2. **Failure behavior entirely absent from individual slides:**  
   - No circuit breaker state diagram (CLOSED → OPEN → HALF-OPEN)
   - No fallback strategy (e.g., return cached stock value, reject order gracefully)
   - No retry policy (exponential backoff? max retries?)
   - No timeout values on OpenFeign calls

3. **Async failure handling partially addressed:**  
   Slide 15 shows dead-letter queue (DLQ) for RabbitMQ — this is good for async failure. However, **event replay** (what happens if a consumer crashes mid-processing?) is not explained. DLQ existence is necessary but not sufficient — the team needs to explain the replay/requeue strategy.

4. **Callers/callees explicit per MS:**  
   Slide 14 gives a global picture but it is a team slide. Each student's individual slide should list: *"Who calls me? (M2→M3 ORDER_IMPORTED), Who do I call? (M3→M1 sync stock check)"* as a table. This may exist on individual interaction schema slides but was not explicitly confirmed in the extraction summary.

5. **Data exchanged per interaction not shown per MS:** The event payload schemas (what fields are in `ORDER_IMPORTED`? what fields in the OpenFeign response from M1 to M3?) are not documented in individual slides.

**Fixes:**
1. **Add a "Failure Handling" section to each student's individual slide** (or add a 5th individual slide per student). Content:
   - Circuit breaker: `@CircuitBreaker(name="m1-catalogue", fallbackMethod="getDefaultStock")`
   - Retry: `@Retry(name="m1-catalogue", maxAttempts=3, waitDuration=500ms)`
   - Fallback behavior: what your service does when the dependency is unavailable
   - Timeout: `connectTimeout` and `readTimeout` values on OpenFeign client
2. **Add an event payload table** per student: event name, producer, consumer, payload fields, expected processing outcome, DLQ behavior.
3. **Add a caller/callee table** on each individual interaction slide: two columns — "Services that call me" and "Services I call" — with sync/async label and justification.
4. **Explain event replay strategy** on slide 15 or in each async interaction: *"If the ORDER_CONFIRMED consumer (M4) crashes, the message remains in RabbitMQ queue (durable=true, ACK not sent). On restart, M4 reprocesses it. Max 3 retries before DLQ."*

---

### I3 — Communication Skills (Individual, 2 pts per student)

**Evidence:** Cannot be assessed from slide content alone — this is evaluated live.

**Estimated level:** ❓ **Not assessable from deck** (live evaluation only)

**Guidance for preparation:**
1. **Each student must introduce themselves** before presenting their microservice (name, role in project, MS owned).
2. **Use the slide as a visual aid, not a script** — bullet points on slide, spoken explanation adds detail.
3. **Transition sentences between students** must be rehearsed: *"I'll now hand over to [Name] who will present M3…"*
4. **Posture:** Face the jury/evaluator, not the screen. Use a pointer or laser for diagrams.
5. **Language:** Maintain consistent French throughout; technical terms (OpenFeign, RabbitMQ, circuit breaker) are acceptable in English.

**Risk:** With 49 slides and 10 minutes, students will rush, which directly damages I3 scores. This reinforces the urgency of reducing slide count (see E6 fix).

---

### I4 — Ability to Answer Questions (Individual, 2 pts per student)

**Evidence:** Assessed live; preparation inferred from depth of deck content.

**Estimated level:** ⚠️ **Partiellement atteint** (deck is a good base, but resilience gaps create blind spots)

**High-probability jury questions and expected answers:**

| Question | Slide basis | Gap |
|---|---|---|
| "Why did you choose MySQL for M3 and not PostgreSQL?" | 13 | Slide gives reason; student must elaborate verbally |
| "What happens if M1 is down when M3 confirms an order?" | 14 | **No answer in deck** — Resilience4j fallback not shown |
| "What is in the ORDER_IMPORTED event payload?" | 14-15 | Payload not shown |
| "Who owns the Order aggregate — M2 or M3?" | 11-12 | **Ambiguous** (see E2 gap) |
| "Show me the circuit breaker configuration for M4→M5" | 48 | **Not shown** |
| "What is in your README?" | 47 | Slide does not show README content |
| "Have all team members committed to the repo?" | 47 | **Not shown** |
| "What task management tool did you use?" | — | **Not in deck** |
| "Why is M7 both IAM and logging?" | 11 | **Not justified** |
| "What does your API return when a product is out of stock?" | 21-24 | Error response not shown |

**Fixes:**
1. Prepare a **Q&A cheat sheet** (not shown to jury) with pre-rehearsed answers to the 10 questions above.
2. Every student must be able to explain **not just their own MS but adjacent ones** — especially M2/M3 boundary and M7 rationale.
3. Run a **mock jury session** (one team member plays evaluator) the day before.

---

## 2. Specific Conflict / Concern Register

| ID | Concern | Location | Severity | Resolution |
|---|---|---|---|---|
| C1 | M2 and M3 both touch Order domain — ownership ambiguous | Slides 11, 12, 14 | 🔴 High | Add explicit ownership statement: M3 owns Order aggregate; M2 is ingestion-only adapter |
| C2 | M7 dual responsibility (IAM + Logging) may violate bounded-context principle | Slides 11, 12, 45-46 | 🟡 Medium | Add explicit justification: "shared infrastructure, not business domain" |
| C3 | M1 (MongoDB) and M7 (MongoDB) may share infrastructure | Slides 13, 45-46 | 🟡 Medium | Confirm separate container per service; add note to slide 13 |
| C4 | M4 and M5 both use PostgreSQL — may share instance | Slide 13 | 🟡 Medium | Add note: separate PostgreSQL containers |
| C5 | Config Server in tech stack (slide 48) but not confirmed in architecture diagram (slide 16) | Slides 16, 48 | 🔴 High | Verify diagram includes Config Server; add if missing |
| C6 | Resilience4j in tech stack but absent from all individual MS slides | Slides 14, 48 | 🔴 High | Add failure-handling section to each student's individual slides |
| C7 | Repo name `dropflow-hub-awd` does not match naming convention | Slide 47 | 🔴 High | Rename repo to `dropflow-hub-<class>-<group>` before defense |
| C8 | 49 slides for a 10-minute defense — structurally impossible | All slides | 🔴 High | Cut to ≤25 slides; merge group slides, reduce individual to 1-2 per student |
| C9 | No task management tool shown | Slide 47 | 🟡 Medium | Add screenshot of Jira/GitHub Projects to slide 47 |
| C10 | Teacher collaborator not demonstrated | Slide 47 | 🟡 Medium | Add screenshot of GitHub collaborator settings |

---

## 3. Score Estimate — BEFORE Changes

> These scores reflect what a typical evaluator would award based solely on the extracted deck content, before any of the fixes above are applied.

| Criterion | Max | Estimated Score | Rationale |
|---|---|---|---|
| **E1** | 2 | **1.75** | Strong content; minor gap on use-case image quality and alternative cases |
| **E2** | 2 | **1.25** | Bounded context map solid; M2/M3 overlap and M7 dual role not justified |
| **E3** | 2 | **1.25** | All 7 DBs distinct; no shared-DB proof; sync/async justifications absent |
| **E4** | 2 | **1.00** | Diagram is image-only — cannot verify Config Server, Docker scope, front-end; JWT reconciliation confirmed |
| **E5** | 2 | **0.50** | Repo name wrong; no task tool, no commit proof, no teacher collaborator |
| **E6** | 2 | **0.50** | 49 slides for 10 minutes is structurally failing; no timing evidence |
| **I1** | 2 | **1.25** | API tables present; status codes and error bodies likely incomplete |
| **I2** | 2 | **0.75** | Global comms map good; Resilience4j invisible in individual slides; event payloads missing |
| **I3** | 2 | **1.25** | Cannot assess live; risk from time pressure; deducted for structural issues |
| **I4** | 2 | **1.00** | Good deck depth; too many blind spots (resilience, Order ownership, repo) |
| **TOTAL** | **20** | **10.50 / 20** | |

**After applying all fixes above, estimated achievable score: 16–17 / 20.**

---

## 4. Priority Fix Checklist (Ordered by Impact)

| Priority | Fix | Effort | Impact on Score |
|---|---|---|---|
| 🔴 1 | Reduce deck to ≤25 slides; add speaker timing notes | Medium | +1.5 (E6) |
| 🔴 2 | Rename GitHub repo to `dropflow-hub-<class>-<group>` | Low | +0.5 (E5) |
| 🔴 3 | Add Resilience4j circuit breaker/fallback to each individual MS slide | High | +1.25 (I2) |
| 🔴 4 | Verify architecture diagram includes Config Server; redraw at high resolution | Medium | +0.75 (E4) |
| 🟡 5 | Add explicit M2/M3 Order ownership statement | Low | +0.5 (E2, I4) |
| 🟡 6 | Add M7 dual-role justification | Low | +0.25 (E2) |
| 🟡 7 | Add sync/async per-call justification column to slide 14 | Low | +0.5 (E3) |
| 🟡 8 | Add GitHub task-management tool screenshot + commit history + collaborator | Low | +0.75 (E5) |
| 🟡 9 | Add status codes (400/401/403/409/422/500/503) to all API tables | Medium | +0.5 (I1) |
| 🟢 10 | Add error JSON response body example per MS | Low | +0.25 (I1, I4) |
| 🟢 11 | Add event payload schema table per MS | Low | +0.25 (I2, I4) |
| 🟢 12 | Conduct mock jury Q&A session | Low | +0.5 (I4, I3) |

---

*Report generated by audit agent. All assessments are based on text extraction from the 49-slide deck. Slide 7 (use-case diagram) and slide 16 (architecture diagram) are image-only and could not be verified — in-person review of these two slides is strongly recommended before the defense.*
