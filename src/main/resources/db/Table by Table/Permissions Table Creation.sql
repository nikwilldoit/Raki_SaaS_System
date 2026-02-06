DROP TABLE IF EXISTS `permissions`;

CREATE TABLE `permissions` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `resource` VARCHAR(50) NOT NULL,
  `action` VARCHAR(50) NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `createdAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_permission_name` (`name`), 
  UNIQUE KEY `uk_resource_action` (`resource`,`action`) 
);

-- User Management
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('user:create', 'user', 'create', 'Create a new user account'),
  ('user:read', 'user', 'read', 'View user details'),
  ('user:update', 'user', 'update', 'Modify user details'),
  ('user:delete', 'user', 'delete', 'Remove a user account'),
  ('role:read', 'role', 'read', 'View available roles'),
  ('role:assign', 'role', 'assign', 'Assign a role to a user'),
  ('permission:read', 'permission', 'read', 'View system permissions');

-- Merchant & Branches
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('merchant:read', 'merchant', 'read', 'View merchant details'),
  ('merchant:update', 'merchant', 'update', 'Update merchant details'),
  ('merchant:deactivate', 'merchant', 'deactivate', 'Deactivate merchant account'),
  ('branch:create', 'branch', 'create', 'Create a new branch'),
  ('branch:read', 'branch', 'read', 'View branch details'),
  ('branch:update', 'branch', 'update', 'Update branch details'),
  ('branch:delete', 'branch', 'delete', 'Remove a branch');

-- Products & Menu
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('product:create', 'product', 'create', 'Create a new product'),
  ('product:read', 'product', 'read', 'View products'),
  ('product:update', 'product', 'update', 'Update product details'),
  ('product:delete', 'product', 'delete', 'Remove a product'),
-- Expanded productType permissions [cite: 1100]
  ('productType:create', 'productType', 'create', 'Create product types'),
  ('productType:read', 'productType', 'read', 'View product types'),
  ('productType:update', 'productType', 'update', 'Update product types'),
  ('productType:delete', 'productType', 'delete', 'Delete product types'),
-- Ingredient Categories [cite: 1101]
  ('ingredientCategory:create', 'ingredientCategory', 'create', 'Create ingredient category'),
  ('ingredientCategory:read', 'ingredientCategory', 'read', 'View ingredient categories'),
  ('ingredientCategory:update', 'ingredientCategory', 'update', 'Update ingredient category'),
  ('ingredientCategory:delete', 'ingredientCategory', 'delete', 'Delete ingredient category'),
-- Ingredients [cite: 1102]
  ('ingredient:create', 'ingredient', 'create', 'Create new ingredient'),
  ('ingredient:read', 'ingredient', 'read', 'View ingredients'),
  ('ingredient:update', 'ingredient', 'update', 'Update ingredient'),
  ('ingredient:delete', 'ingredient', 'delete', 'Delete ingredient'),
-- Catalogue [cite: 1113]
  ('catalogue:read', 'catalogue', 'read', 'View catalogue'),
  ('catalogue:upsert', 'catalogue', 'upsert', 'Set stock or status per product');

-- Orders & Order Items [cite: 1104, 1155]
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
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
  ('orderItem:remove', 'orderItem', 'remove', 'Remove item from order');

-- Payments, Splits, Tips [cite: 1111, 1156]
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('payment:create', 'payment', 'create', 'Process a new payment'),
  ('payment:read', 'payment', 'read', 'View payment records'),
  ('payment:refund', 'payment', 'refund', 'Refund a specific payment transaction'),
  ('paymentSplit:create', 'paymentSplit', 'create', 'Create a split payment'),
  ('paymentSplit:read', 'paymentSplit', 'read', 'View split payment details'),
  ('tip:add', 'tip', 'add', 'Add a tip to a payment');

-- Discounts & Taxes [cite: 1121]
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('taxRate:create', 'taxRate', 'create', 'Create tax rate'),
  ('taxRate:read', 'taxRate', 'read', 'View tax rates'),
  ('taxRate:update', 'taxRate', 'update', 'Update tax rate'),
  ('taxRate:delete', 'taxRate', 'delete', 'Delete tax rate'),
  ('discount:create', 'discount', 'create', 'Create discount rule'),
  ('discount:read', 'discount', 'read', 'View discount rules'),
  ('discount:update', 'discount', 'update', 'Update discount rule'),
  ('discount:delete', 'discount', 'delete', 'Delete discount rule');

-- Services & Schedules [cite: 1130]
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('service:create', 'service', 'create', 'Create service'),
  ('service:read', 'service', 'read', 'View services'),
  ('service:update', 'service', 'update', 'Update service'),
  ('service:delete', 'service', 'delete', 'Delete service'),
  ('schedule:create', 'schedule', 'create', 'Create schedule entry'),
  ('schedule:read', 'schedule', 'read', 'View schedule'),
  ('schedule:update', 'schedule', 'update', 'Update schedule'),
  ('schedule:delete', 'schedule', 'delete', 'Delete schedule');

-- Reservations & Deposits [cite: 1135, 1170]
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
  ('reservation:create', 'reservation', 'create', 'Create reservation'),
  ('reservation:read', 'reservation', 'read', 'View reservations'),
  ('reservation:update', 'reservation', 'update', 'Modify reservation'),
  ('reservation:cancel', 'reservation', 'cancel', 'Cancel reservation'),
  ('reservationDeposit:create', 'reservationDeposit', 'create', 'Collect reservation deposit'),
  ('reservationDeposit:refund', 'reservationDeposit', 'refund', 'Refund reservation deposit'),
  ('cancellationFee:apply', 'cancellationFee', 'apply', 'Apply cancellation fee'),
  ('cancellationFee:waive', 'cancellationFee', 'waive', 'Waive cancellation fee');