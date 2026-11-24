# Strata.FrontEndController: Typed Front-End Operations

**Date:** 2026-10-26
**Branch:** `typed-frontend-operations` (from `development`)
**Source analysis:** `C:\Development\Granite\docs\design\2026-10-26-frontendcontroller-handleinput-design.md` (Option C)

## Intent

Replace the shared `handle-input`/`get-output` endpoints (abstract payloads identified by Jackson `DEDUCTION`, routed by a `source->command->destination` key through an `IRequestProcessor`) with **typed per-subject operations**. Strata becomes a framework layer; applications (e.g. Granite) extend it:

- `IFrontEndControllerService` is extended by application sub-interfaces that declare `handle<Subject>Input` / `get<Subject>Output` methods.
- `FrontEndControllerService` becomes an **abstract base class** holding a generic, reusable pipeline; applications extend it and implement their typed methods with minimal code.

Why: explicit contract and OpenAPI, no type deduction, path-based gateway authorization, per-operation logs/metrics, targeted validation (see source analysis).

## Decisions (made by the user)

1. **Remove:** `handle-input`/`get-output` endpoints, `IRequestProcessor` (and `DefaultRequestProcessor`, `IProcessAction`, `IHandleInputAction`, `IGetOutputAction`, `ICommand`), and `@JsonTypeInfo(use = DEDUCTION)`.
2. **Keep:** the `source->command->destination` key (`KeyBuilder`) — used **for logging only**; `FrontEndInput` and `FrontEndOutput` as abstract base classes.
3. **Approach A:** generic contexts plus generic template helper methods in the abstract `FrontEndControllerService` (functional parameters for the operation step and reply factory).
4. **Contexts:** `HandleInputContext<I,O>` and `GetOutputContext<R extends GetOutputRequest,O>`.
5. **`HandleInputReply<I,O>` echoes the input** back. Applications are responsible for marking sensitive input fields write-only.
6. **Platform layer:** keep only an abstract base controller; remove Admin/User controllers and Admin/User configurations. Applications define audience controllers/configurations and paths.
7. **Clients:** Java and TypeScript clients become abstract bases. **ServiceHost is kept** and hosts a sample subject.
8. **Keep** `FrontEndInputMapper` / `FrontEndOutputMapper` (Java and TS), adapted to the new generic types.
9. **Sample subject** is named **Echo** and lives in `sample` packages inside the existing Components (no new Gradle modules, no `settings.gradle` change).
10. **Tests:** a concrete subject (Echo) exercises the framework; the ServiceTest fixtures include a mapping-completeness test pattern.

## Answer: is `IFrontEndControllerService` an empty tag interface?

**Yes — an empty marker interface.** Any method declared on it must be implemented by every subject's service, controller and client, and the only signature that fits every subject is one taking a generic/abstract payload — which is exactly the shared-endpoint design being removed (deduction, key routing, opaque paths). What *is* common across subjects is structural, not an operation, and is placed where it doesn't force a shared endpoint:

- generic base request/reply DTOs (`HandleInputRequest<I>`, `HandleInputReply<I,O>`, `GetOutputRequest`, `GetOutputReply<O>`),
- generic contexts and the template pipeline in the abstract `FrontEndControllerService`,
- the abstract controller and abstract clients.

The marker still earns its keep: it is the bound for `AbstractFrontEndControllerServiceController<S extends IFrontEndControllerService>`, the base of `IFrontEndControllerServiceClient`, and the root type for the mapping-completeness test.

## Design

### Strata.FrontEndController.Service (`strata.frontendcontroller.service.requestreply`)

| Type | Shape |
|---|---|
| `IFrontEndControllerService` | Empty marker interface. |
| `FrontEndInput` | Abstract. Fields `userId`, `source`, `command`, `destination`; `@JsonIgnore getKey()`. Remove `@JsonTypeInfo`; keep `@JsonIgnoreProperties(ignoreUnknown = true)`. |
| `FrontEndOutput` | Abstract. Same fields. Remove `@JsonTypeInfo`. |
| `HandleInputRequest<I extends FrontEndInput>` | Abstract, extends `AbstractServiceRequest`. Field `I input`; `setInput(I)`, `getInput()`. Overrides base fluent setters returning `HandleInputRequest<I>`. |
| `HandleInputReply<I extends FrontEndInput,O extends FrontEndOutput>` | Abstract, extends `AbstractServiceReply`. Fields `I input`, `O output`. Constructor `(HandleInputRequest<I> originatingRequest)` copies the input. |
| `GetOutputRequest` | Abstract, extends `AbstractServiceRequest`. Fields `userId`, `source`, `command`, `destination`; `@JsonIgnore getKey()`. |
| `GetOutputReply<O extends FrontEndOutput>` | Abstract, extends `AbstractServiceReply`. Field `O output`. |
| `FrontEndInputMapper`, `FrontEndOutputMapper` | Kept; parameter types generalized to `HandleInputRequest<?>`, `HandleInputReply<?,?>`, `GetOutputReply<?>`. |
| `KeyBuilder` | Unchanged. |

