# Payments

The Payments feature handles how customers pay for their orders in the system. It supports both simple “pay everything at once” scenarios and more advanced “split” payments where multiple people share the bill and add their own tips.

## What this feature does

- Calculates how much needs to be paid for an order, taking into account:
  - The total value of the items.
  - Any discounts applied.
  - Any tips added.

- Allows customers to pay:
  - The full amount in a single payment.
  - Only part of the amount, with support for split payments.

- Records each payment with:
  - The total amount paid.
  - The total tip amount.
  - The chosen payment method (cash, card, gift card).
  - Information about who paid and for which items.

- Tracks how much of the order has already been paid and how much is left to pay.
- Once the order is fully paid:
  - Marks the order as closed.
  - Marks all order items as paid.
  - Marks all related payments and splits as completed.

## How full payments work

From the payment summary screen:

1. The system shows:
   - The bill total (after discounts).
   - An input for tip.
   - The amount “left to pay”, including tip.
2. The customer pays the full amount in one go.
3. The system:
   - Records the payment.
   - Checks if the total paid covers the order.
   - If everything is paid:
     - Closes the order.
     - Marks all items as paid.
     - Marks all payments as completed.
     - Returns the user to the main dashboard.
   - If there is still something left to pay, the user can continue to pay later through the payment screens.

## How split payments work

From the split payment screen:

1. The system shows two lists:
   - Unpaid items in the order.
   - Items selected to be paid in the current split.
2. The payer:
   - Chooses which items they want to pay for by moving items from the “Unpaid” list to the “Paid” list.
   - Enters their name.
   - Chooses a payment method (cash, card, gift card).
   - Adds an optional tip.
3. The system:
   - Calculates the amount to pay for this person (selected items plus tip).
   - Records a split payment with:
     - The payer’s name.
     - The amount they paid.
     - The tip they added.
     - The payment method.
     - The items covered by this split.
   - Marks only those items as paid.
   - Checks if the overall order is now fully paid:
     - If yes, closes the order and marks all payments and splits as completed.
     - If not, returns to the payment overview showing what still remains to be paid.

## How payment history is shown

In the payment overview:

- The system shows:
  - The total bill amount.
  - A history of payments already made for the order.
- For each payment, it displays:
  - A payment number.
  - Who paid.
  - A short summary of products or splits covered.
  - The amount paid and the tip.

It also shows how much is still left to pay, so staff and customers can see at a glance whether the order is fully settled.

## When this feature is used

- Whenever a customer is ready to pay for an order.
- When a group of people wants to split the bill so each person pays their share.
- When staff need to check how much has already been paid and how much remains.
- When managers want to ensure that orders are properly closed only after full payment.

## Who typically uses it

- Staff members who process payments at the point of sale.
- Customers indirectly, through staff entering their chosen split and payment method.
- Owners and managers, by reviewing payment summaries and ensuring that orders are correctly paid and closed.

<p align="center">
  <img width="1874" height="440" alt="image" src="https://github.com/user-attachments/assets/bd6bc632-a32b-4552-9b8d-8b20738ee81f" />
</p>

<p align="center">
  <img width="1873" height="445" alt="image" src="https://github.com/user-attachments/assets/6845cb8a-136c-49b7-a346-f8d06bf14fde" />
</p>

<p align="center">
  <img width="1879" height="389" alt="image" src="https://github.com/user-attachments/assets/c267b5d3-2445-49b2-bbee-e56a951f0713" />
</p>
