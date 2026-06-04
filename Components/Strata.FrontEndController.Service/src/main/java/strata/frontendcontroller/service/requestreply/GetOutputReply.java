//////////////////////////////////////////////////////////////////////////////
// GetOutputReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class GetOutputReply
    extends AbstractServiceReply
{
    private FrontEndOutput output;

    public
    GetOutputReply()
    {
        super();
        output = null;
    }

    public
    GetOutputReply(GetOutputRequest originatingRequest)
    {
        super(originatingRequest);
        output = null;
    }

    @Override
    public GetOutputReply
    setReplyId(UUID replyId)
    {
        return (GetOutputReply)super.setReplyId(replyId);
    }

    @Override
    public GetOutputReply
    setOriginatingRequestId(UUID requestId)
    {
        return (GetOutputReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public GetOutputReply
    setTimestamp(Instant timestamp)
    {
        return (GetOutputReply)super.setTimestamp(timestamp);
    }

    @Override
    public GetOutputReply
    setSuccess(boolean success)
    {
        return (GetOutputReply)super.setSuccess(success);
    }

    @Override
    public GetOutputReply
    setSuccessMessage(String successMessage)
    {
        return (GetOutputReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public GetOutputReply
    setFailureMessage(String failureMessage)
    {
        return (GetOutputReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public GetOutputReply
    setException(ExceptionData exception)
    {
        return (GetOutputReply)super.setException(exception);
    }

    public GetOutputReply
    setOutput(FrontEndOutput output)
    {
        this.output = output;
        return this;
    }

    public FrontEndOutput
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////
