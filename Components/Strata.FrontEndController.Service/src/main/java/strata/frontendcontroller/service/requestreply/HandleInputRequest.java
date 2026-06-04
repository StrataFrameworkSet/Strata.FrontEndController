//////////////////////////////////////////////////////////////////////////////
// HandleInputRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;
import strata.foundation.core.transfer.Cookie;

import java.time.Instant;
import java.util.UUID;

public
class HandleInputRequest
    extends AbstractServiceRequest
{
    private FrontEndInput input;

    public
    HandleInputRequest()
    {
        super();
        input = null;
    }

    @Override
    public HandleInputRequest
    setRequestId(UUID requestId)
    {
        return (HandleInputRequest)super.setRequestId(requestId);
    }

    @Override
    public HandleInputRequest
    setTimestamp(Instant timestamp)
    {
        return (HandleInputRequest)super.setTimestamp(timestamp);
    }

    @Override
    public HandleInputRequest
    addHeader(String name,Object value)
    {
        return (HandleInputRequest)super.addHeader(name,value);
    }

    @Override
    public HandleInputRequest
    clearHeaders()
    {
        return (HandleInputRequest)super.clearHeaders();
    }

    @Override
    public HandleInputRequest
    addCookie(Cookie cookie)
    {
        return (HandleInputRequest)super.addCookie(cookie);
    }

    @Override
    public HandleInputRequest
    clearCookies()
    {
        return (HandleInputRequest)super.clearCookies();
    }

    public HandleInputRequest
    setInput(FrontEndInput input)
    {
        this.input = input;
        return this;
    }

    public FrontEndInput
    getInput() { return input; }
}

//////////////////////////////////////////////////////////////////////////////