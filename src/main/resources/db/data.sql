-- =======================================================
-- BUSINESSES
-- Updated columns: type, is_active (snake_case)
-- =======================================================
INSERT INTO `businesses` (`type`, `name`, `address`, `phone`, `is_active`)
VALUES ('BAR', 'The Tipsy Tavern', '123 Main St, Springfield', '555-1234', 'OPEN'),
       ('CAFE', 'Brewed Awakenings', '456 Elm St, Springfield', '555-5678', 'OPEN'),
       ('RESTAURANT', 'Pasta Palace', '789 Oak St, Springfield', '555-8765', 'CLOSED'),
       ('HAIRDRESSER', 'Snip & Style', '321 Maple St, Springfield', '555-4321', 'OPEN'),
       ('BARBERSHOP', 'The Gentlemen''s Cut', '654 Pine St, Springfield', '555-6789', 'OPEN'),
       ('SPA', 'Relaxation Retreat', '987 Cedar St, Springfield', '555-9876', 'CLOSED');

-- =======================================================
-- PERMISSIONS
-- Structure remains consistent, just ensuring table exists
-- =======================================================
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`)
VALUES
-- User Management
('user:create', 'user', 'create', 'Create a new user account'),
('user:read', 'user', 'read', 'View user details'),
('user:update', 'user', 'update', 'Modify user details'),
('user:delete', 'user', 'delete', 'Remove a user account'),
('role:read', 'role', 'read', 'View available roles'),
('role:assign', 'role', 'assign', 'Assign a role to a user'),
('permission:read', 'permission', 'read', 'View system permissions'),

-- Merchant & Branches
('merchant:read', 'merchant', 'read', 'View merchant details'),
('merchant:update', 'merchant', 'update', 'Update merchant details'),
('merchant:deactivate', 'merchant', 'deactivate', 'Deactivate merchant account'),
('branch:create', 'branch', 'create', 'Create a new branch'),
('branch:read', 'branch', 'read', 'View branch details'),
('branch:update', 'branch', 'update', 'Update branch details'),
('branch:delete', 'branch', 'delete', 'Remove a branch'),

-- Products & Menu
('product:create', 'product', 'create', 'Create a new product'),
('product:read', 'product', 'read', 'View products'),
('product:update', 'product', 'update', 'Update product details'),
('product:delete', 'product', 'delete', 'Remove a product'),
('productType:create', 'productType', 'create', 'Create product types'),
('productType:read', 'productType', 'read', 'View product types'),
('productType:update', 'productType', 'update', 'Update product types'),
('productType:delete', 'productType', 'delete', 'Delete product types'),
('ingredientCategory:create', 'ingredientCategory', 'create', 'Create ingredient category'),
('ingredientCategory:read', 'ingredientCategory', 'read', 'View ingredient categories'),
('ingredientCategory:update', 'ingredientCategory', 'update', 'Update ingredient category'),
('ingredientCategory:delete', 'ingredientCategory', 'delete', 'Delete ingredient category'),
('ingredient:create', 'ingredient', 'create', 'Create new ingredient'),
('ingredient:read', 'ingredient', 'read', 'View ingredients'),
('ingredient:update', 'ingredient', 'update', 'Update ingredient'),
('ingredient:delete', 'ingredient', 'delete', 'Delete ingredient'),
('catalogue:read', 'catalogue', 'read', 'View catalogue'),
('catalogue:upsert', 'catalogue', 'upsert', 'Set stock or status per product'),

-- Orders & Order Items
('order:create', 'order', 'create', 'Create a new order'),
('order:read', 'order', 'read', 'View order details'),
('order:update', 'order', 'update', 'Update order details'),
('order:cancel', 'order', 'cancel', 'Cancel an open order'),
('order:close', 'order', 'close', 'Close and finalize an order'),
('order:refund', 'order', 'refund', 'Process an order refund'),
('order:receipt', 'order', 'receipt', 'Generate or view receipt'),
('order:apply-discount', 'order', 'apply-discount', 'Apply an order-level discount'),
('orderItem:add', 'orderItem', 'add', 'Add item to order'),
('orderItem:update', 'orderItem', 'update', 'Update item in order'),
('orderItem:remove', 'orderItem', 'remove', 'Remove item from order'),

-- Payments, Splits, Tips
('payment:create', 'payment', 'create', 'Process a new payment'),
('payment:read', 'payment', 'read', 'View payment records'),
('payment:refund', 'payment', 'refund', 'Refund a specific payment transaction'),
('paymentSplit:create', 'paymentSplit', 'create', 'Create a split payment'),
('paymentSplit:read', 'paymentSplit', 'read', 'View split payment details'),
('tip:add', 'tip', 'add', 'Add a tip to a payment'),

-- Discounts & Taxes
('taxRate:create', 'taxRate', 'create', 'Create tax rate'),
('taxRate:read', 'taxRate', 'read', 'View tax rates'),
('taxRate:update', 'taxRate', 'update', 'Update tax rate'),
('taxRate:delete', 'taxRate', 'delete', 'Delete tax rate'),
('discount:create', 'discount', 'create', 'Create discount rule'),
('discount:read', 'discount', 'read', 'View discount rules'),
('discount:update', 'discount', 'update', 'Update discount rule'),
('discount:delete', 'discount', 'delete', 'Delete discount rule'),

-- Services & Schedules
('service:create', 'service', 'create', 'Create service'),
('service:read', 'service', 'read', 'View services'),
('service:update', 'service', 'update', 'Update service'),
('service:delete', 'service', 'delete', 'Delete service'),
('schedule:create', 'schedule', 'create', 'Create schedule entry'),
('schedule:read', 'schedule', 'read', 'View schedule'),
('schedule:update', 'schedule', 'update', 'Update schedule'),
('schedule:delete', 'schedule', 'delete', 'Delete schedule'),

-- Reservations & Deposits
('reservation:create', 'reservation', 'create', 'Create reservation'),
('reservation:read', 'reservation', 'read', 'View reservations'),
('reservation:update', 'reservation', 'update', 'Modify reservation'),
('reservation:cancel', 'reservation', 'cancel', 'Cancel reservation'),
('reservationDeposit:create', 'reservationDeposit', 'create', 'Collect reservation deposit'),
('reservationDeposit:refund', 'reservationDeposit', 'refund', 'Refund reservation deposit'),
('cancellationFee:apply', 'cancellationFee', 'apply', 'Apply cancellation fee'),
('cancellationFee:waive', 'cancellationFee', 'waive', 'Waive cancellation fee');

-- =======================================================
-- ROLES
-- =======================================================
INSERT INTO `roles` (`name`, `description`)
VALUES ('SuperAdmin', 'Complete system access across all merchants, bypassing tenant scopes'),
       ('Owner',
        'Comprehensive access including user management, merchant settings, financials, and full catalog control'),
       ('Employee', 'Standard operational access to orders, payments, reservations, and read-only catalog access');

-- =======================================================
-- USERS
-- =======================================================

INSERT INTO `users`
(`name`, `role_id`, `business_id`, `phone`, `email`, `password_hash`, `status`)
VALUES ('Test User',
        (SELECT id FROM `roles` WHERE name = 'Employee'),
        NULL,
        NULL,
        'test@test.com',
        '1234',
        'ACTIVE');

INSERT INTO `users`
(`name`, `email`, `password_hash`, `phone`, `role_id`, `business_id`, `status`)
VALUES
-- Owner of "The Tipsy Tavern"
('Alice Tavern-Owner',
 'alice@tipsytavern.com',
 'hashed_secret_2',
 '555-001-0001',
 (SELECT id FROM `roles` WHERE name = 'Owner'),
 (SELECT id FROM `businesses` WHERE name = 'The Tipsy Tavern'),
 'ACTIVE'),

-- Owner of "Snip & Style"
('Bob Stylist-Owner',
 'bob@snipandstyle.com',
 'hashed_secret_3',
 '555-002-0001',
 (SELECT id FROM `roles` WHERE name = 'Owner'),
 (SELECT id FROM `businesses` WHERE name = 'Snip & Style'),
 'ACTIVE');


INSERT INTO `users`
(`name`, `email`, `password_hash`, `phone`, `role_id`, `business_id`, `status`)
VALUES ('System Admin',
        'admin@teamraki.com',
        'hashed_secret_1',
        '555-000-0000',
        (SELECT id FROM `roles` WHERE name = 'SuperAdmin'),
        NULL,
        'ACTIVE');
-- Owner of "Pasta Palace"
INSERT INTO `users`
(`name`, `email`, `password_hash`, `phone`, `role_id`, `business_id`, `status`)
VALUES ('Paula Pasta-Owner',
        'paula@pastapalace.com',
        'hashed_secret_7',
        '555-004-0001',
        (SELECT id FROM `roles` WHERE name = 'Owner'),
        (SELECT id FROM `businesses` WHERE name = 'Pasta Palace'),
        'ACTIVE');
INSERT INTO `users`
(`name`, `email`, `password_hash`, `phone`, `role_id`, `business_id`, `status`)
VALUES
-- John Smith (Stylist)
('John Smith',
 'john.smith@snipandstyle.com',
 'hashed_secret_6',
 '555-123-4567',
 (SELECT id FROM `roles` WHERE name = 'Employee'),
 (SELECT id FROM `businesses` WHERE name = 'Snip & Style'),
 'ACTIVE'),
-- Charlie Waiter (Pasta Palace)
('Charlie Waiter',
 'charlie@pastapalace.com',
 'hashed_secret_4',
 '555-003-0001',
 (SELECT id FROM `roles` WHERE name = 'Employee'),
 (SELECT id FROM `businesses` WHERE name = 'Pasta Palace'),
 'ACTIVE'),
-- Nikolas Waiter (Pasta Palace)
('Nikolas Waiter',
 'nikolas@pastapalace.com',
 'hashed_secret_8',
 '555-003-0002',
 (SELECT id FROM `roles` WHERE name = 'Employee'),
 (SELECT id FROM `businesses` WHERE name = 'Pasta Palace'),
 'ACTIVE'),
-- Vaggelis Waiter (Pasta Palace)
('Vaggelis Waiter',
 'vaggelis@pastapalace.com',
 'hashed_secret_9',
 '555-003-0003',
 (SELECT id FROM `roles` WHERE name = 'Employee'),
 (SELECT id FROM `businesses` WHERE name = 'Pasta Palace'),
 'ACTIVE');

-- =======================================================
-- TAX POLICIES
-- =======================================================

INSERT INTO tax_policies (name, tax_type, rate)
VALUES ('Standard VAT 24%', 'VAT', 24.00),
       ('Reduced VAT 13%', 'VAT', 13.00);


-- =======================================================
-- Discounts For Pasta Palace
-- =======================================================
INSERT INTO `discount_policies`
VALUES (4, 3, 'Tea Day', 'PRODUCT', 'PERCENT', 10.00, NULL, NULL, 1, '2025-12-18 11:00:00', '2025-12-18 11:00:00'),
       (5, 3, 'Burger Day', 'PRODUCT', 'PERCENT', 20.00, NULL, NULL, 1, '2025-12-18 11:01:35', '2025-12-18 11:01:46');



-- =======================================================
-- PRODUCT TYPES
-- =======================================================

INSERT INTO product_types (product_name)
VALUES ('Food'),
       ('Drink'),
       ('Coffee');

-- =======================================================
-- SAMPLE PRODUCTS FOR "Pasta Palace" (RESTAURANT)
-- =======================================================

-- θα χρησιμοποιήσουμε τον ίδιο tax policy (24%) και product types Food/Drink
INSERT INTO `products`
VALUES (12, 'Carbonara', 'Italianooo', NULL, 2, 1, 3, 9.00, NULL, 'PRODUCT', 'ACTIVE', '2025-12-18 10:57:09',
        '2025-12-18 10:57:09'),
       (13, 'Fredo Capuccino', 'Good Coffee', NULL, 2, 3, 3, 2.00, NULL, 'PRODUCT', 'ACTIVE', '2025-12-18 10:57:49',
        '2025-12-18 10:57:49'),
       (14, 'Burger', 'Perfect', NULL, 1, 1, 3, 8.00, 5, 'PRODUCT', 'ACTIVE', '2025-12-18 10:58:31',
        '2025-12-18 11:01:46'),
       (15, 'Tea', 'Healthy', NULL, 2, 2, 3, 2.00, 4, 'PRODUCT', 'ACTIVE', '2025-12-18 10:58:57',
        '2025-12-18 11:00:00');


-- =======================================================
-- INGREDIENT CATEGORIES & INGREDIENTS (για toppings / extras)
-- =======================================================

-- Κατηγορία "Extras" για την Margherita Pizza
INSERT INTO `ingredient_categories`
VALUES (6, 3, 'Sugar', 'Very Sweet', NULL),
       (7, 3, 'Honey', 'Very Healthy', NULL),
       (8, 3, 'Sos', 'Tasty', NULL),
       (9, 3, 'Pasta', 'Italy Traditional', NULL),
       (10, 3, 'Cheese', 'Very good', NULL),
       (11, 3, 'Meat', 'Very Fresh', NULL);


-- Τα ίδια extras
INSERT INTO `ingredients`
VALUES (11, 6, 'white', 1.00),
       (12, 6, 'brown', 2.00),
       (13, 7, 'yellow', 1.00),
       (14, 7, 'brown', 2.00),
       (15, 8, 'Ketchup', 0.50),
       (16, 8, 'Mustard', 0.70),
       (17, 8, 'Mayonnaise', 0.40),
       (18, 9, 'Spaghetti', 2.00),
       (19, 9, 'Pennes', 2.50),
       (20, 10, 'Chedar', 0.30),
       (21, 10, 'Gooda', 0.50),
       (22, 11, 'Pork', 4.00),
       (23, 11, 'Chicken', 2.00);


-- Products Ingredients
INSERT INTO `product_ingredients`
VALUES (13, 11),
       (15, 11),
       (13, 12),
       (15, 12),
       (13, 13),
       (15, 13),
       (13, 14),
       (15, 14),
       (14, 15),
       (14, 16),
       (14, 17),
       (12, 18),
       (12, 19),
       (12, 20),
       (14, 20),
       (12, 21),
       (14, 21),
       (14, 22),
       (14, 23);


-- =======================================================
-- SAMPLE OPEN ORDER + ITEMS ΓΙΑ Pasta Palace
-- =======================================================

INSERT INTO `orders`
VALUES (1, 3, 5, NULL, 'ORD-1', '2025-12-18 11:32:53', 'REFUNDED', ''),
       (2, 3, 5, NULL, 'ORD-2', '2025-12-18 11:33:16', 'OPEN', ''),
       (3, 3, 5, NULL, 'ORD-3', '2025-12-18 11:33:36', 'CLOSED', '');


INSERT INTO `order_items`
VALUES (1, 1, 15, 1, 4.03, 4.03, 'PAID', '2025-12-18 11:32:53'),
       (2, 1, 13, 1, 6.26, 6.26, 'PAID', '2025-12-18 11:32:53'),
       (3, 2, 12, 1, 12.97, 12.97, 'UNPAID', '2025-12-18 11:33:16'),
       (4, 2, 14, 1, 12.94, 12.94, 'UNPAID', '2025-12-18 11:33:16'),
       (5, 3, 12, 1, 12.97, 12.97, 'PAID', '2025-12-18 11:33:36');


-- Payments
INSERT INTO `payments`
VALUES (1, 3, 1, 10.29, 0.00, 'CASH', 0, 'COMPLETED', '2025-12-18 11:32:59', '2025-12-18 11:32:59'),
       (2, 3, 3, 12.97, 0.00, 'CASH', 0, 'COMPLETED', '2025-12-18 11:33:38', '2025-12-18 11:33:38');


-- Refunds
INSERT INTO `refunds`
VALUES (1, 1, 1, NULL, 10.29, 'No reason provided', 'PROCESSED', '2025-12-18 11:33:47');
