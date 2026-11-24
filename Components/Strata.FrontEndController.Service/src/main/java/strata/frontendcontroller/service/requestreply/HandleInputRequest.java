//////////////////////////////////////////////////////////////////////////////
// HandleInputRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;
import strata.foundation.core.transfer.Cookie;

import java.time.Instant;
import java.util.UUID;

public abstract
class HandleInputRequest<I extends FrontEndInput>
    extends AbstractServiceRequest
{
    private I input;

    public
    HandleInputRequest()
    {
        super();
        input = null;
    }

    @Override
    public HandleInputRequest<I>
    setRequestId(UUID requestId)
    {
        return (HandleInputRequest<I>)super.setRequestId(requestId);
    }

    @Override
    public HandleInputRequest<I>
    setTimestamp(Instant timestamp)
    {
        return (HandleInputRequest<I>)super.setTimestamp(timestamp);
    }

    @Override
    public HandleInputRequest<I>
    addHeader(String name,Object value)
    {
        return (HandleInputRequest<I>)super.addHeader(name,value);
    }

    @Override
    public HandleInputRequest<I>
    clearHeaders()
    {
        return (HandleInputRequest<I>)super.clearHeaders();
    }

    @Override
    public HandleInputRequest<I>
    addCookie(Cookie cookie)
    {
        return (HandleInputRequest<I>)super.addCookie(cookie);
    }

    @Override
    public HandleInputRequest<I>
    clearCookies()
    {
        return (HandleInputRequest<I>)super.clearCookies();
    }

    public HandleInputRequest<I>
    setInput(I input)
    {
        this.input = input;
        return this;
    }

    public I
    getInput() { return input; }
}

//////////////////////////////////////////////////////////////////////////////