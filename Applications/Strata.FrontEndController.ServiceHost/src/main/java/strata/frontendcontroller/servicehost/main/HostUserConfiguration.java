//////////////////////////////////////////////////////////////////////////////
// HostUserConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.servicehost.main;

import jakarta.inject.Named;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import strata.foundation.spring.inject.RequestScoped;
import strata.frontendcontroller.server.application.IProcessAction;
import strata.frontendcontroller.server.application.IRequestProcessor;
import strata.frontendcontroller.server.configuration.UserFrontEndControllerConfiguration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public
class HostUserConfiguration
    extends UserFrontEndControllerConfiguration
{
    public static final String AUDIENCE = "User";

    private final ListableBeanFactory beanFactory;

    public
    HostUserConfiguration(ListableBeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    @Override
    @Bean
    @RequestScoped
    @Named("User")
    public IRequestProcessor
    userRequestProcessor()
    {
        return
            new HostRequestProcessor(
                BeanFactoryAnnotationUtils.qualifiedBeansOfType(
                    beanFactory,
                    IProcessAction.class,
                    AUDIENCE));
    }

    @Override
    @Bean
    @Scope("singleton")
    @Named("User")
    public Executor
    userExecutor()
    {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

//////////////////////////////////////////////////////////////////////////////
