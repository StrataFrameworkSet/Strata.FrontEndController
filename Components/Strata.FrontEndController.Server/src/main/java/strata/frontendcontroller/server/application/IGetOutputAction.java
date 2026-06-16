//////////////////////////////////////////////////////////////////////////////
// IGetOutputAction.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;

import java.util.function.Function;

public
interface IGetOutputAction
    extends IProcessAction,Function<GetOutputRequest,FrontEndOutput> {}

//////////////////////////////////////////////////////////////////////////////