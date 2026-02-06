# Reservation
| Variable            | MySQL Data Type                                  | Extracted from    |
| ------------------- | ------------------------------------------------ | ----------------- |
| reservation_id      | VARCHAR(100) PRIMARY KEY                         | stored            |
| customer_full_name  | VARCHAR(255)                                     | stored            |
| customer_email      | VARCHAR(255)                                     | stored            |
| phone_number        | VARCHAR(20)                                      | stored            |
| party_size          | INT                                              | stored            |
| reservation_date    | DATE                                             | stored            |
| reservation_time    | TIME                                             | stored            |
| duration            | VARCHAR(50)                                      | stored            |
| table_number        | INT                                              | stored            |
| special_requests    | TEXT                                             | stored            |
| current_status      | ENUM('CONFIRMED','PENDING','CANCELLED','SEATED') | stored            |
| update_status       | ENUM('CONFIRMED','PENDING','CANCELLED','SEATED') | stored            |
| staff_notes         | TEXT                                             | stored            |
| cancellation_reason | TEXT                                             | stored (optional) |
| reservation_reason  | ENUM(//List of all the services we give)         | stored            |


# User 
| Variable           | MySQL Data Type                    | Extracted from      |
| ------------------ | ---------------------------------- | ------------------- |
| user_id            | INT AUTO_INCREMENT PRIMARY KEY     | stored              |
| first_name         | VARCHAR(100)                       | stored              |
| last_name          | VARCHAR(100)                       | stored              |
| date_of_birth      | DATE                               | stored              |
| email_address      | VARCHAR(255)                       | stored              |
| personal_id        | VARCHAR(100)                       | stored              |
| phone_number       | VARCHAR(20)                        | stored              |
| department         | VARCHAR(100)                       | stored              |
| role               | ENUM('EMPLOYEE','MANAGER','ADMIN') | stored              |
| password           | VARCHAR(255)                       | stored (hashed)     |
| read_access        | BOOLEAN                            | stored              |
| write_access       | BOOLEAN                            | stored              |
| delete_access      | BOOLEAN                            | stored              |
| export_data        | BOOLEAN                            | stored              |
| user_management    | BOOLEAN                            | stored              |
| system_settings    | BOOLEAN                            | stored              |




