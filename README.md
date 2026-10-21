# FixCore Backend

FixCore is a maintenance management platform designed to help industrial organizations manage assets, work orders, spare parts, and maintenance operations.

This repository contains the backend application developed with Java, Spring Boot, PostgreSQL, Domain-Driven Design (DDD), and Clean Architecture.

## 1. Technologies

- Java 21
- Spring Boot 4.0.3
- Maven
- PostgreSQL 17
- Spring Data JPA
- Hibernate
- Flyway
- Jakarta Bean Validation
- JUnit 5
- Spring MVC Test

## 2. Project Architecture

The backend follows a modular monolith architecture based on Domain-Driven Design (DDD) and Clean Architecture.

The application is organized into five bounded contexts:

| Bounded Context | Package | Responsibility |
|---|---|---|
| Identity & Access Management | `iam` | User identity and access management |
| Resource & Asset Management | `assetmanagement` | Management of industrial assets |
| Service Design & Planning | `serviceplanning` | Maintenance planning |
| Service Execution & Monitoring | `serviceexecution` | Execution and monitoring of maintenance work |
| Inventory & Spare Parts Management | `inventory` | Spare parts and inventory management |

Each bounded context is divided into four layers:

- **Domain:** Entities, value objects, business rules, and repository interfaces.
- **Application:** Use cases and application services.
- **Infrastructure:** Persistence implementations and external integrations.
- **Interfaces:** REST controllers and request/response DTOs.

The `shared` package contains reusable technical components.

Additional architectural decisions are documented in `ARCHITECTURE.md`.

## 3. Sprint 3 Scope

The initial backend implementation focuses on three functional areas:

- Resource & Asset Management: plants and machines.
- Service Execution & Monitoring: work orders.
- Inventory & Spare Parts Management: spare parts.

Identity & Access Management and Service Design & Planning are included in the architectural structure but are not part of the initial functional implementation.

## 4. Requirements

Before running the backend, install:

- Java Development Kit (JDK) 21
- PostgreSQL 17
- Git

Maven can be executed using the included Maven Wrapper.

## 5. Database Configuration

The backend uses PostgreSQL for data persistence.

Create a database with the following configuration:

| Property | Value |
|---|---|
| Host | `localhost` |
| Port | `5432` |
| Database | `fixcore` |
| Username | `fixcore_user` |
| Password | Environment variable `DB_PASSWORD` |

The database must be owned by `fixcore_user`.

Database connection settings are defined in:

`src/main/resources/application-dev.properties`

The database password is not stored in the repository.

### Configure the database password

Open PowerShell in the project directory and execute:

```powershell
$secure = Read-Host "PostgreSQL password" -AsSecureString
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)

try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
}
```

The environment variable is available only in the current PowerShell session.

## 6. Running the Application

Clone the repository:

```powershell
git clone https://github.com/TechMakers-upc/Backend.git
cd Backend
```

After configuring PostgreSQL and the `DB_PASSWORD` environment variable, start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs on:

`http://localhost:8080`

The `dev` profile is used by default.

Functional REST endpoints will be added as the bounded contexts are implemented.

## 7. Database Migrations

Flyway is responsible for managing database schema changes.

Migration files are located in:

`src/main/resources/db/migration`

The initial migration is:

`V1__initialize_database.sql`

Flyway automatically executes pending migrations when the application starts with the development database configuration.

Migration history is stored in the `flyway_schema_history` table.

Hibernate is configured with:

`spring.jpa.hibernate.ddl-auto=validate`

This configuration validates the database schema instead of automatically creating or modifying tables.

Previously applied migrations must not be modified. New database changes should be introduced through additional versioned migration files.

## 8. REST API Conventions

The backend uses RESTful APIs with JSON request and response bodies.

General conventions:

- Use resource-oriented endpoints.
- Use `GET` to retrieve resources.
- Use `POST` to create resources.
- Use `PUT` or `PATCH` to update resources.
- Use `DELETE` to remove resources when applicable.
- Use DTOs for API requests and responses.
- Validate incoming data using Jakarta Bean Validation.
- Keep business rules inside the domain and application layers.
- Avoid exposing persistence entities directly through REST endpoints.

Common HTTP status codes:

| Status | Description |
|---|---|
| 200 OK | Successful request |
| 201 Created | Resource successfully created |
| 204 No Content | Successful request without a response body |
| 400 Bad Request | Invalid request |
| 404 Not Found | Resource not found |
| 409 Conflict | Request conflicts with the current resource state |
| 500 Internal Server Error | Unexpected server error |

## 9. Exception Handling

The application includes a shared REST exception handling mechanism.

Its implementation is located in:

`src/main/java/com/techmakers/fixcore/shared/interfaces/rest`

The shared components are:

- `ApiError.java`
- `GlobalExceptionHandler.java`

The current implementation handles request validation failures, constraint violations, and invalid JSON request bodies.

Example validation error:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-10-20T16:30:00Z"
}
```

Additional business-specific errors will be implemented alongside their respective bounded contexts.

## 10. Testing

The backend uses JUnit 5 and Spring MVC Test.

Run the automated tests with:

```powershell
.\mvnw.cmd clean test
```

The current test suite includes:

- Application context initialization.
- Request body validation.
- Invalid JSON request handling.

The tests use the `test` profile, which currently excludes database auto-configuration.

Database integration tests will require a separate test database configuration.

## 11. Development Guidelines

Developers should follow these conventions:

- Keep implementation code inside its corresponding bounded context.
- Separate domain logic from application and infrastructure concerns.
- Avoid direct dependencies between the infrastructure implementations of different contexts.
- Define explicit contracts for interactions between contexts.
- Place REST controllers in the interfaces layer.
- Place use cases in the application layer.
- Keep persistence implementations in the infrastructure layer.
- Manage database changes through Flyway migrations.
- Do not commit passwords, credentials, or environment-specific secrets.
- Verify changes using automated tests before creating pull requests.

## 12. Project Team

**TechMakers — Universidad Peruana de Ciencias Aplicadas (UPC)**

FixCore is developed as part of the Open Source Applications course.

Backend development follows a collaborative workflow using Git and GitHub, with feature branches integrated into the shared development branch.