//////////////////////////////////////////////////////////////////////////////
// HandleInputReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public
class HandleInputReply
    extends AbstractServiceReply
{
    private FrontEndInput input;
    private FrontEndOutput output;


    public HandleInputReply()
    {
        super();
        input  = null;
        output = null;
    }

    public HandleInputReply(HandleInputRequest originatingRequest)
    {
        super(originatingRequest);
        input  = originatingRequest.getInput();
        output = null;
    }

    @Override
    public HandleInputReply
    setReplyId(UUID replyId)
    {
        return (HandleInputReply)super.setReplyId(replyId);
    }

    @Override
    public HandleInputReply
    setOriginatingRequestId(UUID requestId)
    {
        return (HandleInputReply)super.setOriginatingRequestId(requestId);
    }

    @Override
    public HandleInputReply
    setTimestamp(Instant timestamp)
    {
        return (HandleInputReply)super.setTimestamp(timestamp);
    }

    @Override
    public HandleInputReply
    setSuccess(boolean success)
    {
        return (HandleInputReply)super.setSuccess(success);
    }

    @Override
    public HandleInputReply
    setSuccessMessage(String successMessage)
    {
        return (HandleInputReply)super.setSuccessMessage(successMessage);
    }

    @Override
    public HandleInputReply
    setFailureMessage(String failureMessage)
    {
        return (HandleInputReply)super.setFailureMessage(failureMessage);
    }

    @Override
    public HandleInputReply
    setException(ExceptionData exception)
    {
        return (HandleInputReply)super.setException(exception);
    }

    public HandleInputReply
    setInput(FrontEndInput input)
    {
        this.input = input;
        return this;
    }

    public HandleInputReply
    setOutput(FrontEndOutput output)
    {
        this.output = output;
        return this;
    }

    public FrontEndInput
    getInput() { return input; }

    public FrontEndOutput
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////