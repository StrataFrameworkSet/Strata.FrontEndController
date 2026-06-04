//////////////////////////////////////////////////////////////////////////////
// MockFrontEndControllerEventSender.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.event.IFrontEndControllerEventSender;
import strata.frontendcontroller.service.event.FrontEndControllerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.foundation.core.event.CompletableSendResult;
import strata.foundation.core.event.ICompletableSendResult;
import strata.foundation.core.event.IEventSender;
import strata.foundation.core.event.SendResult;

import java.util.concurrent.CompletableFuture;

public
class MockFrontEndControllerEventSender
    implements IFrontEndControllerEventSender
{
    private final Logger logger;

    public
    MockFrontEndControllerEventSender()
    {
        logger = LogManager.getLogger(MockFrontEndControllerEventSender.class);
    }

    @Override
    public IEventSender<FrontEndControllerEvent>
    open() throws Exception
    {
        logger.info("open()");
        return this;
    }

    @Override
    public IEventSender<FrontEndControllerEvent>
    close() throws Exception
    {
        logger.info("close()");
        return this;
    }

    @Override
    public ICompletableSendResult<FrontEndControllerEvent>
    send(FrontEndControllerEvent event)
    {
        logger.info("send(" + event.toString() + ")");
        return
            new CompletableSendResult<>(
                CompletableFuture.completedFuture(new SendResult<>(event)));
    }

    @Override
    public boolean
    isOpen()
    {
        return false;
    }

    @Override
    public boolean
    isClosed()
    {
        return false;
    }
}

//////////////////////////////////////////////////////////////////////////////
