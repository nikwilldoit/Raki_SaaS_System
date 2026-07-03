# POS Web Application

This project is a full‑stack Point of Sale (POS), Software as a Service (SaaS) web application for small businesses, covering menu management, orders, payments, reservations, staff, taxes and admin tools. It was designed and implemented as part of an **Erasmus+** study period at **Vilnius University** (Faculty of Mathematics and Informatics). [web:41][web:50]

## Overview

The application is split into multiple modules/repositories:

- **Frontend (React)** – customer‑ and staff‑facing UI  
  - Frontend codebase: `pos-frontend/` (e.g. `src/Components/...`)

- **Backend (Spring Boot)** – REST APIs, business logic, persistence  
  - Backend codebase: `pos-backend/` (core app under `src/main/java/com/raki/pos/...`)

### Backend domain modules (per package)

Each core domain has its own README inside the backend so you can deep‑dive when browsing the code:

- **Admin / SuperAdmin**  
  Path: [`src/main/java/com/raki/pos/admin/README.md`](pos-backend/src/main/java/com/raki/pos/admin/README.md)  
  Describes SuperAdmin business selection, context switching, and admin flows.

- **Authentication & Security**  
  Path: [`src/main/java/com/raki/pos/auth/README.md`](pos-backend/src/main/java/com/raki/pos/auth/README.md)  
  Describes login, JWT generation/validation, the security filter, and health checks.

- **Business**  
  Path: [`src/main/java/com/raki/pos/business/README.md`](pos-backend/src/main/java/com/raki/pos/business/README.md)  
  Describes business entities, business types, and high‑level configuration per business.

- **Dashboard**  
  Path: [`src/main/java/com/raki/pos/dashboard/README.md`](pos-backend/src/main/java/com/raki/pos/dashboard/README.md)  
  Describes dashboard endpoints and how data (orders, reservations, staff, etc.) is aggregated for the home screen.

- **Discount Policies**  
  Path: [`src/main/java/com/raki/pos/discount/README.md`](pos-backend/src/main/java/com/raki/pos/discount/README.md)  
  Describes discount rules (percent/amount), scopes (product/service/both), and how they affect prices.

- **Menu & Ingredient Categories**  
  Path: [`src/main/java/com/raki/pos/menu/README.md`](pos-backend/src/main/java/com/raki/pos/menu/README.md)  
  Describes ingredient categories, ingredients, product extras/options, and single/multi‑select behaviour.

- **Orders**  
  Path: [`src/main/java/com/raki/pos/order/README.md`](pos-backend/src/main/java/com/raki/pos/order/README.md)  
  Describes the order lifecycle, order items, payment status, and how orders connect to payments and the menu.

- **Payments**  
  Path: [`src/main/java/com/raki/pos/payment/README.md`](pos-backend/src/main/java/com/raki/pos/payment/README.md)  
  Describes full and split payments, tips, payment history, closing orders, and payment/split status handling.

- **Products**  
  Path: [`src/main/java/com/raki/pos/product/README.md`](pos-backend/src/main/java/com/raki/pos/product/README.md)  
  Describes products, product types, the tax/discount price pipeline, and links to ingredient extras.

- **Reservations**  
  Path: [`src/main/java/com/raki/pos/reservation/README.md`](pos-backend/src/main/java/com/raki/pos/reservation/README.md)  
  Describes services, timeslots, reservation creation/update/delete, and availability per business/employee.

- **Staff Management**  
  Path: [`src/main/java/com/raki/pos/staff/README.md`](pos-backend/src/main/java/com/raki/pos/staff/README.md)  
  Describes users/staff, roles (Owner, Employee, SuperAdmin), statuses (ACTIVE, SUSPENDED, FIRED), and access rules.

- **Tax Policies**  
  Path: [`src/main/java/com/raki/pos/tax/README.md`](pos-backend/src/main/java/com/raki/pos/tax/README.md)  
  Describes tax policy definitions, tax types (STANDARD, REDUCED, ZERO), and how they are applied in the price pipeline.

- **Database / Schema**  
  Path: [`src/main/resources/db/README.md`](pos-backend/src/main/resources/db/README.md)  
  Describes core tables, relationships, seed data, and the migration strategy (SQL scripts or Flyway/Liquibase).

This lets readers jump from the main README to each subsystem:

```text
pos-backend/
└─ src/
   └─ main/
      ├─ java/
      │   └─ com/raki/pos/
      │       ├─ admin/README.md
      │       ├─ auth/README.md
      │       ├─ business/README.md
      │       ├─ dashboard/README.md
      │       ├─ discount/README.md
      │       ├─ menu/README.md
      │       ├─ order/README.md
      │       ├─ payment/README.md
      │       ├─ product/README.md
      │       ├─ reservation/README.md
      │       ├─ staff/README.md
      │       └─ tax/README.md
      └─ resources/
          └─ db/README.md
```

## Features

### Core business features

- **Menu & Products**
  - Create, edit and delete products with tax categories, subcategories and ingredient options.
  - Define ingredient categories (e.g. toppings, extras, sizes) and attach them to products.
  - Use discount and tax policies to compute effective prices.

- **Orders & Payments**
  - Support full‑bill payments and split payments by item, payer and method (cash/card/gift card).
  - Track tips and display payment history per order.
  - Automatically close orders when fully paid and mark all items as paid.

