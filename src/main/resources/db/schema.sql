-- Disable FK checks strictly for the session (safety net)
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================================================================
-- CLEANUP (DROP TABLES)
-- ====================================================================================
DROP TABLE IF EXISTS `reservation_timeslots`;
DROP TABLE IF EXISTS `reservations`;
DROP TABLE IF EXISTS `timeslots`;
DROP TABLE IF EXISTS `available_services`;

DROP TABLE IF EXISTS `refunds`;
DROP TABLE IF EXISTS `payment_split_items`;
DROP TABLE IF EXISTS `payment_splits`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;

DROP TABLE IF EXISTS `product_ingredients`;
DROP TABLE IF EXISTS `ingredients`;
DROP TABLE IF EXISTS `ingredient_categories`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `product_types`;

DROP TABLE IF EXISTS `discount_policies`;
DROP TABLE IF EXISTS `tax_policies`;

DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `businesses_hours`;
DROP TABLE IF EXISTS `businesses`;

DROP TABLE IF EXISTS `role_permissions`;
DROP TABLE IF EXISTS `permissions`;
DROP TABLE IF EXISTS `roles`;

SET FOREIGN_KEY_CHECKS = 1;

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
    CONSTRAINT `fk_role_perms_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_perms_perm` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
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
    CONSTRAINT `fk_users_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE SET NULL
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
    CONSTRAINT `fk_discounts_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`) ON DELETE CASCADE
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
        FOREIGN KEY (business_id) REFERENCES businesses (id) ON DELETE CASCADE,

    CONSTRAINT fk_products_tax_policy
        FOREIGN KEY (tax_category) REFERENCES tax_policies (id) ON DELETE RESTRICT,

    CONSTRAINT fk_products_type
        FOREIGN KEY (product_type) REFERENCES product_types (product_type_id) ON DELETE RESTRICT,

    CONSTRAINT fk_products_discount_policy
        FOREIGN KEY (discount_id) REFERENCES discount_policies (id)
            ON DELETE SET NULL
);

CREATE TABLE `ingredient_categories`
(
    category_id    INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    business_id    INT UNSIGNED NOT NULL,
    name           VARCHAR(100) NOT NULL,
    description    TEXT,
    category_image MEDIUMBLOB,

    CONSTRAINT fk_ingcat_business
        FOREIGN KEY (business_id) REFERENCES businesses (id) ON DELETE CASCADE
);

CREATE TABLE `ingredients`
(
    ingredient_id          INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    ingredient_category_id INT UNSIGNED   NOT NULL,
    name                   VARCHAR(255)   NOT NULL,
    price                  DECIMAL(10, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_ingredients_category
        FOREIGN KEY (ingredient_category_id) REFERENCES ingredient_categories (category_id) ON DELETE CASCADE
);

CREATE TABLE `product_ingredients`
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

CREATE TABLE `orders`
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

CREATE TABLE `order_items`
(
    order_item_id  INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id       INT UNSIGNED   NOT NULL,
    product_id     INT UNSIGNED   NOT NULL,

    quantity       INT            NOT NULL DEFAULT 1,
    unit_price     DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_price    DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payment_status ENUM ('UNPAID','PAID')  DEFAULT 'UNPAID',
    created_at     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_order_product (order_id, product_id),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products (product_id) ON DELETE CASCADE
);


-- Payments
CREATE TABLE `payments`
(
    payment_id     INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    business_id    INT UNSIGNED                     NOT NULL,
    order_id       INT UNSIGNED                     NOT NULL,
    total_amount   DECIMAL(10, 2)                   NOT NULL,
    total_tip      DECIMAL(10, 2)                   NOT NULL DEFAULT 0.00,
    payment_method ENUM ('CASH','CARD','GIFT_CARD') NOT NULL,
    is_split       TINYINT(1)                       NOT NULL DEFAULT 0,
    status         ENUM ('PENDING','PROCESSING','COMPLETED','FAILED','CANCELLED')
                                                    NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at   TIMESTAMP                                 DEFAULT NULL,

    CONSTRAINT fk_payments_business FOREIGN KEY (business_id)
        REFERENCES businesses (id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE
);

-- Splits
CREATE TABLE payment_splits
(
    split_id       INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    payment_id     INT UNSIGNED                          NOT NULL,

    payer_name     VARCHAR(255)                                   DEFAULT NULL,
    amount         DECIMAL(10, 2)                        NOT NULL,
    tip_amount     DECIMAL(10, 2)                        NOT NULL DEFAULT 0.00,
    payment_method ENUM ('CASH','CARD','GIFT_CARD')      NOT NULL,
    status         ENUM ('PENDING','COMPLETED','FAILED') NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP                             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_split_payment FOREIGN KEY (payment_id)
        REFERENCES payments (payment_id) ON DELETE CASCADE
);

CREATE TABLE payment_split_items
(
    split_item_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    split_id      INT UNSIGNED   NOT NULL,
    order_item_id INT UNSIGNED   NOT NULL,
    amount        DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_split_items_split FOREIGN KEY (split_id)
        REFERENCES payment_splits (split_id) ON DELETE CASCADE,
    CONSTRAINT fk_split_items_order_item FOREIGN KEY (order_item_id)
        REFERENCES order_items (order_item_id) ON DELETE CASCADE
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
    CONSTRAINT `fk_refunds_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_refunds_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`payment_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_refunds_user` FOREIGN KEY (`processed_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
);

-- -----------------------------------------------------
-- 6. Reservations
-- -----------------------------------------------------

-- ==========================================
-- AVAILABLE SERVICES (The Menu)
-- ==========================================

CREATE TABLE `available_services`
(
    `id`               INT UNSIGNED                NOT NULL AUTO_INCREMENT,
    `business_id`      INT UNSIGNED                NOT NULL,
    `discount_id`      INT UNSIGNED                         DEFAULT NULL,
    `tax_id`           INT UNSIGNED                         DEFAULT NULL,
    `name`             VARCHAR(100)                NOT NULL,
    `description`      VARCHAR(255)                         DEFAULT NULL,
    `price`            DECIMAL(10, 2)              NOT NULL DEFAULT 0.00,
    `duration_minutes` SMALLINT UNSIGNED           NOT NULL DEFAULT 30,
    `status`           ENUM ('ACTIVE', 'DISABLED') NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_services_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- TIMESLOTS (The Inventory)
-- ==========================================

CREATE TABLE `timeslots`
(
    `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `business_id`  INT UNSIGNED NOT NULL,
    `user_id`      INT UNSIGNED NOT NULL,
    `start_time`   DATETIME     NOT NULL,
    `is_available` TINYINT(1)   NOT NULL DEFAULT 1,

    PRIMARY KEY (`id`),
    CONSTRAINT `fk_timeslots_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_timeslots_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- RESERVATIONS (The Bookings)
-- ==========================================

CREATE TABLE `reservations`
(
    `id`                    INT UNSIGNED   NOT NULL AUTO_INCREMENT,
    `business_id`           INT UNSIGNED   NOT NULL,
    `user_id`               INT UNSIGNED   NOT NULL,
    `service_id`            INT UNSIGNED   NOT NULL,
    `customer_name`         VARCHAR(100)   NOT NULL,
    `customer_phone`        VARCHAR(20)    NOT NULL,
    `snapshot_price`        DECIMAL(10, 2) NOT NULL,
    `snapshot_service_name` VARCHAR(100)   NOT NULL,
    `comments`              TEXT,
    `status`                ENUM ('PENDING', 'COMPLETED', 'CANCELLED', 'NO_SHOW') DEFAULT 'PENDING',
    `notification_status`   ENUM ('PENDING', 'SENT', 'FAILED')                    DEFAULT 'PENDING',
    `last_notification_at`  DATETIME                                              DEFAULT NULL,
    `created_at`            TIMESTAMP      NULL                                   DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP      NULL                                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_reservations_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reservations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reservations_service` FOREIGN KEY (`service_id`) REFERENCES `available_services` (`id`) ON DELETE CASCADE
);

-- ==========================================
-- RESERVATION TIMESLOTS (Junction Table)
-- ==========================================

CREATE TABLE `reservation_timeslots`
(
    `id`             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `reservation_id` INT UNSIGNED NOT NULL,
    `timeslot_id`    INT UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_timeslot_booking` (`timeslot_id`),
    CONSTRAINT `fk_res_ts_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_res_ts_timeslot` FOREIGN KEY (`timeslot_id`) REFERENCES `timeslots` (`id`) ON DELETE CASCADE
);