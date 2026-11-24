//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.sample;

import jakarta.inject.Named;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import strata.foundation.spring.inject.RequestScoped;
import strata.frontendcontroller.server.application.ApplicationConfiguration;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@Configuration
@Import({ApplicationConfiguration.class})
public
class EchoFrontEndControllerConfiguration
{
    @Bean
    @Scope("singleton")
    @Named("Echo")
    public Executor
    echoExecutor()
    {
        return ForkJoinPool.commonPool();
    }

    @Bean
    @RequestScoped
    @Named("Echo")
    public IEchoFrontEndControllerService
    echoFrontEndControllerService(
        @Named("Echo")
        Executor    executor,
        ModelMapper mapper)
    {
        return new EchoFrontEndControllerService(mapper,executor);
    }

    @Bean
    @RequestScoped
    @Named("Echo")
    public EchoFrontEndControllerServiceController
    echoFrontEndControllerServiceController(
        @Named("Echo")
        IEchoFrontEndControllerService service)
    {
        return new EchoFrontEndControllerServiceController(service);
    }
}

//////////////////////////////////////////////////////////////////////////////
