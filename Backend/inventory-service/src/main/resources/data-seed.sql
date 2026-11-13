-- ============================================================================
-- DropWear - Seed Data for PostgreSQL (inventory_db)
-- Microservice: inventory-service (Port 8084)
-- Responsable: Yasser Chebbi
-- Description: Jeu de données réaliste pour vêtements streetwear / boxy / oversize
-- ============================================================================

-- Nettoyage préalable (ordre respectant les clés étrangères)
TRUNCATE TABLE reservation_lines CASCADE;
TRUNCATE TABLE stock_reservations CASCADE;
TRUNCATE TABLE stock_movements CASCADE;
TRUNCATE TABLE restock_alerts CASCADE;
TRUNCATE TABLE processed_events CASCADE;
TRUNCATE TABLE stock_items CASCADE;

-- ----------------------------------------------------------------------------
-- 1. ARTICLES EN STOCK (StockItem)
-- Articles dropshipping réels avec variantes (Tailles S, M, L, XL, Coloris)
-- ----------------------------------------------------------------------------
INSERT INTO stock_items (id, sku, product_id, supplier_id, quantity_on_hand, quantity_reserved, reorder_threshold, updated_at, version) VALUES
-- Hoodie Boxy Vintage - Noir Délavé (Fournisseur 1 : Atelier Textile Tunis)
('a0000001-0000-0000-0000-000000000001', 'HOOD-BOXY-BLK-S',  'prod_hoodie_01', 'f0000001-0000-0000-0000-000000000001', 25, 2, 5, NOW(), 0),
('a0000001-0000-0000-0000-000000000002', 'HOOD-BOXY-BLK-M',  'prod_hoodie_01', 'f0000001-0000-0000-0000-000000000001', 40, 5, 8, NOW(), 0),
('a0000001-0000-0000-0000-000000000003', 'HOOD-BOXY-BLK-L',  'prod_hoodie_01', 'f0000001-0000-0000-0000-000000000001', 30, 3, 5, NOW(), 0),
('a0000001-0000-0000-0000-000000000004', 'HOOD-BOXY-BLK-XL', 'prod_hoodie_01', 'f0000001-0000-0000-0000-000000000001',  6, 3, 5, NOW(), 0), -- Disponible = 3 <= Seuil (Alerte attendue !)

-- T-Shirt Oversize Heavyweight 240 GSM - Blanc Craie (Fournisseur 1)
('a0000002-0000-0000-0000-000000000001', 'TSHIRT-OVS-WHT-S', 'prod_tshirt_02', 'f0000001-0000-0000-0000-000000000001', 50, 0, 10, NOW(), 0),
('a0000002-0000-0000-0000-000000000002', 'TSHIRT-OVS-WHT-M', 'prod_tshirt_02', 'f0000001-0000-0000-0000-000000000001', 80, 8, 10, NOW(), 0),
('a0000002-0000-0000-0000-000000000003', 'TSHIRT-OVS-WHT-L', 'prod_tshirt_02', 'f0000001-0000-0000-0000-000000000001', 65, 4, 10, NOW(), 0),
('a0000002-0000-0000-0000-000000000004', 'TSHIRT-OVS-WHT-XL','prod_tshirt_02', 'f0000001-0000-0000-0000-000000000001', 20, 0, 5, NOW(), 0),

-- Pantalon Cargo Baggy - Vert Olive (Fournisseur 2 : Sousse Denim & Co)
('a0000003-0000-0000-0000-000000000001', 'CARGO-BGGY-OLV-M', 'prod_cargo_03',  'f0000002-0000-0000-0000-000000000002', 15, 2, 5, NOW(), 0),
('a0000003-0000-0000-0000-000000000002', 'CARGO-BGGY-OLV-L', 'prod_cargo_03',  'f0000002-0000-0000-0000-000000000002', 12, 1, 4, NOW(), 0),

-- Veste Zip Canvas Oversize - Marron Vintage (Fournisseur 2) - Stock Faible
('a0000004-0000-0000-0000-000000000001', 'JACKET-ZIP-BRN-L',  'prod_jacket_04', 'f0000002-0000-0000-0000-000000000002',  4, 2, 5, NOW(), 0); -- Disponible = 2 <= Seuil

