//////////////////////////////////////////////////////////////////////////////
// HandleInputContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndInput;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

public
class HandleInputContext
{
    private HandleInputRequest request;
    private FrontEndOutput     output;

    public
    HandleInputContext(HandleInputRequest req)
    {
        request = req;
        output  = null;
    }

    public HandleInputContext
    setOutput(FrontEndOutput out)
    {
        output = out;
        return this;
    }

    public HandleInputRequest
    getRequest() { return request; }

    public FrontEndInput
    getInput() { return request.getInput(); }

    public FrontEndOutput
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////
