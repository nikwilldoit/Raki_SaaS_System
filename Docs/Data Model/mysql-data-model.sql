CREATE TABLE product (
    product_id mediumint UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    product_image MEDIUMBLOB,
    tax_category smallint NOT NULL,
    product_type int NOT NULL,
    merchant_id BIGINT NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    type ENUM('product', 'service') NOT NULL DEFAULT 'product',
    status ENUM('active', 'inactive', 'out_of_stock') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id),
    FOREIGN KEY (tax_category) REFERENCES taxRate(taxrate_id),
    FOREIGN KEY (product_type) REFERENCES productTypes(product_type_id)
);

CREATE TABLE productTypes(
    product_type_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL
)

CREATE TABLE IngredientCategory (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_image MEDIUMBLOB,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);


CREATE TABLE Ingredient (
    ingredient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ingredient_category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (ingredient_category_id) REFERENCES IngredientCategory(category_id) ON DELETE CASCADE   
);

CREATE TABLE catalogItem (
    line_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id INT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 1.00,
    status ENUM('PAID', 'UNPAID', 'SELECTED') DEFAULT 'UNPAID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(product_id),
);

CREATE TABLE catalogue (
    catalogue_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    status ENUM('active', 'inactive', 'out_of_stock') DEFAULT 'active',
    stock_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_merchant_product (merchant_id, product_id),
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,                    -- Employee who created the order
    

    order_number VARCHAR(50) NOT NULL,
    discount_id BIGINT,                          -- Reference to order discount applied
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Order status
    order_status ENUM('open', 'closed', 'cancelled', 'refunded') DEFAULT 'open',
    
    -- Additional information
    special_requests TEXT,
                              -- Customer notes
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (discount_id) REFERENCES discount(discount_id) ON DELETE SET NULL

);

