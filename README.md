# Strata.FrontEndController

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Snapshot](https://img.shields.io/badge/snapshot-1.0.0--SNAPSHOT-orange.svg)]()

Front-end controller microservice for the Strata Framework Set. Strata.FrontEndController exposes a request/reply REST API that accepts input from client applications and returns output to them, routing each request to an application-defined action by key. Actions run asynchronously on an `Executor`; the service persists nothing and emits no events.

## Purpose
- Provide a unified developer experience across popular frameworks, libraries, and language platforms.
- Reduce impedance mismatch between different technologies.
- Facilitate rapid development of enterprise-grade applications with best practices and design patterns.

## Features

- **Request/Reply REST API**: `handle-input` and `get-output` operations with typed request and reply DTOs
- **Audience-Specific Services**: separate user and admin services, each with its own request processor and executor
- **Action Routing**: requests are routed by a `source->command->destination` key to `IHandleInputAction` and `IGetOutputAction` implementations
- **Asynchronous Pipeline**: every operation runs a `CompletionStage` pipeline (`validate → mapToContext → operation → buildReply`) on virtual threads
- **Polymorphic Payloads**: `FrontEndInput` and `FrontEndOutput` subtypes are serialized and deserialized by deduction
- **Java and TypeScript Clients**: REST clients for both platforms, sharing the same request/reply contract
- **Container Images**: the ServiceHost and the integration test suite are built as OCI images with Jib

## Architecture

The Strata.FrontEndController microservice follows a layered architecture with a shared service contract, a server implementation, and clients:

![Strata FrontEndController Components](Strata-FrontEndController-Components.png)

Each component builds upon the shared service contract while providing specialized functionality for the server, the clients, and the ServiceHost application.

## Components

### Strata.FrontEndController.Service

The shared contract component. It contains no implementation logic.

**Java Modules:**
- `strata.frontendcontroller.service.requestreply` - Service contract and data transfer objects
  - IFrontEndControllerService interface for the `handleInput` and `getOutput` operations
  - HandleInputRequest/HandleInputReply and GetOutputRequest/GetOutputReply request/reply pairs
  - FrontEndInput and FrontEndOutput abstract, polymorphic payloads
  - FrontEndInputMapper and FrontEndOutputMapper for payload mapping
  - KeyBuilder for building `source->command->destination` action keys

**TypeScript Modules:**
- `RequestReply` - TypeScript mirror of the service contract
  - IFrontEndControllerService, FrontEndInput, FrontEndOutput
  - HandleInputRequest, HandleInputRequestBuilder, HandleInputReply
  - GetOutputRequest, GetOutputRequestBuilder, GetOutputReply
  - FrontEndInputMapper, FrontEndOutputMapper

### Strata.FrontEndController.Client

REST clients for the service.

**Java Modules:**
- `strata.frontendcontroller.client.requestreply` - REST client
  - IFrontEndControllerServiceClient interface
  - FrontEndControllerServiceClient extending AbstractRestClient, targeting the user service by default or the admin service via `ADMIN_SERVICE_PATH`

**TypeScript Modules:**
- `RequestReply` - TypeScript REST client
  - IFrontEndControllerServiceClient interface
  - FrontEndControllerServiceClient extending AbstractRestClient

### Strata.FrontEndController.Server

The server-side implementation.

**Java Modules:**
- `strata.frontendcontroller.server.application` - Service implementation
  - FrontEndControllerService implementing IFrontEndControllerService
  - HandleInputContext and GetOutputContext pipeline context objects
  - IRequestProcessor and DefaultRequestProcessor for routing requests to actions by key
  - IProcessAction, IHandleInputAction and IGetOutputAction action contracts
  - MappingModule and ApplicationConfiguration for ModelMapper and service wiring
- `strata.frontendcontroller.server.platform` - Spring REST controllers
  - AbstractFrontEndControllerServiceController exposing `POST /handle-input` and `POST /get-output`
  - UserFrontEndControllerServiceController and AdminFrontEndControllerServiceController
  - PlatformConfiguration importing the Strata health checks
- `strata.frontendcontroller.server.configuration` - Audience configurations
  - UserFrontEndControllerConfiguration and AdminFrontEndControllerConfiguration, whose request processor and executor beans are overridden by the hosting application

### Strata.FrontEndController.ServiceHost

Spring Boot application that assembles the service-side stack and listens on HTTPS port 8081.

**Java Modules:**
- `strata.frontendcontroller.servicehost.main` - Application host
  - FrontEndControllerServiceHost Spring Boot entry point
  - HostConfiguration importing HostUserConfiguration, HostAdminConfiguration and ServiceConfiguration
  - HostUserConfiguration and HostAdminConfiguration supplying virtual-thread executors and request processors built from qualified `IProcessAction` beans
  - HostRequestProcessor, a DefaultRequestProcessor over the registered actions
  - IntegrationTestConfiguration, active under the `integration-test` profile, registering the action used by the integration tests

## REST Endpoints

### FrontEndController User/Admin Services (`/frontendcontroller-user-service`, `/frontendcontroller-admin-service`)

| Endpoint | Request | Reply |
|---|---|---|
| `POST /handle-input` | `HandleInputRequest` | `HandleInputReply` |
| `POST /get-output` | `GetOutputRequest` | `GetOutputReply` |

## Installation

The current development build is `1.0.0-SNAPSHOT`, built against Strata.Foundation, Strata.Client and Strata.Server **1.0.0** (see [Versioning](#versioning)).

### Gradle

Add the following dependencies to your `build.gradle`:

```gradle
dependencies {
    implementation 'strata.frontendcontroller:strata-frontendcontroller-service:1.0.0-SNAPSHOT'
    implementation 'strata.frontendcontroller:strata-frontendcontroller-client:1.0.0-SNAPSHOT'
    implementation 'strata.frontendcontroller:strata-frontendcontroller-server:1.0.0-SNAPSHOT'
}
```

### Maven

Add the following dependencies to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>strata.frontendcontroller</groupId>
        <artifactId>strata-frontendcontroller-service</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>strata.frontendcontroller</groupId>
        <artifactId>strata-frontendcontroller-client</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>strata.frontendcontroller</groupId>
        <artifactId>strata-frontendcontroller-server</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### TypeScript (npm / yarn)

The TypeScript packages `strata.frontendcontroller.service` and `strata.frontendcontroller.client` are published to npmjs.org. Install the development snapshot by dist-tag:

```bash
npm install strata.frontendcontroller.service@snapshot-1.0.0 strata.frontendcontroller.client@snapshot-1.0.0
# or
yarn add strata.frontendcontroller.service@snapshot-1.0.0 strata.frontendcontroller.client@snapshot-1.0.0
```

### Docker Images

The FrontEndController ServiceHost and the integration test suite are published to GitHub Container Registry:

```bash
docker pull ghcr.io/johnliebenau/strata/strata-frontendcontroller-servicehost:1.0.0-<yyyyMMddHHmmss>
docker pull ghcr.io/johnliebenau/strata/strata-frontendcontroller-testsuite:1.0.0-<yyyyMMddHHmmss>
```

The ServiceHost listens on HTTPS port 8081 and needs `PROPERTIES_ENCRYPTION_KEY`. Set `SPRING_PROFILES_ACTIVE=integration-test` to register the action used by the integration tests.

### Repository Configuration

This package is published to GitHub Packages. Add the repository to your build configuration:

```gradle
repositories {
    maven {
        name "GitHubPackages"
        url "https://maven.pkg.github.com/StrataFrameworkSet/repository"
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
}
```

## Versioning

| | Development snapshot | Release |
|---|---|---|
| Maven jars | `X.Y.0-SNAPSHOT` (e.g. `1.0.0-SNAPSHOT`) | `X.Y.0` (e.g. `1.0.0`) |
| npm packages | `X.Y.0-snapshot.<timestamp>`, dist-tag `snapshot-X.Y.0` | `X.Y.0-release.<timestamp>`, dist-tags `release-X.Y.0` and `latest` |
| Docker images | `X.Y.0-<yyyyMMddHHmmss>` (one per pipeline run) | `X.Y.0` |

- `.github/workflows/build-pipeline.yml` runs on `development` and `release-*`. Its stages are:
  - **commit**: build and test, push the ServiceHost and testsuite images, and publish the npm snapshots
  - **integration**: run the testsuite image's `IntegrationStage` tests against the ServiceHost image
  - **acceptance**: a placeholder
  - **deploy**: publish the snapshot jars to GitHub Packages
- A release is made on a `release-X.Y` branch:
  - its last snapshot is promoted to the release version with `strata-frontendcontroller-promote.ps1` / `strata-frontendcontroller-promote.sh` (run with `--dry-run` / `-DryRun` first). The jars are republished as `X.Y.0`, the npm packages are published as `X.Y.0-release.<timestamp>` with their `strata.*` dependencies pinned to exact release versions, and the newest `X.Y.0-<timestamp>` images (or `--image-tag` / `-ImageTag`) are tagged `X.Y.0`
  - `development` then moves on to the next minor version

## Usage

### 1. Registering Actions in a ServiceHost

Actions are Spring beans. The bean name is the action key (`source`, `source->command` or `source->command->destination`), and the `@Qualifier` selects the audience:

```java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import strata.frontendcontroller.server.application.IGetOutputAction;
import strata.frontendcontroller.server.application.IHandleInputAction;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

@Configuration
public
class CustomerActionConfiguration
{
    @Bean("customer-form->load")
    @Qualifier("User")
    public IGetOutputAction
    loadCustomerForm(ICustomerService service)
    {
        return
            (GetOutputRequest request) ->
                service
                    .getCustomer(request.getUserId())
                    .thenApply(customer -> new CustomerFormOutput().setCustomer(customer));
    }

    @Bean("customer-form->save")
    @Qualifier("User")
    public IHandleInputAction
    saveCustomerForm(ICustomerService service)
    {
        return
            (HandleInputRequest request) ->
                service
                    .saveCustomer((CustomerFormInput)request.getInput())
                    .thenApply(customer -> new CustomerFormOutput().setCustomer(customer));
    }
}
```

`HostUserConfiguration` and `HostAdminConfiguration` collect the actions qualified `"User"` and `"Admin"` into each audience's request processor. A request whose key has no action fails with `No action found for key: <key>`.

### 2. Calling the Service from Java

```java
import strata.frontendcontroller.client.requestreply.FrontEndControllerServiceClient;
import strata.frontendcontroller.service.requestreply.GetOutputReply;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;

FrontEndControllerServiceClient user =
    new FrontEndControllerServiceClient("https://localhost:8081");

FrontEndControllerServiceClient admin =
    new FrontEndControllerServiceClient(
        "https://localhost:8081",
        FrontEndControllerServiceClient.ADMIN_SERVICE_PATH);

GetOutputReply reply =
    user
        .getOutput(
            new GetOutputRequest()
                .setUserId(1L)
                .setSource("customer-form")
                .setCommand("load"))
        .toCompletableFuture()
        .join();

if (!reply.isSuccess())
    System.err.println(reply.getFailureMessage());
```

### 3. Calling the Service from TypeScript

```typescript
import {GetOutputReply} from "strata.frontendcontroller.service/RequestReply";
import {GetOutputRequestBuilder} from "strata.frontendcontroller.service/RequestReply";
import {FrontEndControllerServiceClient} from "strata.frontendcontroller.client/RequestReply";

const client: FrontEndControllerServiceClient =
    new FrontEndControllerServiceClient("https://localhost:8081/frontendcontroller-user-service");

client
    .getOutput(
        new GetOutputRequestBuilder()
            .setUserId(1)
            .setSource("customer-form")
            .build())
    .thenAccept(
        (reply: GetOutputReply) =>
        {
            if (!reply.success)
                console.log(reply.failureMessage);
        });
```

The TypeScript client appends each operation to its base URL, so the base URL includes the service path.

### 4. Defining Inputs and Outputs

Inputs and outputs extend `FrontEndInput` and `FrontEndOutput`. They are serialized with `JsonTypeInfo.Id.DEDUCTION`, so each subtype needs a distinct set of properties and must be registered with the `ObjectMapper` on both sides (see `FrontEndJsonSerializationTest`):

```java
public
class CustomerFormOutput
    extends FrontEndOutput
{
    private Customer customer;

    public CustomerFormOutput
    setCustomer(Customer c)
    {
        customer = c;
        return this;
    }

    public Customer
    getCustomer() { return customer; }
}

mapper.registerSubtypes(CustomerFormInput.class,CustomerFormOutput.class);
```

## Testing

Tests are tagged by pipeline stage:

- **CommitStage**: unit and in-process tests, run by `./gradlew build`
- **IntegrationStage**: `FrontEndControllerServiceTest` subclasses, run by the testsuite image against a running ServiceHost started with `SPRING_PROFILES_ACTIVE=integration-test`

```bash
docker run --rm --network host -e PROPERTIES_ENCRYPTION_KEY \
    ghcr.io/johnliebenau/strata/strata-frontendcontroller-testsuite:1.0.0-<yyyyMMddHHmmss> \
    --package=strata.frontendcontroller --tags=IntegrationStage
```

## Building

### Prerequisites

- Java 23 (Amazon Corretto)
- Node.js 20 or higher
- Yarn package manager
- Gradle (via the included wrapper)

### Build Commands

```bash
# Build and test all Java components (images are pushed only when BUILD_IMAGE_TAG is set)
./gradlew build

# Build the TypeScript components (also publishes npm snapshots, dist-tag snapshot-X.Y.0)
yarn build

# Clean TypeScript build artifacts
yarn clean

# Publish to local repository
./gradlew publishToMavenLocal

# Publish to GitHub Packages (requires credentials)
./gradlew publish
```

### Project Structure

```
Strata.FrontEndController/
├── Applications/
│   └── Strata.FrontEndController.ServiceHost/    # Spring Boot application for service operations
├── Components/
│   ├── Strata.FrontEndController.Service/        # Shared interfaces and DTOs (Java + TypeScript)
│   ├── Strata.FrontEndController.Server/         # Server-side implementation
│   └── Strata.FrontEndController.Client/         # REST client (Java + TypeScript)
├── Tests/
│   ├── Strata.FrontEndController.ServiceTest/    # Test fixtures for service interfaces
│   ├── Strata.FrontEndController.ServerTest/     # Server unit/integration tests
│   ├── Strata.FrontEndController.ClientTest/     # Client integration tests
│   └── Strata.FrontEndController.TestSuite/      # Aggregated test suite (image)
├── gradle/strata-image.gradle                    # Build image tagging for Jib
├── build.gradle                                  # Root build with dependency versions
├── settings.gradle                               # Gradle settings
├── package.json                                  # Root Yarn workspace for TypeScript builds
└── README.md                                     # This file
```

## Contributing

We welcome contributions to the Strata.FrontEndController project! Please follow these guidelines:

1. **Fork the repository** and create your feature branch from `development`
2. **Follow the coding standards** established in the existing codebase
3. **Write tests** for your changes and ensure all existing tests pass
4. **Update documentation** as needed for your changes
5. **Submit a pull request** with a clear description of your changes

### Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/StrataFrameworkSet/Strata.FrontEndController.git
   cd Strata.FrontEndController
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run tests to ensure everything works:
   ```bash
   ./gradlew test
   ```

### Code Style

- Follow Java and TypeScript naming conventions
- Use meaningful variable and method names
- Add appropriate JavaDoc/TSDoc comments for public APIs
- Maintain consistent indentation and formatting
- Write comprehensive unit tests for new functionality
- **Interface Naming**: Prefix all interfaces with "I" (e.g., `IFrontEndControllerService`, `IRequestProcessor`)
- **Method Chaining**: Setters should return reference to "this" to enable fluent method chaining
- **TypeScript Array Types**: Use `Array<Type>` rather than `Type[]`

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

For questions, issues, or contributions, please:

1. Check the [Issues](https://github.com/StrataFrameworkSet/Strata.FrontEndController/issues) page for existing questions
2. Create a new issue if your question hasn't been addressed
3. For development discussions, join our community channels

## Related Projects

- [Strata.Foundation](https://github.com/StrataFrameworkSet/Strata.Foundation) - Foundational components and utilities
- [Strata.Server](https://github.com/StrataFrameworkSet/Strata.Server) - Server-side components and utilities
- [Strata.Client](https://github.com/StrataFrameworkSet/Strata.Client) - Client-side components and utilities
- [Strata.Outbox](https://github.com/StrataFrameworkSet/Strata.Outbox) - Transactional outbox components

---

**Strata.FrontEndController** is part of the Strata Framework Set, providing an enterprise-grade front-end controller for building scalable, maintainable applications.
