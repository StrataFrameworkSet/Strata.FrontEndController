//////////////////////////////////////////////////////////////////////////////
// EchoControllerMappingTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.frontendcontroller.service.requestreply.AbstractControllerMappingTest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

import java.util.List;

@Tag("CommitStage")
public
class EchoControllerMappingTest
    extends AbstractControllerMappingTest
{
    @Test
    public void
    testIncompleteControllerDetected()
    {
        Assertions.assertEquals(
            List.of("getEchoOutput"),
            findUnmappedOperations(
                IEchoFrontEndControllerService.class,
                IncompleteEchoController.class));
    }

    @Override
    protected Class<?>
    getServiceInterface()
    {
        return IEchoFrontEndControllerService.class;
    }

    @Override
    protected Class<?>
    getControllerClass()
    {
        return EchoFrontEndControllerServiceController.class;
    }
}

//////////////////////////////////////////////////////////////////////////////
