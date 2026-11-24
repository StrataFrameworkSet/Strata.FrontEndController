//////////////////////////////////////////////////////////////////////////////
// GetOutputReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public abstract
class GetOutputReply<O extends FrontEndOutput>
    extends AbstractServiceReply
{
    private O output;

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
    public GetOutputReply<O>
    setReplyId(UUID replyId)
    {
        return (GetOutputReply<O>)super.setReplyId(replyId);
    }

    @Override
    public GetOutputReply<O>
    setOriginatingRequestId(UUID requestId)
    {
        return (GetOutputReply<O>)super.setOriginatingRequestId(requestId);
    }

    @Override
    public GetOutputReply<O>
    setTimestamp(Instant timestamp)
    {
        return (GetOutputReply<O>)super.setTimestamp(timestamp);
    }

    @Override
    public GetOutputReply<O>
    setSuccess(boolean success)
    {
        return (GetOutputReply<O>)super.setSuccess(success);
    }

    @Override
    public GetOutputReply<O>
    setSuccessMessage(String successMessage)
    {
        return (GetOutputReply<O>)super.setSuccessMessage(successMessage);
    }

    @Override
    public GetOutputReply<O>
    setFailureMessage(String failureMessage)
    {
        return (GetOutputReply<O>)super.setFailureMessage(failureMessage);
    }

    @Override
    public GetOutputReply<O>
    setException(ExceptionData exception)
    {
        return (GetOutputReply<O>)super.setException(exception);
    }

    public GetOutputReply<O>
    setOutput(O output)
    {
        this.output = output;
        return this;
    }

    public O
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////
