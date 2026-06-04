//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceClientTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.requestreply;

import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;
import strata.frontendcontroller.service.requestreply.FrontEndControllerServiceTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationStage")
public
class FrontEndControllerServiceClientTest
    extends FrontEndControllerServiceTest
{
    private IFrontEndControllerServiceClient subject;

    @BeforeEach
    public void
    setUp() throws Exception
    {
        subject = new FrontEndControllerServiceClient("https://localhost:8081");
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
    testHandleInput()
    {
        super.testHandleInput();
    }

    @Override
    @Test
    public void
    testGetOutput()
    {
        super.testGetOutput();
    }

    @Override
    protected IFrontEndControllerService
    getSubject()
    {
        return subject;
    }

}

//////////////////////////////////////////////////////////////////////////////
