//////////////////////////////////////////////////////////////////////////////
// OnCommitFrontEndControllerEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.event.IFrontEndControllerEventSender;
import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import jakarta.inject.Inject;
import strata.foundation.core.event.IEventSender;
import strata.server.core.event.OnCommitEventSender;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

public
class OnCommitFrontEndControllerEventSender
    extends OnCommitEventSender<FrontEndControllerEvent>
    implements IFrontEndControllerEventSender
{
    @Inject
    public
    OnCommitFrontEndControllerEventSender(
        IEventSender<FrontEndControllerEvent>     imp,
        IUnitOfWorkSynchronizationManager mgr)
    {
        super(imp,mgr);
    }
}

//////////////////////////////////////////////////////////////////////////////
