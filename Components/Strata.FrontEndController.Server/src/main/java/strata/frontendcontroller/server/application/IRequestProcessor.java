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
    process(HandleInputRequest request);

    FrontEndOutput
    process(GetOutputRequest request);
}

//////////////////////////////////////////////////////////////////////////////