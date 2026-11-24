//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import jakarta.inject.Inject;
import org.modelmapper.ModelMapper;
import strata.frontendcontroller.server.application.FrontEndControllerService;
import strata.frontendcontroller.server.application.GetOutputContext;
import strata.frontendcontroller.server.application.HandleInputContext;
import strata.frontendcontroller.service.sample.EchoInput;
import strata.frontendcontroller.service.sample.EchoOutput;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public
class EchoFrontEndControllerService
    extends    FrontEndControllerService
    implements IEchoFrontEndControllerService
{
    @Inject
    public
    EchoFrontEndControllerService(ModelMapper mapper,Executor executor)
    {
        super(mapper,executor);
    }

    @Override
    public CompletionStage<HandleEchoInputReply>
    handleEchoInput(HandleEchoInputRequest request)
    {
        return handleInput(request,this::echoInput,HandleEchoInputReply::new);
    }

    @Override
    public CompletionStage<GetEchoOutputReply>
    getEchoOutput(GetEchoOutputRequest request)
    {
        return getOutput(request,this::echoKey,GetEchoOutputReply::new);
    }

    protected CompletionStage<HandleInputContext<EchoInput,EchoOutput>>
    echoInput(HandleInputContext<EchoInput,EchoOutput> context)
    {
        EchoInput input = context.getInput();

        return
            CompletableFuture.completedFuture(
                context.setOutput(
                    new EchoOutput()
                        .setText(input.getText())
                        .setUserId(input.getUserId())
                        .setSource(input.getSource())
                        .setCommand(input.getCommand())
                        .setDestination(input.getDestination().orElse(null))));
    }

    protected CompletionStage<GetOutputContext<GetEchoOutputRequest,EchoOutput>>
    echoKey(GetOutputContext<GetEchoOutputRequest,EchoOutput> context)
    {
        return
            CompletableFuture.completedFuture(
                context.setOutput(
                    new EchoOutput()
                        .setText("echo:" + context.getKey())
                        .setUserId(context.getUserId())
                        .setSource(context.getSource())
                        .setCommand(context.getCommand())
                        .setDestination(context.getDestination())));
    }
}

//////////////////////////////////////////////////////////////////////////////