CREATE TABLE OrderItem (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    -- Quantity and pricing (snapshot at time of order)
    quantity INT NOT NULL DEFAULT 1,                -- Changed from DECIMAL to INT
    
    payment_status ENUM('Unpaid', 'Paid') DEFAULT 'Unpaid',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_order_product (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES ORDER(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE Discount (
    discount_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type ENUM('order_discount', 'product_discount') NOT NULL,
    value_type ENUM('value', 'percentage') NOT NULL,
    value DECIMAL(3, 2) NOT NULL,
    minimum_order_value DECIMAL(10, 2) DEFAULT 0.00,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status ENUM('active', 'inactive', 'expired') DEFAULT 'inactive',
    current_uses INT DEFAULT 0,
    max_uses INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE
);

CREATE TABLE taxRate (
    taxrate_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    rate DECIMAL(8, 5) NOT NULL,
    description TEXT,
    status ENUM('active', 'inactive', 'pending') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE merchant (
    merchant_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    business_name VARCHAR(255) NOT NULL,
    business_type ENUM('restaurant', 'bar', 'cafe', 'barber', 'hairdresser', 'spa', 'beauty_salon') NOT NULL,
    AddressLine1 VARCHAR(255),
    City VARCHAR(128),
    Region VARCHAR(128),
    PostalCode VARCHAR(32),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

CREATE TABLE branch (
    branch_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    AddressLine1 VARCHAR(255),
    City VARCHAR(128),
    Region VARCHAR(128),
    PostalCode VARCHAR(32),
    phone VARCHAR(20),
    email VARCHAR(255),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE
);


-- ============================================================================
-- ROLE-BASED ACCESS CONTROL (RBAC) SYSTEM
-- ============================================================================
-- This schema implements a complete RBAC system for a multi-tenant SaaS
-- application supporting three user roles: Employee, Business Owner, and SuperAdmin.
-- 
-- Key Features:
-- - Role-based permissions with granular control
-- - Multi-tenant support via merchant_id
-- - Audit trails for security and compliance
-- - Flexible permission assignments
-- ============================================================================

-- ----------------------------------------------------------------------------
-- TABLE: Roles
-- ----------------------------------------------------------------------------
-- Stores all available user roles in the system.
-- Examples: 'Employee', 'Business Owner', 'SuperAdmin'
-- ----------------------------------------------------------------------------
CREATE TABLE Roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,              -- Role name (e.g., 'Employee', 'Business Owner', 'SuperAdmin')
    description VARCHAR(255),                       -- Human-readable description of role responsibilities
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- TABLE: Permissions
-- ----------------------------------------------------------------------------
-- Defines all granular permissions available in the system.
-- Permissions follow a resource:action pattern (e.g., 'user:create', 'order:edit')
-- ----------------------------------------------------------------------------
CREATE TABLE Permissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,              -- Full permission name (e.g., 'user:create')
    resource VARCHAR(50) NOT NULL,                  -- Resource type (e.g., 'user', 'product', 'order')
    action VARCHAR(50) NOT NULL,                    -- Action type (e.g., 'create', 'read', 'update', 'delete')
    description VARCHAR(255),                       -- Description of what this permission allows
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_resource_action (resource, action) -- Prevent duplicate resource:action combinations
);

-- ----------------------------------------------------------------------------
-- TABLE: employee
-- ----------------------------------------------------------------------------
-- Stores all user accounts in the system.
-- Each user belongs to a merchant (business) and has an assigned role.
-- SuperAdmins should have merchant_id = NULL to indicate system-wide access.
-- ----------------------------------------------------------------------------
CREATE TABLE employee (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,                    -- Links user to their business (NULL for SuperAdmins)
    role_id INT NOT NULL,                           -- Links user to their role
    email VARCHAR(255) NOT NULL,                    -- User's email address
    password VARCHAR(255) NOT NULL,            
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    status ENUM('active', 'suspended', 'fired') DEFAULT 'active', -- Account status
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,                      -- Track last login for security monitoring
    
    -- Foreign Keys
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE RESTRICT, -- Prevent deletion of roles in use
    
    -- Constraints
    UNIQUE KEY uk_user_email_merchant (email, merchant_id), -- Email must be unique per merchant
);

-- ----------------------------------------------------------------------------
-- TABLE: Role_Permissions
-- ----------------------------------------------------------------------------
-- Junction table mapping roles to their permissions.
-- This is the core of the RBAC system - defines what each role can do.
-- ----------------------------------------------------------------------------
CREATE TABLE Role_Permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    
    -- Foreign Keys with cascade deletion
    FOREIGN KEY (role_id) REFERENCES Roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES Permissions(id) ON DELETE CASCADE,
);


# Audit logging for system changes
CREATE TABLE audit_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    merchant_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    resource_id BIGINT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE SET NULL,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_merchant (merchant_id),
    INDEX idx_audit_resource (resource, resource_id),
    INDEX idx_audit_created (created_at)
);

-- Payment processing
CREATE TABLE payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    reservation_id BIGINT,
    merchant_id BIGINT NOT NULL,
    payment_method ENUM('cash', 'card', 'gift_card') NOT NULL,
    split BOOLEAN DEFAULT FALSE,
    status ENUM('pending', 'processing', 'completed', 'failed', 'cancelled', 'refunded') DEFAULT 'pending',
    tip_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    FOREIGN KEY (order_id) REFERENCES order(order_id) ON DELETE SET NULL,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
);

-- Split payments for shared bills
CREATE TABLE payment_split (
    split_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,

    customer_name VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    tip_amount DECIMAL(10, 2) DEFAULT 0.00,
    payment_method ENUM('cash', 'card', 'gift_card') NOT NULL,
    status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payment(payment_id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_item(order_item_id) ON DELETE CASCADE,
    UNIQUE KEY uk_payment_split (payment_id, order_item_id)
);

CREATE TABLE payment_split_items (
    split_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    split_id BIGINT NOT NULL,
    order_item_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (split_id) REFERENCES payment_split(split_id) ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES order_item(order_item_id) ON DELETE CASCADE,
);

-- Refund management
CREATE TABLE refund (
    refund_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    payment_id BIGINT,
    merchant_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    reason TEXT NOT NULL,
    refund_type ENUM('full', 'partial') NOT NULL,
    status ENUM('pending', 'approved', 'processed', 'rejected') DEFAULT 'pending',
    processed_by BIGINT,
    approved_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    FOREIGN KEY (order_id) REFERENCES order_header(order_id),
    FOREIGN KEY (payment_id) REFERENCES payment(payment_id) ON DELETE SET NULL,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES user(user_id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES user(user_id) ON DELETE SET NULL,
);

# Services for beauty/spa businesses
CREATE TABLE service (
    service_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    duration INT NOT NULL COMMENT 'Duration in minutes',
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
);

-- # Employee schedule management
CREATE TABLE schedule (
    schedule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    start_time DATE NOT NULL,
    end_time DATE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    status ENUM('active', 'cancelled', 'modified') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(user_id) ON DELETE CASCADE,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_date (user_id, start_time, end_time),
);

# Reservations
CREATE TABLE reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    service_id BIGINT,
    assigned_user_id BIGINT COMMENT 'Employee assigned to provide service',
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status ENUM('pending', 'confirmed', 'in_progress', 'completed', 'cancelled', 'no_show') DEFAULT 'pending',
    deposit_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchant(merchant_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_user_id) REFERENCES user(user_id) ON DELETE SET NULL,
);

# Reservation deposits
CREATE TABLE reservation_deposit (
    deposit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('cash', 'card', 'mobile_payment', 'bank_transfer') NOT NULL,
    status ENUM('pending', 'completed', 'refunded') DEFAULT 'pending',
    transaction_reference VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) ON DELETE CASCADE,
);

# Cancellation fees
CREATE TABLE cancellation_fee (
    fee_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    waived BOOLEAN DEFAULT FALSE,
    waived_by BIGINT,
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) ON DELETE CASCADE,
    FOREIGN KEY (waived_by) REFERENCES user(user_id) ON DELETE SET NULL,
);
