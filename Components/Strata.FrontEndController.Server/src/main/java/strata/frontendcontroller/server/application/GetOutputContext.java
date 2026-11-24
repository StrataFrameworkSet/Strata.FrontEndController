//////////////////////////////////////////////////////////////////////////////
// GetOutputContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;

public
class GetOutputContext<R extends GetOutputRequest,O extends FrontEndOutput>
{
    private final R request;
    private O       output;

    public
    GetOutputContext(R req)
    {
        request = req;
        output  = null;
    }

    public GetOutputContext<R,O>
    setOutput(O out)
    {
        output = out;
        return this;
    }

    public R
    getRequest() { return request; }

    public Long
    getUserId() { return request.getUserId(); }

    public String
    getSource() { return request.getSource(); }

    public String
    getCommand() { return request.getCommand(); }

    public String
    getDestination() { return request.getDestination(); }

    public String
    getKey() { return request.getKey(); }

    public O
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////
