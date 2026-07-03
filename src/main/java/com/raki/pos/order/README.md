# Orders

The Orders feature allows staff to create, review, and manage customer orders for a business. It covers the full lifecycle from selecting products and options, through calculating totals and discounts, all the way to payment and refunds.

## What this feature does

- Lets staff create a new order by:
  - Browsing available products.
  - Choosing size and extra options for each product.
  - Adding one or more items to the order with quantities.
  - Adding notes and special requests (e.g. allergies, table number).
  - Optionally applying a percentage discount to the whole order.

- Calculates prices for the order:
  - Base price per product.
  - Extra charges for selected ingredients or options.
  - Order‑level discount amount.
  - Subtotal and final total for the order.

<p align="center">
  <img width="1166" height="578" alt="image" src="https://github.com/user-attachments/assets/663b0b01-3504-4a3d-af09-25ab8649f65d" />
</p>

<p align="center">
  <img width="515" height="648" alt="image" src="https://github.com/user-attachments/assets/a21b95bf-18ce-4605-a3c8-6254875a4d2e" />
</p>

- Connects orders with [payments](src/main/java/com/raki/pos/payment/README.md):
  - From the order list or order creation screen, staff can proceed to a payment screen for that order.
  - After payment, order status moves from OPEN to CLOSED.
  - Orders that are closed and have payments can be refunded when necessary.

<p align="center">
  <img width="1877" height="444" alt="image" src="https://github.com/user-attachments/assets/64823ef4-c1f4-4999-9194-8ea5eb8b259f" />
</p>

- Sends the completed order to the system, where it is stored with:
  - Business and user information.
  - Order number.
  - Order items and their prices.
  - Status (e.g. OPEN, CLOSED, REFUNDED).

- Provides a list of existing orders for the business:
  - Shows order ID, number, status, and date.
  - Allows staff to quickly see which orders are open, paid, or refunded.

- Supports refunds for eligible orders:
  - Checks that the order has payments.
  - Creates a refund entry with a reason.
  - Marks the order as REFUNDED when appropriate.

<p align="center">
  <img width="1176" height="538" alt="image" src="https://github.com/user-attachments/assets/7ed67141-83c2-4864-8e4c-ce4ae165f20a" />
</p>

## How a typical order is created

1. Staff opens the “New order” screen.
2. They select products from the product list and, if needed, customize them:
   - Choose size (e.g. small/large).
   - Add extras (ingredients or options).
   - Add per‑item notes.
3. Items are added to the order summary with quantity and item total.
4. Staff can adjust quantities, remove items, or add more items.
5. Optional overall notes and a percentage discount can be added to the order.
6. The system shows subtotal, discount amount, and final total.
7. Staff either:
   - Submits the order and keeps it open, or
   - Proceeds directly to payment for immediate checkout.

## When this feature is used

- Whenever a customer places an order for products or services.
- When staff need to review recent orders for monitoring or follow‑up.
- When a payment needs to be taken or a refund needs to be processed.
- When managers want visibility into operational activity through the order list.

## Who typically uses it

- Employees who take orders and manage customer interactions.
- Owners and managers who review orders and handle refunds.