Each subject defines **concrete** subclasses (e.g. `HandleEchoInputRequest extends HandleInputRequest<EchoInput>`) so Jackson binds `I`/`O` from the subclass's type arguments — no type id is needed. Concrete subclasses override fluent setters to narrow the return type (project convention).

### Strata.FrontEndController.Server

**`server.application`**

- `FrontEndControllerService` — `public abstract class`, `implements IFrontEndControllerService`. Constructor `(ModelMapper mapper,Executor executor)`; `protected` getters for mapper and executor. Provides two protected generic template helpers (signatures indicative; builder may refine type-parameter letters):

  ```java
  protected <I extends FrontEndInput,
             O extends FrontEndOutput,
             Q extends HandleInputRequest<I>,
             P extends HandleInputReply<I,O>> CompletionStage<P>
  handleInput(
      Q                                                                          request,
      Function<HandleInputContext<I,O>,CompletionStage<HandleInputContext<I,O>>> operation,
      Function<Q,P>                                                              replyFactory)

  protected <Q extends GetOutputRequest,
             O extends FrontEndOutput,
             P extends GetOutputReply<O>> CompletionStage<P>
  getOutput(
      Q                                                                        request,
      Function<GetOutputContext<Q,O>,CompletionStage<GetOutputContext<Q,O>>>   operation,
      Function<Q,P>                                                            replyFactory)
  ```

  Each runs `supplyAsync(validate, executor) → mapToContext → operation → buildReply`, with `exceptionally → buildReply(exception)`. Pipeline steps (`validate`, `mapToContext`, `buildReply`) are protected generic methods. Log lines include the request id and the key.

  - Standard validation lives in protected, overridable generic helpers: `validate(HandleInputRequest<I>)` (`input` non-null) and `validate(GetOutputRequest)` (`source` non-null). **`userId` is no longer required** by default. A subclass overrides these to change validation for all of its subjects.
  - Each template helper has an overload taking a per-subject validator (`UnaryOperator<Q> validator`) in place of the standard one; the overload without it uses the standard `validate`. A subject validator may call the standard `validate` and then add its own checks. Validation failures throw → failure reply.

  ```java
  return handleInput(request,this::validateEchoInput,this::handleEchoInput,HandleEchoInputReply::new);
  ```
  - Success reply: `success=true`, message `"input handled successfully"` / `"output retrieved successfully"`, output set. Failure reply: `success=false`, `"Unable to handle input"` / `"Unable to get output"`, `ExceptionData.of(e)`.

  Subject usage:

  ```java
  @Override
  public CompletionStage<HandleEchoInputReply>
  handleEchoInput(HandleEchoInputRequest request)
  {
      return handleInput(request,this::handleEchoInput,HandleEchoInputReply::new);
  }

  protected CompletionStage<HandleInputContext<EchoInput,EchoOutput>>
  handleEchoInput(HandleInputContext<EchoInput,EchoOutput> context) { ... }
  ```

- `HandleInputContext<I extends FrontEndInput,O extends FrontEndOutput>` — holds `HandleInputRequest<I>` and `O output`; `setOutput(O)`, `getRequest()`, `getInput()`, `getKey()`, `getOutput()`.
- `GetOutputContext<R extends GetOutputRequest,O extends FrontEndOutput>` — holds `R request` and `O output`; `setOutput(O)`, `getRequest()`, `getUserId()`, `getSource()`, `getCommand()`, `getDestination()`, `getKey()`, `getOutput()`.
- `ApplicationConfiguration`, `MappingModule` — unchanged.
- **Deleted:** `IRequestProcessor`, `DefaultRequestProcessor`, `IProcessAction`, `IHandleInputAction`, `IGetOutputAction`, `ICommand`.

**`server.platform`**

- `AbstractFrontEndControllerServiceController<S extends IFrontEndControllerService>` — holds `S` service and a logger; `protected S getService()`. No mappings.
- `PlatformConfiguration` — unchanged.
- **Deleted:** `AdminFrontEndControllerServiceController`, `UserFrontEndControllerServiceController`.

**`server.configuration`** — **deleted** (`AdminFrontEndControllerConfiguration`, `UserFrontEndControllerConfiguration`).

Convention for subject controllers: the controller **does not implement the subject interface** (avoids a second bean of the interface type and leaves controller signatures free to take HTTP-specific parameters). Every interface method must nonetheless be mapped in the controller; this is enforced by the mapping-completeness test, which every subject's tests must include.

### Strata.FrontEndController.Client

- Java `IFrontEndControllerServiceClient extends IFrontEndControllerService` — `setAuthorizationHeader(String)`, `close()`.
- Java `AbstractFrontEndControllerServiceClient` — abstract, `extends AbstractRestClient implements IFrontEndControllerServiceClient`. Constructor `(String baseUrl,String servicePath)`; carries existing base-URL normalisation, SSL setup and `setAuthorizationHeader`. Subject clients call `doPostAsync(path,ReplyType.class,request)`.
- TS `IFrontEndControllerServiceClient` and abstract `AbstractFrontEndControllerServiceClient` (`setAuthorization`, `close`, protected `initializeRequest`). The concrete `FrontEndControllerServiceClient` is removed in both languages.

