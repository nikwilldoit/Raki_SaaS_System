# Business Management

The Business Management feature allows authorized users to view and update the core profile of the business they are currently associated with. It provides a simple form to edit basic information such as name, address, phone, and open/closed status, backed by secure REST APIs.

## What this module does

- Loads the current user's business profile from the backend.
- Displays a form pre-filled with the business data.
- Allows updating:
  - Business name
  - Business address
  - Phone number
  - Status (OPEN / CLOSED)
- Sends changes to the backend via a secure `PUT` request.
- Shows success and error messages for load and save operations.

## Technologies used

- **Frontend**
  - React functional components
  - React Hooks (`useState`, `useEffect`)
  - Fetch API for HTTP requests
  - CSS module (`ManageBusiness.css`) for styling
- **Backend**
  - Java, Spring Boot
  - Spring Web (REST controllers)
  - Spring Security (authenticated user with JWT / Bearer token)
  - JDBC / `JdbcTemplate` for database access
  - DTO & Repository & Service layers

## Frontend flow

The `ManageBusiness` component:

- Reads the authentication token from:
  - `userData.token` if available, or
  - `localStorage.getItem('authToken')` as a fallback.
- On mount (`useEffect`), calls:
  - `GET http://localhost:8080/api/businesses/me`  
    with `Authorization: Bearer <token>`
- Maps the backend response (`id`, `name`, `address`, `type`, `phone`, `isActive`) into local form state.
- Renders a form with:
  - Business Name (text input)
  - Business Address (text input)
  - Phone (text input)
  - Status (select: OPEN / CLOSED)
- On submit:
  - Sends `PUT http://localhost:8080/api/businesses/me`  
    with JSON body containing the form fields and `Authorization` header.
  - Updates the local form state with the returned JSON.
  - Shows `"Business saved successfully"` on success.
  - Shows error messages if the API responds with non‑OK status.

If there is no user or no token, the component shows an error message and does not attempt to call the API.

## Backend behavior (expected)

The backend endpoints are exposed under:

- `GET /api/businesses/me`  
  Returns the business linked to the currently authenticated user (based on the `users` table and `business_id` field).

- `PUT /api/businesses/me`  
  Updates the fields of the current user’s business:
  - `name`
  - `address`
  - `phone`
  - `isActive`

Internally, the backend:

- Resolves the current user from Spring Security’s `SecurityContext`.
- Finds the corresponding business via the `users` table (`email` → `business_id`).
- Uses `BusinessRepository` (`JdbcTemplate`) to:
  - Read the `Business` entity from the `businesses` table.
  - Apply incoming changes from the `BusinessDTO`.
  - Persist updates with an `UPDATE` statement.

## Data model (business profile)

The business profile handled by this module includes:

- `id`: Unique business identifier.
- `name`: Display name of the business.
- `address`: Physical address.
- `type`: Business category (e.g., RESTAURANT, CAFE, BAR, HAIRDRESSER, BARBERSHOP, SPA).
- `phone`: Contact phone number.
- `isActive`: Operational status, used by the UI as `OPEN` / `CLOSED`.

## How it fits into the app

- This page is typically available only to **Owners** and **SuperAdmins**, via the “Business Management” card on the main Dashboard.
- It provides a central place where authorized users keep the business metadata up to date, which is then reused by other modules (orders, reservations, menu, etc.).
