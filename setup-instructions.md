# Project Setup Instructions (Spring Boot + React)

Follow these steps to get the project running and the database properly set up in IntelliJ.

---

## 1. Clone the Project

Clone the repository and open it in IntelliJ IDEA.

---

## 2. Create the Database Locally

1. Open **MySQL Workbench** (or any MySQL client).
2. Create a new empty database named **pos**.
```sql
   CREATE DATABASE pos;
```

---

## 3. Create the Database User

1. Log into MySQL as root with the following command:
```bash
   mysql -u root -p
```
2. Create the user:
```sql
   CREATE USER 'pos_db'@'localhost' IDENTIFIED BY 'root';
```
3. Grant privileges:
```sql
   GRANT ALL PRIVILEGES ON pos.* TO 'pos_db'@'localhost';
```
4. Apply changes:
```sql
   FLUSH PRIVILEGES;
```
5. Exit:
```sql
   EXIT;
```

---

## 4. Run the Database Schema

1. Locate the SQL files in the project:
```
   src/main/resources/db/
```
   These files include:
   - `Roles Table Creation.sql`
   - `Businesses Table Create.sql`
   - `Users Table Creation.sql`
   - `Permissions Table Creation.sql`
   - `Role_Permissions Table Creation.sql`

2. Run these SQL files **in order** in MySQL Workbench or your MySQL client to create all tables and insert initial data.

   **Important:** Run them in the correct order to avoid foreign key constraint errors:
   1. Roles
   2. Businesses
   3. Permissions
   4. Users
   5. Role_Permissions

---

## 5. Add the Database to IntelliJ

1. Open **View → Tool Windows → Database** in IntelliJ.
   *(Enable it if the panel is not visible.)*

2. Click **"+" → Data Source → MySQL**.

3. Enter your database details:
```text
   Host: localhost
   Port: 3306
   User: pos_db
   Password: root
   Database: pos
```

4. Download the MySQL driver if prompted.

5. Click **Test Connection** — it should show **Successful**.

6. Click **Apply** or **OK** to save the connection.
   IntelliJ will now show your database and tables.

---

## 6. Run the Spring Boot Backend

1. Open a terminal in IntelliJ.
2. Run:
```bash
   ./gradlew bootRun
```

The backend will start at `http://localhost:8080`.

---

## 7. Run the React Frontend

1. Navigate to the frontend directory:
```bash
   cd frontend
```

2. Install dependencies (first time only):
```bash
   npm install
   npm install react-router-dom
   npm install axios
```

3. Start the React development server:
```bash
   npm start
```

React will run at `http://localhost:3000` and communicate with Spring Boot at `http://localhost:8080`.

**Note:** You may need to install additional packages. If you encounter errors, let the team know.

---