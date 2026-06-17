//////////////////////////////////////////////////////////////////////////////
// TestAdminConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.configuration;

import jakarta.inject.Named;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.web.context.request.RequestScope;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.foundation.spring.inject.RequestScoped;
import strata.frontendcontroller.server.application.*;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Configuration
@PropertySource("classpath:test.properties")
public
class TestAdminConfiguration
    extends AdminFrontEndControllerConfiguration
{
    @Override
    @Bean
    @RequestScoped
    @Named("Admin")
    public IRequestProcessor
    adminRequestProcessor()
    {
        Map<String,IProcessAction> actions = new HashMap<>();
        IGetOutputAction action = (GetOutputRequest r) -> CompletableFuture.completedFuture(new TestOutput());

        actions.put("test-source",action);
        return new TestRequestProcessor(actions);
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
