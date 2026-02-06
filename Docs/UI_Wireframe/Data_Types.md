### Log In

| Variable | Data Type   | Extracted from |
| -------- | ------------ | -------------- |
| email    | VARCHAR(50)  | Stored         |
| password | VARCHAR(50)  | Stored         |

---

### Dashboard Metrics

| Variable        | Data Type | Extracted from |
| ---------------- | --------- | -------------- |
| totalCategories | INT       | Calculated     |
| totalProducts   | INT       | Calculated     |
| totalBills      | INT       | Calculated     |

---

## Entities

### Category

| Variable            | Data Type      | Extracted from          |
| -------------------- | -------------- | ----------------------- |
| categoryId           | AUTO INCREMENT | Stored (Auto Increment) |
| categoryName         | VARCHAR(100)   | Stored                  |
| categoryDescription  | TEXT           | Stored                  |
| categoryIcon         | BLOB           | Stored                  |
| totalProducts        | INT            | Calculated              |
| categoryCreatedDate  | DATE           | Stored                  |
| categoryUpdatedDate  | DATE           | Stored                  |

---

### Product

| Variable           | Data Type     | Extracted from       |
| ------------------- | -------------- | -------------------- |
| productId           | VARCHAR(50)   | Stored (Primary Key) |
| productName         | VARCHAR(100)  | Stored               |
| categoryName        | VARCHAR(100)  | Stored               |
| basePrice           | DECIMAL(10,2) | Stored               |
| productIngredients  | VARCHAR(255)  | Stored               |
| description         | TEXT          | Stored               |
| isAvailable         | BOOLEAN       | Stored               |
| createdAt           | DATE          | Stored               |
| updatedAt           | DATE          | Stored               |

---

### User

| Variable | Data Type          | Extracted from |
| --------- | ------------------ | -------------- |
| userId    | INT AUTO INCREMENT | Stored         |
| username  | VARCHAR(50)        | Stored         |
| role      | ENUM               | Stored         |
| email     | VARCHAR(100)       | Stored         |
| password  | VARCHAR(100)       | Stored         |
| isActive  | BOOLEAN            | Stored         |
