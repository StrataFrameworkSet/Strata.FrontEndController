//////////////////////////////////////////////////////////////////////////////
// FrontEndOutput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract
class FrontEndOutput
    implements Serializable
{
    private Long   userId;
    private String source;
    private String command;
    private String destination;

    protected
    FrontEndOutput()
    {
        userId      = null;
        source      = null;
        command     = null;
        destination = null;
    }

    public FrontEndOutput
    setUserId(Long id)
    {
        userId = id;
        return this;
    }

    public FrontEndOutput
    setSource(String src)
    {
        source = src;
        return this;
    }

    public FrontEndOutput
    setCommand(String cmd)
    {
        command = cmd;
        return this;
    }

    public FrontEndOutput
    setDestination(String dest)
    {
        destination = dest;
        return this;
    }

    public Long
    getUserId() { return userId; }

    public String
    getSource() { return source; }

    public Optional<String>
    getCommand() { return Optional.ofNullable(command); }

    public String
    getDestination() { return destination; }
}

//////////////////////////////////////////////////////////////////////////////
