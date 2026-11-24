//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceClientTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.sample;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import strata.frontendcontroller.service.sample.EchoFrontEndControllerServiceTest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

@Tag("IntegrationStage")
public
class EchoFrontEndControllerServiceClientTest
    extends EchoFrontEndControllerServiceTest
{
    private IEchoFrontEndControllerServiceClient subject;

    @BeforeEach
    public void
    setUp() throws Exception
    {
        subject = new EchoFrontEndControllerServiceClient("https://localhost:8081");
    }

    @AfterEach
    public void
    tearDown()
    {
        subject.close();
        subject = null;
    }

    @Override
    @Test
    public void
    testHandleEchoInputWithNullInput()
    {
        super.testHandleEchoInputWithNullInput();
    }

    @Override
    @Test
    public void
    testHandleEchoInput()
    {
        super.testHandleEchoInput();
    }

    @Override
    @Test
    public void
    testGetEchoOutput()
    {
        super.testGetEchoOutput();
    }

    @Override
    @Test
    public void
    testGetEchoOutputWithoutUserId()
    {
        super.testGetEchoOutputWithoutUserId();
    }

    @Override
    @Test
    public void
    testGetEchoOutputWithoutSource()
    {
        super.testGetEchoOutputWithoutSource();
    }

    @Override
    protected IEchoFrontEndControllerService
    getSubject()
    {
        return subject;
    }
}

//////////////////////////////////////////////////////////////////////////////
