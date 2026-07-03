# POS Web Application

This project is a full-stack Point of Sale (POS) Software-as-a-Service (SaaS) web application designed for small businesses. It provides a complete business management solution, including menu management, orders, payments, reservations, staff administration, taxation, and administrative tools.

The project was designed and implemented during an **Erasmus+** study period at **Vilnius University** (Faculty of Mathematics and Informatics).

---

# Overview

The application consists of two main repositories/modules:

- **Frontend (React)** – Customer- and staff-facing user interface
  - Frontend source: [`pos-frontend/`](./pos-frontend/)
  - Main React code: [`pos-frontend/src/Components/`](./pos-frontend/src/Components/)

- **Backend (Spring Boot)** – REST APIs, business logic, authentication, and persistence
  - Backend source: [`pos-backend/`](./pos-backend/)
  - Main application package: [`src/main/java/com/raki/pos/`](./pos-backend/src/main/java/com/raki/pos/)

---

# Backend Domain Modules

Each major backend module includes its own README for a more detailed explanation of its architecture, responsibilities, and implementation.

| Module | Documentation |
|---------|---------------|
| Admin / SuperAdmin | [`src/main/java/com/raki/pos/admin/README.md`](./pos-backend/src/main/java/com/raki/pos/admin/README.md) |
| Authentication & Security | [`src/main/java/com/raki/pos/auth/README.md`](./pos-backend/src/main/java/com/raki/pos/auth/README.md) |
| Business | [`src/main/java/com/raki/pos/business/README.md`](./pos-backend/src/main/java/com/raki/pos/business/README.md) |
| Dashboard | [`src/main/java/com/raki/pos/dashboard/README.md`](./pos-backend/src/main/java/com/raki/pos/dashboard/README.md) |
| Discount Policies | [`src/main/java/com/raki/pos/discount/README.md`](./pos-backend/src/main/java/com/raki/pos/discount/README.md) |
| Menu & Ingredient Categories | [`src/main/java/com/raki/pos/menu/README.md`](./pos-backend/src/main/java/com/raki/pos/menu/README.md) |
| Orders | [`src/main/java/com/raki/pos/order/README.md`](./pos-backend/src/main/java/com/raki/pos/order/README.md) |
| Payments | [`src/main/java/com/raki/pos/payment/README.md`](./pos-backend/src/main/java/com/raki/pos/payment/README.md) |
| Products | [`src/main/java/com/raki/pos/product/README.md`](./pos-backend/src/main/java/com/raki/pos/product/README.md) |
| Reservations | [`src/main/java/com/raki/pos/reservation/README.md`](./pos-backend/src/main/java/com/raki/pos/reservation/README.md) |
| Staff Management | [`src/main/java/com/raki/pos/staff/README.md`](./pos-backend/src/main/java/com/raki/pos/staff/README.md) |
| Tax Policies | [`src/main/java/com/raki/pos/tax/README.md`](./pos-backend/src/main/java/com/raki/pos/tax/README.md) |
| Database Schema | [`src/main/resources/db/README.md`](./pos-backend/src/main/resources/db/README.md) |

Repository layout:

```text
pos-backend/
└── src/
    └── main/
        ├── java/
        │   └── com/raki/pos/
        │       ├── admin/
        │       ├── auth/
        │       ├── business/
        │       ├── dashboard/
        │       ├── discount/
        │       ├── menu/
        │       ├── order/
        │       ├── payment/
        │       ├── product/
        │       ├── reservation/
        │       ├── staff/
        │       └── tax/
        └── resources/
            └── db/
```

---

# Features

## Core Business Features

### Menu & Products

- Create, edit, and delete products.
- Configure tax categories and product categories.
- Define ingredient categories (toppings, extras, sizes, etc.).
- Attach optional ingredients to products.
- Apply tax and discount policies automatically.

### Orders & Payments

- Create and manage customer orders.
- Support both full and split payments.
- Split bills by payer, item, or payment method.
- Track tips and payment history.
- Automatically close orders once fully paid.

### Reservations

- Manage service bookings.
- Configure employees, services, and available time slots.
- Display daily and employee availability.
- Create, update, and cancel reservations.

### Staff Management

- Manage Owners and Employees.
- Update staff roles and employment status.
- Enforce role-based permissions.

### Tax Policies

- Create reusable tax policies.
- Support STANDARD, REDUCED, and ZERO tax rates.
- Integrate tax calculations into product pricing.

### Admin / SuperAdmin

- Switch between businesses.
- Manage multiple business environments.
- Access dashboards within the selected business context.

### Authentication & Security

- Email/password authentication.
- JWT token generation and validation.
- Secure REST APIs.
- Health-check endpoints.

---

# Frontend

The frontend is built with **React** and focuses on usability for both staff and administrators.

Features include:

- Menu management
- Product management
- Ingredient management
- Payment overview
- Split payment interface
- Reservation wizard
- Reservation management
- Staff administration
- SuperAdmin business selection

Implementation highlights:

- Functional React components
- React Hooks
- Fetch API
- JWT Bearer authentication
- Multi-step workflows for reservations and payments

---

# Backend

The backend is built with **Spring Boot** and **Spring Security**.

Main components include:

- REST Controllers
- Service layer
- DTOs
- SQL persistence using JdbcTemplate
- JWT authentication
- Security filters
- Transaction management

---

# Technologies

## Frontend

- React
- React Router
- CSS Modules / Custom CSS
- Fetch API
- JavaScript (ES6+)

---

## Backend

- Java 17+
- Spring Boot
- Spring Web
- Spring Security
- JdbcTemplate
- Log4j2
- Jakarta Validation
- JJWT

---

## Database

Relational database (PostgreSQL or MySQL) including tables such as:

- users
- roles
- businesses
- products
- product_types
- discount_policies
- tax_policies
- ingredient_categories
- ingredients
- product_ingredients
- orders
- order_items
- payments
- payment_splits
- payment_split_items
- available_services
- timeslots
- reservations
- reservation_timeslots

---

# Erasmus+ at Vilnius University

This project was developed during an **Erasmus+ exchange programme** at **Vilnius University** in Vilnius, Lithuania.

The project combined academic study with practical software engineering experience through the development of a production-style full-stack application.

The implementation involved:

- Full-stack web development with React and Spring Boot
- REST API design
- Authentication and authorization
- Database design
- Business workflow implementation
- Software architecture
- International collaboration under the Erasmus+ programme

For more information:

- [Vilnius University – Erasmus+ Studies](https://mif.vu.lt/lt3/en/studies/exchange-studies/erasmus-studies)
- [Vilnius University Exchange Opportunities](https://www.vu.lt/en/students/services-for-students/exchange-opportunities)

---

# Repository Structure

```text
root/
├── README.md
├── pos-frontend/
├── pos-backend/
│   └── src/
│       └── main/
│           ├── java/
│           └── resources/
├── docs/
└── scripts/
```

---

# Getting Started

## 1. Clone the repositories

```bash
git clone <frontend-repository-url>
git clone <backend-repository-url>
```

---

## 2. Configure the environment

- Configure your database connection.
- Set the JWT secret in the backend configuration.
- Update the frontend API URL if necessary (default: `http://localhost:8080`).

---

## 3. Run the backend

```bash
mvn spring-boot:run
```

or start the Spring Boot application from your IDE.

---

## 4. Run the frontend

```bash
npm install
npm start
```

---

## 5. Open the application

Navigate to:

```
http://localhost:3000
```

and log in using a test account.
