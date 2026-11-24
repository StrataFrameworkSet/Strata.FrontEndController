//////////////////////////////////////////////////////////////////////////////
// HandleEchoInputReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import strata.foundation.core.transfer.ExceptionData;
import strata.frontendcontroller.service.requestreply.HandleInputReply;

import java.time.Instant;
import java.util.UUID;

public
class HandleEchoInputReply
    extends HandleInputReply<EchoInput,EchoOutput>
{
    public
    HandleEchoInputReply()
    {
        super();
    }

    public
    HandleEchoInputReply(HandleEchoInputRequest originatingRequest)
    {
        super(originatingRequest);
    }

    @Override
    public HandleEchoInputReply
    setReplyId(UUID replyId)
    {
        return (HandleEchoInputReply)super.setReplyId(replyId);
    }

    @Override
    public HandleEchoInputReply
    setOriginatingRequestId(UUID requestId)
    {
        return (HandleEchoInputReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public HandleEchoInputReply
    setTimestamp(Instant timestamp)
    {
        return (HandleEchoInputReply)super.setTimestamp(timestamp);
    }

    @Override
    public HandleEchoInputReply
    setSuccess(boolean success)
    {
        return (HandleEchoInputReply)super.setSuccess(success);
    }

    @Override
    public HandleEchoInputReply
    setSuccessMessage(String successMessage)
    {
        return (HandleEchoInputReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public HandleEchoInputReply
    setFailureMessage(String failureMessage)
    {
        return (HandleEchoInputReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public HandleEchoInputReply
    setException(ExceptionData exception)
    {
        return (HandleEchoInputReply)super.setException(exception);
    }

    @Override
    public HandleEchoInputReply
    setInput(EchoInput input)
    {
        return (HandleEchoInputReply)super.setInput(input);
    }

    @Override
    public HandleEchoInputReply
    setOutput(EchoOutput output)
    {
        return (HandleEchoInputReply)super.setOutput(output);
    }
}

//////////////////////////////////////////////////////////////////////////////
