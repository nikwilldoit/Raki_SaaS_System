# Menu Management

The Menu Management feature allows a business to structure the items it offers by defining products and the ingredient categories behind them. It provides tools to organize the menu, configure ingredient options, and keep the menu clean and up to date.

## What this feature does

- Provides a **Menu dashboard** with quick access to:
  - [**Products**](https://github.com/nikwilldoit/Raki_SaaS_System/blob/main/src/main/java/com/raki/pos/product/README.md) – for adding, editing, and deleting menu items.
  - **Ingredient Categories** – for defining groups of ingredients (e.g., toppings, add‑ons).
- Adapts the description based on business type:
  - Food businesses (restaurants, cafés, bars) manage products and ingredient categories for their menu.
  - Service businesses manage services and their configurable options.

<p align="center">
  <img width="1161" height="285" alt="image" src="https://github.com/user-attachments/assets/d2eecb97-689b-4947-b883-fc3dabd04030" />
</p>

## Ingredient Categories

The ingredient categories part of the menu management:

- Shows a list of ingredient categories for the business, including:
  - Category name.
  - Description.
  - Number of ingredients assigned to each category.
- Allows creating new ingredient categories by specifying:
  - Name.
  - Description.
  - One or more ingredients, each with:
    - Name.
    - Price (optionally, for price adjustments).
- Allows editing existing categories:
  - Update the category name and description.
  - Add new ingredients.
  - Modify the names and prices of existing ingredients.
  - Remove ingredients that are no longer needed.
- Allows deleting categories:
  - Removes the category and its ingredients from the system after confirmation.

The list can be filtered and sorted by category name, and users can reset filters easily.

<p align="center">
  <img width="1161" height="591" alt="image" src="https://github.com/user-attachments/assets/9e54d8c1-de13-46af-8ce5-c2b5b5c0c6e8" />
</p>

## Product ingredient options

The menu management also supports **ingredient options per product**:

- For each product, the system can attach ingredient categories and specific ingredients that can be selected when building orders.
- For a given product, ingredient categories are fetched with:
  - Category name.
  - Whether the category is **single‑select** (e.g., size, one choice) or multi‑select (extras).
  - A list of ingredient options with names and price adjustments.
- These ingredient options are then used in the order flow to let staff customize each item (sizes, extras, toppings) while reflecting correct pricing.

<p align="center">
  <img width="417" height="457" alt="image" src="https://github.com/user-attachments/assets/333aa184-82ec-49c4-94d5-318de0195328" />
</p>

## How this fits into the menu

Together, products and ingredient categories define what appears in the customer‑facing menu:

- Products represent the main items (e.g., dishes, drinks, services).
- Ingredient categories and ingredients represent the configurable parts of those items (e.g., sugar levels, toppings, add‑on services).
- By keeping categories and ingredients structured, the business can:
  - Offer clear and flexible customization.
  - Maintain consistent pricing rules.
  - Make future changes to the menu quickly and safely.

## When this feature is used

- When setting up the menu or service catalogue for the first time.
- When new products or options are introduced (e.g., seasonal ingredients).
- When existing categories and ingredient lists need to be reorganized or cleaned up.
- When prices for extras or options need to be updated.

## Who typically uses it

- Owners and managers who design the menu and decide how products can be customized.
- Staff with appropriate permissions who maintain ingredient categories and options over time.
