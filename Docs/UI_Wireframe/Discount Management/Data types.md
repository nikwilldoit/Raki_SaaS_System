## Discount Management UI Wireframes — MySQL Data Types (Normalized)

### Discount
| Variable               | Data Type                                                                | Extracted from |
|----------------------- |--------------------------------------------------------------------------|----------------|
| id                     | BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT                               | stored         |
| name                   | VARCHAR(255) NOT NULL                                                    | stored         |
| code                   | VARCHAR(64) NOT NULL UNIQUE                                              | stored         |
| type                   | ENUM('ORDER','PRODUCT') NOT NULL                                         | stored         |
| value_type             | ENUM('AMOUNT','PERCENT') NOT NULL                                        | stored         |
| amount_value           | DECIMAL(12,2) NULL                                                       | stored         |
| percent_value          | DECIMAL(5,2) NULL                                                        | stored         |
| min_order_value        | DECIMAL(12,2) NOT NULL DEFAULT 0.00                                      | stored         |
| start_date             | DATETIME NOT NULL                                                        | stored         |
| end_date               | DATETIME NULL                                                            | stored         |
| total_uses_limit       | INT UNSIGNED NULL                                                        | stored         |
| uses_per_order         | INT UNSIGNED NULL                                                        | stored         |
| uses_per_product       | INT UNSIGNED NULL                                                        | stored         |
| status                 | ENUM('ACTIVE','SCHEDULED','INACTIVE','EXPIRED') NOT NULL                 | stored         |
| created_at             | TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP                             | stored         |
| updated_at             | TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | stored         |
