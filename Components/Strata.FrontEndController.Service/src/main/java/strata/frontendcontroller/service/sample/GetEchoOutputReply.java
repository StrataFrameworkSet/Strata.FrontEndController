//////////////////////////////////////////////////////////////////////////////
// GetEchoOutputReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import strata.foundation.core.transfer.ExceptionData;
import strata.frontendcontroller.service.requestreply.GetOutputReply;

import java.time.Instant;
import java.util.UUID;

public
class GetEchoOutputReply
    extends GetOutputReply<EchoOutput>
{
    public
    GetEchoOutputReply()
    {
        super();
    }

    public
    GetEchoOutputReply(GetEchoOutputRequest originatingRequest)
    {
        super(originatingRequest);
    }

    @Override
    public GetEchoOutputReply
    setReplyId(UUID replyId)
    {
        return (GetEchoOutputReply)super.setReplyId(replyId);
    }

    @Override
    public GetEchoOutputReply
    setOriginatingRequestId(UUID requestId)
    {
        return (GetEchoOutputReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public GetEchoOutputReply
    setTimestamp(Instant timestamp)
    {
        return (GetEchoOutputReply)super.setTimestamp(timestamp);
    }

    @Override
    public GetEchoOutputReply
    setSuccess(boolean success)
    {
        return (GetEchoOutputReply)super.setSuccess(success);
    }

    @Override
    public GetEchoOutputReply
    setSuccessMessage(String successMessage)
    {
        return (GetEchoOutputReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public GetEchoOutputReply
    setFailureMessage(String failureMessage)
    {
        return (GetEchoOutputReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public GetEchoOutputReply
    setException(ExceptionData exception)
    {
        return (GetEchoOutputReply)super.setException(exception);
    }

    @Override
    public GetEchoOutputReply
    setOutput(EchoOutput output)
    {
        return (GetEchoOutputReply)super.setOutput(output);
    }
}

//////////////////////////////////////////////////////////////////////////////
