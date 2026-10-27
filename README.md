# FixCore Backend

FixCore helps industrial plants keep their machines running. Plant managers register machines and spare parts, plan preventive maintenance, and assign work orders; technicians report failures, log their repairs, and record the parts they use; the operations manager follows every plant from one dashboard.

This repository holds the REST API behind the [FixCore Frontend](https://github.com/TechMakers-upc/Frontend). It is a Spring Boot modular monolith organized with Domain-Driven Design and Clean Architecture.

## 1. Technologies

- Java 21 and Spring Boot 4.0.3 (Spring MVC, Spring Data JPA, Spring Security, Actuator)
- PostgreSQL 18 with Flyway migrations
- JWT authentication (jjwt) with BCrypt password hashing
- springdoc-openapi for Swagger documentation
- JUnit 5, Spring MVC Test, H2 and ArchUnit for tests
- Maven Wrapper and Docker

## 2. Bounded Contexts

| Context | Package | What it owns |
|---|---|---|
| Identity & Access Management | `iam` | Users, roles, sign-up, sign-in and JWT tokens |
| Resource & Asset Management | `assetmanagement` | Plants, machines, machine status and manuals |
| Service Design & Planning | `serviceplanning` | Preventive maintenance plans and their schedule |
| Service Execution & Monitoring | `serviceexecution` | Failure reports and work orders |
| Inventory & Spare Parts | `inventory` | Spare parts, stock levels and stock movements |
| Analytics | `analytics` | Read-only KPIs for dashboards and reports |
| Notifications | `notifications` | Role-based alerts derived from the current state |

`shared` holds technical pieces used by every context: the base auditable entity, the naming strategy, the exception handler, CORS and the plant access contract.

Contexts never touch each other's persistence. When one needs another, it calls that context's facade in `interfaces/acl` through a port of its own. For example, closing a work order resolves its failure, moves the maintenance plan forward and puts the machine back to `operational`, all through facades. The details are in [ARCHITECTURE.md](ARCHITECTURE.md).

## 3. Roles and Plant Scope

| Role | Value | Scope |
|---|---|---|
| Plant manager | `plant-manager` | Manages machines, parts, plans and work orders of their own plant |
| Technician | `technician` | Reports failures and executes the work orders of their own plant |
| Operations manager | `operations-manager` | Reads and manages every plant |

Writing to another plant returns `403 Forbidden`.

## 4. Requirements

- JDK 21
- PostgreSQL 18 (or Docker)
- Git

## 5. Running Locally

### 5.1 Database

Create a `fixcore` database owned by a `fixcore_user` login. Flyway creates every table on the first start.

```sql
CREATE ROLE fixcore_user WITH LOGIN PASSWORD 'choose-a-password';
CREATE DATABASE fixcore OWNER fixcore_user;
```

### 5.2 Start the API

The `dev` profile is the default. It connects to `localhost:5432/fixcore` and loads demo data.

```powershell
$env:DB_PASSWORD = "choose-a-password"
.\mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080` and Swagger UI is at `http://localhost:8080/swagger-ui.html`.

### 5.3 Demo accounts

The `dev` profile seeds three plants (Ate, Callao and Arequipa), their machines, plans, parts, failures and work orders. Every demo account uses the password `FixCore2026`.

| Email | Role | Plant |
|---|---|---|
| `victor.salazar@fixcore.demo` | Operations manager | All |
| `carla.garcia@fixcore.demo` | Plant manager | Planta Ate |
| `jose.ramirez@fixcore.demo` | Technician | Planta Ate |
| `jairo.mendoza@fixcore.demo` | Plant manager | Planta Callao |
| `rosa.quispe@fixcore.demo` | Technician | Planta Callao |
| `ana.paredes@fixcore.demo` | Plant manager | Planta Arequipa |
| `luis.huaman@fixcore.demo` | Technician | Planta Arequipa |

Seeders only add what is missing, so restarting the app does not duplicate data.

## 6. Running with Docker

Copy `.env.example` to `.env`, set a database password and a JWT secret of at least 32 characters, then run:

```bash
docker compose up --build
```

This starts PostgreSQL and the API with the `prod` profile on port `8080`. The `prod` profile does not load demo data and keeps Swagger off unless `SWAGGER_ENABLED=true`.

## 7. Configuration

| Variable | Profile | Purpose |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | all | `dev` by default, `prod` for deployments |
| `DB_URL` | prod | JDBC URL, for example `jdbc:postgresql://host:5432/fixcore` |
| `DB_USERNAME` | dev, prod | Database user (`fixcore_user` in dev) |
| `DB_PASSWORD` | dev, prod | Database password |
| `JWT_SECRET` | prod | Signing key for tokens, required, at least 32 characters |
| `JWT_EXPIRATION_DAYS` | all | Token lifetime, 7 days by default |
| `CORS_ALLOWED_ORIGINS` | all | Comma-separated Frontend origins |
| `SWAGGER_ENABLED` | prod | `false` by default |
| `PORT` | prod | HTTP port, 8080 by default |

`GET /actuator/health` is public so a hosting platform can check the service.

## 8. API

Every endpoint lives under `/api/v1` and, except authentication and health, needs an `Authorization: Bearer <token>` header. Enum values travel in lowercase kebab case, such as `in-progress` or `plant-manager`.

| Area | Endpoints |
|---|---|
| Authentication | `POST /authentication/sign-up`, `POST /authentication/sign-in` |
| Users | `GET /users`, `GET /users/me`, `GET /users/{id}`, `GET /roles` |
| Plants | `GET POST /plants`, `GET PUT DELETE /plants/{id}` |
| Machines | `GET POST /assets`, `GET PUT DELETE /assets/{id}`, `PATCH /assets/{id}/status`, `POST /assets/{id}/manuals`, `DELETE /assets/{id}/manuals/{index}` |
| Maintenance plans | `GET POST /maintenance-plans`, `GET PUT DELETE /maintenance-plans/{id}`, `PATCH .../schedule`, `POST .../suspension`, `POST .../reactivation`, `POST .../completions` |
| Spare parts | `GET POST /spare-parts`, `GET PUT DELETE /spare-parts/{id}`, `POST .../entries`, `POST .../adjustments`, `POST .../consumptions`, `GET /stock-movements` |
| Failures | `GET POST /failures`, `GET /failures/{id}`, `PATCH .../priority`, `POST .../resolution` |
| Work orders | `GET POST /work-orders`, `GET PUT DELETE /work-orders/{id}`, `PATCH .../assignment`, `POST .../start`, `POST .../work-logs`, `POST .../part-usages`, `POST .../completion` |
| Analytics | `GET /analytics/summary`, `GET /analytics/downtime-ranking`, `GET /analytics/technicians/{id}/performance` (optional `plantId`, `from`, `to`) |
| Alerts | `GET /alerts` |

Swagger UI documents every request and response body.

### Errors

Errors share one shape:

```json
{
  "status": 409,
  "message": "Only pending work orders can start",
  "timestamp": "2026-10-27T16:30:00Z"
}
```

| Status | When |
|---|---|
| 400 | Invalid body, parameter or value |
| 401 | Missing or invalid token, wrong credentials |
| 403 | Role not allowed, or data from another plant |
| 404 | Resource not found |
| 409 | Duplicate data or a business rule was broken |

## 9. Database Migrations

Migrations live in `src/main/resources/db/migration` (`V1` to `V9` today) and run on startup. Hibernate only validates the schema (`ddl-auto=validate`). Never edit a migration that was already applied; add a new versioned file instead.

## 10. Tests

```powershell
.\mvnw.cmd test
```

The suite runs on an in-memory H2 database in PostgreSQL mode and covers domain rules, repository adapters, REST controllers, security, the cross-context maintenance flow, plant scope, analytics and alerts. `ArchitectureTests` fails the build if a domain class depends on Spring, JPA or an outer layer.

## 11. Git Workflow

We follow Gitflow. Work starts from `develop` on a `feature/*` branch, is pushed and merged back through a pull request with a merge commit, and reaches `main` through a `release/*` branch. Commits use Conventional Commits (`feat`, `fix`, `test`, `docs`, `build`) and stay small.

## 12. Team

**TechMakers** — Universidad Peruana de Ciencias Aplicadas (UPC), Open Source Applications course.