### TypeScript mirror (Service)

`RequestReply/`: `FrontEndInput`, `FrontEndOutput` (unchanged), `HandleInputRequest<I extends FrontEndInput>`, `HandleInputReply<I extends FrontEndInput,O extends FrontEndOutput>`, `GetOutputRequest` (`userId` optional, `source`, `command`, `destination`), `GetOutputReply<O extends FrontEndOutput>`, generic `HandleInputRequestBuilder<I>`, `GetOutputRequestBuilder` (adds `command`), empty `IFrontEndControllerService`, mappers adapted to generics. `Array<T>` syntax throughout.

### Echo sample subject

| Module | Package / folder | Types |
|---|---|---|
| Service (Java) | `strata.frontendcontroller.service.sample` | `EchoInput` (`text`), `EchoOutput` (`text`), `HandleEchoInputRequest`, `HandleEchoInputReply`, `GetEchoOutputRequest`, `GetEchoOutputReply`, `IEchoFrontEndControllerService` (`handleEchoInput`, `getEchoOutput`) |
| Service (TS) | `Sample/` + `index.ts` | Mirrors of the above |
| Server | `strata.frontendcontroller.server.sample` | `EchoFrontEndControllerService` (handle: output text = input text; get: output text derived from the request key), `EchoFrontEndControllerServiceController` (`@RestController`, `@RequestMapping("/frontendcontroller-service")`, `POST /handle-echo-input`, `POST /get-echo-output`), `EchoFrontEndControllerConfiguration` (imports `ApplicationConfiguration`; `@Named("Echo")` executor bean defaulting to `ForkJoinPool.commonPool()`, overridable in tests; request-scoped service and controller beans) |
| Client (Java) | `strata.frontendcontroller.client.sample` | `IEchoFrontEndControllerServiceClient`, `EchoFrontEndControllerServiceClient` (service path `frontendcontroller-service/`) |
| Client (TS) | `Sample/` + `index.ts` | Mirrors of the above |

TS subpath imports resolve by folder (`strata.frontendcontroller.service/Sample`); no `package.json` change is needed.

### ServiceHost

`HostConfiguration` imports `EchoFrontEndControllerConfiguration` instead of the Admin/User configurations; OpenAPI `packagesToScan` adds `strata.frontendcontroller.server.sample`. Port 8081 unchanged.

### Tests

- **ServiceTest fixtures** (no Spring dependency available):
  - `EchoFrontEndControllerServiceTest` (abstract, replaces `FrontEndControllerServiceTest`): handle with null input → failure; handle with text → success, output text equals input text, input echoed; get with source → success; get without source → failure.
  - `AbstractControllerMappingTest` (mapping-completeness pattern): given a service interface and a controller class, for every method of the interface (including inherited sub-interface methods, excluding `Object` methods) the controller declares or inherits a public method with the same name, the same parameter types and a compatible return type, carrying an annotation whose simple name ends in `Mapping`. Failures name every unmapped method. Reflection only (no Spring dependency in the fixture).
- **ServerTest:**
  - `EchoFrontEndControllerServiceServerTest` extends the fixture; a test configuration subclasses `EchoFrontEndControllerConfiguration` overriding the executor with `CurrentThreadExecutor`.
  - `EchoControllerMappingTest` extends the mapping fixture.
  - `FrontEndJsonSerializationTest` rewritten: concrete request/reply round trips bind concrete input/output types with no type property.
  - `ModelMapperTest` kept.
  - **Deleted:** `DefaultRequestProcessorTest`, `TestRequestProcessor`, Admin/User configuration tests and `TestAdminConfiguration`/`TestUserConfiguration`. `TestInput`/`TestOutput` removed unless still useful.
- **ClientTest:** `EchoFrontEndControllerServiceClientTest` (IntegrationStage) against `https://localhost:8081`.

## Constraints

- Follow `CLAUDE.md` and the vault's Code Formatting Rules (headers/footers, Allman braces, return type on its own line, column alignment, fluent setters with narrowed overrides, method ordering, no explanatory comments, no static inner classes, `Array<T>` in TS).
- No changes to Gradle/Yarn tooling or `settings.gradle`; no build-file dependency changes are expected.
- The user builds and tests; the agent does not run builds, start daemons, or commit unless asked.
- Update the project `CLAUDE.md` to describe the new architecture.

## Non-goals

- Migrating Granite (downstream) — separate work.
- Gateway rules, OpenAPI client generation, versioning schemes.

## Left to the builder

- Exact generic type-parameter letters and helper overloads.
- Echo `getEchoOutput` output content.
- Whether `TestInput`/`TestOutput` survive.
