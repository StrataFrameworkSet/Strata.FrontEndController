//////////////////////////////////////////////////////////////////////////////
// FrontEndInput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public abstract
class FrontEndInput
    implements Serializable
{
    private Long   userId;
    private String source;
    private String command;
    private String destination;

    protected
    FrontEndInput()
    {
        userId      = null;
        source      = null;
        command     = null;
        destination = null;
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
    setCommand(String cmd)
    {
        command = cmd;
        return this;
    }

    public FrontEndInput
    setDestination(String dest)
    {
        destination = dest;
        return this;
    }

    public Long
    getUserId() { return userId; }

    public String
    getSource() { return source; }

    public String
    getCommand() { return command; }

    public Optional<String>
    getDestination() { return Optional.ofNullable(destination); }

    @JsonIgnore
    public String
    getKey()
    {
        return KeyBuilder.build(source,command,destination);
    }
}

//////////////////////////////////////////////////////////////////////////////
