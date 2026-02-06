-- Disable FK checks strictly for the session (safety net)
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================================
-- 0. CLEANUP (DROP TABLES)
-- Ordered explicitly to handle dependencies (Child tables first, then Parents)
-- ====================================================================================

-- Drop Reservation & Service dependencies
DROP TABLE IF EXISTS `reservation_timeslots`;
DROP TABLE IF EXISTS `reservations`;
DROP TABLE IF EXISTS `timeslots`;
DROP TABLE IF EXISTS `available_services`;

-- Drop Order & Payment dependencies
DROP TABLE IF EXISTS `refunds`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `order_item_options`; -- Legacy cleanup
DROP TABLE IF EXISTS `orders`;

-- Drop Product & Menu dependencies
DROP TABLE IF EXISTS `product_ingredients`;
DROP TABLE IF EXISTS `ingredients`;
DROP TABLE IF EXISTS `ingredient_categories`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `product_types`;

-- Legacy Menu Tables (Cleaning up old structures if they exist)
DROP TABLE IF EXISTS `menu_option_values`;
DROP TABLE IF EXISTS `menu_options`;
DROP TABLE IF EXISTS `menus`;

-- Drop Policies (Discounts/Tax)
DROP TABLE IF EXISTS `discount_policies`;
DROP TABLE IF EXISTS `tax_policies`;

-- Drop Core Business & User entities
-- 'users' depends on 'roles' and 'businesses', so it must go first
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `businesses_hours`;
DROP TABLE IF EXISTS `businesses`;

-- Drop Security (RBAC)
-- 'role_permissions' depends on 'roles' and 'permissions'
DROP TABLE IF EXISTS `role_permissions`;
DROP TABLE IF EXISTS `permissions`;
DROP TABLE IF EXISTS `roles`;


-- ====================================================================================
-- 1. Security & Access Control (RBAC)
-- ====================================================================================

CREATE TABLE `roles`
(
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(50)  NOT NULL,
    `description` VARCHAR(255)      DEFAULT NULL,
    `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_roles_name` (`name`)
);

CREATE TABLE `permissions`
(
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100) NOT NULL,
    `resource`    VARCHAR(50)  NOT NULL,
    `action`      VARCHAR(50)  NOT NULL,
    `description` VARCHAR(255)      DEFAULT NULL,
    `created_at`  TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permissions_name` (`name`),
    UNIQUE KEY `uk_permissions_resource_action` (`resource`, `action`)
);

CREATE TABLE `role_permissions`
(
    `role_id`       INT UNSIGNED NOT NULL,
    `permission_id` INT UNSIGNED NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`),
    CONSTRAINT `fk_role_perms_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
    CONSTRAINT `fk_role_perms_perm` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
);

-- ====================================================================================
-- 2. Business & Users
-- ====================================================================================

CREATE TABLE `businesses`
(
    `id`         INT UNSIGNED                                                         NOT NULL AUTO_INCREMENT,
    `type`       ENUM ('BAR','CAFE','RESTAURANT', 'HAIRDRESSER', 'BARBERSHOP', 'SPA') NOT NULL,
    `name`       VARCHAR(150)                                                         NOT NULL,
    `address`    VARCHAR(255)                                                              DEFAULT NULL,
    `phone`      VARCHAR(20)                                                               DEFAULT NULL,
    `is_active`  ENUM ('OPEN','CLOSED')                                                    DEFAULT 'OPEN',
    `created_at` TIMESTAMP                                                            NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP                                                            NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `businesses_hours`
(
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `business_id` INT UNSIGNED NOT NULL,
    `day_of_week` VARCHAR(10)  NOT NULL,
    `open_time`   TIME         NOT NULL,
    `close_time`  TIME         NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_businesses_hour_business`
        FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`)
            ON DELETE CASCADE
);

CREATE TABLE `users`
(
    `id`            INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `business_id`   INT UNSIGNED                          DEFAULT NULL,
    `role_id`       INT UNSIGNED                          DEFAULT NULL,
    `name`          VARCHAR(100) NOT NULL,
    `email`         VARCHAR(100) NOT NULL,
    `phone`         VARCHAR(20)                           DEFAULT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `status`        ENUM ('ACTIVE', 'SUSPENDED', 'FIRED') DEFAULT 'ACTIVE',
    `created_at`    TIMESTAMP    NULL                     DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP    NULL                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    CONSTRAINT `fk_users_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`),
    CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
);

-- ====================================================================================
-- 3. Policies (Tax & Discount)
-- ====================================================================================

CREATE TABLE `tax_policies`
(
    `id`         INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(100)  NOT NULL,
    `tax_type`   VARCHAR(50)   NOT NULL,
    `rate`       DECIMAL(5, 2) NOT NULL,
    `created_at` TIMESTAMP     NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `discount_policies`
(
    `id`             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `business_id`    INT UNSIGNED      DEFAULT NULL,
    `name`           VARCHAR(100)      DEFAULT NULL,
    `scope`          VARCHAR(50)       DEFAULT NULL,
    `discount_type`  VARCHAR(50)       DEFAULT NULL,
    `discount_value` DECIMAL(10, 2)    DEFAULT NULL,
    `start_date`     DATE              DEFAULT NULL,
    `end_date`       DATE              DEFAULT NULL,
    `is_active`      TINYINT(1)        DEFAULT 1,
    `created_at`     TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_discounts_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`)
);

