//////////////////////////////////////////////////////////////////////////////
// TestRequestProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import java.util.Map;

public
class TestRequestProcessor
    extends DefaultRequestProcessor
{
    public
    TestRequestProcessor()
    {
        super();
    }

    public
    TestRequestProcessor(Map<String,IProcessAction> actions)
    {
        super(actions);
    }
}

//////////////////////////////////////////////////////////////////////////////
