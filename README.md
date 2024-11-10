
# Task Management System (TMS)

## Overview

A Spring Boot Technical Exercise Task, Simply for managing tasks with user authentication, authorization, task CRUD, and filtering capabilities.

## Features

- **Task CRUD**: Create, Read, Update, Delete tasks with attributes like title, description, status, priority, and due date.
- **Authentication**: JWT-based login and signup.
- **Role-Based Access**: Permissions based on user roles (e.g., admin-only delete).
- **Search**: Filter tasks by title, description, status, priority, and due date.

## Prerequisites

### Clone the Repository
```bash
git clone https://github.com/your-repo/task-management-system.git
cd task-management-system
```

- **Java 11+**
- **MySQL 5.7+**
- **Maven**

## Database Setup

Run the following script in MySQL to set up the database:

```sql
-- Create the MySQL user
CREATE USER 'tms_admin' IDENTIFIED BY 'P@ssw0rd';

-- Create the schema
CREATE SCHEMA tms;

-- Grant privileges
GRANT ALL PRIVILEGES ON tms.* TO 'tms_admin'@'%';
```

## Running the Application

```bash
mvn spring-boot:run
```

## API Endpoints

### Authentication Endpoints

- **POST** `/v1/auth/login`
   - **Description**: Authenticate a user with email and password to obtain a JWT token.

- **POST** `/v1/auth/refresh-token`
   - **Description**: Generate a new access token using a valid refresh token.

- **POST** `/v1/auth/signup`
   - **Description**: Register a new user with default role `USER`.

- **POST** `/v1/auth/logout`
   - **Description**: Logs out the authenticated user, invalidating the session.

- **POST** `/v1/auth/change-password`
   - **Description**: Allows an authenticated user to change their password.

### Task Management Endpoints

- **POST** `/v1/tasks`
   - **Description**: Create a new task.

- **GET** `/v1/tasks/{id}`
   - **Description**: Retrieve task details by ID.

- **GET** `/v1/tasks/user`
   - **Description**: Retrieve all tasks associated with the authenticated user.

- **GET** `/v1/tasks`
   - **Description**: Retrieve all tasks (Admin only).

- **PUT** `/v1/tasks/{id}`
   - **Description**: Update an existing task by ID.

- **DELETE** `/v1/tasks/{id}`
   - **Description**: Delete a task by ID (Admin only).

- **GET** `/v1/tasks/search`
   - **Description**: Search tasks based on criteria like title, description, status, priority, and due date (Admin only).

### User Management Endpoints (Admin Only)

- **POST** `/v1/users`
   - **Description**: Create a new user with specific role and credentials.

- **GET** `/v1/users`
   - **Description**: Retrieve a list of all users.

- **GET** `/v1/users/{id}`
   - **Description**: Retrieve a specific user's details by ID.

- **PUT** `/v1/users/{id}`
   - **Description**: Update details of a user by ID.

- **DELETE** `/v1/users/{id}`
   - **Description**: Delete a user by ID.

---

## Testing

Run unit tests:

```bash
mvn test
```

---
