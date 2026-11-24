//////////////////////////////////////////////////////////////////////////////
// StrictPipelineTestService.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.GetOutputRequest;

import java.util.Objects;

public
class StrictPipelineTestService
    extends PipelineTestService
{
    public
    StrictPipelineTestService()
    {
        super();
    }

    @Override
    protected <Q extends GetOutputRequest> Q
    validate(Q request)
    {
        super.validate(request);
        Objects.requireNonNull(
            request.getUserId(),
            "validation failed: userId was not provided");
        return request;
    }
}

//////////////////////////////////////////////////////////////////////////////
