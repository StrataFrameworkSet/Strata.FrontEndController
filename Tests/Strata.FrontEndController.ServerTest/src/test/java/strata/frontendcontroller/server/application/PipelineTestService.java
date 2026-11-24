//////////////////////////////////////////////////////////////////////////////
// PipelineTestService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.modelmapper.ModelMapper;
import strata.foundation.core.concurrent.CurrentThreadExecutor;
import strata.frontendcontroller.service.sample.EchoInput;
import strata.frontendcontroller.service.sample.EchoOutput;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public
class PipelineTestService
    extends    FrontEndControllerService
    implements IEchoFrontEndControllerService
{
    private Function<HandleInputContext<EchoInput,EchoOutput>,CompletionStage<HandleInputContext<EchoInput,EchoOutput>>>                     handleOperation;
    private Function<GetOutputContext<GetEchoOutputRequest,EchoOutput>,CompletionStage<GetOutputContext<GetEchoOutputRequest,EchoOutput>>> getOperation;

    public
    PipelineTestService()
    {
        super(new ModelMapper(),new CurrentThreadExecutor());
        handleOperation = this::echoInput;
        getOperation    = this::echoKey;
    }

    @Override
    public CompletionStage<HandleEchoInputReply>
    handleEchoInput(HandleEchoInputRequest request)
    {
        return handleInput(request,handleOperation,HandleEchoInputReply::new);
    }

    @Override
    public CompletionStage<GetEchoOutputReply>
    getEchoOutput(GetEchoOutputRequest request)
    {
        return getOutput(request,getOperation,GetEchoOutputReply::new);
    }

    public CompletionStage<HandleEchoInputReply>
    handleEchoInput(
        HandleEchoInputRequest                request,
        UnaryOperator<HandleEchoInputRequest> validator)
    {
        return handleInput(request,validator,handleOperation,HandleEchoInputReply::new);
    }

    public PipelineTestService
    setHandleOperation(
        Function<HandleInputContext<EchoInput,EchoOutput>,CompletionStage<HandleInputContext<EchoInput,EchoOutput>>> operation)
    {
        handleOperation = operation;
        return this;
    }

    public PipelineTestService
    setGetOperation(
        Function<GetOutputContext<GetEchoOutputRequest,EchoOutput>,CompletionStage<GetOutputContext<GetEchoOutputRequest,EchoOutput>>> operation)
    {
        getOperation = operation;
        return this;
    }

    private CompletionStage<HandleInputContext<EchoInput,EchoOutput>>
    echoInput(HandleInputContext<EchoInput,EchoOutput> context)
    {
        return
            CompletableFuture.completedFuture(
                context.setOutput(
                    new EchoOutput().setText(context.getInput().getText())));
    }

    private CompletionStage<GetOutputContext<GetEchoOutputRequest,EchoOutput>>
    echoKey(GetOutputContext<GetEchoOutputRequest,EchoOutput> context)
    {
        return
            CompletableFuture.completedFuture(
                context.setOutput(new EchoOutput().setText(context.getKey())));
    }
}

//////////////////////////////////////////////////////////////////////////////
