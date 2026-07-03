# Dashboard

The Dashboard is the main home screen of the application that users see after logging in. It provides a clear overview of what they can do in the system and offers quick access to the key features based on their role and the type of business they belong to.

## What this feature does

* Shows a personalized welcome area with the current user's information.
* Detects whether the user has a business assigned and, if not, prevents access to business-specific features.
* Adapts what is visible on the screen depending on:

  * The user's role (Employee, Owner, SuperAdmin).
  * The business type (Restaurant, Café, Bar, Hairdresser, Barbershop, Spa).

## Dashboard adaptation by Business Type

The Dashboard dynamically changes depending on the type of business assigned to the user. Each business type has access only to the features that are relevant to its daily operations.

* **First screenshot:** [Dashboard for Business Type: HAIRDRESSER](#business-type-hairdresser). Since a hairdresser operates through appointments rather than food orders, the Dashboard displays features such as **Reservations** and **Create Reservation**, while food-related features like **Order Management** and **Menu Management** are not shown.

* **Second screenshot:** [Dashboard for Business Type: RESTAURANT](#business-type-restaurant). Restaurant businesses focus on managing customer orders and menus, so the Dashboard includes features such as **Order Management**, **Orders List**, **Menu Management**, **Tax Management**, and other restaurant-related management tools.

This behavior ensures that every business sees only the functionality that matches its business model, resulting in a cleaner interface and a more efficient workflow.

---

## Main actions available from the Dashboard

Depending on the user's role and business type, the Dashboard can show action cards for:

* **Order Management**
  Access to creating and handling customer orders for food and drinks.

* **Reservations**
  Access to viewing and managing reservations for services (e.g., haircuts, spa sessions).

* **Create Reservation**
  Quick entry point to create a new reservation directly from the home screen.

* **Menu Management**
  Tools to manage products and categories in the menu for food businesses.

* **Staff Management**
  Access to managing employees and their presence in the system.

* **Tax Management**
  Entry point for managing tax rules and rates used by the business.

* **Orders List**
  Overview of existing orders for monitoring and review.

* **Discount Management**
  Access to creating and managing discounts applied to orders or products.

* **Business Management**
  Access to viewing and updating the main details of the business profile.

## When this feature is used

* Right after login, as the starting point for the user's daily work.
* When staff need to quickly move between managing orders, reservations, menu, staff, taxes, discounts, or business details.
* When owners or SuperAdmins want a central place to navigate all management features in one screen.

## Who sees what

* **Employees**
  Typically see only operational features relevant to their work, such as orders and reservations.

* **Owners / Managers**
  See additional management features such as menu, staff, discounts, and business information.

* **SuperAdmins**
  Have the widest view, including business selection and high-level management options.

## Business Type: HAIRDRESSER {#business-type-hairdresser}

<p align="center">
  <img width="1198" height="563" alt="Dashboard - Hairdresser" src="https://github.com/user-attachments/assets/e440e18c-de29-4334-b0e8-17197bba53cf" />
</p>

---

## Business Type: RESTAURANT {#business-type-restaurant}

<p align="center">
  <img width="1206" height="516" alt="Dashboard - Restaurant" src="https://github.com/user-attachments/assets/67ae2975-1b6d-4f9a-8b46-2aa24aa539e9" />
</p>

