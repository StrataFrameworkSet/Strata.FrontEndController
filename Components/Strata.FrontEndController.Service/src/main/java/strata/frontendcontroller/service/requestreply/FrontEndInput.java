//////////////////////////////////////////////////////////////////////////////
// FrontEndInput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Optional;

public abstract
class FrontEndInput
    implements Serializable
{
    private Long   userId;
    private String source;
    private String destination;
    private String command;

    protected
    FrontEndInput()
    {
        userId      = null;
        source      = null;
        destination = null;
        command     = null;
    }

    public FrontEndInput
    setUserId(Long id)
    {
        userId = id;
        return this;
    }

    public FrontEndInput
    setSource(String src)
    {
        source = src;
        return this;
    }

    public FrontEndInput
    setDestination(String dest)
    {
        destination = dest;
        return this;
    }

    public FrontEndInput
    setCommand(String cmd)
    {
        command = cmd;
        return this;
    }

    public Long
    getUserId() { return userId; }

    public String
    getSource() { return source; }

    public Optional<String>
    getDestination() { return Optional.ofNullable(destination); }

    public String
    getCommand() { return command; }

    @JsonIgnore
    public String
    getKey()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(source);
        getDestination().ifPresent(dest -> builder.append('.').append(dest));
        builder.append('.').append(command);

        return builder.toString();
    }
}

//////////////////////////////////////////////////////////////////////////////
