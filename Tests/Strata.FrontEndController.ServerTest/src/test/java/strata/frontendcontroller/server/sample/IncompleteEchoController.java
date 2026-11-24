//////////////////////////////////////////////////////////////////////////////
// IncompleteEchoController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import org.springframework.web.bind.annotation.PostMapping;
import strata.frontendcontroller.service.sample.HandleEchoInputReply;
import strata.frontendcontroller.service.sample.HandleEchoInputRequest;

import java.util.concurrent.CompletionStage;

public
class IncompleteEchoController
{
    @PostMapping("/handle-echo-input")
    public CompletionStage<HandleEchoInputReply>
    handleEchoInput(HandleEchoInputRequest request)
    {
        return null;
    }
}

//////////////////////////////////////////////////////////////////////////////
