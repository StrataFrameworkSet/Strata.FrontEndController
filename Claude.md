# Strata.FrontEndController Microservice

## Overview

Strata.FrontEndController is a Spring Boot microservice that provides the front-end controller for the Strata system. It is a framework layer: the microservice exposes a request/reply REST service API of typed, per-subject operations (e.g. `handleEchoInput` / `getEchoOutput`) that applications extend with their own subjects. Actions run asynchronously on an Executor; the service persists nothing and emits no events.

## Technology Stack

- **Language**: Java 22 (Amazon Corretto), TypeScript
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
│   └── Strata.FrontEndController.ServiceHost/    # Spring Boot app hosting the sample subject
├── Components/
│   ├── Strata.FrontEndController.Service/        # Shared interfaces and DTOs (Java + TypeScript)
│   ├── Strata.FrontEndController.Server/         # Server-side implementation (application, platform, sample)
│   └── Strata.FrontEndController.Client/         # REST client bases and sample clients (Java + TypeScript)
├── Tests/
│   ├── Strata.FrontEndController.ServiceTest/    # Test fixtures for service interfaces and controller mappings
│   ├── Strata.FrontEndController.ServerTest/     # Server unit/integration tests
│   ├── Strata.FrontEndController.ClientTest/     # Client integration tests
│   └── Strata.FrontEndController.TestSuite/      # Aggregated test suite
├── build.gradle                              # Root build with dependency versions
├── settings.gradle                           # Multi-project module declarations
└── package.json                              # Root Yarn workspace for TypeScript builds
```

## Architecture

The microservice follows a strict layered architecture with CQRS (Command Query Responsibility Segregation). Each subject declares its own operations on its own service interface, controller and client. The framework supplies generic base types and a template pipeline; applications supply the subject-specific parts.

### Layers (Strata.FrontEndController.Server)

Each layer is organized into a dedicated Java package:

- **`server.platform`** — Abstract REST controller base and platform Spring configuration (health checks). Depends on `server.application`.
- **`server.application`** — Abstract service implementation (`FrontEndControllerService`), generic context objects, ModelMapper configuration. No upward dependencies.
- **`server.sample`** — Echo sample subject: service, controller and configuration that wire the sample into the host.

### Component Responsibilities

#### Strata.FrontEndController.Service

The shared contract component. Contains no implementation logic. Defines:

- **Service interfaces**: `IFrontEndControllerService` is an empty marker interface. Subjects extend it with their own operations (e.g. `IEchoFrontEndControllerService` declares `handleEchoInput` and `getEchoOutput`). No operation is declared on the marker, because any method would have to fit every subject.
- **Request/Reply DTOs**: generic bases `HandleInputRequest<I>`, `HandleInputReply<I,O>` (echoes the input; applications mark sensitive input fields write-only), `GetOutputRequest` (abstract; `userId`, `source`, `command`, `destination`; `getKey()`), `GetOutputReply<O>`.
- **Data transfer objects**: `FrontEndInput` (abstract), `FrontEndOutput` (abstract). They carry no Jackson type ids; each subject's concrete subclasses bind the generic types.
- **Mappers**: `FrontEndInputMapper`, `FrontEndOutputMapper`, generalized to the generic reply and request types.
- **Keys**: `KeyBuilder` builds the `source->command->destination` key. It is used for logging only, not for routing.
- **TypeScript mirror**: All interfaces and DTOs are also defined in TypeScript under `src/main/typescript/`, with `Array<T>` syntax.

#### Strata.FrontEndController.Server

The server-side implementation:

- **Application layer** (`server.application`):
  - `FrontEndControllerService` — `public abstract class` implementing `IFrontEndControllerService`. Constructed with `(ModelMapper,Executor)`. Provides generic protected template helpers `handleInput(...)` and `getOutput(...)`, each taking the request, an optional validator, an operation step and a reply factory (see Pipeline below).
  - Standard validation: `validate` requires `input` to be non-null for handle requests and `source` to be non-null for get requests. `userId` is not required. Subclasses override these generic `validate` methods to change validation for all their subjects.
  - Context objects: `HandleInputContext<I,O>`, `GetOutputContext<R,O>`
  - `MappingModule` — ModelMapper module for type mappings (currently registers none)
  - `ApplicationConfiguration` — Spring `@Configuration` that provides the shared `ModelMapper` singleton

- **Platform layer** (`server.platform`):
  - `AbstractFrontEndControllerServiceController<S extends IFrontEndControllerService>` — Base controller holding the service `S`. It declares no mappings; each subject controller maps its own endpoints.
  - `PlatformConfiguration` — Spring configuration that imports Strata's `HealthCheckConfiguration`

- **Sample layer** (`server.sample`): `EchoFrontEndControllerService`, `EchoFrontEndControllerServiceController` and `EchoFrontEndControllerConfiguration` (see Echo Sample below).

#### Strata.FrontEndController.Client

REST client implementations:

- **Client interfaces** (Java): `IFrontEndControllerServiceClient` extends `IFrontEndControllerService`
- **Abstract base clients** (Java and TypeScript): `AbstractFrontEndControllerServiceClient` extends `AbstractRestClient` (Java) and carries base URL, SSL setup, and authorization handling. Subject clients call `doPostAsync` with their path and reply type. The Java clients append the service path (e.g. `frontendcontroller-service/`) to the base URL; the TypeScript clients post relative to `baseUrl`, so TS callers include the service path in `baseUrl`.
- **Sample clients** (`client.sample`, TypeScript `Sample/`): `IEchoFrontEndControllerServiceClient` and `EchoFrontEndControllerServiceClient`
- **TypeScript mirror**: `IFrontEndControllerServiceClient` and `AbstractFrontEndControllerServiceClient` (`setAuthorization`, `close`, protected `initializeRequest`)

### Applications

- **FrontEndControllerServiceHost** — Spring Boot application that assembles the service-side stack. `HostConfiguration` imports `EchoFrontEndControllerConfiguration`, which hosts the Echo sample subject. Listens on port 8081.
- Applications that extend the framework define their own audience controllers, configurations and paths.

## Commonly Used Design Patterns

### CQRS (Command Query Responsibility Segregation)

Each subject's operations (e.g. `handleEchoInput`, `getEchoOutput`) are declared on its own service interface, implemented in its service, mapped by its controller and called through its client. The `FrontEndControllerServiceHost` application hosts them.

### Request-Reply

All service operations accept a typed request DTO and return a typed reply DTO. Requests extend `AbstractServiceRequest` (with `requestId`, `timestamp`, headers, cookies). Replies extend `AbstractServiceReply` (with `replyId`, `originatingRequestId`, `success`, `successMessage`, `failureMessage`, `exception`). Each operation has its own concrete request/reply pair that binds the generic base, e.g. `HandleEchoInputRequest extends HandleInputRequest<EchoInput>` and `HandleEchoInputReply extends HandleInputReply<EchoInput,EchoOutput>`.

### Pipeline (Chain of Responsibility)

All service operations follow an asynchronous pipeline pattern using `CompletionStage`, run by the abstract service's template helpers:

```
validate (optional per-subject validator) → mapToContext → operation → buildReply
                                                                        ↓ (on failure)
                                                                   buildReply(exception)
