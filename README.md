# **Raki** - Full-Stack POS SaaS Web Application

**Raki** is a full-stack Point of Sale (POS), Software-as-a-Service (SaaS) web application designed for small businesses. It provides a complete business management solution, including menu management, orders, payments, reservations, staff administration, taxation, and administrative tools.

The project was designed and implemented during an **Erasmus+** study period at **Vilnius University** (Faculty of Mathematics and Informatics), as part of the **Software Design** course taught by [**Vasilij Savin**](https://www.old.vu.lt/studijos/edukaciniu-kompetenciju-centras/vu-mentorystes-programa-destytojams/mentoriai-d/vasilij-savin).

---

# Overview

The application consists of two main modules:

- **Backend (Spring Boot)** – REST APIs, business logic, authentication, and persistence  
  - Backend package: [`backend-path`](https://github.com/nikwilldoit/Raki_SaaS_System/tree/main/src/main/java/com/raki/pos)
    
- **Frontend (React)** – "customer" and "staff-facing" user interface  
  - Frontend code: [`frontend-path`](https://github.com/nikwilldoit/Raki_SaaS_System/tree/main/front_end/src/Components)
    
- **DataBase (MySQL)**  
  - DB package: [`db-structure`](https://github.com/nikwilldoit/Raki_SaaS_System/tree/main/src/main/resources/db)
---

# Backend Domain Modules

Each major backend module includes its own README for a more detailed explanation of its architecture, responsibilities, and implementation.

| Module | Documentation |
|--------|---------------|
| Admin | [`src/main/java/com/raki/pos/admin/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/admin/README.md) |
| Authentication & Security | [`src/main/java/com/raki/pos/auth/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/auth/README.md) |
| Business | [`src/main/java/com/raki/pos/business/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/business/README.md) |
| Dashboard | [`src/main/java/com/raki/pos/dashboard/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/dashboard/README.md) |
| Discount Policies | [`src/main/java/com/raki/pos/discount/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/discount/README.md) |
| Menu & Ingredient Categories | [`src/main/java/com/raki/pos/menu/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/menu/README.md) |
| Orders | [`src/main/java/com/raki/pos/order/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/order/README.md) |
| Payments | [`src/main/java/com/raki/pos/payment/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/payment/README.md) |
| Products | [`src/main/java/com/raki/pos/product/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/product/README.md) |
| Reservations | [`src/main/java/com/raki/pos/reservation/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/reservation/README.md) |
| Staff Management | [`src/main/java/com/raki/pos/staff/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/staff/README.md) |
| Tax Policies | [`src/main/java/com/raki/pos/tax/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/tax/README.md) |
| Database Schema | [`src/main/resources/db/README.md`](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/resources/db/README.md) |

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

# Core Business Features

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
- **Planned** integration with **Stripe** to support secure online payment processing.

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

- REST controllers
- Service layer
- DTOs
- SQL persistence using `JdbcTemplate`
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
- JWT

---

## Database

Relational database (MySQL) including tables such as:

- `users`
- `roles`
- `businesses`
- `products`
- `product_types`
- `discount_policies`
- `tax_policies`
- `ingredient_categories`
- `ingredients`
- `product_ingredients`
- `orders`
- `order_items`
- `payments`
- `payment_splits`
- `payment_split_items`
- `available_services`
- `timeslots`
- `reservations`
- `reservation_timeslots`

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

[Project Setup Instructions (Spring Boot + React)](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/setup-instructions.md)

---

# Authors

This project was designed and developed by:

- [**Nikolaos Poulopoulos**](https://github.com/nikwilldoit)
- [**Evangelos Kampouris**](https://github.com/evangelos-kampouris)
