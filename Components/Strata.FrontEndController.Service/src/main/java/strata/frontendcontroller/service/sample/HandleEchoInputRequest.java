//////////////////////////////////////////////////////////////////////////////
// HandleEchoInputRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import strata.foundation.core.transfer.Cookie;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

import java.time.Instant;
import java.util.UUID;

public
class HandleEchoInputRequest
    extends HandleInputRequest<EchoInput>
{
    public
    HandleEchoInputRequest()
    {
        super();
    }

    @Override
    public HandleEchoInputRequest
    setRequestId(UUID requestId)
    {
        return (HandleEchoInputRequest)super.setRequestId(requestId);
    }

    @Override
    public HandleEchoInputRequest
    setTimestamp(Instant timestamp)
    {
        return (HandleEchoInputRequest)super.setTimestamp(timestamp);
    }

    @Override
    public HandleEchoInputRequest
    addHeader(String name,Object value)
    {
        return (HandleEchoInputRequest)super.addHeader(name,value);
    }

    @Override
    public HandleEchoInputRequest
    clearHeaders()
    {
        return (HandleEchoInputRequest)super.clearHeaders();
    }

    @Override
    public HandleEchoInputRequest
    addCookie(Cookie cookie)
    {
        return (HandleEchoInputRequest)super.addCookie(cookie);
    }

    @Override
    public HandleEchoInputRequest
    clearCookies()
    {
        return (HandleEchoInputRequest)super.clearCookies();
    }

    @Override
    public HandleEchoInputRequest
    setInput(EchoInput input)
    {
        return (HandleEchoInputRequest)super.setInput(input);
    }
}

//////////////////////////////////////////////////////////////////////////////
