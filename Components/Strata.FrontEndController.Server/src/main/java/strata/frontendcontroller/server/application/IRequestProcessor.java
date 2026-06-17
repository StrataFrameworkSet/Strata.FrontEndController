//////////////////////////////////////////////////////////////////////////////
// IRequestProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

import java.util.concurrent.CompletionStage;

public
interface IRequestProcessor
{
    CompletionStage<FrontEndOutput>
    process(HandleInputRequest request);

    CompletionStage<FrontEndOutput>
    process(GetOutputRequest request);
}

//////////////////////////////////////////////////////////////////////////////