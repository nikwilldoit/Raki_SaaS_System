# Dashboard

The Dashboard is the main landing page that users see after logging in. It provides a clear overview of the available functionality and offers quick access to the features that are relevant to the user's role and business type.

## What this feature does

* Displays a personalized welcome area with the current user's information.
* Detects whether the user is assigned to a business and, if not, prevents access to business-specific features.
* Dynamically adapts the available options based on:

  * The user's role (Employee, Owner, SuperAdmin).
  * The business type (Restaurant, Café, Bar, Hairdresser, Barbershop, Spa).

## Dashboard adaptation by Business Type

The Dashboard dynamically changes depending on the type of business assigned to the user. Each business type only sees the features that are relevant to its daily operations.

* **First screenshot:** [Dashboard for Business Type: HAIRDRESSER](#business-type-hairdresser). Since hairdressers primarily operate through appointments rather than food orders, the Dashboard displays features such as **Reservations** and **Create Reservation**, while food-related features like **Order Management**, **Orders List**, and **Menu Management** are not available.

* **Second screenshot:** [Dashboard for Business Type: RESTAURANT](#business-type-restaurant). Restaurants focus on customer orders and menu management, so the Dashboard includes features such as **Order Management**, **Orders List**, **Menu Management**, **Tax Management**, and other restaurant-related management tools.

This dynamic behavior ensures that each business only sees the functionality that matches its business model, resulting in a cleaner interface and a more efficient workflow.

---

## Main actions available from the Dashboard

Depending on the user's role and business type, the Dashboard may display the following action cards:

* **Order Management**
  Create and manage customer food and beverage orders.

* **Reservations**
  View and manage reservations for appointment-based businesses (e.g., hairdressers and spas).

* **Create Reservation**
  Quickly create a new reservation directly from the Dashboard.

* **Menu Management**
  Manage products and menu categories for food-service businesses.

* **Staff Management**
  Manage employees and their access to the system.

* **Tax Management**
  Configure and maintain the tax rules used by the business.

* **Orders List**
  View and monitor existing customer orders.

* **Discount Management**
  Create and manage discounts applied to products or orders.

* **Business Management**
  View and update the business profile and settings.

## When this feature is used

* Immediately after login as the starting point for the user's daily work.
* When staff need quick access to operational features such as orders or reservations.
* When Owners or SuperAdmins need to manage menus, staff, taxes, discounts, or business information.

## Who sees what

* **Employees**
  Typically see only the operational features required for their daily tasks, such as orders and reservations.

* **Owners / Managers**
  See additional management features, including menu, staff, discounts, taxes, and business information.

* **SuperAdmins**
  Have the broadest access, including business selection and system-wide management features.

---

## Business Type: RESTAURANT

<p align="center">
  <img width="1198" height="563" alt="Dashboard - Hairdresser" src="https://github.com/user-attachments/assets/e440e18c-de29-4334-b0e8-17197bba53cf" />
</p>

---

## Business Type: HAIRDRESSER

<p align="center">
  <img width="1206" height="516" alt="Dashboard - Restaurant" src="https://github.com/user-attachments/assets/67ae2975-1b6d-4f9a-8b46-2aa24aa539e9" />
</p>
