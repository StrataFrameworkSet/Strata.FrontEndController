//////////////////////////////////////////////////////////////////////////////
// TestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.server.platform.PlatformConfiguration;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.request.RequestScope;
import strata.frontendcontroller.server.domain.DomainConfiguration;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.core.inject.ApplicationConfigurationProvider;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:test.properties")
@Import({ApplicationConfiguration.class,DomainConfiguration.class,PlatformConfiguration.class})
public
class TestConfiguration
{
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

    @Bean
    @Scope("singleton")
    public IRequestProcessor
    requestProcessor()
    {
        Map<String,IProcessAction> actions = new HashMap<>();
        IGetOutputAction action = (GetOutputRequest r) -> new TestOutput();

        actions.put("test-source",action);
        return new TestRequestProcessor(actions);
    }
}

//////////////////////////////////////////////////////////////////////////////
