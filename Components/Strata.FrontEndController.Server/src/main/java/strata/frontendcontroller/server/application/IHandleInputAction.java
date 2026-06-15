/// ///////////////////////////////////////////////////////////////////////////
// IHandleInputAction.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndOutput;
import strata.frontendcontroller.service.requestreply.HandleInputRequest;

import java.util.function.Function;

public
interface IHandleInputAction
    extends IFrontEndAction,Function<HandleInputRequest,FrontEndOutput> {}

//////////////////////////////////////////////////////////////////////////////