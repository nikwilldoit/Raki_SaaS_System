### Business
| Variable   | Data Type                                          | Stored / Calculated |
|------------|----------------------------------------------------|---------------------|
| id         | MEDIUMINT UNSIGNED PRIMARY KEY AUTO_INCREMENT      | Stored              |
| name       | VARCHAR(255) NOT NULL                              | Stored              |
| status     | ENUM('ACTIVE','PAUSED','INACTIVE') NOT NULL        | Stored              |
| owner_id   | INT                                                | Stored              |

### Branch
| Variable          | Data Type                                                    | Stored / Calculated |
|-------------------|--------------------------------------------------------------|---------------------|
| name              | VARCHAR(255) NOT NULL                                        | Stored              |
| status            | ENUM('ACTIVE','PAUSED','INACTIVE') NOT NULL DEFAULT 'ACTIVE' | Stored              |
| address_line1     | VARCHAR(255) NOT NULL                                        | Stored              |
| address_line2     | VARCHAR(255) NULL                                            | Stored              |
| city              | VARCHAR(128) NOT NULL                                        | Stored              |
| region            | VARCHAR(128) NULL                                            | Stored              |
| postalCode        | VARCHAR(32) NULL                                             | Stored              |
| managerName       | VARCHAR(160) NULL                                            | Stored              |
| branchPhoneNumber | VARCHAR(32) NULL                                             | Stored              |