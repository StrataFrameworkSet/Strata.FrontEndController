//////////////////////////////////////////////////////////////////////////////
// TestInput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndInput;

public
class TestInput
    extends FrontEndInput
{
    private String payload;

    public
    TestInput()
    {
        super();
        payload = null;
    }

    public TestInput
    setPayload(String p)
    {
        payload = p;
        return this;
    }

    public String
    getPayload() { return payload; }
}

//////////////////////////////////////////////////////////////////////////////
