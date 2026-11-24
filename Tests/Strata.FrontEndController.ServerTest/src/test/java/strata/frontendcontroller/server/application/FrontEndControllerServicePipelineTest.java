//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServicePipelineTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.frontendcontroller.service.sample.EchoInput;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class FrontEndControllerServicePipelineTest
{
    @Test
    public void
    testHandleInputSucceeds()
    {
        PipelineTestService  service = new PipelineTestService();
        HandleEchoInputReply reply   =
            service
                .handleEchoInput(createHandleRequest("hello"))
                .toCompletableFuture()
                .join();

        assertTrue(reply.isSuccess(),reply.getFailureMessage());
        assertEquals("input handled successfully",reply.getSuccessMessage());
        assertEquals("hello",reply.getOutput().getText());
    }

    @Test
    public void
    testHandleInputOperationThrows()
    {
        PipelineTestService service =
            new PipelineTestService()
                .setHandleOperation(
                    context ->
                    {
                        throw new IllegalStateException("operation failed");
                    });
        HandleEchoInputReply reply =
            service
                .handleEchoInput(createHandleRequest("hello"))
                .toCompletableFuture()
                .join();

        assertFalse(reply.isSuccess());
        assertEquals("Unable to handle input",reply.getFailureMessage());
        assertNotNull(reply.getException());
    }

    @Test
    public void
    testGetOutputOperationFails()
    {
        PipelineTestService service =
            new PipelineTestService()
                .setGetOperation(
                    context -> CompletableFuture.failedFuture(new IllegalStateException()));
        GetEchoOutputReply reply =
            service
                .getEchoOutput(createGetRequest(1L))
                .toCompletableFuture()
                .join();

        assertFalse(reply.isSuccess());
        assertEquals("Unable to get output",reply.getFailureMessage());
    }

    @Test
    public void
    testCustomValidatorRejects()
    {
        AtomicBoolean       invoked = new AtomicBoolean(false);
        PipelineTestService service =
            new PipelineTestService()
                .setHandleOperation(
                    context ->
                    {
                        invoked.set(true);
                        return CompletableFuture.completedFuture(context);
                    });
        HandleEchoInputReply reply =
            service
                .handleEchoInput(
                    createHandleRequest(null),
                    request ->
                    {
                        if (request.getInput().getText() == null)
                            throw new IllegalArgumentException("validation failed: text was not provided");

                        return request;
                    })
                .toCompletableFuture()
                .join();

        assertFalse(reply.isSuccess());
        assertEquals("Unable to handle input",reply.getFailureMessage());
        assertFalse(invoked.get());
    }

    @Test
    public void
    testStandardValidateOverridable()
    {
        GetEchoOutputReply standard =
            new PipelineTestService()
                .getEchoOutput(createGetRequest(null))
                .toCompletableFuture()
                .join();
        GetEchoOutputReply strict =
            new StrictPipelineTestService()
                .getEchoOutput(createGetRequest(null))
                .toCompletableFuture()
                .join();

        assertTrue(standard.isSuccess(),standard.getFailureMessage());
        assertEquals("output retrieved successfully",standard.getSuccessMessage());
        assertFalse(strict.isSuccess());
        assertEquals("Unable to get output",strict.getFailureMessage());
    }

    private HandleEchoInputRequest
    createHandleRequest(String text)
    {
        return
            new HandleEchoInputRequest()
                .setRequestId(UUID.randomUUID())
                .setInput(
                    new EchoInput()
                        .setUserId(1L)
                        .setSource("source")
                        .setCommand("echo")
                        .setText(text));
    }

    private GetEchoOutputRequest
    createGetRequest(Long userId)
    {
        return
            new GetEchoOutputRequest()
                .setRequestId(UUID.randomUUID())
                .setUserId(userId)
                .setSource("source")
                .setCommand("echo");
    }
}

//////////////////////////////////////////////////////////////////////////////
