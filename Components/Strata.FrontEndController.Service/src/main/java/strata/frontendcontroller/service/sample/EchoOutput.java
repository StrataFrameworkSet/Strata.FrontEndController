//////////////////////////////////////////////////////////////////////////////
// EchoOutput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;

@JsonIgnoreProperties(ignoreUnknown = true)
public
class EchoOutput
    extends FrontEndOutput
{
    private String text;

    public
    EchoOutput()
    {
        super();
        text = null;
    }

    @Override
    public EchoOutput
    setUserId(Long id)
    {
        return (EchoOutput)super.setUserId(id);
    }

    @Override
    public EchoOutput
    setSource(String src)
    {
        return (EchoOutput)super.setSource(src);
    }

    @Override
    public EchoOutput
    setCommand(String cmd)
    {
        return (EchoOutput)super.setCommand(cmd);
    }

    @Override
    public EchoOutput
    setDestination(String dest)
    {
        return (EchoOutput)super.setDestination(dest);
    }

    public EchoOutput
    setText(String text)
    {
        this.text = text;
        return this;
    }

    public String
    getText() { return text; }
}

//////////////////////////////////////////////////////////////////////////////
