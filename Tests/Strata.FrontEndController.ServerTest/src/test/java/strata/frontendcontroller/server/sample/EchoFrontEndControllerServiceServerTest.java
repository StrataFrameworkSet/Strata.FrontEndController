//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceServerTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import strata.frontendcontroller.server.application.TestConfiguration;
import strata.frontendcontroller.service.sample.EchoFrontEndControllerServiceTest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;
import strata.server.spring.inject.RequestAttributeMap;

@Tag("CommitStage")
public
class EchoFrontEndControllerServiceServerTest
    extends EchoFrontEndControllerServiceTest
{
    private static ApplicationContext              context;
    private        IEchoFrontEndControllerService subject;

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
        subject = context.getBean("echoFrontEndControllerService",IEchoFrontEndControllerService.class);
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
