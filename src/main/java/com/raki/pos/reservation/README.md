<img width="1212" height="493" alt="image" src="https://github.com/user-attachments/assets/31f06035-4d73-41f7-abfe-1e403e563786" /># Reservations

The Reservations feature allows a business to book, review, and adjust appointments for its services. It covers the full flow from selecting a service and employee, through picking a date and time, to confirming, listing, editing, and canceling reservations.

## What this feature does

- Provides a **Reservation creation page** where staff can:
  - Enter customer information (name, phone, optionally email).
  - Choose a service from the list of available services.
  - Choose a specific employee or let the system assign “Any available”.
  - Select a date on a calendar that respects business availability.
  - Select a time slot from the available times for that date.
  - Review a summary of the appointment before confirming.
  - Submit the reservation and receive a clear success or error message.

  <p align="center">
  <img width="1212" height="493" alt="image" src="https://github.com/user-attachments/assets/a6396cf3-6f6e-41f4-bafe-81791585368a" />
</p>

- Offers a **Reservations list page** that:
  - Shows all reservations for the current business.
  - Displays for each reservation:
    - Customer name and contact phone.
    - Date and time of the appointment.
    - Service name.
    - Employee name.
    - Status (e.g., PENDING, CONFIRMED, CANCELED).
  - Clearly indicates when no reservations exist.

- Includes a **Reservation edit dialog** that:
  - Opens for a selected reservation.
  - Allows updating customer name, customer phone, and status.
  - Saves changes back to the system.
  - Closes and refreshes the list after a successful update.

- Supports **reservation cancellation**:
  - Lets staff delete a reservation.
  - Frees up the associated time slot so it becomes available again.
  - Maintains a clean schedule and avoids conflicting bookings.

<p align="center">
  <img width="894" height="905" alt="image" src="https://github.com/user-attachments/assets/8414478b-c750-4a60-8cc3-59199262e461" />
</p>

## How availability works

- When creating a reservation:
  - The system loads services and employees for the current business.
  - The user selects a service and an employee (or “Any available”).
  - The user selects a date using the calendar.
  - The system requests available time slots for that business, and if needed, for the chosen employee.
  - Only valid, future dates with available slots are selectable.
  - Only times that are free (not booked and marked as available) appear as options.

This ensures that reservations are always created for real, available slots, and helps avoid double‑booking.

## How reservations are created

1. Staff opens the reservation creation screen.
2. They fill in customer details.
3. They choose a service and an employee (or let the system find any available staff).
4. They pick a date and then a specific time from the available slots.
5. The page shows a brief summary (service, date & time, employee, status).
6. Staff confirms and submits the reservation.
7. The system:
   - Creates a reservation record with the chosen service, customer, and employee.
   - Links the reservation to a specific time slot.
   - Marks that time slot as unavailable so it cannot be double‑booked.

If a slot is not available, the system refuses the booking and reports that the slot is unavailable.

## How reservations appear in the list

In the reservations list:

- Each reservation row shows:
  - Customer name and phone.
  - Date and time of the appointment.
  - Service name.
  - Employee name.
  - Current status.
- Staff can:
  - Open an edit window to change the customer details or status.
  - Cancel a reservation when necessary.

If the reservation is deleted:

- The associated time slot is freed so it can be used for new bookings.
- The reservation is removed from the list.

<p align="center">
  <img width="394" height="383" alt="image" src="https://github.com/user-attachments/assets/e64900d6-baa5-444d-909b-078e121407fc" />
</p>

## When this feature is used

- When customers call or visit to book appointments (e.g., haircuts, spa sessions).
- When staff need to see upcoming and past reservations at a glance.
- When appointment details change (customer phone, status) and must be updated.
- When reservations are canceled and the schedule must be kept clean and accurate.

## Who typically uses it

- Employees who manage bookings for customers day‑to‑day.
- Owners and managers who monitor the schedule and adjust reservations.
- Not used directly by customers, but it represents their booked appointments in the system.
