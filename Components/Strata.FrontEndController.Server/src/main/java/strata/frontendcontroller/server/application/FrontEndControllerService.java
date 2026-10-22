//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.*;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import strata.foundation.core.transfer.ExceptionData;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public
class FrontEndControllerService
    implements IFrontEndControllerService
{
    private final IRequestProcessor processor;
    private final ModelMapper       mapper;
    private final Executor          executor;
    private final Logger            logger;

    @Inject
    public
    FrontEndControllerService(IRequestProcessor p,ModelMapper m,Executor e)
    {
        processor = p;
        mapper = m;
        executor = e;
        logger = LogManager.getLogger(FrontEndControllerService.class);
    }

    @Override
    public CompletionStage<HandleInputReply>
    handleInput(HandleInputRequest request)
    {
        logger.debug("handleInput({})",request.getRequestId());
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),executor)
                .thenApply(validatedRequest -> mapToContext(validatedRequest))
                .thenCompose(context -> handleInput(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    @Override
    public CompletionStage<GetOutputReply>
    getOutput(GetOutputRequest request)
    {
        logger.debug("getOutput({})",request.getRequestId());
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),executor)
                .thenApply(validatedRequest -> mapToContext(validatedRequest))
                .thenCompose(context -> getOutput(context))
                .thenApply(context -> buildReply(context))
                .exceptionally(exception -> buildReply(exception,request));
    }

    protected HandleInputRequest
    validate(HandleInputRequest request)
    {
        logger.debug("<handleInput> validate({})",request.getRequestId());

        Objects.requireNonNull(
            request.getInput(),
            "validation failed: input was not provided");
        return request;
    }

    protected GetOutputRequest
    validate(GetOutputRequest request)
    {
        logger.debug("<getOutput> validate({})",request.getRequestId());

        Objects.requireNonNull(
            request.getUserId(),
            "validation failed: userId was not provided");
        Objects.requireNonNull(
            request.getSource(),
            "validation failed: source was not provided");
        return request;
    }

    protected HandleInputContext
    mapToContext(HandleInputRequest request)
    {
        logger.debug("<handleInput> mapToContext({})",request.getRequestId());
        return new HandleInputContext(request);
    }

    protected GetOutputContext
    mapToContext(GetOutputRequest request)
    {
        logger.debug("<getOutput> mapToContext({})",request.getRequestId());
        return new GetOutputContext(request);
    }

    protected CompletionStage<HandleInputContext>
    handleInput(HandleInputContext context)
    {
        logger.debug("handleInput({})",context);
        return
            processor
                .process(context.getRequest())
                .thenApply(output -> context.setOutput(output));
    }

    protected CompletionStage<GetOutputContext>
    getOutput(GetOutputContext context)
    {
        logger.debug("getOutput({})",context);
        return
            processor
                .process(context.getRequest())
                .thenApply(output -> context.setOutput(output));
    }

    private HandleInputReply
    buildReply(HandleInputContext context)
    {
        logger.info("building successful handleInput reply");

        return
            new HandleInputReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage("input handled successfully")
                .setOutput(context.getOutput());
    }

    private HandleInputReply
    buildReply(Throwable e,HandleInputRequest request)
    {
        logger.info("building exception reply: {}",e.getMessage());
        return
            new HandleInputReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to handle input")
                .setException(ExceptionData.of(e));
    }

    private GetOutputReply
    buildReply(GetOutputContext context)
    {
        logger.info("building successful getOutput reply");

        return
            new GetOutputReply(context.getRequest())
                .setSuccess(true)
                .setSuccessMessage("output retrieved successfully")
                .setOutput(context.getOutput());
    }

    private GetOutputReply
    buildReply(Throwable e,GetOutputRequest request)
    {
        logger.info("building exception reply: {}",e.getMessage());
        return
            new GetOutputReply(request)
                .setSuccess(false)
                .setFailureMessage("Unable to get output")
                .setException(ExceptionData.of(e));
    }
}

//////////////////////////////////////////////////////////////////////////////
