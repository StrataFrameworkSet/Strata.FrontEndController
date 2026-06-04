//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerKafkaEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.event;

import strata.frontendcontroller.service.event.IFrontEndControllerEventListener;
import strata.frontendcontroller.service.event.IFrontEndControllerEventReceiver;
import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import strata.foundation.kafka.event.AbstractKafkaEventReceiver;

import java.util.Map;

public
class FrontEndControllerKafkaEventReceiver
    extends AbstractKafkaEventReceiver<FrontEndControllerEvent,IFrontEndControllerEventListener>
    implements IFrontEndControllerEventReceiver
{
    public
    FrontEndControllerKafkaEventReceiver(Map<String,Object> p,String topic)
    {
        super(p,FrontEndControllerEvent.class,topic);
    }
}

//////////////////////////////////////////////////////////////////////////////