-- ====================================================================================
-- 4. Products Menu Management
-- ====================================================================================

CREATE TABLE `product_types`
(
    product_type_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_name    VARCHAR(255) NOT NULL
);

CREATE TABLE `products`
(
    product_id    INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(255)                NOT NULL,
    description   TEXT,
    product_image MEDIUMBLOB,
    tax_category  INT UNSIGNED                NOT NULL,
    product_type  INT UNSIGNED                NOT NULL,
    business_id   INT UNSIGNED                NOT NULL,
    base_price    DECIMAL(10, 2)              NOT NULL DEFAULT 0.00,

    discount_id   INT UNSIGNED                         DEFAULT NULL,

    type          ENUM ('PRODUCT', 'SERVICE') NOT NULL DEFAULT 'PRODUCT',
    status        ENUM ('ACTIVE', 'INACTIVE')          DEFAULT 'ACTIVE',
    created_at    TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP                            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_business
        FOREIGN KEY (business_id) REFERENCES businesses (id),

    CONSTRAINT fk_products_tax_policy
        FOREIGN KEY (tax_category) REFERENCES tax_policies (id),

    CONSTRAINT fk_products_type
        FOREIGN KEY (product_type) REFERENCES product_types (product_type_id),

    CONSTRAINT fk_products_discount_policy
        FOREIGN KEY (discount_id) REFERENCES discount_policies (id)
            ON DELETE SET NULL
);

CREATE TABLE ingredient_categories
(
    category_id    INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    business_id    INT UNSIGNED NOT NULL,
    name           VARCHAR(100) NOT NULL,
    description    TEXT,
    category_image MEDIUMBLOB,

    CONSTRAINT fk_ingcat_business
        FOREIGN KEY (business_id) REFERENCES businesses (id) ON DELETE CASCADE
);

CREATE TABLE ingredients
(
    ingredient_id          INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    ingredient_category_id INT UNSIGNED   NOT NULL,
    name                   VARCHAR(255)   NOT NULL,
    price                  DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_ingredients_category
        FOREIGN KEY (ingredient_category_id) REFERENCES ingredient_categories (category_id) ON DELETE CASCADE
);

CREATE TABLE product_ingredients
(
    product_id    INT UNSIGNED NOT NULL,
    ingredient_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (product_id, ingredient_id),
    CONSTRAINT fk_pi_product
        FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE,
    CONSTRAINT fk_pi_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id) ON DELETE CASCADE
);

-- ====================================================================================
-- 5. Orders & Order Items
-- ====================================================================================

CREATE TABLE orders
(
    id               INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    business_id      INT UNSIGNED NOT NULL,
    user_id          INT UNSIGNED NOT NULL,
    discount_id      INT UNSIGNED                                     DEFAULT NULL,

    order_number     VARCHAR(50)  NOT NULL,
    order_date       TIMESTAMP                                        DEFAULT CURRENT_TIMESTAMP,

    status           ENUM ('OPEN', 'CLOSED', 'CANCELLED', 'REFUNDED') DEFAULT 'OPEN',
    special_requests TEXT,

    CONSTRAINT fk_orders_business
        FOREIGN KEY (business_id) REFERENCES businesses (id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_orders_discount_policy
        FOREIGN KEY (discount_id) REFERENCES discount_policies (id) ON DELETE SET NULL
);

CREATE TABLE order_items
(
    order_item_id  INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id       INT UNSIGNED NOT NULL,
    product_id     INT UNSIGNED NOT NULL,

    quantity       INT          NOT NULL  DEFAULT 1,
    payment_status ENUM ('UNPAID','PAID') DEFAULT 'UNPAID',
    created_at     TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_order_product (order_id, product_id),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products (product_id)
);

CREATE TABLE `payments`
(
    `id`             INT UNSIGNED                     NOT NULL AUTO_INCREMENT,
    `order_id`       INT UNSIGNED                     NOT NULL,
    `amount`         DECIMAL(10, 2)                   NOT NULL,
    `method`         ENUM ('CARD','CASH','GIFT_CARD') NOT NULL,
    `transaction_id` VARCHAR(100)                          DEFAULT NULL,
    `paid_at`        TIMESTAMP                        NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_payments_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
);

CREATE TABLE `refunds`
(
    `id`                   INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    `order_id`             INT UNSIGNED        DEFAULT NULL,
    `payment_id`           INT UNSIGNED        DEFAULT NULL,
    `processed_by_user_id` INT UNSIGNED        DEFAULT NULL,
    `amount`               DECIMAL(10, 2) NOT NULL,
    `reason`               TEXT,
    `status`               VARCHAR(20)         DEFAULT 'PROCESSED',
    `refunded_at`          TIMESTAMP      NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_refunds_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    CONSTRAINT `fk_refunds_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`),
    CONSTRAINT `fk_refunds_user` FOREIGN KEY (`processed_by_user_id`) REFERENCES `users` (`id`)
);