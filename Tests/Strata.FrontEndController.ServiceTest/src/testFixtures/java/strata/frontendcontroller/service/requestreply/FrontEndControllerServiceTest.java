//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationStage")
public abstract
class FrontEndControllerServiceTest
{
    private IFrontEndControllerService subject;

    @Test
    public void
    testHandleInput()
    {
        HandleInputReply reply =
            getSubject()
                .handleInput(
                    new HandleInputRequest()
                        .setInput(null))
                .toCompletableFuture()
                .join();

        Assertions.assertFalse(reply.isSuccess());
    }

    @Test
    public void
    testGetOutput()
    {
        GetOutputReply reply =
            getSubject()
                .getOutput(
                    new GetOutputRequest()
                        .setUserId(1L)
                        .setSource("test-source"))
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
    }

    protected abstract IFrontEndControllerService
    getSubject();
}

//////////////////////////////////////////////////////////////////////////////
