//////////////////////////////////////////////////////////////////////////////
// HandleInputReply.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceReply;
import strata.foundation.core.transfer.ExceptionData;

import java.time.Instant;
import java.util.UUID;

public abstract
class HandleInputReply<I extends FrontEndInput,O extends FrontEndOutput>
    extends AbstractServiceReply
{
    private I input;
    private O output;

    public
    HandleInputReply()
    {
        super();
        input  = null;
        output = null;
    }

    public
    HandleInputReply(HandleInputRequest<I> originatingRequest)
    {
        super(originatingRequest);
        input  = originatingRequest.getInput();
        output = null;
    }

    @Override
    public HandleInputReply<I,O>
    setReplyId(UUID replyId)
    {
        return (HandleInputReply<I,O>)super.setReplyId(replyId);
    }

    @Override
    public HandleInputReply<I,O>
    setOriginatingRequestId(UUID requestId)
    {
        return (HandleInputReply<I,O>)super.setOriginatingRequestId(requestId);
    }

    @Override
    public HandleInputReply<I,O>
    setTimestamp(Instant timestamp)
    {
        return (HandleInputReply<I,O>)super.setTimestamp(timestamp);
    }

    @Override
    public HandleInputReply<I,O>
    setSuccess(boolean success)
    {
        return (HandleInputReply<I,O>)super.setSuccess(success);
    }

    @Override
    public HandleInputReply<I,O>
    setSuccessMessage(String successMessage)
    {
        return (HandleInputReply<I,O>)super.setSuccessMessage(successMessage);
    }

    @Override
    public HandleInputReply<I,O>
    setFailureMessage(String failureMessage)
    {
        return (HandleInputReply<I,O>)super.setFailureMessage(failureMessage);
    }

    @Override
    public HandleInputReply<I,O>
    setException(ExceptionData exception)
    {
        return (HandleInputReply<I,O>)super.setException(exception);
    }

    public HandleInputReply<I,O>
    setInput(I input)
    {
        this.input = input;
        return this;
    }

    public HandleInputReply<I,O>
    setOutput(O output)
    {
        this.output = output;
        return this;
    }

    public I
    getInput() { return input; }

    public O
    getOutput() { return output; }
}

//////////////////////////////////////////////////////////////////////////////