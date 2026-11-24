//////////////////////////////////////////////////////////////////////////////
// HostRequestProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.servicehost.main;

import strata.frontendcontroller.server.application.DefaultRequestProcessor;
import strata.frontendcontroller.server.application.IProcessAction;

import java.util.Map;

public
class HostRequestProcessor
    extends DefaultRequestProcessor
{
    public
    HostRequestProcessor(Map<String,IProcessAction> actions)
    {
        super(actions);
    }
}

//////////////////////////////////////////////////////////////////////////////