- **Reservations**
  - Offer service bookings with available services, employees and time slots.
  - Show availability per day and per employee.
  - Create, update and cancel reservations, freeing time slots when needed.

- **Staff Management**
  - List staff for the current business (Owners, Employees).
  - Edit staff details, roles and status (ACTIVE, SUSPENDED, FIRED).
  - Enforce permissions (e.g. Owners cannot edit other Owners).

- **Tax Policies**
  - Configure named tax policies (e.g. “VAT 24%”) with a rate and type (STANDARD / REDUCED / ZERO).
  - Apply taxes in the product price pipeline before discounts.

- **Admin / SuperAdmin**
  - SuperAdmin users can switch between businesses by name.
  - After selection, all dashboards and management screens operate in the chosen business context.

- **Authentication & Security**
  - Email/password login endpoint that returns a JWT token.
  - JWT‑based request authentication via a custom filter.
  - Health‑check endpoints for connectivity and deployment checks.

### Frontend

The UI is built with **React** and focuses on clarity and usability for staff:

- Separate pages for:
  - Menu dashboard and product/ingredient management.
  - Payment summary, split payments and payment overview per order.
  - Reservation creation wizard, reservation list and reservation edit dialog.
  - Staff management and SuperAdmin business selection.
- Uses fetch with bearer tokens for secure API calls.
- Implements step‑by‑step flows (e.g. reservation creation: customer → service/employee → time slot → confirmation).

### Backend

The backend is built with **Spring Boot** and **Spring Security**:

- REST controllers per domain (products, menu, reservations, payments, staff, taxes, admin, auth).
- Business logic in service classes, with transactional boundaries where needed.
- Persistence via **JdbcTemplate** and SQL, mapped to DTOs and model classes.
- JWT utilities for token creation, validation and claim extraction.
- Security filter that populates the `SecurityContext` based on the JWT token.

## Technologies

### Frontend

- **React** (functional components, hooks: `useState`, `useEffect`, `useMemo`)
- **React Router** for navigation (dashboard, payments, reservations, menu, admin)
- **CSS Modules / custom CSS** for styling (e.g. `Payment.css`, `ManageProducts.css`, `ReservationCreatePage.css`)
- Native **fetch API** for HTTP requests with `Authorization: Bearer <token>`

### Backend

- **Java 17+** (or the version used)
- **Spring Boot**
  - Spring Web (REST controllers)
  - Spring Security (JWT‑based authentication)
- **JdbcTemplate** for database access with SQL queries
- **Log4j2** for logging
- **Jakarta Validation** (`@Valid`, `@NotBlank`, `@NotNull`, `@Min`) for request DTOs
- **JJWT** (`io.jsonwebtoken`) for JWT handling

### Database

- Relational database (e.g. PostgreSQL / MySQL – adjust as needed) with tables such as:
  - `users`, `roles`, `businesses`
  - `products`, `product_types`, `discount_policies`, `tax_policies`
  - `ingredient_categories`, `ingredients`, `product_ingredients`
  - `orders`, `order_items`, `payments`, `payment_splits`, `payment_split_items`
  - `available_services`, `timeslots`, `reservations`, `reservation_timeslots`

_You can adapt table names and the DB engine if they differ._

## Erasmus at Vilnius University

This application was developed during an **Erasmus+ exchange** at **Vilnius University** in Vilnius, Lithuania, as part of studies in software engineering / information systems. The Erasmus+ programme supports students to study abroad at partner universities and gain international experience in their field. [web:41][web:50]

Working on this POS web application at Vilnius University combined:

- Practical experience in full‑stack web development (React + Spring Boot).
- Realistic business workflows (orders, payments, bookings, staff, taxes).
- International collaboration and academic supervision under the Erasmus+ framework.

You can find more about Vilnius University and its Erasmus+ opportunities on the official site:
- [Vilnius University – Erasmus+ studies](https://mif.vu.lt/lt3/en/studies/exchange-studies/erasmus-studies) [web:50]
- [Vilnius University exchange opportunities](https://www.vu.lt/en/students/services-for-students/exchange-opportunities) [web:41]

## Repository structure

Example structure for a monorepo with frontend + backend:

```text
root/
├─ README.md                # Main overview (this file)
├─ pos-frontend/            # React app
├─ pos-backend/             # Spring Boot backend
│  └─ src/
│     └─ main/
│        ├─ java/com/raki/pos/...  # Domain packages + READMEs
│        └─ resources/db/README.md # DB schema/docs
├─ docs/                    # Extra documentation, diagrams
└─ scripts/                 # Helper scripts (build, deploy, seed)
```

## Getting started

1. **Clone the repositories**
   - Frontend: `git clone <frontend-repo-url>`
   - Backend: `git clone <backend-repo-url>`

2. **Configure environment**
   - Set database connection and JWT secret in backend configuration.
   - Adjust API base URL in frontend (`http://localhost:8080` by default).

3. **Run backend**
   - `mvn spring-boot:run` or via IDE (run the main Application class).

4. **Run frontend**
   - `npm install`
   - `npm start`

5. Open the app in your browser (typically `http://localhost:3000`) and log in with a test user.