```

Each subject supplies its operation step and reply factory, and optionally a validator (overload taking a `UnaryOperator<Q>`):

```java
@Override
public CompletionStage<HandleEchoInputReply>
handleEchoInput(HandleEchoInputRequest request)
{
    return handleInput(request,this::echoInput,HandleEchoInputReply::new);
}
```

Without a validator, the standard `validate` is used. A per-subject validator may call the standard `validate` and then add its own checks, and it must return the request it was given (the handle success reply is built from the original request). Validation failures throw and become a failure reply. Success replies set `success=true` with a fixed success message and the output; failure replies set `success=false` with a fixed failure message and `ExceptionData`.

### Context Object

Each service pipeline uses a generic context object (`HandleInputContext<I,O>`, `GetOutputContext<R,O>`) that carries the request, the intermediate state and the output through the operation step. Context objects use fluent setters and provide convenience getters that delegate to the underlying request.

### Subject Pattern (Adding an Operation)

- Service interface: extends `IFrontEndControllerService` and declares the subject's operations.
- Service: extends `FrontEndControllerService` and implements the interface, delegating to `handleInput`/`getOutput`.
- Controller: extends `AbstractFrontEndControllerServiceController`, does **not** implement the subject interface, and maps every interface method with an annotation ending in `Mapping`.
- Mapping completeness: the subject's controller test extends `AbstractControllerMappingTest` (ServiceTest fixture), which fails and names every interface method that the controller does not map.

### Composable Configuration

Spring `@Configuration` classes are composed via `@Import`. A base `PlatformConfiguration` provides shared platform beans (health checks). `ApplicationConfiguration` provides the shared `ModelMapper` singleton. A subject configuration (e.g. `EchoFrontEndControllerConfiguration`) imports `ApplicationConfiguration` and provides its named executor, service and controller. `HostConfiguration` at the application level imports and assembles the required configurations.

## Echo Sample

The Echo subject is the reference implementation of the framework and is used by tests:

- **Service (Java)**: `strata.frontendcontroller.service.sample` — `EchoInput`, `EchoOutput`, `HandleEchoInputRequest`/`Reply`, `GetEchoOutputRequest`/`Reply`, `IEchoFrontEndControllerService`
- **Server**: `strata.frontendcontroller.server.sample` — `EchoFrontEndControllerService` (handle echoes the input text; get derives its output from the request key), `EchoFrontEndControllerServiceController`, `EchoFrontEndControllerConfiguration` (named `Echo` executor, defaulting to `ForkJoinPool.commonPool()` and overridable in tests)
- **Client**: `strata.frontendcontroller.client.sample` — `IEchoFrontEndControllerServiceClient`, `EchoFrontEndControllerServiceClient`
- **TypeScript**: `Sample/` folders with `index.ts` in the Service and Client components

## REST Endpoints

### Echo Sample Service (`/frontendcontroller-service`)

| Endpoint | Request | Reply |
|---|---|---|
| `POST /handle-echo-input` | `HandleEchoInputRequest` | `HandleEchoInputReply` |
| `POST /get-echo-output` | `GetEchoOutputRequest` | `GetEchoOutputReply` |

Applications define their own paths and endpoints for their subjects.

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

All interfaces are prefixed with `I`. Examples: `IFrontEndControllerService`, `IEchoFrontEndControllerService`, `IFrontEndControllerServiceClient`.

### Class and Method Formatting

The `class` keyword and the `implements`/`extends` clause each start on their own line. Opening braces are on their own line (Allman style). Method return types are on a separate line from the method name.

Key formatting rules:

- **File header/footer**: Every Java file begins and ends with a `//////////////////////////////////////////////////////////////////////////////` comment. The second line contains the filename as a comment.
- **`class` on its own line**: The access modifier (`public`) is on one line, and `class ClassName` is on the next line. `extends` and `implements` clauses are indented on subsequent lines.
- **Method return type on its own line**: The access modifier and return type are on one line, and the method name with parameters is on the next line.
- **Allman brace style**: Opening braces `{` are on their own line for classes, methods, constructors, and control structures.
- **Compact getters**: Simple getters that return a single field use an inline body on the same line as the method name: `getFoo() { return foo; }`.
- **Column alignment**: Related field declarations are column-aligned by type and name (e.g., `private final ModelMapper       mapper;` and `private final Executor          executor;`).
- **Constructor field alignment**: Assignment statements in constructors are column-aligned on the `=` sign.
- **Minimal space after commas**: Method calls and declarations use `(param1,param2)` with no space after commas in most cases, or a single space after commas when it improves readability.

