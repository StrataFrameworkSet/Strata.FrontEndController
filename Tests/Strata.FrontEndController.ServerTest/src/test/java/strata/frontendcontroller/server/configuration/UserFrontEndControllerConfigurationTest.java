//////////////////////////////////////////////////////////////////////////////
// UserFrontEndControllerConfigurationTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.configuration;

import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import strata.frontendcontroller.server.application.IRequestProcessor;
import strata.frontendcontroller.service.requestreply.*;
import strata.server.spring.inject.RequestAttributeMap;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
public
class UserFrontEndControllerConfigurationTest
{
    private static ApplicationContext         context;
    private        IFrontEndControllerService service;
    private        IRequestProcessor          processor;

    @BeforeAll
    public static void
    initialize()
    {
        context =
            new AnnotationConfigApplicationContext(
                TestUserConfiguration.class);
    }

    @BeforeEach
    public void
    setUp()
    {
        RequestContextHolder.setRequestAttributes(new RequestAttributeMap());
        service   = context.getBean(IFrontEndControllerService.class);
        processor = context.getBean(IRequestProcessor.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        service   = null;
        processor = null;
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void
    testContextLoads()
    {
        assertNotNull(context);
    }

    @Test
    public void
    testServiceBeanAvailable()
    {
        assertNotNull(service);
    }

    @Test
    public void
    testRequestProcessorBeanOverridden()
    {
        assertNotNull(processor);
        assertDoesNotThrow(
            () -> processor.process(
                new GetOutputRequest()
                    .setSource("test-source")));
    }

    @Test
    public void
    testHandleInput()
    {
        HandleInputReply reply =
            service
                .handleInput(
                    new HandleInputRequest()
                        .setInput(null))
                .toCompletableFuture()
                .join();

        assertFalse(reply.isSuccess());
    }

    @Test
    public void
    testGetOutput()
    {
        GetOutputReply reply =
            service
                .getOutput(
                    new GetOutputRequest()
                        .setUserId(1L)
                        .setSource("test-source"))
                .toCompletableFuture()
                .join();

        assertTrue(reply.isSuccess(),reply.getFailureMessage());
    }
}

//////////////////////////////////////////////////////////////////////////////
