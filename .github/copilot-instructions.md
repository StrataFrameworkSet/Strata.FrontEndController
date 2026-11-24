# Strata.FrontEndController Microservice

## Overview

Strata.FrontEndController is a Spring Boot microservice that provides the front-end controller for the Strata system. The microservice exposes a request/reply REST service API. Actions run asynchronously on an Executor; the service persists nothing and emits no events.

## Technology Stack

- **Language**: Java 23 (Amazon Corretto), TypeScript
- **Build**: Gradle (multi-project), Yarn (TypeScript components)
- **Framework**: Spring Boot 3.4, Spring 6.2
- **Object Mapping**: ModelMapper 3.2
- **Containerization**: Google Jib (OCI images)
- **Testing**: JUnit Jupiter 5
- **Foundation Libraries**: Strata Framework Set (strata.foundation.core, strata.server.core, strata.client.core)

## Project Structure

```
Strata.FrontEndController/
├── Applications/
│   └── Strata.FrontEndController.ServiceHost/    # Spring Boot app for service operations
├── Components/
│   ├── Strata.FrontEndController.Service/        # Shared interfaces and DTOs (Java + TypeScript)
│   ├── Strata.FrontEndController.Server/         # Server-side implementation (application, platform, configuration)
│   └── Strata.FrontEndController.Client/         # REST client (Java + TypeScript)
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

The microservice follows a strict layered architecture.

### Layers (Strata.FrontEndController.Server)

Each layer is organized into a dedicated Java package:

- **`server.platform`** — Spring REST controllers and platform Spring configuration (health checks). Depends on `server.application`.
- **`server.application`** — Service implementation (`FrontEndControllerService`), context objects, ModelMapper configuration. No upward dependencies.
- **`server.configuration`** — `AdminFrontEndControllerConfiguration` and `UserFrontEndControllerConfiguration`, which wire the request processor, executor, service and controller for each audience.

### Component Responsibilities

#### Strata.FrontEndController.Service

The shared contract component. Contains no implementation logic. Defines:

- **Service interfaces**: `IFrontEndControllerService`
- **Request/Reply DTOs**: `HandleInputRequest`/`Reply`, `GetOutputRequest`/`Reply`
- **Data transfer objects**: `FrontEndInput` (abstract), `FrontEndOutput` (abstract)
- **Mappers**: `FrontEndInputMapper`, `FrontEndOutputMapper`
- **TypeScript mirror**: All interfaces and DTOs are also defined in TypeScript under `src/main/typescript/`

#### Strata.FrontEndController.Server

The server-side implementation:

- **Application layer** (`server.application`):
  - `FrontEndControllerService` — Implements `IFrontEndControllerService`. Provides `handleInput` and `getOutput` operations.
  - Context objects: `HandleInputContext`, `GetOutputContext`
  - `MappingModule` — ModelMapper module for type mappings (currently registers none)
  - `ApplicationConfiguration` — Spring `@Configuration` that wires frontendcontroller services with request scope

- **Platform layer** (`server.platform`):
  - `AbstractFrontEndControllerServiceController` — Base controller exposing `POST /handle-input` and `POST /get-output`, delegating to `IFrontEndControllerService`
  - `AdminFrontEndControllerServiceController` / `UserFrontEndControllerServiceController` — Spring `@RestController`s mapped under `/frontendcontroller-admin-service` and `/frontendcontroller-user-service`
  - `PlatformConfiguration` — Spring configuration that imports Strata's `HealthCheckConfiguration`

#### Strata.FrontEndController.Client

REST client implementations:

- **Request/Reply clients** (Java): `FrontEndControllerServiceClient` extending `AbstractRestClient` — `FrontEndControllerServiceClient(baseUrl)` targets the user service; `FrontEndControllerServiceClient(baseUrl,ADMIN_SERVICE_PATH)` targets the admin service
- **Client interfaces** (Java): `IFrontEndControllerServiceClient`
- **TypeScript mirror**: `FrontEndControllerServiceClient` with corresponding interface

### Applications

- **FrontEndControllerServiceHost** — Spring Boot application that assembles the service-side stack: `HostConfiguration` imports `HostUserConfiguration`, `HostAdminConfiguration`, and `ServiceConfiguration`. Listens on port 8081.
  - `HostUserConfiguration` / `HostAdminConfiguration` extend the audience configurations (whose `*RequestProcessor` and `*Executor` beans must be overridden by the application). Each supplies a virtual-thread `Executor` and a `HostRequestProcessor` (a `DefaultRequestProcessor`) built from the `IProcessAction` beans qualified with Spring `@Qualifier("User")` / `@Qualifier("Admin")`; the bean name is the action key. No actions are registered by default.
  - `IntegrationTestConfiguration` (`@Profile("integration-test")`) registers a user `test-source` get-output action for the `IntegrationStage` tests; the build pipeline starts the ServiceHost image with `SPRING_PROFILES_ACTIVE=integration-test`.

## Commonly Used Design Patterns

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

### Context Object

Each service pipeline uses a typed context object (e.g., `HandleInputContext`, `GetOutputContext`) that carries the request, intermediate state, and results through the pipeline steps. Context objects use fluent setters and provide convenience getters that delegate to the underlying request.

### Composable Configuration

Spring `@Configuration` classes are composed via `@Import`. A base `PlatformConfiguration` provides shared platform beans (health checks), and `AdminFrontEndControllerConfiguration`/`UserFrontEndControllerConfiguration` each import `ApplicationConfiguration` and wire an audience-specific service and controller. `HostConfiguration` at the application level imports and assembles all required configurations.

## REST Endpoints

### FrontEndController Admin/User Services (`/frontendcontroller-admin-service`, `/frontendcontroller-user-service`)

| Endpoint | Request | Reply |
|---|---|---|
| `POST /handle-input` | `HandleInputRequest` | `HandleInputReply` |
| `POST /get-output` | `GetOutputRequest` | `GetOutputReply` |

## Versioning and Release

| | Development snapshot | Release |
|---|---|---|
| Maven jars | `X.Y.0-SNAPSHOT` (e.g. `1.0.0-SNAPSHOT`) | `X.Y.0` (e.g. `1.0.0`) |
| npm packages | `X.Y.0-snapshot.<timestamp>`, dist-tag `snapshot-X.Y.0` | `X.Y.0-release.<timestamp>`, dist-tags `release-X.Y.0` and `latest` |
| Docker images | `X.Y.0-<yyyyMMddHHmmss>` (one per pipeline run) | `X.Y.0` |

- The version is set by `major`/`minor`/`patch` in the root `build.gradle`. The npm version base is set by `increment-snapshot` and the `snapshot-X.Y.0` publish tag in each TypeScript component.
- `.github/workflows/build-pipeline.yml` runs on `development` and `release-*` (pull requests build and test only). Its stages are:
  - **commit**: Gradle build and test, push the ServiceHost and TestSuite images (tagged by `gradle/strata-image.gradle` from `BUILD_IMAGE_TAG`), and publish the npm snapshots of `strata.frontendcontroller.service` and `.client`
  - **integration**: run the ServiceHost image and the TestSuite image's `IntegrationStage` tests against it
  - **acceptance**: a placeholder
  - **deploy**: publish the snapshot jars (`strata-frontendcontroller-service`, `-client`, `-server`, `-testsuite`) to GitHub Packages
- Locally, `./gradlew build` skips Jib unless `BUILD_IMAGE_TAG` is set to `<version>-<yyyyMMddHHmmss>`.
- A release is made on a `release-X.Y` branch:
  - its last snapshot is promoted to the release version with `strata-frontendcontroller-promote.ps1` / `strata-frontendcontroller-promote.sh` (run with `--dry-run` / `-DryRun` first). Jars are republished as `X.Y.0`, npm packages get their `strata.*` dependencies pinned to exact release versions, and the newest `X.Y.0-<timestamp>` images (or `--image-tag` / `-ImageTag`) are tagged `X.Y.0`
  - `development` then moves on to the next minor version
- Strata.FrontEndController 1.0.0 depends on the Strata 1.0.0 releases (Maven `strata-foundation-core`, `strata-server-spring`, `strata-client-core`/`-spring` `1.0.0`; npm `strata.foundation.core`/`strata.client.core@release-1.0.0`).

## Preferred Coding Style and Idioms

### Interface Naming

All interfaces are prefixed with `I`. Examples: `IFrontEndControllerService`, `IFrontEndControllerServiceClient`.

### Class and Method Formatting

The `class` keyword and the `implements`/`extends` clause each start on their own line. Opening braces are on their own line (Allman style). Method return types are on a separate line from the method name.

Key formatting rules:

- **File header/footer**: Every Java file begins and ends with a `//////////////////////////////////////////////////////////////////////////////` comment. The second line contains the filename as a comment.
- **`class` on its own line**: The access modifier (`public`) is on one line, and `class ClassName` is on the next line. `extends` and `implements` clauses are indented on subsequent lines.
- **Method return type on its own line**: The access modifier and return type are on one line, and the method name with parameters is on the next line.
- **Allman brace style**: Opening braces `{` are on their own line for classes, methods, constructors, and control structures.
- **Compact getters**: Simple getters that return a single field use an inline body on the same line as the method name: `getFoo() { return foo; }`.
- **Column alignment**: Related field declarations are column-aligned by type and name.
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
