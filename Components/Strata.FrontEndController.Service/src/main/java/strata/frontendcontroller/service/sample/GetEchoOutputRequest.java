//////////////////////////////////////////////////////////////////////////////
// GetEchoOutputRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import strata.foundation.core.transfer.Cookie;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;

import java.time.Instant;
import java.util.UUID;

public
class GetEchoOutputRequest
    extends GetOutputRequest
{
    public
    GetEchoOutputRequest()
    {
        super();
    }

    @Override
    public GetEchoOutputRequest
    setRequestId(UUID requestId)
    {
        return (GetEchoOutputRequest)super.setRequestId(requestId);
    }

    @Override
    public GetEchoOutputRequest
    setTimestamp(Instant timestamp)
    {
        return (GetEchoOutputRequest)super.setTimestamp(timestamp);
    }

    @Override
    public GetEchoOutputRequest
    addHeader(String name,Object value)
    {
        return (GetEchoOutputRequest)super.addHeader(name,value);
    }

    @Override
    public GetEchoOutputRequest
    clearHeaders()
    {
        return (GetEchoOutputRequest)super.clearHeaders();
    }

    @Override
    public GetEchoOutputRequest
    addCookie(Cookie cookie)
    {
        return (GetEchoOutputRequest)super.addCookie(cookie);
    }

    @Override
    public GetEchoOutputRequest
    clearCookies()
    {
        return (GetEchoOutputRequest)super.clearCookies();
    }

    @Override
    public GetEchoOutputRequest
    setUserId(Long id)
    {
        return (GetEchoOutputRequest)super.setUserId(id);
    }

    @Override
    public GetEchoOutputRequest
    setSource(String s)
    {
        return (GetEchoOutputRequest)super.setSource(s);
    }

    @Override
    public GetEchoOutputRequest
    setCommand(String s)
    {
        return (GetEchoOutputRequest)super.setCommand(s);
    }

    @Override
    public GetEchoOutputRequest
    setDestination(String d)
    {
        return (GetEchoOutputRequest)super.setDestination(d);
    }
}

//////////////////////////////////////////////////////////////////////////////
