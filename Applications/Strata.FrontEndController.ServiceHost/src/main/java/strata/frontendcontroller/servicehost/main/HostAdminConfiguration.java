//////////////////////////////////////////////////////////////////////////////
// HostAdminConfiguration.java
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
import strata.frontendcontroller.server.configuration.AdminFrontEndControllerConfiguration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public
class HostAdminConfiguration
    extends AdminFrontEndControllerConfiguration
{
    public static final String AUDIENCE = "Admin";

    private final ListableBeanFactory beanFactory;

    public
    HostAdminConfiguration(ListableBeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    @Override
    @Bean
    @RequestScoped
    @Named("Admin")
    public IRequestProcessor
    adminRequestProcessor()
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
    @Named("Admin")
    public Executor
    adminExecutor()
    {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

//////////////////////////////////////////////////////////////////////////////
