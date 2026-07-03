# Super Admin

The Super Admin Business Selection feature allows a SuperAdmin user to choose which business they want to work with inside the system. It provides a simple way to switch context so that all dashboard and management features operate on the selected business.

## What this feature does

- Checks that the current user is a SuperAdmin before allowing access.
- Lets the SuperAdmin enter the **exact name** of a business they want to access.
- Attempts to find a business with that name in the system.
- If a matching business is found:
  - Updates the SuperAdmin’s account to be associated with that business.
  - Reloads the user’s dashboard data for the newly selected business.
  - Redirects the user to the main dashboard in the new business context.
- If no matching business is found, shows a clear error message indicating that the business could not be found or an error occurred.

## How business selection works

From the SuperAdmin selection page:

1. The SuperAdmin sees a message explaining that they should enter the exact business name (for example, `"Pasta Palace"`).
2. They type the business name into the input field and submit the form.
3. The system:
   - Verifies that the user is indeed a SuperAdmin.
   - Searches for a business with the given name.
   - If found, assigns that business to the SuperAdmin user account.
   - Requests fresh dashboard information for that business (role, business type, business ID).
   - Updates the stored user data and sends the SuperAdmin to the dashboard.
4. If the business is not found or an error occurs, the page displays an error such as:
   - “Business not found or error selecting business.”

<p align="center">
  <img width="1544" height="281" alt="image" src="https://github.com/user-attachments/assets/fcb14f91-97fe-4408-86a6-2fb60058f131" />
</p>

## How it affects the rest of the app

Once a business is selected:

- The SuperAdmin’s dashboard and all linked features (orders, reservations, menu, staff, taxes, discounts, business management) operate in the context of that business.
- The SuperAdmin can then:
  - View and manage data for the chosen business.
  - Switch to another business later by returning to the selection page and repeating the process.

This makes it possible for a single SuperAdmin account to work across multiple businesses without needing separate logins.

<p align="center">
  <img width="1265" height="743" alt="image" src="https://github.com/user-attachments/assets/63fa0149-62e1-4b7c-ad08-2b4b6a5d24a1" />
</p>

## When this feature is used

- When a SuperAdmin first logs in and needs to attach themselves to a specific business.
- When a SuperAdmin wants to switch from one business to another to perform administrative tasks.
- When managing multiple tenants or branches that share a common SuperAdmin user.

<p align="center">
  <img width="841" height="40" alt="image" src="https://github.com/user-attachments/assets/60733214-8f36-4cae-9bf2-3b14908a445d" />
</p>
