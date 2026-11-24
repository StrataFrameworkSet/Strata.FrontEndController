//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceAsyncTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import strata.frontendcontroller.service.sample.EchoInput;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;
import strata.server.spring.inject.RequestAttributeMap;

@Tag("CommitStage")
public
class EchoFrontEndControllerServiceAsyncTest
{
    private static ApplicationContext                      context;
    private        IEchoFrontEndControllerService          service;
    private        EchoFrontEndControllerServiceController controller;

    @BeforeAll
    public static void
    initialize()
    {
        context =
            new AnnotationConfigApplicationContext(
                AsyncTestConfiguration.class);
    }

    @BeforeEach
    public void
    setUp()
    {
        RequestContextHolder.setRequestAttributes(new RequestAttributeMap());
        service    = context.getBean("echoFrontEndControllerService",IEchoFrontEndControllerService.class);
        controller = context.getBean("echoFrontEndControllerServiceController",EchoFrontEndControllerServiceController.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        service    = null;
        controller = null;
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void
    testHandleEchoInputThroughService()
    {
        HandleEchoInputReply reply =
            service
                .handleEchoInput(createHandleRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertEquals("hello",reply.getOutput().getText());
    }

    @Test
    public void
    testGetEchoOutputThroughService()
    {
        GetEchoOutputReply reply =
            service
                .getEchoOutput(createGetRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertEquals("echo:echo->say",reply.getOutput().getText());
    }

    @Test
    public void
    testHandleEchoInputThroughController()
    {
        HandleEchoInputReply reply =
            controller
                .handleEchoInput(createHandleRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertEquals("hello",reply.getOutput().getText());
    }

    @Test
    public void
    testGetEchoOutputThroughController()
    {
        GetEchoOutputReply reply =
            controller
                .getEchoOutput(createGetRequest())
                .toCompletableFuture()
                .join();

        Assertions.assertTrue(reply.isSuccess(),reply.getFailureMessage());
        Assertions.assertEquals("echo:echo->say",reply.getOutput().getText());
    }

    private HandleEchoInputRequest
    createHandleRequest()
    {
        return
            new HandleEchoInputRequest()
                .setInput(
                    new EchoInput()
                        .setSource("echo")
                        .setCommand("say")
                        .setText("hello"));
    }

    private GetEchoOutputRequest
    createGetRequest()
    {
        return
            new GetEchoOutputRequest()
                .setUserId(1L)
                .setSource("echo")
                .setCommand("say");
    }
}

//////////////////////////////////////////////////////////////////////////////
