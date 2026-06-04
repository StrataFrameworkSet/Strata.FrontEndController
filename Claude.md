# Strata.FrontEndController Microservice

## Overview

Strata.FrontEndController is a Spring Boot microservice that manages the FrontEndController entity within the Strata system. The microservice exposes command and query REST APIs for CRUD operations on FrontEndController entities and emits domain events (FrontEndControllerCreated, FrontEndControllerUpdated, FrontEndControllerDestroyed) as frontendcontroller state changes occur. Events are propagated to external consumers via the Transactional Outbox pattern and Kafka.

## Technology Stack

- **Language**: Java 22 (Amazon Corretto), TypeScript
- **Build**: Gradle (multi-project), Yarn (TypeScript components)
- **Framework**: Spring Boot 3.4, Spring 6.2
- **Persistence**: JPA / Hibernate 6.4, PostgreSQL (production), H2 (testing)
- **Object Mapping**: ModelMapper 3.2
- **Eventing**: Kafka, Transactional Outbox (strata.outbox)
- **Containerization**: Google Jib (OCI images)
- **Testing**: JUnit Jupiter 5
- **Foundation Libraries**: Strata Framework Set (strata.foundation.core, strata.server.core, strata.client.core, strata.outbox.core)

## Project Structure

```
Strata.FrontEndController/
├── Applications/
│   └── Strata.FrontEndController.ServiceHost/    # Spring Boot app for service operations
├── Components/
│   ├── Strata.FrontEndController.Service/        # Shared interfaces, DTOs, and events (Java + TypeScript)
│   ├── Strata.FrontEndController.Server/         # Server-side implementation (domain, application, platform)
│   └── Strata.FrontEndController.Client/         # REST client and Kafka event receiver (Java + TypeScript)
├── Tests/
│   ├── Strata.FrontEndController.ServiceTest/    # Test fixtures for service interfaces
│   ├── Strata.FrontEndController.ServerTest/     # Server unit/integration tests
│   ├── Strata.FrontEndController.ClientTest/     # Client integration tests
│   └── Strata.FrontEndController.TestSuite/      # Aggregated test suite
├── build.gradle                              # Root build with dependency versions
├── settings.gradle                           # Multi-project module declarations
└── package.json                              # Root Yarn workspace for TypeScript builds
```

## Architecture

The microservice follows a strict layered architecture with CQRS (Command Query Responsibility Segregation). Commands and queries are separated into distinct host applications, service interfaces, and controllers.

### Layers (Strata.FrontEndController.Server)

Each layer is organized into a dedicated Java package:

- **`server.platform`** — Spring REST controllers, Spring configuration, JPA/Hibernate unit-of-work wiring, frontendcontroller event sender implementations. Depends on `server.application` and `server.domain`.
- **`server.application`** — Service implementation (`FrontEndControllerService`), context objects, ModelMapper configuration. Depends on `server.domain`.
- **`server.domain`** — Domain frontendcontroller (`FrontEndController`), repository interface/implementation, domain event types. No upward dependencies.

### Component Responsibilities

#### Strata.FrontEndController.Service

The shared contract component. Contains no implementation logic. Defines:

- **Service interfaces**: `IFrontEndControllerService`
- **Request/Reply DTOs**: `HandleInputRequest`/`Reply`, `GetOutputRequest`/`Reply`
- **Data transfer objects**: `FrontEndInput` (abstract), `FrontEndOutput` (abstract)
- **Mappers**: `FrontEndInputMapper`, `FrontEndOutputMapper`
- **Event interfaces**: `IFrontEndControllerEventSender`, `IFrontEndControllerEventReceiver`, `IFrontEndControllerEventListener`
- **TypeScript mirror**: All interfaces and DTOs are also defined in TypeScript under `src/main/typescript/`

#### Strata.FrontEndController.Server

The server-side implementation:

- **Domain layer** (`server.domain`):
  - `FrontEndController` — JPA frontendcontroller extending `AbstractEntityAndDomainEventSource`; fires domain events on create/update/destroy
  - `IFrontEndControllerRepository` / `FrontEndControllerRepository` — Repository interface and JPA implementation with `findAllByIdIn`, `findAllByFoo`, `findAllByFooIn` queries
  - Domain events: `FrontEndControllerCreated`, `FrontEndControllerUpdated`, `FrontEndControllerDestroyed` extending `AbstractFrontEndControllerEvent`
  - Observer pattern: `IFrontEndControllerEventObserver`, `IFrontEndControllerEventSource`

