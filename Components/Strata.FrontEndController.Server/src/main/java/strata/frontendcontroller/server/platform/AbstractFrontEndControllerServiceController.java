//////////////////////////////////////////////////////////////////////////////
// AbstractFrontEndControllerServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

public abstract
class AbstractFrontEndControllerServiceController<S extends IFrontEndControllerService>
{
    private final S      service;
    private final Logger logger;

    protected
    AbstractFrontEndControllerServiceController(S service)
    {
        this.service = service;
        this.logger  = LogManager.getLogger(AbstractFrontEndControllerServiceController.class);
    }

    protected S
    getService() { return service; }

    protected Logger
    getLogger() { return logger; }
}

//////////////////////////////////////////////////////////////////////////////
