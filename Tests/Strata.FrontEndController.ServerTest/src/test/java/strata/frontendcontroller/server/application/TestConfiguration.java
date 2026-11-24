//////////////////////////////////////////////////////////////////////////////
// TestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import jakarta.inject.Named;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.request.RequestScope;
import strata.foundation.core.concurrent.CurrentThreadExecutor;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.frontendcontroller.server.platform.PlatformConfiguration;
import strata.frontendcontroller.server.sample.EchoFrontEndControllerConfiguration;

import java.util.concurrent.Executor;

@Configuration
@PropertySource("classpath:test.properties")
@Import({PlatformConfiguration.class})
public
class TestConfiguration
    extends EchoFrontEndControllerConfiguration
{
    @Override
    @Bean
    @Scope("singleton")
    @Named("Echo")
    public Executor
    echoExecutor()
    {
        return new CurrentThreadExecutor();
    }

    @Bean
    public BeanFactoryPostProcessor
    beanFactoryPostProcessor()
    {
        return
            factory ->
                factory.registerScope(
                    "request",
                    new RequestScope());
    }

    @Bean
    @Scope("singleton")
    public IConfiguration
    configuration()
    {
        return
            new ApplicationConfigurationProvider("test").get();
    }
}

//////////////////////////////////////////////////////////////////////////////
