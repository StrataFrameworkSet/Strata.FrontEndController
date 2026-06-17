//////////////////////////////////////////////////////////////////////////////
// DefaultRequestProcessorTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.frontendcontroller.service.requestreply.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class DefaultRequestProcessorTest
{
    @Test
    public void
    testDefaultConstructorCreatesEmptyProcessor()
    {
        IRequestProcessor processor = createProcessor();

        HandleInputRequest handleRequest =
            createHandleInputRequest("src","dest","cmd");

        GetOutputRequest getRequest =
            createGetOutputRequest("src","dest");

        assertThrows(
            CompletionException.class,
            () -> processor.process(handleRequest).toCompletableFuture().join());
        assertThrows(
            CompletionException.class,
            () -> processor.process(getRequest).toCompletableFuture().join());
    }

    @Test
    public void
    testConstructorWithHandleInputActionsOnly()
    {
        FrontEndOutput             expected = createOutput("handleOutput");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src->cmd->dest",action);

        IRequestProcessor processor = createProcessor(actions);

        HandleInputRequest handleRequest =
            createHandleInputRequest("src","dest","cmd");

        GetOutputRequest getRequest =
            createGetOutputRequest("src","dest");

        assertSame(expected,processor.process(handleRequest).toCompletableFuture().join());
        assertThrows(
            CompletionException.class,
            () -> processor.process(getRequest).toCompletableFuture().join());
    }

    @Test
    public void
    testConstructorWithGetOutputActionsOnly()
    {
        FrontEndOutput             expected = createOutput("getOutput");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src->dest",action);

        IRequestProcessor processor = createProcessor(actions);

        GetOutputRequest getRequest =
            createGetOutputRequest("src","dest");

        HandleInputRequest handleRequest =
            createHandleInputRequest("src","dest",null);

        assertSame(expected,processor.process(getRequest).toCompletableFuture().join());
        assertThrows(
            CompletionException.class,
            () -> processor.process(handleRequest).toCompletableFuture().join());
    }

    @Test
    public void
    testConstructorWithMixedActionTypes()
    {
        FrontEndOutput             handleExpected = createOutput("handleOutput");
        FrontEndOutput             getExpected    = createOutput("getOutput");
        IHandleInputAction         handleAction   = (HandleInputRequest r) -> CompletableFuture.completedFuture(handleExpected);
        IGetOutputAction           getAction      = (GetOutputRequest r) -> CompletableFuture.completedFuture(getExpected);
        Map<String,IProcessAction> actions        = new HashMap<>();

        actions.put("src->cmd->dest",handleAction);
        actions.put("src->dest",getAction);

        IRequestProcessor processor = createProcessor(actions);

        assertSame(
            handleExpected,
            processor.process(createHandleInputRequest("src","dest","cmd")).toCompletableFuture().join());
        assertSame(
            getExpected,
            processor.process(createGetOutputRequest("src","dest")).toCompletableFuture().join());
    }

    @Test
    public void
    testConstructorWithEmptyMap()
    {
        IRequestProcessor processor = createProcessor(new HashMap<>());

        HandleInputRequest handleRequest =
            createHandleInputRequest("src","dest","cmd");

        GetOutputRequest getRequest =
            createGetOutputRequest("src","dest");

        assertThrows(
            CompletionException.class,
            () -> processor.process(handleRequest).toCompletableFuture().join());
        assertThrows(
            CompletionException.class,
            () -> processor.process(getRequest).toCompletableFuture().join());
    }

    @Test
    public void
    testProcessHandleInputDispatchesByKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA->cmdA->destA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("sourceA","destA","cmdA")).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessHandleInputWithSourceOnlyKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("sourceA",null,null)).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessHandleInputWithSourceAndDestinationKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA->destA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("sourceA","destA",null)).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessHandleInputSelectsCorrectActionAmongMultiple()
    {
        FrontEndOutput             output1 = createOutput("first");
        FrontEndOutput             output2 = createOutput("second");
        IHandleInputAction         action1 = (HandleInputRequest r) -> CompletableFuture.completedFuture(output1);
        IHandleInputAction         action2 = (HandleInputRequest r) -> CompletableFuture.completedFuture(output2);
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("a->c->b",action1);
        actions.put("x->z->y",action2);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("x","y","z")).toCompletableFuture().join();

        assertSame(output2,actual);
    }

    @Test
    public void
    testProcessHandleInputActionReceivesRequest()
    {
        AtomicReference<HandleInputRequest> captured = new AtomicReference<>();
        FrontEndOutput                      expected = createOutput("result");
        IHandleInputAction                  action   = (HandleInputRequest r) ->
        {
            captured.set(r);
            return CompletableFuture.completedFuture(expected);
        };
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("src->cmd->dest",action);

        IRequestProcessor  processor = createProcessor(actions);
        HandleInputRequest request   = createHandleInputRequest("src","dest","cmd");

        processor.process(request).toCompletableFuture().join();

        assertSame(request,captured.get());
    }

    @Test
    public void
    testProcessHandleInputThrowsForUnregisteredKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("a->c->b",action);

        IRequestProcessor  processor = createProcessor(actions);
        HandleInputRequest request   = createHandleInputRequest("x","y","z");

        CompletionException exception = assertThrows(
            CompletionException.class,
            () -> processor.process(request).toCompletableFuture().join());

        assertTrue(exception.getCause().getMessage().contains("No action found for key: x->z->y"));
    }

    @Test
    public void
    testProcessHandleInputThrowsWhenNoActionsRegistered()
    {
        IRequestProcessor  processor = createProcessor();
        HandleInputRequest request   = createHandleInputRequest("a","b","c");

        assertThrows(
            CompletionException.class,
            () -> processor.process(request).toCompletableFuture().join());
    }

    @Test
    public void
    testProcessGetOutputDispatchesByKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA->destA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("sourceA","destA")).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessGetOutputWithSourceOnlyKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("sourceA",null)).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessGetOutputSelectsCorrectActionAmongMultiple()
    {
        FrontEndOutput             output1 = createOutput("first");
        FrontEndOutput             output2 = createOutput("second");
        IGetOutputAction           action1 = (GetOutputRequest r) -> CompletableFuture.completedFuture(output1);
        IGetOutputAction           action2 = (GetOutputRequest r) -> CompletableFuture.completedFuture(output2);
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("a->b",action1);
        actions.put("x->y",action2);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("x","y")).toCompletableFuture().join();

        assertSame(output2,actual);
    }

    @Test
    public void
    testProcessGetOutputActionReceivesRequest()
    {
        AtomicReference<GetOutputRequest> captured = new AtomicReference<>();
        FrontEndOutput                    expected = createOutput("result");
        IGetOutputAction                  action   = (GetOutputRequest r) ->
        {
            captured.set(r);
            return CompletableFuture.completedFuture(expected);
        };
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("src->dest",action);

        IRequestProcessor processor = createProcessor(actions);
        GetOutputRequest  request   = createGetOutputRequest("src","dest");

        processor.process(request).toCompletableFuture().join();

        assertSame(request,captured.get());
    }

    @Test
    public void
    testProcessGetOutputThrowsForUnregisteredKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("a->b",action);

        IRequestProcessor processor = createProcessor(actions);
        GetOutputRequest  request   = createGetOutputRequest("x","y");

        CompletionException exception = assertThrows(
            CompletionException.class,
            () -> processor.process(request).toCompletableFuture().join());

        assertTrue(exception.getCause().getMessage().contains("No action found for key: x->y"));
    }

    @Test
    public void
    testProcessGetOutputThrowsWhenNoActionsRegistered()
    {
        IRequestProcessor processor = createProcessor();
        GetOutputRequest  request   = createGetOutputRequest("a","b");

        assertThrows(
            CompletionException.class,
            () -> processor.process(request).toCompletableFuture().join());
    }

    @Test
    public void
    testHandleInputActionNotRoutedToGetOutput()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src->dest",action);

        IRequestProcessor processor = createProcessor(actions);
        GetOutputRequest  request   = createGetOutputRequest("src","dest");

        assertThrows(
            CompletionException.class,
            () -> processor.process(request).toCompletableFuture().join());
    }

    @Test
    public void
    testGetOutputActionNotRoutedToHandleInput()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src->cmd->dest",action);

        IRequestProcessor processor = createProcessor(actions);
        HandleInputRequest request  = createHandleInputRequest("src","dest","cmd");

        assertThrows(
            CompletionException.class,
            () -> processor.process(request).toCompletableFuture().join());
    }

    @Test
    public void
    testSameKeyOverwriteRetainsLastAction()
    {
        FrontEndOutput             handleExpected = createOutput("handleResult");
        FrontEndOutput             getExpected    = createOutput("getResult");
        IHandleInputAction         handleAction   = (HandleInputRequest r) -> CompletableFuture.completedFuture(handleExpected);
        IGetOutputAction           getAction      = (GetOutputRequest r) -> CompletableFuture.completedFuture(getExpected);
        Map<String,IProcessAction> actions        = new HashMap<>();

        actions.put("src->dest",handleAction);
        actions.put("src->dest",getAction);

        IRequestProcessor processor = createProcessor(actions);

        assertSame(
            getExpected,
            processor.process(createGetOutputRequest("src","dest")).toCompletableFuture().join());
        assertThrows(
            CompletionException.class,
            () -> processor.process(
                createHandleInputRequest("src","dest",null)).toCompletableFuture().join());
    }

    @Test
    public void
    testHandleInputKeyBuiltFromSourceCommandDestination()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("s->c->d",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("s","d","c")).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testGetOutputKeyBuiltFromSourceDestinationOnly()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("s->d",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("s","d")).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testGetOutputKeyWithNullDestination()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("s",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("s",null)).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    @Test
    public void
    testGetOutputKeyWithNullSource()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> CompletableFuture.completedFuture(expected);
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest(null,null)).toCompletableFuture().join();

        assertSame(expected,actual);
    }

    private static IRequestProcessor
    createProcessor()
    {
        return new TestRequestProcessor();
    }

    private static IRequestProcessor
    createProcessor(Map<String,IProcessAction> actions)
    {
        return new TestRequestProcessor(actions);
    }

    private static HandleInputRequest
    createHandleInputRequest(String source,String destination,String command)
    {
        TestInput input = new TestInput();

        input.setSource(source);
        input.setDestination(destination);
        input.setCommand(command);

        return new HandleInputRequest().setInput(input);
    }

    private static GetOutputRequest
    createGetOutputRequest(String source,String destination)
    {
        return
            new GetOutputRequest()
                .setSource(source)
                .setDestination(destination);
    }

    private static FrontEndOutput
    createOutput(String source)
    {
        TestOutput output = new TestOutput();

        output.setSource(source);
        return output;
    }
}

//////////////////////////////////////////////////////////////////////////////
