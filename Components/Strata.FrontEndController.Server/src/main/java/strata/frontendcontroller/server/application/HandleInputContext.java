//////////////////////////////////////////////////////////////////////////////
// HandleInputContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndInput;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

public
class HandleInputContext<I extends FrontEndInput,O extends FrontEndOutput>
{
    private final HandleInputRequest<I> request;
    private O                           output;

    public
    HandleInputContext(HandleInputRequest<I> req)
    {
        request = req;
        output  = null;
    }

    public HandleInputContext<I,O>
    setOutput(O out)
    {
        output = out;
        return this;
    }

    public HandleInputRequest<I>
    getRequest() { return request; }

    public I
    getInput() { return request.getInput(); }

    public String
    getKey() { return getInput().getKey(); }

    public O
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////
