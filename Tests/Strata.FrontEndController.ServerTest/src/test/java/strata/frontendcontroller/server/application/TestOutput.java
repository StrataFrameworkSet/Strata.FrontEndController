//////////////////////////////////////////////////////////////////////////////
// TestOutput.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;

public
class TestOutput
    extends FrontEndOutput
{
    private String payload;

    public
    TestOutput()
    {
        super();
        payload = null;
    }

    public TestOutput
    setPayload(String p)
    {
        payload = p;
        return this;
    }

    public String
    getPayload() { return payload; }
}

//////////////////////////////////////////////////////////////////////////////
