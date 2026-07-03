# Staff Management

The Staff Management feature allows a business to view and maintain the employees who work for it. It provides a clear overview of staff members and a simple way to update their details, roles and statuses.

## What this feature does

- Shows a list of staff members for the current business.
- For each employee, displays:
  - Name
  - Email
  - Phone
  - Role (e.g., Owner, Employee)
  - Status (e.g., ACTIVE, SUSPENDED, FIRED)
- Allows editing an employee’s information through a dedicated form.
- Ensures that only permitted users (such as Owners and SuperAdmins) can view and update staff for the business.
- Applies basic safety rules, for example:
  - Owners cannot edit other Owners.

<p align="center">
  <img width="396" height="512" alt="image" src="https://github.com/user-attachments/assets/6039c4a9-f8a5-4d8e-a298-63c546e9ae0b" />
</p>

## How staff information is managed

From the staff management screen:

1. The user sees all employees for their business, excluding SuperAdmins.
2. For each staff member, they can open an **Edit Employee** dialog.
3. In the edit form, they can update:
   - Name
   - Email
   - Phone number
   - Role (Owner or Employee)
   - Status (ACTIVE, SUSPENDED, FIRED)
   - New password (optional; can be left empty to keep the current one)
4. After saving, the staff list refreshes to reflect the changes.
5. If something goes wrong, a clear error message is shown.

If there are no employees for the business, the page clearly informs the user that no staff have been found.

<p align="center">
  <img width="1173" height="424" alt="image" src="https://github.com/user-attachments/assets/1461e7d9-eeed-45e4-ad27-01a9f86db98c" />
</p>

## How access is controlled

- **SuperAdmins** can see both owners and employees for the current business.
- **Owners** can see and edit employees but are prevented from editing other Owners.
- Users without sufficient rights are blocked from accessing staff management and receive a “not allowed” response.

## When this feature is used

- When a new employee joins and their account needs to be updated or corrected.
- When an employee’s role changes (e.g., promotion to Owner or change to Employee).
- When an employee’s status changes (e.g., suspension or termination).
- When contact details (email, phone) need to stay up to date for communication and security.

## Who typically uses it

- Business owners who manage their team.
- SuperAdmins who need oversight and control across businesses.

<p align="center">
  <img width="907" height="173" alt="image" src="https://github.com/user-attachments/assets/dcee4364-b0d5-4fdc-be55-2bf6f6f9ee9b" />
</p>
