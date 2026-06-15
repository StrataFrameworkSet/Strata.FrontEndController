//////////////////////////////////////////////////////////////////////////////
// IRequestProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

public
interface IRequestProcessor
{
    FrontEndOutput
    handleInput(HandleInputRequest request);

    FrontEndOutput
    getOutput(GetOutputRequest request);
}

//////////////////////////////////////////////////////////////////////////////