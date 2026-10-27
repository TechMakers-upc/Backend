# FixCore Backend Architecture

## Overview

FixCore Backend is a modular monolith built with Java 21, Spring Boot 4 and Maven. Each business area is a bounded context with its own packages, tables and rules, following Domain-Driven Design and Clean Architecture. The contexts run in one process and share one PostgreSQL database, but none of them reads another one's tables.

## Bounded Contexts

| Context | Aggregates | Main rules |
|---|---|---|
| `iam` | User | Unique email, technicians need a plant, any plant given must exist, BCrypt passwords, JWT sessions |
| `assetmanagement` | Plant, Asset | Unique plant name, unique machine code per plant, status `operational`, `maintenance` or `down` |
| `serviceplanning` | MaintenancePlan | Frequency of 1 to 730 days, standing `scheduled`, `due-soon`, `overdue` or `suspended`, completion moves the next date forward |
| `serviceexecution` | Failure, WorkOrder | Sequential `OT-YYYY-NNNN` codes, pending → in progress → completed, only technicians can be assigned |
| `inventory` | SparePart, StockMovement | Unique SKU per plant, stock never below zero, every change leaves a movement |
| `analytics` | none (read model) | Availability, downtime, MTTR, preventive compliance, technician performance |
| `notifications` | none (read model) | Alerts by role: assigned orders, critical failures, plans due or overdue, low stock |

`shared` holds technical building blocks only: `AuditableEntity`, the snake case naming strategy, `GlobalExceptionHandler`, `EnumValueAssembler`, CORS and OpenAPI configuration, and the `PlantAccessGuard` contract.

## Layers

Every context uses the same four layers.

| Layer | Contents | May depend on |
|---|---|---|
| `domain` | Aggregates, value objects, commands, queries, repository ports, service contracts, pure domain services | Nothing outside the JDK and `shared.domain` |
| `application` | Command and query service implementations, seeders, outbound ports in `outboundservices` | Domain |
| `infrastructure` | JPA entities, mappers, Spring Data repositories, repository adapters, ACL adapters, security | Domain and application |
| `interfaces` | REST controllers, resources, assemblers, and the context facade in `interfaces/acl` | Domain and application |

The domain stays free of Spring, JPA and HTTP. Aggregates are plain Java classes; JPA entities live in `infrastructure/persistence/jpa` and are translated by mappers. `ArchitectureTests` (ArchUnit) enforces these rules on every build.

## Cross-Context Communication

A context exposes what others may use through a facade in `interfaces/acl`. The consumer declares its own port in `application/outboundservices/acl` and implements it with an adapter in `infrastructure/acl` that calls the facade. The consumer's application code only sees its own port.

```
WorkOrderExecutionServiceImpl ──> AssetStatusUpdater (port)
                                        │
                     AssetStatusUpdaterAdapter (infrastructure/acl)
                                        │
                              AssetsContextFacade (assetmanagement)
```

Facades return primitives or small summary records such as `AssetSummary` or `WorkOrderSummary`, never aggregates, so a change inside one context does not leak into another.

### Maintenance flow

| Event | Side effects |
|---|---|
| A technician reports a failure that stops the line | The machine becomes `down` |
| A work order starts | An `operational` machine becomes `maintenance` |
| A part is used in a work order | Stock decreases and a consumption movement is linked to the order |
| A work order is completed | Its failure is resolved, its plan moves to the next date, and the machine returns to `operational` when no stopped-line failure or other order in progress remains |

### Read models

`analytics` and `notifications` own no tables. They read summaries from the other facades through one port each (`MaintenanceRecordsProvider`, `AlertSourcesProvider`), translate them into their own value objects, and compute results with pure domain services (`MaintenanceMetrics`, `AlertComposer`).

## Security

- Stateless JWT authentication. `BearerAuthorizationRequestFilter` validates the token and loads the user.
- Roles are checked with `@PreAuthorize` on controllers (`PLANT_MANAGER`, `TECHNICIAN`, `OPERATIONS_MANAGER`).
- Plant scope is checked in the application layer through `PlantAccessGuard`. IAM implements it from the authenticated user: operations managers reach every plant, everyone else only their own, and calls without a user (seeders) are allowed. A violation returns `403`.
- Public paths: authentication, `/actuator/health`, and Swagger when it is enabled.

## Persistence

- PostgreSQL in `dev` and `prod`, H2 in PostgreSQL mode for tests.
- Flyway owns the schema; Hibernate only validates it.
- Tables use plural snake case names produced by `SnakeCaseWithPluralizedTablePhysicalNamingStrategy`.
- Demo data comes from JSON files in `src/main/resources/seed`. They reference plants, machines and users by name, code or email, never by id, so they work on any database.

## Profiles

| Profile | Database | Demo data | Swagger | Secrets |
|---|---|---|---|---|
| `dev` | `localhost:5432/fixcore` | Yes | On | Development JWT key allowed |
| `test` | H2 in memory | Per test class | On | Fixed test key |
| `prod` | `DB_URL` | No | Off unless `SWAGGER_ENABLED=true` | `JWT_SECRET` required |

## Build and Run

```bash
./mvnw test
./mvnw spring-boot:run
docker compose up --build
```
