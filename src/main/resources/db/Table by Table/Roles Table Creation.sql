DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
);

INSERT INTO `roles` (`name`, `description`) VALUES
   ('SuperAdmin', 'Complete system access across all merchants, bypassing tenant scopes'),
   ('Owner', 'Comprehensive access including user management, merchant settings, financials, and full catalog control'),
   ('Employee', 'Standard operational access to orders, payments, reservations, and read-only catalog access');