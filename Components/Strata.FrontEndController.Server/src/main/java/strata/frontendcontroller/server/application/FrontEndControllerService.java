//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import strata.foundation.core.transfer.ExceptionData;
import strata.frontendcontroller.service.requestreply.FrontEndInput;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.GetOutputReply;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;
import strata.frontendcontroller.service.requestreply.HandleInputReply;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;
import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract
class FrontEndControllerService
    implements IFrontEndControllerService
{
    private final ModelMapper mapper;
    private final Executor    executor;
    private final Logger      logger;

    protected
    FrontEndControllerService(ModelMapper m,Executor e)
    {
        mapper   = m;
        executor = e;
        logger   = LogManager.getLogger(FrontEndControllerService.class);
    }

    protected <I extends FrontEndInput,O extends FrontEndOutput,Q extends HandleInputRequest<I>,P extends HandleInputReply<I,O>> CompletionStage<P>
    handleInput(
        Q                                                                          request,
        Function<HandleInputContext<I,O>,CompletionStage<HandleInputContext<I,O>>> operation,
        Function<Q,P>                                                              replyFactory)
    {
        return handleInput(request,r -> validate(r),operation,replyFactory);
    }

    protected <I extends FrontEndInput,O extends FrontEndOutput,Q extends HandleInputRequest<I>,P extends HandleInputReply<I,O>> CompletionStage<P>
    handleInput(
        Q                                                                          request,
        UnaryOperator<Q>                                                           validator,
        Function<HandleInputContext<I,O>,CompletionStage<HandleInputContext<I,O>>> operation,
        Function<Q,P>                                                              replyFactory)
    {
        logger.debug("handleInput({},{})",request.getRequestId(),getKey(request));
        return
            CompletableFuture
                .supplyAsync(() -> validator.apply(request),executor)
                .thenApply(validatedRequest -> this.<I,O>mapToContext(validatedRequest))
                .thenCompose(operation)
                .thenApply(context -> buildReply(context,request,replyFactory))
                .exceptionally(exception -> buildReply(exception,request,replyFactory));
    }

    protected <Q extends GetOutputRequest,O extends FrontEndOutput,P extends GetOutputReply<O>> CompletionStage<P>
    getOutput(
        Q                                                                      request,
        Function<GetOutputContext<Q,O>,CompletionStage<GetOutputContext<Q,O>>> operation,
        Function<Q,P>                                                          replyFactory)
    {
        return getOutput(request,r -> validate(r),operation,replyFactory);
    }

    protected <Q extends GetOutputRequest,O extends FrontEndOutput,P extends GetOutputReply<O>> CompletionStage<P>
    getOutput(
        Q                                                                      request,
        UnaryOperator<Q>                                                       validator,
        Function<GetOutputContext<Q,O>,CompletionStage<GetOutputContext<Q,O>>> operation,
        Function<Q,P>                                                          replyFactory)
    {
        logger.debug("getOutput({},{})",request.getRequestId(),request.getKey());
        return
            CompletableFuture
                .supplyAsync(() -> validator.apply(request),executor)
                .thenApply(validatedRequest -> this.<Q,O>mapToContext(validatedRequest))
                .thenCompose(operation)
                .thenApply(context -> buildReply(context,replyFactory))
                .exceptionally(exception -> buildReply(exception,request,replyFactory));
    }

    protected <Q extends HandleInputRequest<?>> Q
    validate(Q request)
    {
        logger.debug("<handleInput> validate({},{})",request.getRequestId(),getKey(request));

        Objects.requireNonNull(
            request.getInput(),
            "validation failed: input was not provided");
        return request;
    }

    protected <Q extends GetOutputRequest> Q
    validate(Q request)
    {
        logger.debug("<getOutput> validate({},{})",request.getRequestId(),request.getKey());

        Objects.requireNonNull(
            request.getSource(),
            "validation failed: source was not provided");
        return request;
    }

    protected <I extends FrontEndInput,O extends FrontEndOutput> HandleInputContext<I,O>
    mapToContext(HandleInputRequest<I> request)
    {
        logger.debug("<handleInput> mapToContext({},{})",request.getRequestId(),getKey(request));
        return new HandleInputContext<>(request);
    }

    protected <Q extends GetOutputRequest,O extends FrontEndOutput> GetOutputContext<Q,O>
    mapToContext(Q request)
    {
        logger.debug("<getOutput> mapToContext({},{})",request.getRequestId(),request.getKey());
        return new GetOutputContext<>(request);
    }

    protected <I extends FrontEndInput,O extends FrontEndOutput,Q extends HandleInputRequest<I>,P extends HandleInputReply<I,O>> P
    buildReply(HandleInputContext<I,O> context,Q request,Function<Q,P> replyFactory)
    {
        P reply = replyFactory.apply(request);

        logger.debug("<handleInput> buildReply({},{})",request.getRequestId(),getKey(request));
        reply
            .setSuccess(true)
            .setSuccessMessage("input handled successfully")
            .setOutput(context.getOutput());
        return reply;
    }

    protected <I extends FrontEndInput,O extends FrontEndOutput,Q extends HandleInputRequest<I>,P extends HandleInputReply<I,O>> P
    buildReply(Throwable e,Q request,Function<Q,P> replyFactory)
    {
        P reply = replyFactory.apply(request);

        logger.debug("<handleInput> buildReply({},{}): {}",request.getRequestId(),getKey(request),e.getMessage());
        reply
            .setSuccess(false)
            .setFailureMessage("Unable to handle input")
            .setException(ExceptionData.of(e));
        return reply;
    }

    protected <Q extends GetOutputRequest,O extends FrontEndOutput,P extends GetOutputReply<O>> P
    buildReply(GetOutputContext<Q,O> context,Function<Q,P> replyFactory)
    {
        P reply = replyFactory.apply(context.getRequest());

        logger.debug("<getOutput> buildReply({},{})",context.getRequest().getRequestId(),context.getKey());
        reply
            .setSuccess(true)
            .setSuccessMessage("output retrieved successfully")
            .setOutput(context.getOutput());
        return reply;
    }

    protected <Q extends GetOutputRequest,O extends FrontEndOutput,P extends GetOutputReply<O>> P
    buildReply(Throwable e,Q request,Function<Q,P> replyFactory)
    {
        P reply = replyFactory.apply(request);

        logger.debug("<getOutput> buildReply({},{}): {}",request.getRequestId(),request.getKey(),e.getMessage());
        reply
            .setSuccess(false)
            .setFailureMessage("Unable to get output")
            .setException(ExceptionData.of(e));
        return reply;
    }

    protected ModelMapper
    getMapper() { return mapper; }

    protected Executor
    getExecutor() { return executor; }

    private String
    getKey(HandleInputRequest<?> request)
    {
        return request.getInput() != null ? request.getInput().getKey() : null;
    }
}

//////////////////////////////////////////////////////////////////////////////