- **Application layer** (`server.application`):
  - `FrontEndControllerService` — Implements `IFrontEndControllerService`. Provides `handleInput` and `getOutput` operations.
  - Context objects: `HandleInputContext`, `GetOutputContext`
  - `MappingModule` — ModelMapper type mappings between FrontEndController domain objects and FrontEndController DTOs
  - `ApplicationConfiguration` — Spring `@Configuration` that wires frontendcontroller services with request scope

- **Platform layer** (`server.platform`):
  - `FrontEndControllerServiceController` — Spring `@RestController` exposing service endpoints under `/frontendcontroller-service/`
  - `PlatformConfiguration` — Base Spring configuration for JPA EntityManagerFactory, UnitOfWork, and synchronization manager
  - `ServicePlatformConfiguration` — Extends `PlatformConfiguration`; wires service controller
  - Event senders: `OnCommitFrontEndControllerEventSender` (sends after transaction commit), `OutboxEventFrontEndControllerEventSender` (persists to outbox table), `MockFrontEndControllerEventSender` (testing)

#### Strata.FrontEndController.Client

REST client and event receiver implementations:

- **Request/Reply clients** (Java): `FrontEndControllerServiceClient` extending `AbstractRestClient`
- **Client interfaces** (Java): `IFrontEndControllerServiceClient`
- **Event receiver**: `FrontEndControllerKafkaEventReceiver`, `FrontEndControllerKafkaAvroEventReceiver` — Kafka consumers for FrontEndController events
- **TypeScript mirror**: `FrontEndControllerServiceClient` with corresponding interface

### Applications

- **FrontEndControllerServiceHost** — Spring Boot application that assembles the service-side stack: `HostConfiguration` imports `ApplicationConfiguration`, `DomainConfiguration`, `ServicePlatformConfiguration`, and `ServiceConfiguration`. Listens on port 8081.

## Commonly Used Design Patterns

### CQRS (Command Query Responsibility Segregation)

Service operations (handleInput, getOutput) are exposed via `IFrontEndControllerService`, `FrontEndControllerServiceController`, and the `FrontEndControllerServiceHost` application.

### Request-Reply

All service operations accept a typed request DTO and return a typed reply DTO. Requests extend `AbstractServiceRequest` (with `requestId`, `timestamp`, headers, cookies). Replies extend `AbstractServiceReply` (with `replyId`, `originatingRequestId`, `success`, `successMessage`, `failureMessage`, `exception`). Each operation has its own request/reply pair (e.g., `HandleInputRequest` → `HandleInputReply`).

### Pipeline (Chain of Responsibility)

All service methods follow an asynchronous pipeline pattern using `CompletionStage`:

```
validate → mapToContext → [operation steps] → buildReply
                                                  ↓ (on failure)
                                             buildReply(exception)
```

Each step receives and returns a typed context object that accumulates state through the pipeline. The pattern ensures consistent validation, error handling, and reply construction across all operations.

### Observer (Domain Events)

The `FrontEndController` domain object extends `AbstractEntityAndDomainEventSource` and fires domain events (`FrontEndControllerCreated`, `FrontEndControllerUpdated`, `FrontEndControllerDestroyed`) via the observer pattern.

### Transactional Outbox

Event delivery uses the Transactional Outbox pattern to guarantee at-least-once delivery. `OnCommitFrontEndControllerEventSender` defers sending until the database transaction commits. `OutboxEventFrontEndControllerEventSender` serializes the event into an outbox table within the same transaction as the frontendcontroller state change, and a separate process polls the outbox and publishes to Kafka.

### Repository

`IFrontEndControllerRepository` extends the Strata `IRepository<Long,FrontEndController>` interface providing standard `findById`, `save`, `delete` operations. The concrete `FrontEndControllerRepository` extends `AbstractRepository` and registers named JPA queries for `findAllByIdIn`, `findAllByFoo`, and `findAllByFooIn`.

