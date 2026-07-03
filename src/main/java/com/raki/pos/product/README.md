<img width="415" height="596" alt="image" src="https://github.com/user-attachments/assets/77df0d3d-8ee5-4e44-91b5-3ffa2640d206" /># Products

The Products feature allows a business to define, organize, and maintain the items it sells, such as food, drinks, or services. It provides a central place to create, edit, list, and remove products and to attach tax categories, subcategories, and ingredient options.

## What this feature does

- Shows a list of all products for the current business, including:
  - Product name
  - Subcategory (e.g., menu category)
  - Description
  - Price
  - Status (Active / Inactive)
  - Creation and last update dates
- Allows adding new products through a dedicated modal.
- Allows editing existing products to update their details and options.
- Allows deleting products that are no longer needed.
- Lets users filter and sort products by:
  - Name (search text)
  - Category
  - Status (Active/Inactive)
  - Name order (A–Z / Z–A).

<p align="center">
  <img width="1161" height="537" alt="image" src="https://github.com/user-attachments/assets/f11c336b-6672-4da9-bb51-c78b26c7675a" />
</p>

## How products are defined

When creating or editing a product, the user can specify:

- **Basic details**
  - Product name.
  - Description (optional).
  - Price, with validation that it is non‑negative.
  - Status (Active or Inactive).

- **Tax category**
  - Selects a tax policy to be applied to the product (e.g., VAT type).

- **Subcategory**
  - Selects a product type (e.g., menu section or service type) to help organize products.

- **Ingredient category and ingredients**
  - Chooses an ingredient category (e.g., toppings, add‑ons).
  - Selects one or more ingredients from that category that can be used when building orders.
  - Sees all selected ingredients grouped by category for clarity.

<p align="center">
  <img width="415" height="596" alt="image" src="https://github.com/user-attachments/assets/a137626b-640f-480b-9958-82a937bfe53d" />
</p>

Once saved, the product appears in the list and becomes available to order and pricing flows with the chosen tax and discount rules.

## How prices and adjustments are handled

- Each product has a base price.
- The system can factor in:
  - The selected tax policy to determine the taxed price.
  - Any discount policy that applies to the product.
- When products are fetched for use in other screens (like order creation), they are provided with their effective price after tax and discount, so staff always see the correct amount.

## How products appear and are managed

In the products management page:

- The product table displays key information and provides action buttons for:
  - **Edit** – open a modal to change product details and ingredient options.
    <p align="center">
      <img width="416" height="554" alt="image" src="https://github.com/user-attachments/assets/a67e0d16-15fc-49e3-a071-a71991de7a4c" />
    </p>
  - **Delete** – open a confirmation dialog to remove the product.
    <p align="center">
      <img width="355" height="188" alt="image" src="https://github.com/user-attachments/assets/e0872c37-7bfd-449f-ad8c-f6e1788fdc40" />
    </p>

If there are no products yet, the page clearly informs the user that no products are found.

The filter bar lets users:

- Search by product name.
- Restrict to a specific category or see all categories.
- Limit the list to Active or Inactive products.
- Reset all filters and sorting with a single “Clear Filters” action.

## When this feature is used

- When the business menu or service catalogue is being set up for the first time.
- When new items are added to the offering.
- When existing products need changes in price, description, tax category, or availability.
- When old products should be removed to keep the menu clean.

## Who typically uses it

- Owners and managers who define the menu or service list and pricing.
- Staff who may occasionally update product information under supervision.
