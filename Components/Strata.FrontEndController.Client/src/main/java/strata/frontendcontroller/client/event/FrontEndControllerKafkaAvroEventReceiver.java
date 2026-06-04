//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerKafkaAvroEventReceiver.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.event;

import strata.frontendcontroller.service.event.IFrontEndControllerEventListener;
import strata.frontendcontroller.service.event.IFrontEndControllerEventReceiver;
import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import strata.foundation.kafka.event.AbstractKafkaAvroEventReceiver;

import java.util.Map;

public
class FrontEndControllerKafkaAvroEventReceiver
    extends AbstractKafkaAvroEventReceiver<FrontEndControllerEvent,IFrontEndControllerEventListener>
    implements IFrontEndControllerEventReceiver
{
    public
    FrontEndControllerKafkaAvroEventReceiver(Map<String,Object> properties,String topic)
    {
        super(properties,FrontEndControllerEvent.class,topic);
    }
}

//////////////////////////////////////////////////////////////////////////////
