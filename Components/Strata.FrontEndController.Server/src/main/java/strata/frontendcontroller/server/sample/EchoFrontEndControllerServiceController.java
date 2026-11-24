//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import strata.frontendcontroller.server.platform.AbstractFrontEndControllerServiceController;
import strata.frontendcontroller.service.sample.GetEchoOutputReply;
import strata.frontendcontroller.service.sample.GetEchoOutputRequest;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

import java.util.concurrent.CompletionStage;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/frontendcontroller-service")
public
class EchoFrontEndControllerServiceController
    extends AbstractFrontEndControllerServiceController<IEchoFrontEndControllerService>
{
    @Inject
    public
    EchoFrontEndControllerServiceController(
        @Named("Echo")
        IEchoFrontEndControllerService service)
    {
        super(service);
    }

    @PostMapping(
        value = "/handle-echo-input",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<HandleEchoInputReply>
    handleEchoInput(@RequestBody HandleEchoInputRequest request)
    {
        getLogger().info("delegating handleEchoInput to implementation service");
        return getService().handleEchoInput(request);
    }

    @PostMapping(
        value = "/get-echo-output",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompletionStage<GetEchoOutputReply>
    getEchoOutput(@RequestBody GetEchoOutputRequest request)
    {
        getLogger().info("delegating getEchoOutput to implementation service");
        return getService().getEchoOutput(request);
    }
}

//////////////////////////////////////////////////////////////////////////////
