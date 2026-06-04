/// ///////////////////////////////////////////////////////////////////////////
// IFrontEndControllerEventToOutboxEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import strata.outbox.core.sender.ISourceToOutboxEventMapper;

public
interface IFrontEndControllerEventToOutboxEventMapper
    extends ISourceToOutboxEventMapper<FrontEndControllerEvent> {}

//////////////////////////////////////////////////////////////////////////////