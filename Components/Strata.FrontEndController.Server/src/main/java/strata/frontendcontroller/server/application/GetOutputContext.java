//////////////////////////////////////////////////////////////////////////////
// GetOutputContext.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;
import strata.frontendcontroller.service.requestreply.KeyBuilder;

public
class GetOutputContext
{
    private GetOutputRequest request;
    private FrontEndOutput   output;

    public
    GetOutputContext(GetOutputRequest req)
    {
        request = req;
        output  = null;
    }

    public GetOutputContext
    setOutput(FrontEndOutput out)
    {
        output = out;
        return this;
    }

    public GetOutputRequest
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
    getKey()
    {
        return KeyBuilder.build(getSource(),getCommand(),getDestination());
    }

    public FrontEndOutput
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////
