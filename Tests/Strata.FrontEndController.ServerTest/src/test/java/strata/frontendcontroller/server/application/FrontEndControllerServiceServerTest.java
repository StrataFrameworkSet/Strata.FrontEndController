//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceServerTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;
import strata.frontendcontroller.service.requestreply.FrontEndControllerServiceTest;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import strata.server.spring.inject.RequestAttributeMap;

@Tag("CommitStage")
public
class FrontEndControllerServiceServerTest
    extends FrontEndControllerServiceTest
{
    private static ApplicationContext         context;
    private        IFrontEndControllerService subject;

    @BeforeAll
    public static void
    initialize()
    {
        context =
            new AnnotationConfigApplicationContext(
                TestConfiguration.class);
    }

    @BeforeEach
    public void
    setUp() throws Exception
    {
        RequestContextHolder.setRequestAttributes(new RequestAttributeMap());
        subject = context.getBean(IFrontEndControllerService.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        subject = null;
        RequestContextHolder.resetRequestAttributes();
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
