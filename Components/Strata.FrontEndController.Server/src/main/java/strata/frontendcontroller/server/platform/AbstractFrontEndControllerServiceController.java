//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import strata.frontendcontroller.service.requestreply.*;

import java.util.concurrent.CompletionStage;

public abstract
class AbstractFrontEndControllerServiceController
{
    private IFrontEndControllerService implementation;
    private Logger                     logger;

    protected
    AbstractFrontEndControllerServiceController(IFrontEndControllerService imp)
    {
        implementation = imp;
        logger = LogManager.getLogger(AbstractFrontEndControllerServiceController.class);
    }

    @PostMapping(
        value = "/handle-input",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<HandleInputReply>
    handleInput(@RequestBody HandleInputRequest request)
    {
        logger.info("delegating handleInput to implementation service");
        return implementation.handleInput(request);
    }

    @PostMapping(
        value = "/get-output",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<GetOutputReply>
    getOutput(@RequestBody GetOutputRequest request)
    {
        logger.info("delegating getOutput to implementation service");
        return implementation.getOutput(request);
    }
}

//////////////////////////////////////////////////////////////////////////////
