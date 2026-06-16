//////////////////////////////////////////////////////////////////////////////
// DefaultRequestProcessorTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.frontendcontroller.service.requestreply.*;

import java.util.HashMap;
import java.util.Map;
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
            IllegalArgumentException.class,
            () -> processor.process(handleRequest));
        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(getRequest));
    }

    @Test
    public void
    testConstructorWithHandleInputActionsOnly()
    {
        FrontEndOutput             expected = createOutput("handleOutput");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src.dest.cmd",action);

        IRequestProcessor processor = createProcessor(actions);

        HandleInputRequest handleRequest =
            createHandleInputRequest("src","dest","cmd");

        GetOutputRequest getRequest =
            createGetOutputRequest("src","dest");

        assertSame(expected,processor.process(handleRequest));
        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(getRequest));
    }

    @Test
    public void
    testConstructorWithGetOutputActionsOnly()
    {
        FrontEndOutput             expected = createOutput("getOutput");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src.dest",action);

        IRequestProcessor processor = createProcessor(actions);

        GetOutputRequest getRequest =
            createGetOutputRequest("src","dest");

        HandleInputRequest handleRequest =
            createHandleInputRequest("src","dest",null);

        assertSame(expected,processor.process(getRequest));
        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(handleRequest));
    }

    @Test
    public void
    testConstructorWithMixedActionTypes()
    {
        FrontEndOutput             handleExpected = createOutput("handleOutput");
        FrontEndOutput             getExpected    = createOutput("getOutput");
        IHandleInputAction         handleAction   = (HandleInputRequest r) -> handleExpected;
        IGetOutputAction           getAction      = (GetOutputRequest r) -> getExpected;
        Map<String,IProcessAction> actions        = new HashMap<>();

        actions.put("src.dest.cmd",handleAction);
        actions.put("src.dest",getAction);

        IRequestProcessor processor = createProcessor(actions);

        assertSame(
            handleExpected,
            processor.process(createHandleInputRequest("src","dest","cmd")));
        assertSame(
            getExpected,
            processor.process(createGetOutputRequest("src","dest")));
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
            IllegalArgumentException.class,
            () -> processor.process(handleRequest));
        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(getRequest));
    }

    @Test
    public void
    testProcessHandleInputDispatchesByKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA.destA.cmdA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("sourceA","destA","cmdA"));

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessHandleInputWithSourceOnlyKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("sourceA",null,null));

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessHandleInputWithSourceAndDestinationKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA.destA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("sourceA","destA",null));

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessHandleInputSelectsCorrectActionAmongMultiple()
    {
        FrontEndOutput             output1 = createOutput("first");
        FrontEndOutput             output2 = createOutput("second");
        IHandleInputAction         action1 = (HandleInputRequest r) -> output1;
        IHandleInputAction         action2 = (HandleInputRequest r) -> output2;
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("a.b.c",action1);
        actions.put("x.y.z",action2);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("x","y","z"));

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
            return expected;
        };
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("src.dest.cmd",action);

        IRequestProcessor  processor = createProcessor(actions);
        HandleInputRequest request   = createHandleInputRequest("src","dest","cmd");

        processor.process(request);

        assertSame(request,captured.get());
    }

    @Test
    public void
    testProcessHandleInputThrowsForUnregisteredKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("a.b.c",action);

        IRequestProcessor  processor = createProcessor(actions);
        HandleInputRequest request   = createHandleInputRequest("x","y","z");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(request));

        assertTrue(exception.getMessage().contains("No action found for key: x.y.z"));
    }

    @Test
    public void
    testProcessHandleInputThrowsWhenNoActionsRegistered()
    {
        IRequestProcessor  processor = createProcessor();
        HandleInputRequest request   = createHandleInputRequest("a","b","c");

        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(request));
    }

    @Test
    public void
    testProcessGetOutputDispatchesByKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA.destA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("sourceA","destA"));

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessGetOutputWithSourceOnlyKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("sourceA",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("sourceA",null));

        assertSame(expected,actual);
    }

    @Test
    public void
    testProcessGetOutputSelectsCorrectActionAmongMultiple()
    {
        FrontEndOutput             output1 = createOutput("first");
        FrontEndOutput             output2 = createOutput("second");
        IGetOutputAction           action1 = (GetOutputRequest r) -> output1;
        IGetOutputAction           action2 = (GetOutputRequest r) -> output2;
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("a.b",action1);
        actions.put("x.y",action2);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("x","y"));

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
            return expected;
        };
        Map<String,IProcessAction> actions = new HashMap<>();

        actions.put("src.dest",action);

        IRequestProcessor processor = createProcessor(actions);
        GetOutputRequest  request   = createGetOutputRequest("src","dest");

        processor.process(request);

        assertSame(request,captured.get());
    }

    @Test
    public void
    testProcessGetOutputThrowsForUnregisteredKey()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("a.b",action);

        IRequestProcessor processor = createProcessor(actions);
        GetOutputRequest  request   = createGetOutputRequest("x","y");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(request));

        assertTrue(exception.getMessage().contains("No action found for key: x.y"));
    }

    @Test
    public void
    testProcessGetOutputThrowsWhenNoActionsRegistered()
    {
        IRequestProcessor processor = createProcessor();
        GetOutputRequest  request   = createGetOutputRequest("a","b");

        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(request));
    }

    @Test
    public void
    testHandleInputActionNotRoutedToGetOutput()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src.dest",action);

        IRequestProcessor processor = createProcessor(actions);
        GetOutputRequest  request   = createGetOutputRequest("src","dest");

        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(request));
    }

    @Test
    public void
    testGetOutputActionNotRoutedToHandleInput()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("src.dest.cmd",action);

        IRequestProcessor processor = createProcessor(actions);
        HandleInputRequest request  = createHandleInputRequest("src","dest","cmd");

        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(request));
    }

    @Test
    public void
    testSameKeyOverwriteRetainsLastAction()
    {
        FrontEndOutput             handleExpected = createOutput("handleResult");
        FrontEndOutput             getExpected    = createOutput("getResult");
        IHandleInputAction         handleAction   = (HandleInputRequest r) -> handleExpected;
        IGetOutputAction           getAction      = (GetOutputRequest r) -> getExpected;
        Map<String,IProcessAction> actions        = new HashMap<>();

        actions.put("src.dest",handleAction);
        actions.put("src.dest",getAction);

        IRequestProcessor processor = createProcessor(actions);

        assertSame(
            getExpected,
            processor.process(createGetOutputRequest("src","dest")));
        assertThrows(
            IllegalArgumentException.class,
            () -> processor.process(
                createHandleInputRequest("src","dest",null)));
    }

    @Test
    public void
    testHandleInputKeyBuiltFromSourceDestinationCommand()
    {
        FrontEndOutput             expected = createOutput("result");
        IHandleInputAction         action   = (HandleInputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("s.d.c",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createHandleInputRequest("s","d","c"));

        assertSame(expected,actual);
    }

    @Test
    public void
    testGetOutputKeyBuiltFromSourceDestinationOnly()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("s.d",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("s","d"));

        assertSame(expected,actual);
    }

    @Test
    public void
    testGetOutputKeyWithNullDestination()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("s",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest("s",null));

        assertSame(expected,actual);
    }

    @Test
    public void
    testGetOutputKeyWithNullSource()
    {
        FrontEndOutput             expected = createOutput("result");
        IGetOutputAction           action   = (GetOutputRequest r) -> expected;
        Map<String,IProcessAction> actions  = new HashMap<>();

        actions.put("",action);

        IRequestProcessor processor = createProcessor(actions);
        FrontEndOutput    actual    =
            processor.process(createGetOutputRequest(null,null));

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
