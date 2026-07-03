# Authentication & Security

The Authentication feature handles how users log in to the system and how their identity is carried securely across all backend requests. It combines a simple login endpoint with JSON Web Tokens (JWT) and a security filter to protect the rest of the API.

## What this feature does

- Provides a **login endpoint** that:
  - Accepts an email and password from the frontend.
  - Validates that both fields are present and the email is well‑formed.
  - Checks the database for an active user with matching credentials.
  - If successful:
    - Returns the user’s display name.
    - Returns a signed JWT token that the frontend can store and reuse.
  - If credentials are wrong, returns an appropriate error.

- Exposes simple **health‑check endpoints**:
  - `/` – indicates that the POS backend is running.
  - `/api/test` – confirms that the backend is reachable.

- Uses a **JWT utility** to:
  - Generate tokens that include the user’s email and an expiration time.
  - Validate tokens on subsequent requests.
  - Extract the email (subject) from a token safely.

- Includes a **security filter (JwtAuthenticationFilter)** that:
  - Intercepts every HTTP request.
  - Looks for an `Authorization: Bearer <token>` header.
  - Verifies the token and extracts the user’s email.
  - If valid, marks the user as authenticated in the security context for that request.
  - Lets unauthenticated requests continue, so other security rules decide whether access is allowed.

## How login works

1. The user enters their email and password on the frontend login form.
2. The frontend sends these credentials to `POST /api/login`.
3. The backend:
   - Validates the input (required email, required password, valid email format).
   - Looks up an active user with the matching email and password in the database.
   - If found:
     - Generates a JWT token tied to that email with a fixed validity period.
     - Returns a response containing:
       - The user’s name.
       - The token.
   - If not found or an error occurs, it throws an authentication error and returns an appropriate HTTP error.

The frontend then stores this token (for example in `localStorage`) and reuses it on all subsequent API calls.

## How protected requests are handled

For any secured endpoint:

- The frontend includes the JWT token in the `Authorization` header:
  - `Authorization: Bearer <token>`.
- The JWT filter:
  - Reads the header.
  - Extracts and validates the token:
    - Confirms that the token belongs to the same email.
    - Confirms that the token has not expired.
  - If valid, sets an authenticated user into the Spring Security context.
- Controllers and services can then:
  - Use the authenticated user’s email to load user/business information.
  - Apply role‑based checks and business‑level rules.

If the token is invalid or missing, the filter does not authenticate the user, and security configuration decides whether that endpoint is accessible or should respond with an error.

## Health checks and connectivity

Two simple endpoints support health and connectivity checks:

- `/` – returns a short message confirming that the POS backend is running and ready to work with the React frontend.
- `/api/test` – returns a simple success message to confirm that the API is reachable.

These are useful during development, deployment, and debugging to verify that the server and basic routing function correctly.

## When this feature is used

- Every time a user logs into the app.
- On every API request that requires authentication (orders, reservations, menu, staff, admin actions, etc.).
- During environment checks to confirm the backend is up and connected.

## Who typically uses it

- All application users (Owners, Employees, SuperAdmins) when signing in.
- The frontend, automatically, whenever it attaches tokens to secure requests.
- DevOps and developers, when verifying that the backend and security pipeline are healthy.
