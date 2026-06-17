//////////////////////////////////////////////////////////////////////////////
// GetOutputRequest.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import strata.foundation.core.transfer.AbstractServiceRequest;
import strata.foundation.core.transfer.Cookie;

import java.time.Instant;
import java.util.UUID;

public
class GetOutputRequest
    extends AbstractServiceRequest
{
    private Long   userId;
    private String source;
    private String command;
    private String destination;

    public
    GetOutputRequest()
    {
        super();
        userId      = null;
        source      = null;
        command     = null;
        destination = null;
    }

    @Override
    public GetOutputRequest
    setRequestId(UUID requestId)
    {
        return (GetOutputRequest)super.setRequestId(requestId);
    }

    @Override
    public GetOutputRequest
    setTimestamp(Instant timestamp)
    {
        return (GetOutputRequest)super.setTimestamp(timestamp);
    }

    @Override
    public GetOutputRequest
    addHeader(String name,Object value)
    {
        return (GetOutputRequest)super.addHeader(name,value);
    }

    @Override
    public GetOutputRequest
    clearHeaders()
    {
        return (GetOutputRequest)super.clearHeaders();
    }

    @Override
    public GetOutputRequest
    addCookie(Cookie cookie)
    {
        return (GetOutputRequest)super.addCookie(cookie);
    }

    @Override
    public GetOutputRequest
    clearCookies()
    {
        return (GetOutputRequest)super.clearCookies();
    }

    public GetOutputRequest
    setUserId(Long id)
    {
        userId = id;
        return this;
    }

    public GetOutputRequest
    setSource(String s)
    {
        source = s;
        return this;
    }

    public GetOutputRequest
    setCommand(String s)
    {
        command = s;
        return this;
    }

    public GetOutputRequest
    setDestination(String d)
    {
        destination = d;
        return this;
    }

    public Long
    getUserId() { return userId; }

    public String
    getSource() { return source; }

    public String
    getCommand() { return command; }

    public String
    getDestination() { return destination; }
}

//////////////////////////////////////////////////////////////////////////////
