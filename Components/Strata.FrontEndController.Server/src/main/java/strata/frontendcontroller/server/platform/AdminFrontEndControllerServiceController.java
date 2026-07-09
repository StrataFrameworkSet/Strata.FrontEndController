//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceController.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/frontendcontroller-admin-service")
public
class AdminFrontEndControllerServiceController
    extends AbstractFrontEndControllerServiceController
{
    @Inject
    public
    AdminFrontEndControllerServiceController(
        @Named("Admin")
        IFrontEndControllerService imp)
    {
        super(imp);
    }
}

//////////////////////////////////////////////////////////////////////////////
