/// ///////////////////////////////////////////////////////////////////////////
// OutboxEventFrontEndControllerEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.event.IFrontEndControllerEventSender;
import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import strata.outbox.core.repository.IOutboxEventRepository;
import strata.outbox.core.sender.OutboxEventServiceEventSender;

public
class OutboxEventFrontEndControllerEventSender
    extends OutboxEventServiceEventSender<FrontEndControllerEvent,IFrontEndControllerEventSender>
    implements IFrontEndControllerEventSender
{
    public
    OutboxEventFrontEndControllerEventSender(IOutboxEventRepository repository)
    {
        super(new FrontEndControllerEventToOutboxEventMapper(),repository);
    }

    @Override
    protected IFrontEndControllerEventSender
    getSelf()
    {
        return this;
    }

}

//////////////////////////////////////////////////////////////////////////////