### Context Object

Each service pipeline uses a typed context object (e.g., `HandleInputContext`, `GetOutputContext`) that carries the request, intermediate state, and results through the pipeline steps. Context objects use fluent setters and provide convenience getters that delegate to the underlying request.

### Strategy (Event Sending)

The `IFrontEndControllerEventSender` interface has multiple implementations (`MockFrontEndControllerEventSender`, `OnCommitFrontEndControllerEventSender`, `OutboxEventFrontEndControllerEventSender`) that are selected via Spring configuration, allowing the event delivery strategy to be swapped without changing service code.

### Composable Configuration

Spring `@Configuration` classes are composed via `@Import`. A base `PlatformConfiguration` provides shared JPA/UnitOfWork beans, and `ServicePlatformConfiguration` extends it with service-specific beans. `HostConfiguration` at the application level imports and assembles all required configurations.

## REST Endpoints

### FrontEndController Service (`/frontendcontroller-service/`)

| Endpoint | Request | Reply |
|---|---|---|
| `POST /handle-input` | `HandleInputRequest` | `HandleInputReply` |
| `POST /get-output` | `GetOutputRequest` | `GetOutputReply` |

## Preferred Coding Style and Idioms

### Interface Naming

All interfaces are prefixed with `I`. Examples: `IFrontEndControllerRepository`, `IFrontEndControllerService`, `IFrontEndControllerEventSender`, `IFrontEndControllerEventObserver`, `IFrontEndControllerServiceClient`.

### Class and Method Formatting

The `class` keyword and the `implements`/`extends` clause each start on their own line. Opening braces are on their own line (Allman style). Method return types are on a separate line from the method name.

Key formatting rules:

- **File header/footer**: Every Java file begins and ends with a `//////////////////////////////////////////////////////////////////////////////` comment. The second line contains the filename as a comment.
- **`class` on its own line**: The access modifier (`public`) is on one line, and `class ClassName` is on the next line. `extends` and `implements` clauses are indented on subsequent lines.
- **Method return type on its own line**: The access modifier and return type are on one line, and the method name with parameters is on the next line.
- **Allman brace style**: Opening braces `{` are on their own line for classes, methods, constructors, and control structures.
- **Compact getters**: Simple getters that return a single field use an inline body on the same line as the method name: `getFoo() { return foo; }`.
- **Column alignment**: Related field declarations are column-aligned by type and name (e.g., `private final IFrontEndControllerRepository  repository;` and `private final ModelMapper            mapper;`).
- **Constructor field alignment**: Assignment statements in constructors are column-aligned on the `=` sign.
- **Minimal space after commas**: Method calls and declarations use `(param1,param2)` with no space after commas in most cases, or a single space after commas when it improves readability.

### Fluent Setters and Fluent Methods

All setters return `this` (or the concrete subclass type) to enable method chaining. This applies to request, reply, context, and data classes. When overriding base class setters, the return type is narrowed to the concrete class via a cast:

```java
@Override
public HandleInputReply
setSuccess(boolean success)
{
    return (HandleInputReply)super.setSuccess(success);
}
```

Fluent style is also used for context mutation methods and builder patterns:

```java
return
    new HandleInputReply(context.getRequest())
        .setSuccess(true)
        .setSuccessMessage("input handled successfully")
        .setOutput(context.getOutput());
```

### Method Organization

Within a class, methods are grouped by access level: public methods first, then protected, then private. Within each group, methods follow the declaration order of the interface or base class they implement. For service implementations, the order is: public service methods, then protected pipeline steps (`validate`, `mapToContext`, operation methods, `buildReply`), then private helper methods.

### Stream vs. Loop Style

Prefer explicit `for` loops for simple iteration. Use streams with `Collectors` for set-difference, mapping, and collecting operations. Never use side effects inside stream `map` or `filter` operations — derive intermediate collections in separate stream pipelines instead.

### Validation

Each pipeline starts with a `validate` step. Use `Objects.requireNonNull` for required fields and throw `IllegalArgumentException` for empty collections.

### TypeScript Array Types

Use `Array<Type>` syntax rather than `Type[]` in all TypeScript code.
