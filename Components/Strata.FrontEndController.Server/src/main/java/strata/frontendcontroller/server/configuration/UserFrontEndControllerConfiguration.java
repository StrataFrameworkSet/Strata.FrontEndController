//////////////////////////////////////////////////////////////////////////////
// UserFrontEndControllerConfiguration.java
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
import strata.frontendcontroller.server.platform.UserFrontEndControllerServiceController;
import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

@Configuration
@Import({ApplicationConfiguration.class})
public
class UserFrontEndControllerConfiguration
{
    @Bean
    @RequestScoped
    @Named("User")
    public IRequestProcessor
    userRequestProcessor()
    {
        throw
            new UnsupportedOperationException(
                "Must be overridden by application");
    }

    @Bean
    @Scope("singleton")
    @Named("User")
    public Executor
    userExecutor()
    {
        throw
            new UnsupportedOperationException(
                "Must be overridden by application");
    }

    @Bean
    @RequestScoped
    @Named("User")
    public IFrontEndControllerService
    userFrontEndControllerService(
        @Named("User")
        IRequestProcessor processor,
        @Named("User")
        Executor          executor,
        ModelMapper       mapper)
    {
        return new FrontEndControllerService(processor,mapper,executor);
    }

    @Bean
    @RequestScoped
    @Named("User")
    public UserFrontEndControllerServiceController
    userFrontEndControllerServiceController(
        @Named("User")
        IFrontEndControllerService service)
    {
        return new UserFrontEndControllerServiceController(service);
    }
}

//////////////////////////////////////////////////////////////////////////////
