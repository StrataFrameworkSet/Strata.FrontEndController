/// ///////////////////////////////////////////////////////////////////////////
// FrontEndControllerEventToOutboxEventMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import strata.outbox.core.repository.OutboxEvent;
import strata.outbox.core.shared.MappingException;
import strata.outbox.core.shared.ObjectMapperProvider;

public
class FrontEndControllerEventToOutboxEventMapper
    implements IFrontEndControllerEventToOutboxEventMapper
{
    private final ObjectMapper mapper;

    public
    FrontEndControllerEventToOutboxEventMapper()
    {
        this.mapper = new ObjectMapperProvider().get();
    }

    @Override
    public OutboxEvent
    map(FrontEndControllerEvent event)
        throws MappingException
    {
        try
        {
            return
                new OutboxEvent()
                    .setSourceType(
                        FrontEndControllerEvent
                            .class
                            .getTypeName())
                    .setSourceId(event.getSourceId().toString())
                    .setEventType(
                        FrontEndControllerEvent
                            .class
                            .getSimpleName())
                    .setEventPayload(mapper.writeValueAsString(event));
        }
        catch (JsonProcessingException e)
        {
            throw new MappingException(e);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
