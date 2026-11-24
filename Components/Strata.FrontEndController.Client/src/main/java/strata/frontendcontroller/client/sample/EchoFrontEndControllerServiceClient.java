//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceClient.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.sample;

import strata.frontendcontroller.client.requestreply.AbstractFrontEndControllerServiceClient;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;

import java.util.concurrent.CompletionStage;

public
class EchoFrontEndControllerServiceClient
    extends AbstractFrontEndControllerServiceClient
    implements IEchoFrontEndControllerServiceClient
{
    public
    EchoFrontEndControllerServiceClient(String baseUrl)
    {
        super(baseUrl,"frontendcontroller-service/");
    }

    @Override
    public CompletionStage<HandleEchoInputReply>
    handleEchoInput(HandleEchoInputRequest request)
    {
        return
            doPostAsync(
                "handle-echo-input",
                HandleEchoInputReply.class,
                request);
    }

    @Override
    public CompletionStage<GetEchoOutputReply>
    getEchoOutput(GetEchoOutputRequest request)
    {
        return
            doPostAsync(
                "get-echo-output",
                GetEchoOutputReply.class,
                request);
    }

    @Override
    public EchoFrontEndControllerServiceClient
    setAuthorizationHeader(String accessToken)
    {
        return (EchoFrontEndControllerServiceClient)super.setAuthorizationHeader(accessToken);
    }
}

//////////////////////////////////////////////////////////////////////////////
