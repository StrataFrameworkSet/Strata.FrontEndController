# Strata.FrontEndController

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Snapshot](https://img.shields.io/badge/snapshot-1.0.0--SNAPSHOT-orange.svg)]()

Front-end controller framework for the Strata Framework Set. Strata.FrontEndController provides the building blocks for typed, per-subject request/reply operations between client applications and a backend: generic request/reply DTOs, an asynchronous template pipeline, an abstract controller and abstract Java and TypeScript clients. Applications extend it with their own subjects (for example `handleUsersInput` / `getUsersOutput`); the bundled Echo subject is the runnable reference. The service persists nothing and emits no events.

## Purpose
- Provide a unified developer experience across popular frameworks, libraries, and language platforms.
- Reduce impedance mismatch between different technologies.
- Facilitate rapid development of enterprise-grade applications with best practices and design patterns.

## Features

- **Typed Per-Subject Operations**: each subject declares its own `handle<Subject>Input` / `get<Subject>Output` operations with concrete request and reply types, so Jackson binds payloads without type ids
- **Template Pipeline**: the abstract `FrontEndControllerService` runs every operation as `validate → mapToContext → operation → buildReply` on an `Executor`, with standard or per-subject validation and uniform success/failure replies
- **Keys for Logging and Dispatch**: inputs and output requests carry `source`, `command` and `destination`, combined by `KeyBuilder` into `source->command->destination` keys
- **Mapping Completeness**: the `AbstractControllerMappingTest` fixture fails when a controller leaves a subject operation unmapped
- **Java and TypeScript Clients**: abstract REST clients that subject clients extend with one method per operation
- **Echo Sample**: a complete subject (service, controller, configuration and clients) hosted by the ServiceHost and used by the tests
- **Container Images**: the ServiceHost and the integration test suite are built as OCI images with Jib

## Architecture

The Strata.FrontEndController framework follows a layered architecture with a shared service contract, a server-side template, and clients:

![Strata FrontEndController Components](Strata-FrontEndController-Components.png)

Applications build on the shared contract and the server template to define their own subjects, controllers, configurations and clients.

## Components

### Strata.FrontEndController.Service

The shared contract component. It contains no implementation logic.

**Java Modules:**
- `strata.frontendcontroller.service.requestreply` - Framework contract
  - IFrontEndControllerService marker interface, extended by each subject's service interface
  - `HandleInputRequest<I>`, `HandleInputReply<I,O>` (echoes the input), `GetOutputRequest` and `GetOutputReply<O>` generic base request/reply types
  - FrontEndInput and FrontEndOutput abstract payloads with `userId`, `source`, `command` and `destination`
  - FrontEndInputMapper and FrontEndOutputMapper for payload mapping
  - KeyBuilder for building `source->command->destination` keys
- `strata.frontendcontroller.service.sample` - Echo sample contract
  - EchoInput, EchoOutput, HandleEchoInputRequest/Reply, GetEchoOutputRequest/Reply and IEchoFrontEndControllerService

**TypeScript Modules:**
- `RequestReply` - TypeScript mirror of the framework contract, including the generic `HandleInputRequestBuilder<I>` and `GetOutputRequestBuilder`
- `Sample` - TypeScript mirror of the Echo contract

### Strata.FrontEndController.Server

The server-side framework.

**Java Modules:**
- `strata.frontendcontroller.server.application` - Template pipeline
  - FrontEndControllerService, the abstract base for subject services, with the generic `handleInput` / `getOutput` template methods
  - `HandleInputContext<I,O>` and `GetOutputContext<R,O>` pipeline context objects
  - MappingModule and ApplicationConfiguration providing the shared ModelMapper
- `strata.frontendcontroller.server.platform` - Spring platform layer
  - AbstractFrontEndControllerServiceController, the base for subject controllers
  - PlatformConfiguration importing the Strata health checks
- `strata.frontendcontroller.server.sample` - Echo sample server
  - EchoFrontEndControllerService, EchoFrontEndControllerServiceController and EchoFrontEndControllerConfiguration

### Strata.FrontEndController.Client

REST clients for subjects.

**Java Modules:**
- `strata.frontendcontroller.client.requestreply` - IFrontEndControllerServiceClient and AbstractFrontEndControllerServiceClient (base URL, service path, SSL setup, authorization header)
- `strata.frontendcontroller.client.sample` - IEchoFrontEndControllerServiceClient and EchoFrontEndControllerServiceClient

**TypeScript Modules:**
- `RequestReply` - IFrontEndControllerServiceClient and AbstractFrontEndControllerServiceClient
- `Sample` - IEchoFrontEndControllerServiceClient and EchoFrontEndControllerServiceClient

### Strata.FrontEndController.ServiceHost

Spring Boot application that hosts the Echo sample and listens on HTTPS port 8081.

**Java Modules:**
- `strata.frontendcontroller.servicehost.main` - Application host
  - FrontEndControllerServiceHost Spring Boot entry point
  - HostConfiguration importing EchoFrontEndControllerConfiguration and ServiceConfiguration

## REST Endpoints

### Echo Sample Service (`/frontendcontroller-service`)

| Endpoint | Request | Reply |
|---|---|---|
| `POST /handle-echo-input` | `HandleEchoInputRequest` | `HandleEchoInputReply` |
| `POST /get-echo-output` | `GetEchoOutputRequest` | `GetEchoOutputReply` |

Applications define their own paths and endpoints for their subjects.

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

The ServiceHost hosts the Echo sample subject, listens on HTTPS port 8081 and needs `PROPERTIES_ENCRYPTION_KEY`.

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

The examples below build a **Users** subject the way an application extends the framework (this is the approach Granite takes). Types prefixed `Users`, the action classes and the downstream clients are application code; everything else comes from Strata.FrontEndController. The Echo sample in this repository is the runnable reference for the same pattern.

### 1. Inputs, Outputs, Requests and Replies

Each subject defines concrete inputs and outputs, and one concrete request/reply class per operation, so Jackson binds the payload types from the class's type arguments:

```java
public
class UsersInput
    extends FrontEndInput
{
    private List<CreateUserData> usersToCreate;
    private List<UpdateUserData> usersToUpdate;
    private List<Long>           userIdsToDelete;
    // fluent setters returning UsersInput, getters
}

public
class UsersOutput
    extends FrontEndOutput
{
    private List<UserData> users;
    private List<String>   failureMessages;
    // fluent setters returning UsersOutput, getters
}

public class HandleUsersInputRequest extends HandleInputRequest<UsersInput> { ... }
public class HandleUsersInputReply   extends HandleInputReply<UsersInput,UsersOutput> { ... }
public class GetUsersOutputRequest   extends GetOutputRequest { ... }
public class GetUsersOutputReply     extends GetOutputReply<UsersOutput> { ... }
```

The reply classes provide a constructor taking the originating request, which the template uses as the reply factory (`HandleUsersInputReply::new`).

### 2. Service Interface

```java
public
interface IUsersFrontEndControllerService
    extends IFrontEndControllerService
{
    CompletionStage<HandleUsersInputReply>
    handleUsersInput(HandleUsersInputRequest request);

    CompletionStage<GetUsersOutputReply>
    getUsersOutput(GetUsersOutputRequest request);
}
```

### 3. Service

The service extends `FrontEndControllerService` and implements each operation with one call to the template. The operation step dispatches on the request key (`source->command->destination` for inputs, `source->destination` for output requests without a command) to named action classes:

```java
public
class UsersFrontEndControllerService
    extends    FrontEndControllerService
    implements IUsersFrontEndControllerService
{
    private final Map<String,IInputAction<UsersInput,UsersOutput>>             inputActions;
    private final Map<String,IOutputAction<GetUsersOutputRequest,UsersOutput>> outputActions;

    public
    UsersFrontEndControllerService(
        ModelMapper         mapper,
        Executor            executor,
        IUserQueryClient    queryClient,
        IUserCommandClient  commandClient,
        UsersRequestContext requestContext)
    {
        super(mapper,executor);
        inputActions =
            Map.of(
                "User.All->Save->UsersEditableView",new SaveUsersView(queryClient,commandClient,requestContext),
                "User.All->Delete->UsersEditableView",new DeleteUsersView(queryClient,commandClient,requestContext));
        outputActions =
            Map.of(
                "User.All->UsersEditableView",new InitializeUsersView(queryClient,requestContext));
    }

    @Override
    public CompletionStage<HandleUsersInputReply>
    handleUsersInput(HandleUsersInputRequest request)
    {
        return handleInput(request,this::dispatchInput,HandleUsersInputReply::new);
    }

    @Override
    public CompletionStage<GetUsersOutputReply>
    getUsersOutput(GetUsersOutputRequest request)
    {
        return getOutput(request,this::dispatchOutput,GetUsersOutputReply::new);
    }

    protected CompletionStage<HandleInputContext<UsersInput,UsersOutput>>
    dispatchInput(HandleInputContext<UsersInput,UsersOutput> context)
    {
        IInputAction<UsersInput,UsersOutput> action = inputActions.get(context.getKey());

        if (action == null)
            return CompletableFuture.failedFuture(new IllegalArgumentException("No action found for key: " + context.getKey()));

        return action.apply(context.getInput()).thenApply(context::setOutput);
    }

    protected CompletionStage<GetOutputContext<GetUsersOutputRequest,UsersOutput>>
    dispatchOutput(GetOutputContext<GetUsersOutputRequest,UsersOutput> context)
    {
        IOutputAction<GetUsersOutputRequest,UsersOutput> action = outputActions.get(context.getKey());

        if (action == null)
            return CompletableFuture.failedFuture(new IllegalArgumentException("No action found for key: " + context.getKey()));

        return action.apply(context.getRequest()).thenApply(context::setOutput);
    }
}
```

`IInputAction` (`CompletionStage<O> apply(I input)`) and `IOutputAction` (`CompletionStage<O> apply(R request)`) are application interfaces; each action is a class that calls downstream services. A missing key, a validation failure or a failed action produces a reply with `success=false`. To add subject-specific validation, pass a validator: `handleInput(request,this::validateUsersInput,this::dispatchInput,HandleUsersInputReply::new)`.

### 4. Controller

The controller extends `AbstractFrontEndControllerServiceController`, maps every operation, and does **not** implement the service interface:

```java
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("${users.frontendcontroller.service-path}")
public
class UsersFrontEndControllerServiceController
    extends AbstractFrontEndControllerServiceController<IUsersFrontEndControllerService>
{
    @Inject
    public
    UsersFrontEndControllerServiceController(@Named("Users") IUsersFrontEndControllerService service)
    {
        super(service);
    }

    @PostMapping(value = "/handle-users-input",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<HandleUsersInputReply>
    handleUsersInput(@RequestBody HandleUsersInputRequest request)
    {
        return getService().handleUsersInput(request);
    }

    @PostMapping(value = "/get-users-output",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<GetUsersOutputReply>
    getUsersOutput(@RequestBody GetUsersOutputRequest request)
    {
        return getService().getUsersOutput(request);
    }
}
```

Every subject includes a mapping-completeness test built on the ServiceTest fixture:

```java
@Tag("CommitStage")
public
class UsersControllerMappingTest
    extends AbstractControllerMappingTest
{
    @Override
    protected Class<?>
    getServiceInterface() { return IUsersFrontEndControllerService.class; }

    @Override
    protected Class<?>
    getControllerClass() { return UsersFrontEndControllerServiceController.class; }
}
```

### 5. Configuration and Request Context

The executor is a singleton; the service and controller are request-scoped. Operations run on executor threads where the HTTP request is **not** available, so anything the actions need from the request (cookies, verified token, caller identity) is captured once on the servlet thread, when the request-scoped service is created, and passed to the actions:

```java
@Configuration
@Import({ApplicationConfiguration.class})
public
class UsersFrontEndControllerConfiguration
{
    @Bean
    @Scope("singleton")
    @Named("Users")
    public Executor
    usersExecutor()
    {
        return new BoundedExecutor(200,Duration.ofSeconds(10));
    }

    @Bean
    @RequestScoped
    @Named("Users")
    public IUsersFrontEndControllerService
    usersFrontEndControllerService(
        @Named("Users")
        Executor           executor,
        ModelMapper        mapper,
        IUserQueryClient   queryClient,
        IUserCommandClient commandClient)
    {
        return
            new UsersFrontEndControllerService(
                mapper,
                executor,
                queryClient,
                commandClient,
                UsersRequestContext.fromCurrentRequest());
    }

    @Bean
    @RequestScoped
    @Named("Users")
    public UsersFrontEndControllerServiceController
    usersFrontEndControllerServiceController(@Named("Users") IUsersFrontEndControllerService service)
    {
        return new UsersFrontEndControllerServiceController(service);
    }
}
```

The host application imports the subject configurations and registers the `request` scope; `EchoFrontEndControllerConfiguration` and the ServiceHost's `HostConfiguration` show the same wiring.

### 6. Java Client

```java
public
class UsersFrontEndControllerServiceClient
    extends AbstractFrontEndControllerServiceClient
    implements IUsersFrontEndControllerServiceClient
{
    public
    UsersFrontEndControllerServiceClient(String baseUrl)
    {
        super(baseUrl,"frontendcontroller-user-service/");
    }

    @Override
    public CompletionStage<HandleUsersInputReply>
    handleUsersInput(HandleUsersInputRequest request)
    {
        return doPostAsync("handle-users-input",HandleUsersInputReply.class,request);
    }

    @Override
    public CompletionStage<GetUsersOutputReply>
    getUsersOutput(GetUsersOutputRequest request)
    {
        return doPostAsync("get-users-output",GetUsersOutputReply.class,request);
    }
}
```

### 7. TypeScript Client and Front End

The TypeScript contract mirrors the Java types as interfaces, and the client extends `AbstractFrontEndControllerServiceClient` with one method per operation:

```typescript
export
class UsersFrontEndControllerServiceClient
    extends AbstractFrontEndControllerServiceClient
    implements IUsersFrontEndControllerServiceClient
{
    constructor(baseUrl: string)
    {
        super(baseUrl);
    }

    handleUsersInput(request: HandleUsersInputRequest): ICompletionStage<HandleUsersInputReply>
    {
        return this.doPost<HandleUsersInputReply,HandleUsersInputRequest>(
            "handle-users-input",
            this.initializeRequest(request));
    }

    getUsersOutput(request: GetUsersOutputRequest): ICompletionStage<GetUsersOutputReply>
    {
        return this.doPost<GetUsersOutputReply,GetUsersOutputRequest>(
            "get-users-output",
            this.initializeRequest(request));
    }
}
```

A front end builds the client from its API base URL and the service path, and sends typed requests with the framework builders:

```typescript
const client: UsersFrontEndControllerServiceClient =
    new UsersFrontEndControllerServiceClient(apiBaseUrl + "/" + "frontendcontroller-user-service");

const input: UsersInput =
    {
        userId: currentUserId,
        source: "User.All",
        command: "Save",
        destination: "UsersEditableView",
        usersToCreate: usersToCreate,
        usersToUpdate: usersToUpdate,
        userIdsToDelete: userIdsToDelete
    };

client
    .handleUsersInput(HandleInputRequestBuilder.newBuilder<UsersInput>().setInput(input).build())
    .thenAccept(
        (reply: HandleUsersInputReply) =>
        {
            if (!reply.success)
                showError(reply.failureMessage);
            else
                showUsers(reply.output.users);
        });

client
    .getUsersOutput(
        GetOutputRequestBuilder
            .newBuilder()
            .setSource("User.All")
            .setDestination("UsersEditableView")
            .build())
    .thenAccept((reply: GetUsersOutputReply) => showUsers(reply.output.users));
```

## Testing

Tests are tagged by pipeline stage:

- **CommitStage**: run by `./gradlew build`. Covers the template pipeline (`FrontEndControllerServicePipelineTest`), JSON binding of concrete request/reply types (`FrontEndJsonSerializationTest`), ModelMapper configuration, controller mapping completeness (`EchoControllerMappingTest`), the Echo subject in process (`EchoFrontEndControllerServiceServerTest`), and the Echo subject on executor threads through its request-scoped beans (`EchoFrontEndControllerServiceAsyncTest`).
- **IntegrationStage**: `EchoFrontEndControllerServiceTest` subclasses, run by the testsuite image against a running ServiceHost.

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
- **Interface Naming**: Prefix all interfaces with "I" (e.g., `IFrontEndControllerService`, `IEchoFrontEndControllerService`)
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
