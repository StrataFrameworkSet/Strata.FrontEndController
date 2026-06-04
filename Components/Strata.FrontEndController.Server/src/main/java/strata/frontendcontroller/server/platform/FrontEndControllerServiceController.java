//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.requestreply.*;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletionStage;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/frontendcontroller-service")
@EnableTransactionManagement
public
class FrontEndControllerServiceController
{
    private IFrontEndControllerService implementation;
    private Logger                     logger;

    @Inject
    public
    FrontEndControllerServiceController(IFrontEndControllerService imp)
    {
        implementation = imp;
        logger = LogManager.getLogger(FrontEndControllerServiceController.class);
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
