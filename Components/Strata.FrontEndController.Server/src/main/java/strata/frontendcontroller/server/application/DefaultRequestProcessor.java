//////////////////////////////////////////////////////////////////////////////
// DefaultRequestProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.foundation.core.transfer.AbstractServiceRequest;
import strata.frontendcontroller.service.requestreply.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public abstract
class DefaultRequestProcessor
    implements IRequestProcessor
{
    private final Map<String,IHandleInputAction> handleInputActions;
    private final Map<String,IGetOutputAction>   getOutputActions;

    protected
    DefaultRequestProcessor()
    {
        this(new HashMap<>());
    }

    protected
    DefaultRequestProcessor(Map<String,IProcessAction> actions)
    {
        this.handleInputActions = filterActionsByType(IHandleInputAction.class,actions);
        this.getOutputActions = filterActionsByType(IGetOutputAction.class,actions);
    }

    @Override
    public CompletionStage<FrontEndOutput>
    process(HandleInputRequest request)
    {
        return getHandleInputAction(getKey(request)).apply(request);
    }

    @Override
    public CompletionStage<FrontEndOutput>
    process(GetOutputRequest request)
    {
        return getGetOutputAction(getKey(request)).apply(request);
    }

    protected IHandleInputAction
    getHandleInputAction(String key)
    {
        return
            handleInputActions.containsKey(key)
                ? handleInputActions.get(key)
                : (HandleInputRequest r) ->
                    CompletableFuture.failedFuture(
                        new IllegalArgumentException("No action found for key: " + key));
    }

    protected IGetOutputAction
    getGetOutputAction(String key)
    {
        return
            getOutputActions.containsKey(key)
                ? getOutputActions.get(key)
                : (GetOutputRequest r) ->
                    CompletableFuture.failedFuture(
                        new IllegalArgumentException("No action found for key: " + key));
    }

    private static <R extends AbstractServiceRequest> String
    getKey(R request)
    {
        if (request instanceof HandleInputRequest hir)
            return hir.getInput().getKey();

        if (request instanceof GetOutputRequest gor)
            return KeyBuilder.build(gor.getSource(),gor.getCommand(),gor.getDestination());

        return "";
    }

    private static <T extends IProcessAction> Map<String,T>
    filterActionsByType(Class<T> type,Map<String,IProcessAction> actions)
    {
        return
            actions
                .entrySet()
                .stream()
                .filter(e -> type.isInstance(e.getValue()))
                .collect(
                    HashMap::new,
                    (m,e) -> m.put(e.getKey(),type.cast(e.getValue())),
                    HashMap::putAll);
    }
}

//////////////////////////////////////////////////////////////////////////////
