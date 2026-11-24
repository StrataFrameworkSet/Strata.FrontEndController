# Typed Front-End Operations Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace Strata.FrontEndController's shared `handle-input`/`get-output` endpoints with a framework for typed per-subject operations, demonstrated by an Echo sample subject.

**Architecture:** `IFrontEndControllerService` becomes an empty marker; generic base request/reply DTOs, generic contexts and an abstract `FrontEndControllerService` with generic template-pipeline helpers carry the shared behavior. Subjects (Echo here, Granite's later) add a sub-interface, concrete DTO subclasses, a service subclass, a controller and a client.

**Tech Stack:** Java 22, Spring Boot 3.4 / Spring 6.2, Jackson, ModelMapper 3.2, JUnit Jupiter 5, TypeScript (Yarn workspaces).

**Spec:** `docs/superpowers/specs/2026-10-26-typed-frontend-operations-design.md`

## Global Constraints

- Branch `typed-frontend-operations`. **The user builds, tests and commits.** Agents do not run Gradle/Yarn, start/stop daemons, or commit. Each task ends with a self-review and IDE diagnostics (`mcp__ide__getDiagnostics`, if available) instead of a build.
- No changes to Gradle/Yarn tooling, `settings.gradle`, `build.gradle` or `package.json` files.
- Formatting per project `CLAUDE.md` and the vault's Code Formatting Rules: 78-slash header/footer with filename on line 2 (TS `index.ts` excepted); `public` / `class X` on separate lines; return type on its own line; Allman braces; brace-less single-statement bodies; column-aligned fields and constructor assignments; fluent setters, with base-class overrides narrowed by cast; compact getters; method ordering per the vault; `(a,b)` with no space after commas; `Array<T>` in TS.
- No explanatory comments, no JavaDoc, no static inner classes.
- Package roots: `strata.frontendcontroller.service.requestreply`, `.service.sample`, `strata.frontendcontroller.server.application`, `.server.platform`, `.server.sample`, `strata.frontendcontroller.client.requestreply`, `.client.sample`.
- Validation defaults: handle → `input` non-null (`"validation failed: input was not provided"`); get → `source` non-null (`"validation failed: source was not provided"`). `userId` is **not** validated.
- Reply messages: `"input handled successfully"`, `"output retrieved successfully"`, `"Unable to handle input"`, `"Unable to get output"`.
- Echo HTTP: controller `@RequestMapping("/frontendcontroller-service")`, `POST /handle-echo-input`, `POST /get-echo-output`; client service path `frontendcontroller-service/`; host port 8081.

## Review Focus

1. The operation step **throws synchronously** instead of returning a failed stage → a failure reply, not an escaped exception (Task 4 test `testHandleInputOperationThrows`).
2. The operation step **returns a failed stage** → a failure reply carrying `ExceptionData` (Task 4 test `testGetOutputOperationFails`).
3. A **get request without `userId`** → success, because userId is no longer validated (Task 3 fixture `testGetEchoOutputWithoutUserId`).
4. **Request JSON with unknown properties** (e.g. a legacy client still sending extra fields) → ignored, concrete input bound (Task 7 `testHandleEchoInputRequestIgnoresUnknown`).
5. **A controller missing a mapping** → the mapping-completeness fixture fails and names the method (Task 7 `testIncompleteControllerDetected`).

---

### Task 1: Service base types (Java) — model: Sonnet 4.6

**Files:**
- Modify: `Components/Strata.FrontEndController.Service/src/main/java/strata/frontendcontroller/service/requestreply/{IFrontEndControllerService,FrontEndInput,FrontEndOutput,HandleInputRequest,HandleInputReply,GetOutputRequest,GetOutputReply,FrontEndInputMapper,FrontEndOutputMapper}.java`
- Unchanged: `KeyBuilder.java`

**Interfaces — Produces:**
- `public interface IFrontEndControllerService {}` (empty).
- `FrontEndInput` / `FrontEndOutput`: remove `@JsonTypeInfo` and its import; everything else unchanged.
- `public abstract class HandleInputRequest<I extends FrontEndInput> extends AbstractServiceRequest`: `HandleInputRequest<I> setInput(I)`, `I getInput()`; overrides of `setRequestId/setTimestamp/addHeader/clearHeaders/addCookie/clearCookies` return `HandleInputRequest<I>`.
- `public abstract class HandleInputReply<I extends FrontEndInput,O extends FrontEndOutput> extends AbstractServiceReply`: constructors `()` and `(HandleInputRequest<I> originatingRequest)` (copies input); `setInput(I)`, `setOutput(O)`, `getInput()`, `getOutput()`; base-setter overrides return `HandleInputReply<I,O>`.
- `public abstract class GetOutputRequest extends AbstractServiceRequest`: fields `userId`, `source`, `command`, `destination` with fluent setters/getters (as today); `@JsonIgnore public String getKey()` via `KeyBuilder.build`.
- `public abstract class GetOutputReply<O extends FrontEndOutput> extends AbstractServiceReply`: constructors `()` and `(GetOutputRequest originatingRequest)`; `setOutput(O)`, `getOutput()`.
- Mappers keep their method names; parameter types become `HandleInputRequest<?>`, `HandleInputReply<?,?>`, `GetOutputReply<?>`.

- [ ] **Step 1:** Apply the changes above.
- [ ] **Step 2:** Self-review against Global Constraints; check IDE diagnostics for this module. Callers in other modules will not compile until Tasks 4–6; that is expected.

### Task 2: Echo sample contract (Java) — model: Sonnet 4.6

**Files (create, all in `Components/Strata.FrontEndController.Service/src/main/java/strata/frontendcontroller/service/sample/`):**
`EchoInput.java`, `EchoOutput.java`, `HandleEchoInputRequest.java`, `HandleEchoInputReply.java`, `GetEchoOutputRequest.java`, `GetEchoOutputReply.java`, `IEchoFrontEndControllerService.java`

**Interfaces:**
- Consumes: Task 1 types.
- Produces:
  - `EchoInput extends FrontEndInput` with `String text` (`setText` returning `EchoInput`, `getText`). `EchoOutput extends FrontEndOutput` with the same field.
  - `HandleEchoInputRequest extends HandleInputRequest<EchoInput>`; `HandleEchoInputReply extends HandleInputReply<EchoInput,EchoOutput>` with constructors `()` and `(HandleEchoInputRequest)`; `GetEchoOutputRequest extends GetOutputRequest`; `GetEchoOutputReply extends GetOutputReply<EchoOutput>` with constructors `()` and `(GetEchoOutputRequest)`. Each overrides every inherited fluent setter to narrow the return type to itself.
  - `IEchoFrontEndControllerService extends IFrontEndControllerService`: `CompletionStage<HandleEchoInputReply> handleEchoInput(HandleEchoInputRequest request)`; `CompletionStage<GetEchoOutputReply> getEchoOutput(GetEchoOutputRequest request)`.

- [ ] **Step 1:** Create the files.
- [ ] **Step 2:** Self-review and diagnostics.

### Task 3: Service test fixtures — model: Sonnet 4.6

**Files:**
- Delete: `Tests/Strata.FrontEndController.ServiceTest/src/testFixtures/java/strata/frontendcontroller/service/requestreply/FrontEndControllerServiceTest.java`
- Create: `Tests/Strata.FrontEndController.ServiceTest/src/testFixtures/java/strata/frontendcontroller/service/sample/EchoFrontEndControllerServiceTest.java`
- Create: `Tests/Strata.FrontEndController.ServiceTest/src/testFixtures/java/strata/frontendcontroller/service/requestreply/AbstractControllerMappingTest.java`

**Interfaces:**
- Consumes: Task 2.
- Produces:
  - `@Tag("IntegrationStage") public abstract class EchoFrontEndControllerServiceTest` with `protected abstract IEchoFrontEndControllerService getSubject()`. Tests (each `.toCompletableFuture().join()`):
    - `testHandleEchoInputWithNullInput`: `setInput(null)` → `assertFalse(reply.isSuccess())`.
    - `testHandleEchoInput`: input `text="hello"`, `source="echo"`, `command="say"` → `assertTrue(reply.isSuccess(),reply.getFailureMessage())`, `assertEquals("hello",reply.getOutput().getText())`, `assertEquals("hello",reply.getInput().getText())`.
    - `testGetEchoOutput`: `setUserId(1L).setSource("echo")` → success, `assertNotNull(reply.getOutput())`.
    - `testGetEchoOutputWithoutUserId`: `setSource("echo")` only → success.
    - `testGetEchoOutputWithoutSource`: `setUserId(1L)` only → `assertFalse(reply.isSuccess())`.
  - `public abstract class AbstractControllerMappingTest` with `protected abstract Class<?> getServiceInterface()`, `protected abstract Class<?> getControllerClass()`, and `@Test public void testAllOperationsMapped()`. It collects `getServiceInterface().getMethods()` (skipping `Object`-declared methods). For each method it looks up `getControllerClass().getMethod(name,parameterTypes)`, then checks that `serviceMethod.getReturnType().isAssignableFrom(controllerMethod.getReturnType())` and that the controller method has an annotation whose `annotationType().getSimpleName().endsWith("Mapping")`. All unmapped method names are collected, and the test fails with `"unmapped operations: " + names`. Also expose `protected List<String> findUnmappedOperations(Class<?> service,Class<?> controller)` so negative tests can call it. No Spring imports; there is no `@Tag` on the abstract class, and subclasses tag themselves.

- [ ] **Step 1:** Create the fixtures. They act as the failing tests for Tasks 4–7.
- [ ] **Step 2:** Self-review and diagnostics.

### Task 4: Abstract FrontEndControllerService and generic contexts — model: Opus 5.5

**Files:**
- Modify: `Components/Strata.FrontEndController.Server/src/main/java/strata/frontendcontroller/server/application/{FrontEndControllerService,HandleInputContext,GetOutputContext}.java`
- Delete: same package `{IRequestProcessor,DefaultRequestProcessor,IProcessAction,IHandleInputAction,IGetOutputAction,ICommand}.java`
- Test: `Tests/Strata.FrontEndController.ServerTest/src/test/java/strata/frontendcontroller/server/application/FrontEndControllerServicePipelineTest.java` (create)

**Interfaces:**
- Consumes: Tasks 1–2.
- Produces:
  - `HandleInputContext<I extends FrontEndInput,O extends FrontEndOutput>`: constructor `(HandleInputRequest<I>)`; `setOutput(O)` returns `HandleInputContext<I,O>`; `getRequest()`, `getInput()`, `getKey()` (`getInput().getKey()`), `getOutput()`.
  - `GetOutputContext<R extends GetOutputRequest,O extends FrontEndOutput>`: constructor `(R)`; `setOutput(O)`; `getRequest()` returns `R`; `getUserId()`, `getSource()`, `getCommand()`, `getDestination()`, `getKey()`, `getOutput()`.
  - `public abstract class FrontEndControllerService implements IFrontEndControllerService`, with constructor `protected FrontEndControllerService(ModelMapper mapper,Executor executor)` and `protected ModelMapper getMapper()`, `protected Executor getExecutor()`. Protected helpers:
    ```java
    <I extends FrontEndInput,O extends FrontEndOutput,Q extends HandleInputRequest<I>,P extends HandleInputReply<I,O>> CompletionStage<P>
    handleInput(Q request,Function<HandleInputContext<I,O>,CompletionStage<HandleInputContext<I,O>>> operation,Function<Q,P> replyFactory)

    <I extends FrontEndInput,O extends FrontEndOutput,Q extends HandleInputRequest<I>,P extends HandleInputReply<I,O>> CompletionStage<P>
    handleInput(Q request,UnaryOperator<Q> validator,Function<HandleInputContext<I,O>,CompletionStage<HandleInputContext<I,O>>> operation,Function<Q,P> replyFactory)

    <Q extends GetOutputRequest,O extends FrontEndOutput,P extends GetOutputReply<O>> CompletionStage<P>
    getOutput(Q request,Function<GetOutputContext<Q,O>,CompletionStage<GetOutputContext<Q,O>>> operation,Function<Q,P> replyFactory)

    <Q extends GetOutputRequest,O extends FrontEndOutput,P extends GetOutputReply<O>> CompletionStage<P>
    getOutput(Q request,UnaryOperator<Q> validator,Function<GetOutputContext<Q,O>,CompletionStage<GetOutputContext<Q,O>>> operation,Function<Q,P> replyFactory)
    ```
    The three-argument forms delegate with `r -> validate(r)`. The overridable standard steps are `protected <Q extends HandleInputRequest<?>> Q validate(Q)`, `protected <Q extends GetOutputRequest> Q validate(Q)`, plus generic `mapToContext` and `buildReply` (success and exception variants, built from the factory and then populated via the fluent setters; the populated reply variable is returned). Pipeline: `supplyAsync(() -> validator.apply(request),executor) → thenApply(mapToContext) → thenCompose(operation) → thenApply(buildReply) → exceptionally(buildReply(e,request,factory))`. Debug log lines carry the request id and the key; the key log is null-safe when the input is null.

- [ ] **Step 1: Write `FrontEndControllerServicePipelineTest`** (`@Tag("CommitStage")`). It uses a test-local subclass `PipelineTestService extends FrontEndControllerService` placed in its own file in the same test package (no inner classes), with `new ModelMapper()` and `CurrentThreadExecutor`. The subclass exposes Echo-typed public methods that call the helpers with configurable operations. Tests:
  - `testHandleInputSucceeds`: output text equals input text; `getSuccessMessage()` equals `"input handled successfully"`.
  - `testHandleInputOperationThrows`: the operation throws `IllegalStateException` synchronously → `!isSuccess()`, `getFailureMessage()` equals `"Unable to handle input"`, `getException()` not null.
  - `testGetOutputOperationFails`: the operation returns `CompletableFuture.failedFuture(new IllegalStateException())` → `!isSuccess()`, failure message `"Unable to get output"`.
  - `testCustomValidatorRejects`: the validator overload throws `IllegalArgumentException` for `text == null` → `!isSuccess()`, and the operation is never invoked (assert via an `AtomicBoolean`).
  - `testStandardValidateOverridable`: a second subclass `StrictPipelineTestService` (own file) overrides `validate(GetOutputRequest)` to also require `userId` → a get without userId fails.
- [ ] **Step 2:** Implement the contexts and the service, and delete the processor/action types.
- [ ] **Step 3:** Self-review and diagnostics. The user runs the tests at the end.

### Task 5: Platform layer, Echo server sample and ServiceHost — model: Sonnet 4.6

**Files:**
- Modify: `Components/Strata.FrontEndController.Server/src/main/java/strata/frontendcontroller/server/platform/AbstractFrontEndControllerServiceController.java` (also fix its header filename)
- Delete: `.../server/platform/{AdminFrontEndControllerServiceController,UserFrontEndControllerServiceController}.java`, `.../server/configuration/{AdminFrontEndControllerConfiguration,UserFrontEndControllerConfiguration}.java`
- Create: `Components/Strata.FrontEndController.Server/src/main/java/strata/frontendcontroller/server/sample/{EchoFrontEndControllerService,EchoFrontEndControllerServiceController,EchoFrontEndControllerConfiguration}.java`
- Modify: `Applications/Strata.FrontEndController.ServiceHost/src/main/java/strata/frontendcontroller/servicehost/main/HostConfiguration.java`

**Interfaces:**
- Consumes: Tasks 2, 4.
- Produces:
  - `public abstract class AbstractFrontEndControllerServiceController<S extends IFrontEndControllerService>`: constructor `protected (S service)`, `protected S getService()`, `protected Logger getLogger()`. It has no mappings.
  - `EchoFrontEndControllerService extends FrontEndControllerService implements IEchoFrontEndControllerService`: `@Inject` constructor `(ModelMapper,Executor)`. `handleEchoInput` sets output `EchoOutput.setText(input.getText())` and copies userId/source/command/destination from the input. `getEchoOutput` sets output text to `"echo:" + context.getKey()` and copies the key fields.
  - `EchoFrontEndControllerServiceController extends AbstractFrontEndControllerServiceController<IEchoFrontEndControllerService>` (does **not** implement the interface). It is annotated `@RestController @CrossOrigin(origins = "*") @RequestMapping("/frontendcontroller-service")`, has an `@Inject` constructor taking `@Named("Echo") IEchoFrontEndControllerService`, and two methods matching the interface signatures, each `@PostMapping(value = "/handle-echo-input" | "/get-echo-output",produces = MediaType.APPLICATION_JSON_VALUE)`, returning `@ResponseBody` with an `@RequestBody` parameter.
  - `EchoFrontEndControllerConfiguration` (`@Configuration @Import({ApplicationConfiguration.class})`) with beans:
    - `echoExecutor()`: `@Scope("singleton") @Named("Echo")`, returns `ForkJoinPool.commonPool()`.
    - `echoFrontEndControllerService(@Named("Echo") Executor,ModelMapper)`: `@RequestScoped @Named("Echo")`, returns `IEchoFrontEndControllerService`.
    - `echoFrontEndControllerServiceController(@Named("Echo") IEchoFrontEndControllerService)`: `@RequestScoped @Named("Echo")`.
  - `HostConfiguration`: `@Import` replaces the User/Admin configurations with `EchoFrontEndControllerConfiguration.class`, and `packagesToScan` adds `"strata.frontendcontroller.server.sample"`.

- [ ] **Step 1:** Apply the changes above.
- [ ] **Step 2:** Self-review and diagnostics.

### Task 6: Java client — model: Sonnet 4.6

**Files:**
- Delete: `Components/Strata.FrontEndController.Client/src/main/java/strata/frontendcontroller/client/requestreply/FrontEndControllerServiceClient.java`
- Create: `.../client/requestreply/AbstractFrontEndControllerServiceClient.java`
- Modify: `.../client/requestreply/IFrontEndControllerServiceClient.java` (unchanged contract; verify it compiles against the marker)
- Create: `.../client/sample/{IEchoFrontEndControllerServiceClient,EchoFrontEndControllerServiceClient}.java`

**Interfaces:**
- Consumes: Task 2.
- Produces:
  - `public abstract class AbstractFrontEndControllerServiceClient extends AbstractRestClient implements IFrontEndControllerServiceClient`, with constructor `protected (String baseUrl,String servicePath)`. It moves over `setAuthorizationHeader`, `preprocessBaseUrl`, `createBuilder` and `createSslContext` from the deleted client unchanged; `setAuthorizationHeader` returns `AbstractFrontEndControllerServiceClient`.
  - `IEchoFrontEndControllerServiceClient extends IFrontEndControllerServiceClient,IEchoFrontEndControllerService`.
  - `EchoFrontEndControllerServiceClient extends AbstractFrontEndControllerServiceClient implements IEchoFrontEndControllerServiceClient` with constructor `(String baseUrl)` → `super(baseUrl,"frontendcontroller-service/")`. Its methods call `doPostAsync("handle-echo-input",HandleEchoInputReply.class,request)` and `doPostAsync("get-echo-output",GetEchoOutputReply.class,request)`. It overrides `setAuthorizationHeader` to narrow the return type.

- [ ] **Step 1:** Apply the changes above.
- [ ] **Step 2:** Self-review and diagnostics.

### Task 7: Server and client tests — model: Sonnet 4.6

**Files:**
- Delete (ServerTest, `src/test/java/strata/frontendcontroller/server/`): `application/{DefaultRequestProcessorTest,TestRequestProcessor,FrontEndControllerServiceServerTest,TestInput,TestOutput}.java`, `configuration/` (all four files)
- Modify: `application/TestConfiguration.java` so it extends `EchoFrontEndControllerConfiguration`. It overrides `echoExecutor()` (same annotations) to return `CurrentThreadExecutor` and keeps the `@PropertySource`, request-scope post-processor and `IConfiguration` beans. It imports `PlatformConfiguration`.
- Modify: `application/FrontEndJsonSerializationTest.java` (rewrite)
- Create: `.../server/sample/EchoFrontEndControllerServiceServerTest.java`, `.../server/sample/EchoControllerMappingTest.java`, `.../server/sample/IncompleteEchoController.java`
- Delete: `Tests/Strata.FrontEndController.ClientTest/src/test/java/strata/frontendcontroller/client/requestreply/FrontEndControllerServiceClientTest.java`
- Create: `Tests/Strata.FrontEndController.ClientTest/src/test/java/strata/frontendcontroller/client/sample/EchoFrontEndControllerServiceClientTest.java`

**Interfaces:**
- Consumes: Tasks 2–6.

- [ ] **Step 1:** `EchoFrontEndControllerServiceServerTest` (`@Tag("CommitStage")`) extends `EchoFrontEndControllerServiceTest`. It follows the old server test's lifecycle (context from `TestConfiguration`, `RequestAttributeMap`) and gets the bean via `context.getBean("echoFrontEndControllerService",IEchoFrontEndControllerService.class)`. It re-declares each fixture test with `@Test` calling `super`, as today.
- [ ] **Step 2:** `EchoControllerMappingTest` (`@Tag("CommitStage")`) extends `AbstractControllerMappingTest` with `IEchoFrontEndControllerService.class` / `EchoFrontEndControllerServiceController.class`. Add `testIncompleteControllerDetected`: `assertEquals(List.of("getEchoOutput"),findUnmappedOperations(IEchoFrontEndControllerService.class,IncompleteEchoController.class))`, where `IncompleteEchoController` maps only `handleEchoInput`.
- [ ] **Step 3:** Rewrite `FrontEndJsonSerializationTest` using `ObjectMapperSupplier`, with no `registerSubtypes`:
  - `testHandleEchoInputRequestRoundTrip`: the input is `EchoInput` with every field set; checks requestId, timestamp, text, and key fields.
  - `testHandleEchoInputRequestIgnoresUnknown`: the JSON gets an extra top-level property and an extra input property → it deserializes and `getText()` is preserved.
  - `testHandleEchoInputReplyRoundTrip`: the reply echoes input and output; both are bound as `EchoInput`/`EchoOutput`.
  - `testGetEchoOutputRequestRoundTrip` and `testGetEchoOutputReplyRoundTrip`.
  - `testNoTypeProperty`: the serialized input JSON contains no `"@type"` and no `"type"` key.
- [ ] **Step 4:** `EchoFrontEndControllerServiceClientTest` (`@Tag("IntegrationStage")`) extends `EchoFrontEndControllerServiceTest` against `new EchoFrontEndControllerServiceClient("https://localhost:8081")`, mirroring the old client test lifecycle.
- [ ] **Step 5:** Self-review and diagnostics. `ModelMapperTest` must still work with the modified `TestConfiguration`.

### Task 8: TypeScript service and client — model: Sonnet 4.6

**Files:**
- Modify (`Components/Strata.FrontEndController.Service/src/main/typescript/RequestReply/`): `IFrontEndControllerService.ts` (empty interface), `HandleInputRequest.ts` (`HandleInputRequest<I extends FrontEndInput>`, `input: I`), `HandleInputReply.ts` (`<I extends FrontEndInput,O extends FrontEndOutput>`, `input: I; output: O`), `GetOutputRequest.ts` (`userId?: number | null; source: string; command?: string | null; destination?: string | null`), `GetOutputReply.ts` (`<O extends FrontEndOutput>`), `HandleInputRequestBuilder.ts` (generic `<I extends FrontEndInput>`), `GetOutputRequestBuilder.ts` (adds `command`; `build()` includes it), `FrontEndInputMapper.ts`/`FrontEndOutputMapper.ts` (structural param types stay valid; adjust only if needed), `index.ts` (unchanged exports).
- Create (`.../typescript/Sample/`): `EchoInput.ts` (`extends FrontEndInput`, `text: string | null`), `EchoOutput.ts`, `HandleEchoInputRequest.ts` (`interface HandleEchoInputRequest extends HandleInputRequest<EchoInput> {}`), `HandleEchoInputReply.ts`, `GetEchoOutputRequest.ts`, `GetEchoOutputReply.ts`, `IEchoFrontEndControllerService.ts` (`handleEchoInput`/`getEchoOutput` returning `ICompletionStage<...>`), `index.ts`. Sample imports base types via relative `../RequestReply/...` paths.
- Client (`Components/Strata.FrontEndController.Client/src/main/typescript/`): delete `RequestReply/FrontEndControllerServiceClient.ts`. Create `RequestReply/AbstractFrontEndControllerServiceClient.ts` (`export abstract class`; constructor/headers, `setAuthorization`, `close`, and `protected initializeRequest`, all moved from the deleted class). Update `RequestReply/index.ts`. Create `Sample/{IEchoFrontEndControllerServiceClient,EchoFrontEndControllerServiceClient}.ts` and `Sample/index.ts`. The Echo client's base URL is used as given, and it posts to `"handle-echo-input"`/`"get-echo-output"` via `doPost`. It imports from `"strata.frontendcontroller.service/RequestReply"` and `"strata.frontendcontroller.service/Sample"`.
- Formatting: add header/footer to new non-index TS files, use `Array<T>`, and follow the existing import style of neighboring files.

- [ ] **Step 1:** Apply the changes above.
- [ ] **Step 2:** Self-review. Grep for leftover references to `FrontEndControllerServiceClient` (non-abstract), `handleInput(`/`getOutput(` on the service interfaces, and `DEDUCTION`.

### Task 9: Documentation — model: Haiku 4.5

**Files:** Modify `CLAUDE.md` (project).

- [ ] **Step 1:** Update Overview, Component Responsibilities, Applications, REST Endpoints and the Pipeline section to describe the marker interface, generic base DTOs/contexts, the abstract service's template helpers (with standard overridable validation and the validator overload), the abstract controller and clients, the Echo sample, and the mapping-completeness test convention. Remove the Admin/User and `IRequestProcessor` references.

### Task 10: Whole-branch review — model: Opus 5.5

- [ ] **Step 1:** A fresh reviewer checks the full branch diff against the spec and Global Constraints and lists defects. Fixes are applied, then the user is notified to build and test.
