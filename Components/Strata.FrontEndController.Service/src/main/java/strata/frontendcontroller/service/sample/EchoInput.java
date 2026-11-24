//////////////////////////////////////////////////////////////////////////////
// EchoInput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import strata.frontendcontroller.service.requestreply.FrontEndInput;

@JsonIgnoreProperties(ignoreUnknown = true)
public
class EchoInput
    extends FrontEndInput
{
    private String text;

    public
    EchoInput()
    {
        super();
        text = null;
    }

    @Override
    public EchoInput
    setUserId(Long id)
    {
        return (EchoInput)super.setUserId(id);
    }

    @Override
    public EchoInput
    setSource(String src)
    {
        return (EchoInput)super.setSource(src);
    }

    @Override
    public EchoInput
    setCommand(String cmd)
    {
        return (EchoInput)super.setCommand(cmd);
    }

    @Override
    public EchoInput
    setDestination(String dest)
    {
        return (EchoInput)super.setDestination(dest);
    }

    public EchoInput
    setText(String text)
    {
        this.text = text;
        return this;
    }

    public String
    getText() { return text; }
}

//////////////////////////////////////////////////////////////////////////////
