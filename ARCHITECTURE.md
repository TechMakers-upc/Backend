# FixCore Backend Architecture

## Overview

FixCore Backend is a modular monolith built with Java 21,
Spring Boot 4 and Maven.

The application follows Domain-Driven Design (DDD)
and Clean Architecture principles.

## Bounded Contexts

- iam: Identity and Access Management
- assetmanagement: Resource and Asset Management
- serviceplanning: Service Design and Planning
- serviceexecution: Service Execution and Monitoring
- inventory: Inventory and Spare Parts Management

The shared package contains reusable technical components
and abstractions, not business logic owned by other contexts.

## Architecture Layers

### Domain
Contains entities, value objects, domain services,
business rules and repository abstractions.

The domain must not depend on Spring, JPA or HTTP.

### Application
Contains use cases and application services.

It coordinates domain operations through abstractions.

### Infrastructure
Contains technical implementations such as persistence
adapters, database mappings and external integrations.

### Interfaces
Contains REST controllers, request and response DTOs
and HTTP-specific mappings.

## Dependency Rules

- Domain must remain independent of other layers.
- Application may depend on Domain.
- Infrastructure may depend on Domain and Application.
- Interfaces may depend on Application and Domain.
- Business contexts must not directly access another
  context's infrastructure or persistence implementation.
- Cross-context interactions should use explicit contracts.

## Sprint 3 Scope

The initial functional implementation focuses on:

- Service Execution: Work Orders
- Inventory: Spare Parts
- Asset Management: Plants and Machines

IAM and Service Planning are reserved for future
implementation and are not considered operational.

## Development

Build:

./mvnw clean package

Run:

./mvnw spring-boot:run

Database configuration is required before running
the complete application with JPA enabled.
