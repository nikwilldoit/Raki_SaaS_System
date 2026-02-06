DROP TABLE IF EXISTS `user`;

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `userId` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `roleId` int UNSIGNED DEFAULT NULL,
  `merchant_id` int UNSIGNED DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `passwordHash` varchar(255) NOT NULL,
  `isActive` enum('ACTIVE', 'SUSPENDED', 'FIRED') DEFAULT 'ACTIVE',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_user_role` (`roleId`),
  KEY `fk_user_merchant` (`merchant_id`),
  CONSTRAINT `fk_user_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `businesses` (`businessId`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`roleId`) REFERENCES `roles` (`id`)
);

INSERT INTO `users`
(`name`, `roleId`, `merchant_id`, `phone`, `email`, `passwordHash`, `isActive`)
VALUES
    ('Test User',
     (SELECT id FROM `roles` WHERE name = 'Employee'),
     NULL,
     NULL,
     'test@test.com',
     '1234',
     'ACTIVE');

-- =======================================================
-- 1. SUPER ADMIN (System Level)
-- =======================================================
-- Access to all merchants, so merchant_id is NULL.
INSERT INTO `users`
(`name`, `email`, `passwordHash`, `phone`, `roleId`, `merchant_id`, `isActive`)
VALUES
    (
        'System Admin',
        'admin@teamraki.com',
        'hashed_secret_1',
        '555-000-0000',
        (SELECT id FROM `roles` WHERE name = 'SuperAdmin'),
        NULL,
        'ACTIVE'
    );

-- =======================================================
-- 2. OWNERS (One per key business)
-- =======================================================
INSERT INTO `users`
(`name`, `email`, `passwordHash`, `phone`, `roleId`, `merchant_id`, `isActive`)
VALUES
-- Owner of "The Tipsy Tavern" (Bar)
(
    'Alice Tavern-Owner',
    'alice@tipsytavern.com',
    'hashed_secret_2',
    '555-001-0001',
    (SELECT id FROM `roles` WHERE name = 'Owner'),
    (SELECT businessId FROM `businesses` WHERE name = 'The Tipsy Tavern'),
    'ACTIVE'
),
-- Owner of "Snip & Style" (Hairdresser)
(
    'Bob Stylist-Owner',
    'bob@snipandstyle.com',
    'hashed_secret_3',
    '555-002-0001',
    (SELECT id FROM `roles` WHERE name = 'Owner'),
    (SELECT businessId FROM `businesses` WHERE name = 'Snip & Style'),
    'ACTIVE'
);

-- =======================================================
-- 3. EMPLOYEES - BEAUTY SECTOR (Services)
-- Matches "Reservation Flows" (Pages 6-9, 27-29)
-- =======================================================
INSERT INTO `users`
(`name`, `email`, `passwordHash`, `phone`, `roleId`, `merchant_id`, `isActive`)
VALUES
-- John Smith (Stylist from wireframe pg. 27) -> Assigned to "Snip & Style"
(
    'John Smith',
    'john.smith@snipandstyle.com',
    'hashed_secret_6',
    '555-123-4567',
    (SELECT id FROM `roles` WHERE name = 'Employee'),
    (SELECT businessId FROM `businesses` WHERE name = 'Snip & Style'),
    'ACTIVE'
);

-- =======================================================
-- 4. EMPLOYEES - CATERING SECTOR (Food/Drink)
-- Matches "Order Flows" (Pages 3-5, 18-21)
-- =======================================================
INSERT INTO `users`
(`name`, `email`, `passwordHash`, `phone`, `roleId`, `merchant_id`, `isActive`)
VALUES
-- Waiter at "Pasta Palace" (Handling the Pizza/Burger orders)
(
    'Charlie Waiter',
    'charlie@pastapalace.com',
    'hashed_secret_4',
    '555-003-0001',
    (SELECT id FROM `roles` WHERE name = 'Employee'),
    (SELECT businessId FROM `businesses` WHERE name = 'Pasta Palace'),
    'ACTIVE'
);
