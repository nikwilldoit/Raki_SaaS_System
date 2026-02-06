## Payment UI Wireframes Data Types

### Order
| Variable    | Data Type         | Extracted from |
| --------    | ----------------- | ---------      |
| totalAmount | DECIMAL(12,2)     | Calculated     |
| leftToPay   | DECIMAL(12,2)     | Calculated     |

### Line Item
| Variable          | Data Type       | Extracted from |
| --------          | --------------- | ---------      |
| Product           | BIGINT UNSIGNED | stored         |
| Quantity          | INT UNSIGNED    | stored         |
| Payment Status    | ENUM            | stored         |
| Payer Name        | VARCHAR(100)    | Input          |

### Product
| Variable              | Data Type      | Extracted from |
| --------              | ---------------| ---------      |
| productName           | VARCHAR(255)   | Stored         |
| tax                   | DECIMAL(12,2)  | Stored         |
| discount              | DECIMAL(12,2)  | Stored         |
| productTotalPrice     | DECIMAL(12,2)  | Calculated     |
| productIngredients    | DECIMAL(12,2)  | Calculated     |

### Card Payment
| Variable              | Data Type       | Extracted from   |
| --------              | --------------- | ---------        |
| Card Number           | VARCHAR(19)     | Input            |
| expMonth              | TINYINT UNSIGNED| Input            |
| expYear               | YEAR            | Input            |
| cardCvc               | CHAR(4)         | Input            |
| subTotalPrice         | DECIMAL(12,2)   | Calculated       |
| totalDiscounts        | DECIMAL(12,2)   | Calculated       |
| totalTax              | DECIMAL(12,2)   | Calculated       |
| totalPaymentAmount    | DECIMAL(12,2)   | Calculated       |
