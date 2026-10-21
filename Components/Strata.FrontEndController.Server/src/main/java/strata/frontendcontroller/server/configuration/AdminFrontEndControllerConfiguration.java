//////////////////////////////////////////////////////////////////////////////
// AdminFrontEndControllerConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.configuration;

import jakarta.inject.Named;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import strata.foundation.spring.inject.RequestScoped;

import java.util.concurrent.Executor;
import strata.frontendcontroller.server.application.ApplicationConfiguration;
import strata.frontendcontroller.server.application.FrontEndControllerService;
import strata.frontendcontroller.server.application.IRequestProcessor;
import strata.frontendcontroller.server.platform.PlatformConfiguration;
import strata.frontendcontroller.server.platform.AdminFrontEndControllerServiceController;
import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

@Configuration
@Import({ApplicationConfiguration.class})
public
class AdminFrontEndControllerConfiguration
{
    @Bean
    @RequestScoped
    @Named("Admin")
    public IRequestProcessor
    adminRequestProcessor()
    {
        throw
            new UnsupportedOperationException(
                "Must be overridden by application");
    }

    @Bean
    @Scope("singleton")
    @Named("Admin")
    public Executor
    adminExecutor()
    {
        throw
            new UnsupportedOperationException(
                "Must be overridden by application");
    }

    @Bean
    @RequestScoped
    @Named("Admin")
    public IFrontEndControllerService
    adminFrontEndControllerService(
        @Named("Admin")
        IRequestProcessor processor,
        @Named("Admin")
        Executor          executor,
        ModelMapper       mapper)
    {
        return new FrontEndControllerService(processor,mapper,executor);
    }

    @Bean
    @RequestScoped
    @Named("Admin")
    public AdminFrontEndControllerServiceController
    adminFrontEndControllerServiceController(
        @Named("Admin")
        IFrontEndControllerService service)
    {
        return new AdminFrontEndControllerServiceController(service);
    }
}

//////////////////////////////////////////////////////////////////////////////
