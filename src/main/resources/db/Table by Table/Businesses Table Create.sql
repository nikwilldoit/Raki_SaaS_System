DROP TABLE IF EXISTS `business`;

DROP TABLE IF EXISTS `businesses`;

CREATE TABLE `businesses` (
  `businessId` int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `businessType` enum('BAR','CAFE','RESTAURANT', 'HAIRDRESSER', 'BARBERSHOP', 'SPA') NOT NULL,
  `name` varchar(150) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `isActive` enum('OPEN','CLOSED') DEFAULT 'OPEN',
  `createdAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO `businesses` (`businessType`, `name`, `address`, `phone`, `isActive`) VALUES
('BAR', 'The Tipsy Tavern', '123 Main St, Springfield', '555-1234', 'OPEN'),
('CAFE', 'Brewed Awakenings', '456 Elm St, Springfield', '555-5678', 'OPEN'),
('RESTAURANT', 'Pasta Palace', '789 Oak St, Springfield', '555-8765', 'CLOSED'),
('HAIRDRESSER', 'Snip & Style', '321 Maple St, Springfield', '555-4321', 'OPEN'),
('BARBERSHOP', 'The Gentlemen''s Cut', '654 Pine St, Springfield', '555-6789', 'OPEN'),
('SPA', 'Relaxation Retreat', '987 Cedar St, Springfield', '555-9876', 'CLOSED');