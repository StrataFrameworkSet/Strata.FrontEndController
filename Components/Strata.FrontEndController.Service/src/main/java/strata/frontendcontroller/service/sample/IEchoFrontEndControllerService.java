//////////////////////////////////////////////////////////////////////////////
// IEchoFrontEndControllerService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

import java.util.concurrent.CompletionStage;

public
interface IEchoFrontEndControllerService
    extends IFrontEndControllerService
{
    CompletionStage<HandleEchoInputReply>
    handleEchoInput(HandleEchoInputRequest request);

    CompletionStage<GetEchoOutputReply>
    getEchoOutput(GetEchoOutputRequest request);
}

//////////////////////////////////////////////////////////////////////////////
