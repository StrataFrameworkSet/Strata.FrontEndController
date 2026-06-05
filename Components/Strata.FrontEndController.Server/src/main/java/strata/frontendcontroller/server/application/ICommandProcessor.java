//////////////////////////////////////////////////////////////////////////////
// ICommandProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndInput;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;

public
interface ICommandProcessor
{
    FrontEndOutput
    process(FrontEndInput input);

    FrontEndOutput
    getOutput(String key);
}

//////////////////////////////////////////////////////////////////////////////