### Fluent Setters and Fluent Methods

All setters return `this` (or the concrete subclass type) to enable method chaining. This applies to request, reply, context, and data classes. When overriding base class setters, the return type is narrowed to the concrete class via a cast:

```java
@Override
public HandleEchoInputReply
setSuccess(boolean success)
{
    return (HandleEchoInputReply)super.setSuccess(success);
}
```

Fluent style is also used for context mutation methods and builder patterns:

```java
return
    new HandleEchoInputReply(request)
        .setSuccess(true)
        .setSuccessMessage("input handled successfully")
        .setOutput(context.getOutput());
```

### Method Organization

Within a class, methods are grouped by access level: public methods first, then protected, then private. Within each group, methods follow the declaration order of the interface or base class they implement. For service implementations, the order is: public service methods, then protected pipeline steps (`validate`, `mapToContext`, operation methods, `buildReply`), then private helper methods.

### Stream vs. Loop Style

Prefer explicit `for` loops for simple iteration. Use streams with `Collectors` for set-difference, mapping, and collecting operations. Never use side effects inside stream `map` or `filter` operations — derive intermediate collections in separate stream pipelines instead.

### Validation

Each pipeline starts with a `validate` step. The standard `validate` methods are overridable in the abstract service. Per-subject validators are passed to the template helpers and may call the standard `validate` first. Use `Objects.requireNonNull` for required fields and throw `IllegalArgumentException` for empty collections.

### TypeScript Array Types

Use `Array<Type>` syntax rather than `Type[]` in all TypeScript code.
