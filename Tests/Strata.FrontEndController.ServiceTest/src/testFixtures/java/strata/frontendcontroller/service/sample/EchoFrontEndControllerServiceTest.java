//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationStage")
public abstract
class EchoFrontEndControllerServiceTest
{
    @Test
    public void
    testHandleEchoInputWithNullInput()
    {
        HandleEchoInputReply reply =
            getSubject()
                .handleEchoInput(
                    new HandleEchoInputRequest()
                        .setInput(null))
                .toCompletableFuture()
                .join();

        Assertions.assertFalse(reply.isSuccess());
    }

    @Test
    public void
    testHandleEchoInput()
    {
        HandleEchoInputReply reply =
            getSubject()
                .handleEchoInput(
                    new HandleEchoInputRequest()
                        .setInput(
                            new EchoInput()
                                .setSource("echo")
                                .setCommand("say")
                                .setText("hello")))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertEquals("hello",reply.getOutput().getText());
        Assertions.assertEquals("hello",reply.getInput().getText());
    }

    @Test
    public void
    testGetEchoOutput()
    {
        GetEchoOutputReply reply =
            getSubject()
                .getEchoOutput(
                    new GetEchoOutputRequest()
                        .setUserId(1L)
                        .setSource("echo"))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertNotNull(reply.getOutput());
    }

    @Test
    public void
    testGetEchoOutputWithoutUserId()
    {
        GetEchoOutputReply reply =
            getSubject()
                .getEchoOutput(
                    new GetEchoOutputRequest()
                        .setSource("echo"))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
    }

    @Test
    public void
    testGetEchoOutputWithoutSource()
    {
        GetEchoOutputReply reply =
            getSubject()
                .getEchoOutput(
                    new GetEchoOutputRequest()
                        .setUserId(1L))
                .toCompletableFuture()
                .join();

        Assertions.assertFalse(reply.isSuccess());
    }

    protected abstract IEchoFrontEndControllerService
    getSubject();
}

//////////////////////////////////////////////////////////////////////////////
