//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.server.domain.IFrontEndControllerRepository;
import strata.frontendcontroller.service.requestreply.*;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import strata.foundation.core.concurrent.CurrentThreadExecutor;
import strata.foundation.core.transfer.ExceptionData;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public
class FrontEndControllerService
    implements ITransactionalFrontEndControllerService
{
    private final IFrontEndControllerRepository repository;
    private final ModelMapper mapper;
    private final Logger logger;

    @Inject
    public FrontEndControllerService(IFrontEndControllerRepository r,ModelMapper m)
    {
        repository = r;
        mapper = m;
        logger = LogManager.getLogger(FrontEndControllerService.class);
    }

    @Override
    public CompletionStage<HandleInputReply>
    handleInput(HandleInputRequest request)
    {
        logger.debug("handleInput({})",request.getRequestId());
        return
            CompletableFuture
                .supplyAsync(() -> validate(request),new CurrentThreadExecutor())
                .thenApply(validatedRequest -> mapToContext(validatedRequest))
                .thenApply(context -> handleInput(context))
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
                .supplyAsync(() -> validate(request),new CurrentThreadExecutor())
                .thenApply(validatedRequest -> mapToContext(validatedRequest))
                .thenApply(context -> getOutput(context))
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

    protected HandleInputContext
    handleInput(HandleInputContext context)
    {
        return context;
    }

    protected GetOutputContext
    getOutput(GetOutputContext context)
    {
        return context;
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
