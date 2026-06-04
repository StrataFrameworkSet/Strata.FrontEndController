//////////////////////////////////////////////////////////////////////////////
// ServicePlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.annotation.RequestScope;
import strata.foundation.core.configuration.IConfiguration;
import strata.outbox.core.repository.RequestScopedRepositoryConfiguration;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.repository.LocalContainerEntityManagerFactoryBeanProvider;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;
import strata.server.spring.unitofwork.SpringUnitOfWorkSynchronizationManager;

@Configuration
@EnableTransactionManagement
@Import({RequestScopedRepositoryConfiguration.class})
public
class ServicePlatformConfiguration
    extends PlatformConfiguration
{

    @Bean
    @RequestScope
    public FrontEndControllerServiceController
    frontendcontrollerServiceController(IFrontEndControllerService service)
    {
        return new FrontEndControllerServiceController(service);
    }

}

//////////////////////////////////////////////////////////////////////////////