-- ----------------------------------------------------------------------------
-- 2. RÉSERVATIONS TEMPORAIRES (StockReservation + ReservationLine)
-- Exemples : commande panier en cours de validation (ACTIVE), confirmée, expirée
-- ----------------------------------------------------------------------------
INSERT INTO stock_reservations (id, reference_type, reference_id, idempotency_key, status, expires_at, created_at, confirmed_at) VALUES
-- Réservation 1 : Commande en cours de validation téléphonique / paiement
('b0000001-0000-0000-0000-000000000001', 'COMMANDE', 'ord_1001', 'idemp_ord_1001_v1', 'ACTIVE', NOW() + INTERVAL '25 minutes', NOW() - INTERVAL '5 minutes', NULL),
-- Réservation 2 : Commande validée et expédiée
('b0000002-0000-0000-0000-000000000002', 'COMMANDE', 'ord_1000', 'idemp_ord_1000_v1', 'CONFIRMEE', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '3 hours', NOW() - INTERVAL '2 hours'),
-- Réservation 3 : Réservation pour échange SAV RMA
('b0000003-0000-0000-0000-000000000003', 'ECHANGE', 'rma_501',  'idemp_rma_501_v1',  'ACTIVE', NOW() + INTERVAL '12 days', NOW() - INTERVAL '2 days', NULL);

INSERT INTO reservation_lines (id, stock_reservation_id, sku, quantity) VALUES
('c0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001', 'HOOD-BOXY-BLK-M', 2),
('c0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001', 'TSHIRT-OVS-WHT-L', 1),
('c0000002-0000-0000-0000-000000000001', 'b0000002-0000-0000-0000-000000000002', 'CARGO-BGGY-OLV-M', 1),
('c0000003-0000-0000-0000-000000000001', 'b0000003-0000-0000-0000-000000000003', 'JACKET-ZIP-BRN-L', 1);

-- ----------------------------------------------------------------------------
-- 3. HISTORIQUE D'AUDIT DES MOUVEMENTS (StockMovement)
-- Types : ENTREE, SORTIE, RESERVATION, LIBERATION, RETOUR, AJUSTEMENT
-- ----------------------------------------------------------------------------
INSERT INTO stock_movements (id, sku, type, quantity, reference_id, created_at) VALUES
('d0000001-0000-0000-0000-000000000001', 'HOOD-BOXY-BLK-M', 'ENTREE', 50, 'bl_fournisseur_101', NOW() - INTERVAL '3 days'),
('d0000001-0000-0000-0000-000000000002', 'HOOD-BOXY-BLK-M', 'RESERVATION', 2, 'ord_1001', NOW() - INTERVAL '5 minutes'),
('d0000002-0000-0000-0000-000000000001', 'CARGO-BGGY-OLV-M', 'SORTIE', 1, 'ord_1000', NOW() - INTERVAL '2 hours'),
('d0000003-0000-0000-0000-000000000001', 'TSHIRT-OVS-WHT-M', 'RETOUR', 1, 'rma_499', NOW() - INTERVAL '1 day');

-- ----------------------------------------------------------------------------
-- 4. ALERTES DE RÉAPPROVISIONNEMENT (RestockAlert)
-- Générées automatiquement quand stock disponible <= seuil
-- ----------------------------------------------------------------------------
INSERT INTO restock_alerts (id, sku, supplier_id, available_at_alert, threshold, status, created_at, resolved_at) VALUES
('e0000001-0000-0000-0000-000000000001', 'HOOD-BOXY-BLK-XL', 'f0000001-0000-0000-0000-000000000001', 3, 5, 'OUVERTE', NOW() - INTERVAL '1 hour', NULL),
('e0000002-0000-0000-0000-000000000001', 'JACKET-ZIP-BRN-L',  'f0000002-0000-0000-0000-000000000002', 2, 5, 'OUVERTE', NOW() - INTERVAL '30 minutes', NULL);

-- ----------------------------------------------------------------------------
-- 5. ÉVÉNEMENTS DÉJÀ TRAITÉS POUR IDEMPOTENCE (ProcessedEvent)
-- ----------------------------------------------------------------------------
INSERT INTO processed_events (event_id, topic, processed_at) VALUES
('f0000001-e000-0000-0000-000000000001', 'catalogue.variante-creee', NOW() - INTERVAL '4 days'),
('f0000002-e000-0000-0000-000000000002', 'commande.validee', NOW() - INTERVAL '2 hours');
