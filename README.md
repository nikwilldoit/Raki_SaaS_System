# Raki SaaS System

Raki SaaS System is a Software-as-a-Service platform for small and medium businesses in the catering (bars, cafés, restaurants) and beauty (barbers, hairdressers, spas) sectors. It digitalizes day‑to‑day operations such as orders, payments, reservations, discounts, taxes, and user management in a role‑based, auditable way.

The goal of this project is to serve as a detailed, implementable technical blueprint that a separate development team can use to build a production‑grade system.

---

## Table of Contents

1. [Overview](#overview)
2. [Core Features](#core-features)
3. [Business Flows](#business-flows)
   - [Order Flows](#order-flows)
   - [Reservation Flows](#reservation-flows)
   - [Tax Management](#tax-management-business)
   - [User Management](#user-management-business)
   - [Discount Management](#discount-management-business)
4. [Technical Flows & UI](#technical-flows--ui)
   - [Order Flow](#order-flow-technical)
   - [Payment & Split Payments](#payment--split-payments)
   - [Logging Pipeline](#logging-pipeline)
   - [Reservation Flow](#reservation-flow-technical)
   - [Discount Flow](#discount-flow-technical)
   - [Menu & Product Management](#menu--product-management)
   - [Tax Management Module](#tax-management-module)
   - [User & RBAC Management](#user--rbac-management)
5. [Role-Based Access Control](#role-based-access-control)
   - [Permission Catalog](#permission-catalog)
   - [Role Bundles](#role-bundles)
6. [High-Level Architecture](#high-level-architecture)
7. [Data Model](#data-model)
8. [Technology Assumptions](#technology-assumptions)
9. [Getting Started](#getting-started)
10. [Contributing](#contributing)
11. [Future Work](#future-work)

---

## Overview

This specification describes the business logic, data model, and API contracts for a multi‑tenant SaaS platform that supports multiple merchants and branches. The system is primarily operated by business employees; customers interact indirectly through booking and payment.

The core functional areas are:

- Order Management: create, modify, cancel, refund orders; split checks, tips, discounts.
- Service Reservations: create, update, cancel reservations for services, with optional deposits and cancellation fees.
- System Management: manage products, menus, taxes, discounts, users, roles, and merchant details.

All flows are designed so they can be implemented as REST/JSON APIs described via OpenAPI, backed by a relational database and a set of microservices or modular monolith components.

---

## Core Features

- Order lifecycle: create, edit, cancel, close, refund, apply product‑level and order‑level discounts, calculate taxes on the fly.
- Reservation lifecycle: create, edit, cancel reservations for services with employee assignment, deposits, and cancellation fees.
- Menu & product management: maintain catalogues, ingredient categories, per‑product ingredient options, and tax categories.
- Discount & tax modules: central configuration of discounts (order/product) and tax rules with auditability and permission checks.
- User & role management: RBAC with predefined role bundles (Employee, Owner/Manager, SuperAdmin) and fine‑grained permissions.
- Logging & observability: consistent JSON logging across services, centralized collection, storage, dashboards, and alerts.

---

## Business Flows

### Order Flows

#### Create

**Preconditions**

- Customers are seated and ready to order.
- Employee is authenticated and has permission to create orders.
- Menu and stock information are up to date; unavailable items are known.

**Flow**

1. Customer reviews the menu and starts ordering.
2. Employee opens a new order, assigns an order number, and informs about unavailable products.
3. Customer specifies items, ingredients/options, and quantities.
4. Employee records extra‑cost ingredients or add‑ons when applicable.
5. Employee confirms items with the customer and records them.
6. Customer adds more items; employee repeats the selection and confirmation process.
7. Employee totals the order, applying item‑level discounts, service fees, and taxes.
8. Employee may apply additional discounts (item or order level) if authorized.
9. Customer decides whether to split the bill; if split, each participant chooses payment method and tip.
10. Payment is processed and recorded.
11. Order status is set to paid/closed.

**Key Entities**

- Employee, Order, Cart, Product, ProductIngredients, Discount, Payment, Tip, Stock, Food Catalogue.

**Components**

- Order Management
- Payment Management
- Inventory
- User Manager / Permissions

**Actions**

- Customer edits order before closure.
- Employee applies discounts.
- Customer selects payment method and pays.
- Employee checks unavailable items and updates order accordingly.

#### Edit

**Preconditions**

- Order exists and is still open (not paid).
- Employee has permission to update orders.

**Flow**

1. Customer requests changes (add/remove items, change quantity/options).
2. Employee retrieves the open order.
3. Employee applies requested changes.
4. Employee confirms changes with the customer.
5. Order continues through to payment with updated details.

#### Cancel

**Preconditions**

- Order exists and is open/unpaid.
- Customer or employee decides to cancel.
- Employee is authorized to cancel open orders.

**Flow**

1. Customer or employee initiates cancellation.
2. Employee reviews the open order.
3. Employee confirms cancellation with the customer when possible.
4. Order is marked as canceled and excluded from further processing.

#### Refund

**Preconditions**

- Order has been closed and paid.
- Customer requests a refund.

**Flow**

1. Customer explains reason for refund.
2. Manager reviews the request.
3. If approved, manager decides full vs partial refund.
4. Employee returns the amount (cash, card, gift card).
5. Employee issues refund receipt/confirmation.
6. Order status changes to refunded.

**Entities**

- Refund, Order, Payment.

**Actions**

- Manager approves and defines refund scope.
- Employee performs the refund and records it.

---

### Reservation Flows

#### Creation

**Preconditions**

- Business supports reservations for services.
- Staff is authorized to accept reservations.
- Availability (timeslots, employees, tables) is up to date.

**Flow**

1. Customer requests a reservation (phone, in person, other).
2. Customer may request a specific employee, date, and time.
3. Staff checks availability for requested slot.
4. If available, reservation is created with customer details.
5. Employee is assigned to the reservation.
6. Customer receives verbal or written confirmation (slip, message).

**Entities**

- Customer, Reservation, Schedule, Employee, Service, Payment/Deposit, Notification.

**Components**

- Reservation Management System
- Payment System (for deposits)
- Notification Service (SMS, email)

#### Edit

**Preconditions**

- Reservation exists and is active.
- Requesting party (customer or staff) is authorized.

**Flow**

1. Customer requests change (time, date, service).
2. Staff reviews reservation and checks new availability.
3. If capacity exists, reservation is updated.
4. Customer is informed of new details and any pricing implications.

#### Cancel

**Preconditions**

- Reservation exists and is not yet fulfilled.
- Customer or business decides to cancel.

**Flow**

1. Cancellation request is initiated.
2. Existing reservation details are reviewed.
3. If within permitted timeframe, cancellation is approved.
4. Reservation is marked as canceled.
5. Deposits/refunds follow the cancellation rules (forfeiture or refund).
6. Customer is informed of cancellation and any fees.

**Entities**

- Reservation, CancellationFee, ReservationDeposit.

**Actions**

- Customer cancels reservation.
- System applies cancellation fees or refunds deposits based on rules.

---

### Tax Management (Business)

#### Create (Tax Setup & Configuration)

**Preconditions**

- Business operates in a tax jurisdiction (VAT, sales tax, service tax).
- Official tax policies and rules exist.
- Authorized personnel can create tax rules.

**Flow**

1. Owner or administrator identifies need for a new tax rule.
2. Manager defines tax name/type, rate/percentage, description, effective date.
3. Tax rule is stored and becomes available for price calculation based on status (e.g. pending, active).

**Entity**

- Tax (taxRate).

#### Edit (Adjustment)

**Preconditions**

- Change has been authorized by management.
- Editor has administrative permissions.

**Flow**

1. Admin decides an existing tax must be updated.
2. Proposed changes are documented and approved (owner/accountant).
3. Staff is informed about the change and effective date.
4. System updates tax configuration and applies new rate automatically.
5. Changes are logged for audit and traceability.

#### Delete (Deactivate or Remove)

**Preconditions**

- Tax rule is obsolete, repealed, or replaced.
- Rule is not used by active orders or pending transactions.
- Deactivation/removal is formally authorized.

**Flow**

1. Admin identifies obsolete tax rule.
2. Admin verifies no active transactions depend on it.
3. Tax rule is deactivated (preferred) or deleted.
4. System records date and reason; rule is archived.
5. Tax rule is no longer applied to new transactions.

#### Cancel (Rejected Tax Configuration)

**Preconditions**

- A tax setup or modification request was attempted.
- Request is invalid, unauthorized, or incomplete.
- Rule is not yet applied to live transactions.

**Flow**

1. Manager initiates tax setup or change.
2. Organization reviews and finds invalid details.
3. Request is marked as canceled or not approved.
4. No active tax record is created; if started, it is marked "Canceled".
5. Requester is informed and may correct and resubmit later.
6. Cancellation is logged in review notes.

---

### User Management (Business)

#### Create

**Preconditions**

- A new employee has been hired.
- Manager/owner can add employees to records.

**Flow**

1. New hire provides personal/contact details.
2. Manager creates a new user record.
3. Manager assigns a role (e.g. Waiter, Cashier, Supervisor).
4. Responsibilities and permissions are explained.
5. Record is stored securely.

**Entities**

- User, Role, Permission.

#### Edit

**Preconditions**

- Employee record exists.
- Manager decides to change role, permissions, or details.

**Flow**

1. Manager identifies need to update user.
2. Manager retrieves the user record.
3. Old information is updated with new details and reasoning.
4. Changes are discussed with the employee.
5. Updated record is stored securely again.

#### Delete

**Preconditions**

- Employee leaves the organization.
- No remaining obligations.

**Flow**

1. Manager retrieves employee record.
2. Record is marked inactive/terminated with final employment date.
3. Manager signs the entry.
4. Record is archived and user account can be deactivated/deleted in the system.

---

### Discount Management (Business)

> “Product” covers both food items and services (e.g. haircut, massage).

#### Creation

**Preconditions**

- Responsible employee (manager/owner) has authority to create promotions.
- Employee is logged in and has discount‑creation permissions.

**Flow**

1. Employee decides to introduce a new discount.
2. Scope is defined: specific product/service or entire order/reservation.
3. Discount format is defined: fixed amount or percentage rate.
4. Optional constraints: minimum price, duration, usage limits.
5. Rules are recorded in a central log and notice is created.
6. Discount is communicated to staff and posted in staff area.

**Entity**

- Discount.

#### Edit

**Preconditions**

- Discount is active.
- Authorized employee manages promotions.

**Flow**

1. Employee decides to alter the discount (percentage, duration, rules).
2. Original notice/log entry is found and updated with new terms and date.
3. Staff is verbally informed.
4. Updated notice is posted.

#### Delete

**Preconditions**

- Discount should be discontinued.
- Authorized employee manages promotions.

**Flow**

1. Employee announces discount termination from a specific date/time.
2. Physical/system notice is removed.
3. Log entry is marked "expired" or "discontinued" with end date.
4. Staff stops applying the discount to new orders.

---

## Technical Flows & UI

### Order Flow (Technical)

**Preconditions**

- No unavailable items (for the base scenario).
- Employee has permissions to apply additional discounts.
- Product selection UI supports category filtering and search.

**Menu Retrieval**

- System performs a real‑time query to fetch the latest active business rules:
  - Order‑level and product‑level discounts.
  - Tax regulations and rates.
  - Product menu and ingredient options.

**Price Calculation**

- Price is calculated on the fly, not stored:
  - Sum of base price of each product.
  - Plus extra ingredients/add‑ons.
  - Minus product‑level discounts.
  - Plus product‑level taxes.
- Orders aggregate item prices, order‑level discounts, and taxes for a final total.

**Product Ingredients**

- Products without ingredients do not show an ingredient overview.
- Products can have default/preselected ingredients automatically selected.
- Employee can unselect, add, or change ingredients per item.

UI wireframes (not embedded here) show:

- Product list with categories and search.
- Ingredient selection view with extended categories for demonstration.

---

### Payment & Split Payments

**Preconditions**

- Order or reservation exists and is ready for payment.
- Employee has permissions to process payments.

**Split Payment UI Concept**

Each item quantity is represented with chips:

- Blue — “Selected by you”: quantity you are about to pay for; clickable to adjust.
- Black — “Available”: unselected quantity available to any guest.
- Gray — “Locked by others”: temporarily selected by another guest; becomes available if they change/cancel.
- Green — “Paid”: quantities that are fully paid and immutable.

**Selection Rules**

- Clicking a chip assigns/adjusts quantity for the current payer.
- Setting quantity to 0 returns item to unselected (black).
- After successful payment, items move to “Paid” section.
- Gray quantities unlock automatically when other guests complete or cancel.

**Price Calculation**

Total is computed on the fly as the sum of:

- Base product prices.
- Extra ingredient/add‑on costs.
- Applied product‑level or order‑level discounts.
- Applicable taxes.

**Split Check**

When splitting:

- System divides payable items among participants.
- Each participant selects:
  - Payment method (cash, card, gift card).
  - Optional tip for their share.

**Transaction Recording**

- After payment, system logs:
  - Payment details (method, split items, tip).
  - Order status updated to paid/closed.
  - Audit trail entries for payment and discount usage.

---

### Logging Pipeline

**Preconditions**

- Consistent logging format.
- Secure infrastructure connectivity.
- Proper RBAC and encryption.
- Sufficient storage and alerting.
- Clear ownership and compliance policies.

**Sources**

- Applications & services (backend, frontend, APIs).
- Infrastructure (servers, containers, load balancers).
- Security tools (authentication systems, firewalls).

**Format**

- All components log in JSON including:
  - Timestamp
  - Service name
  - Environment
  - Severity
  - Trace ID

**Collection**

- Log agents (e.g., Fluent Bit, Filebeat, OpenTelemetry Collector):
  - Run on each machine/container.
  - Read local logs and push to central logging service.
  - Filter and clean logs to avoid secrets (passwords, tokens).

**Ingestion & Storage**

- Logs enter queue/stream for durability.
- Then go to an indexed storage system for search.
- Old logs are archived or deleted according to retention policy.

**Permissions**

- Only system components write logs.
- Admins/SuperAdmins can delete or change storage settings.
- Developers have read access to relevant service logs.

**Viewing & Use**

- Dashboards expose health, errors, performance.
- Alerts fire on error spikes or suspicious patterns.
- Access and log usage are monitored and audited.

---

### Reservation Flow (Technical)

Reservation creation consists of four main steps:

1. Customer info: name, phone, email.
2. Employee & service selection: chosen employee and service.
3. Date & time selection:
   - Past/unavailable dates shown in grey.
   - Availability derived from selected employee’s schedule.
4. Details review and confirmation.

**Preconditions**

- Single phone number country (simplified assumption).
- Upon creating a reservation:
  - System fetches latest timeslots and availability.
  - Employee inputs customer data and system validates it.
  - Selecting service and employee refreshes date & time options for that employee.

**Flow**

- On “Confirm booking”:
  - Reservation is stored in database.
  - Reservation appears in dashboard overview.

For edit or deletion:

- Employee selects a reservation from the dashboard.
- Same creation wireframe is used to edit details.
- Cancel action sets reservation status to canceled.

---

### Discount Flow (Technical)

**Preconditions**

- Authorized user is logged in with discount management permissions.

**Flow**

1. User clicks "Create Discount" in discount management interface.
2. Form appears to define discount parameters.
3. User fills:
   - Discount Name (unique).
   - Discount Type:
     - Order Discount (scope: whole order).
     - Product Discount (scope: selected products).
   - If Product Discount:
     - Select one or more products.
   - If Order Discount:
     - Optional minimum order value.
   - Value:
     - Fixed amount or percentage.
   - Time Period:
     - Start and end date, or no expiration.
   - Usage Limits:
     - Total uses, uses per order/product.
   - Status:
     - Active, Scheduled, Inactive.
4. User clicks "Save Discount".
5. System validates input and stores discount.
6. User is redirected to discount overview where the new discount is listed.

Edit/delete follow similar flows using the same create/edit page and updating status or marking discounts as expired.

---

### Menu & Product Management

#### Manage Ingredient Categories

**Category Overview**

- Table lists all categories with:
  - Name
  - Description
  - Number of ingredients
  - Number of products using the category
  - Creation and last update timestamps
- Actions per category (view, edit, delete).
- Search filter and sortable columns (e.g. Name A–Z).

#### Add/Edit Category

- Add:
  - Required: Category Name, Ingredients.
  - Optional: Description.
  - Ingredients added dynamically via "Add Ingredient".
  - Quantity per ingredient can be specified.
- Edit:
  - Form pre-filled with category’s Name, Description, Ingredients.
  - Ingredients can be edited, removed, or new ones added.

#### Delete Category

- Deleting prompts a confirmation message with category name.
- Action is irreversible.

#### Manage Products

**Product Overview**

- Table lists products with:
  - Name
  - Category
  - Description
  - Price
  - Status (Active, Out of Stock, Inactive)
  - Creation/update dates
- Actions: view, edit, delete.
- Filters:
  - Category
  - Status
- Search bar and "Clear Filters" option.

**Delete Product**

- Confirmation message with product name.
- Warning that deletion cannot be undone.

**Add Product**

- Required fields:
  - Product Name
  - Tax Category (e.g. Food, Beverages, Service)
  - Catalogue category (subcategory)
  - Ingredient Categories and selected ingredients.
- Ingredient category is a dropdown:
  - Selecting it loads its ingredients as checkboxes.
  - User selects ingredients available for orders.
  - Once at least one ingredient is selected, the category is attached to the product.
- Price and optional Description complete the form.

---

### Tax Management Module

- Tax is a configurable rate defined in the Tax Management module.
- It is automatically applied during on‑the‑fly price calculation based on product tax category and tax active status.
- Tax configuration is governed by:
  - Role‑based permissions.
  - Audit logs for creation, update, deactivation.

- Administrator creates tax rule with:
  - Name
  - Rate percentage
  - Description
  - Status (e.g. pending, active)
  - Timestamps for creation/update.

- Products and services have tax rate applied to their base price, and additional taxes may be calculated during order pricing.

---

### User & RBAC Management

The system uses role‑based access control (RBAC) with:

- Users bound to roles.
- Roles bound to permissions via a permission catalog.
- Predefined role bundles: Employee, Owner/Manager, SuperAdmin.

---

## Role-Based Access Control

### Permission Catalog

Permissions are expressed as `resource:action` pairs.

#### User Management / RBAC

- `user:create`
- `user:read`
- `user:update`
- `user:delete`
- `role:read`
- `role:assign`
- `permission:read`

#### Merchant & Branches

- `merchant:read`
- `merchant:update`
- `merchant:deactivate`
- `branch:create`
- `branch:read`
- `branch:update`
- `branch:delete`

#### Products & Menu

- `product:create`
- `product:read`
- `product:update`
- `product:delete`
- `productType:read`
- `productType:create`
- `productType:update`
- `productType:delete`
- `ingredientCategory:create`
- `ingredientCategory:read`
- `ingredientCategory:update`
- `ingredientCategory:delete`
- `ingredient:create`
- `ingredient:read`
- `ingredient:update`
- `ingredient:delete`
- `catalogue:read`
- `catalogue:upsert` (set stock/status per product in catalogue)

#### Orders & Order Items

- `order:create`
- `order:read`
- `order:update`
- `order:cancel`
- `order:close`
- `order:refund`
- `order:receipt`
- `orderItem:add`
- `orderItem:update`
- `orderItem:remove`
- `order:apply-discount` (order‑level discount)

#### Payments, Splits, Tip

- `payment:create` (implied by payment flow)
- `payment:read`
- `payment:refund` (refund a payment, distinct from `order:refund`)
- `paymentSplit:create`
- `paymentSplit:read`
- `tip:add`

#### Discounts & Taxes

- `discount:create`
- `discount:read`
- `discount:update`
- `discount:delete`
- `taxRate:create`
- `taxRate:read`
- `taxRate:update`
- `taxRate:delete`

#### Services & Schedules

- `service:create`
- `service:read`
- `service:update`
- `service:delete`
- `schedule:create`
- `schedule:read`
- `schedule:update`
- `schedule:delete`

#### Reservations, Deposits, Cancellation Fees

- `reservation:create`
- `reservation:read`
- `reservation:update`
- `reservation:cancel`
- `reservationDeposit:create`
- `reservationDeposit:refund`
- `cancellationFee:apply`
- `cancellationFee:waive`

---

### Role Bundles

#### Employee

- Users/RBAC:
  - `role:read`, `permission:read`
- Products & Catalogue:
  - `product:read`, `productType:read`
  - `ingredientCategory:read`, `ingredient:read`
  - `catalogue:read`
- Orders:
  - `order:create`
  - `order:read`
  - `order:update`
  - `orderItem:add`
  - `orderItem:update`
  - `orderItem:remove`
  - `order:cancel` (open orders only)
  - `order:receipt`
- Payments:
  - `payment:create`
  - `payment:read`
  - `paymentSplit:create`
  - `paymentSplit:read`
  - `tip:add`
- Reservations:
  - `reservation:create`
  - `reservation:read`
  - `reservation:update`
  - `reservation:cancel`
- Services/Schedules:
  - `service:read`
  - `schedule:read`

#### Owner / Manager

Includes everything Employee has, plus:

- Users/RBAC:
  - `user:create`, `user:read`, `user:update`, `user:delete`
  - `role:assign`, `role:read`, `permission:read`
- Merchant/Branches:
  - `merchant:read`, `merchant:update`
  - `branch:create`, `branch:read`, `branch:update`, `branch:delete`
- Products:
  - Full CRUD on `product`, `ingredientCategory`, `ingredient`
  - `catalogue:upsert`
- Discounts & Taxes:
  - Full CRUD on discounts (`discount:*`) and tax rates (`taxRate:*`)
  - `order:apply-discount`
- Orders/Payments:
  - `order:close`
  - `order:refund`
  - `payment:refund`
- Services/Schedules:
  - Full CRUD on `service:*`, `schedule:*`
- Reservations Extras:
  - `reservationDeposit:create`
  - `reservationDeposit:refund`
  - `cancellationFee:apply`
  - `cancellationFee:waive`

Guardrail: Owners **cannot** assign the SuperAdmin role.

#### SuperAdmin

- All permissions above, across all merchants (bypassing tenant scope).
- Full administrative control over system configuration and multi‑tenant operations.

---

## High-Level Architecture

At a high level, the system can be implemented as:

- API layer (REST/JSON) implementing the flows and enforcing RBAC.
- Service modules:
  - Orders & Payments
  - Menu & Inventory
  - Reservations & Schedules
  - Discounts & Taxes
  - User & RBAC
  - Logging/Observability
- Shared infrastructure:
  - Relational database for core entities.
  - Message queue/stream for logs and events.
  - Centralized auth & permissions.

Architecture diagrams (e.g. C4 model, service boundaries) can be added here once the implementation is locked in.

---

## Data Model

The data model describes relationships between the main entities.

### Products & Menu

- `taxRate` – `product` (1‑1)
- `productTypes` – `product` (1‑1)
- `product` – `IngredientCategory` (1‑N)
- `IngredientCategory` – `Ingredient` (1‑N)
- `product` – `OrderItem` (1‑1)

### Orders & Payments

- `orders` – `OrderItem` (1‑N)
- `orders` – `payment` (1‑1)
- `orders` – `Discount` (1‑1)
- `orders` – `refund` (1‑1)
- `payment` – `payment_split` (1‑1)
- `payment_split` – `payment_split_items` (1‑N)
- `payment` – `merchant` (1‑1)
- `payment` – `refund` (1‑1)
- `merchant` – `catalogue` (1‑1)
- `catalogue` – `catalogueItem` (1‑N)

### RBAC

- `Role_Permissions` – `Permissions` (1‑N)
- `Roles` – `Role_Permissions` (1‑N)
- `Roles` – `employees` (1‑1)

### Reservations & Services

- `employee` – `schedule` (1‑N)
- `employee` – `cancelation_fee` (1‑1)
- `merchant` – `schedule` (1‑N)
- `merchant` – `reservation` (1‑N)
- `service` – `reservation` (1‑N)
- `service` – `schedule` (1‑1)
- `reservation` – `cancelation_fee` (1‑1)
- `reservation` – `reservation_deposit` (1‑1)
- `merchant` – `service` (1‑N)
- `employee` – `reservation` (1‑N)

### Branches

- `merchant` – `branch` (1‑N)

---

## Technology Assumptions

The spec is technology‑agnostic but is designed to fit common web stacks, for example:

- Backend:
  - Node.js/Express or NestJS
  - Python/FastAPI or Django REST
  - Java/Spring Boot
- Frontend:
  - React, Vue, or Angular SPA using the API.
- Database:
  - PostgreSQL or MySQL for relational data.
- Logging:
  - ELK stack, OpenSearch, or a hosted logging service.
- Auth:
  - JWT‑based auth with RBAC middleware.

You can adapt this section to the actual stack used (frameworks, libraries, DevOps tools).

---

## Getting Started

> This section should be updated once the concrete tech stack and code structure are finalized.

Typical steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/nikwilldoit/Raki_SaaS_System.git
   cd Raki_SaaS_System
   ```
2. Install backend dependencies (e.g., `npm install`, `pip install -r requirements.txt`, or `mvn install`).
3. Configure environment:
   - Database connection string.
   - Auth secrets (JWT).
   - Logging endpoints.
4. Run migrations to create schema.
5. Start services and open the documented API endpoints (e.g., via Swagger/OpenAPI UI).

---

## Contributing

- Use the existing flows and permission catalog as the single source of truth for business logic.
- Keep documentation and OpenAPI specs in sync with implementation.
- Add unit/integration tests for critical flows (order creation, payment, reservation, discount, tax changes).
- Follow coding guidelines and commit conventions defined by the project.

---

## Future Work

- Implement full OpenAPI specification for all modules.
- Add multi‑merchant onboarding and billing.
- Extend analytics and reporting dashboards (sales, reservations, staff performance).
- Support more complex tax and fee structures (service charges, regional rules).
- Add localization and multi‑currency support.
