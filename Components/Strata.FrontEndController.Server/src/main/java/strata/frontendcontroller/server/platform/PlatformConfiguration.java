//////////////////////////////////////////////////////////////////////////////
// PlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import strata.foundation.spring.inject.RequestScoped;
import strata.foundation.spring.inject.SingletonScoped;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import strata.foundation.core.configuration.IConfiguration;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.health.HealthCheckConfiguration;
import strata.server.spring.repository.LocalContainerEntityManagerFactoryBeanProvider;
import strata.server.spring.repository.RequestScopeRepositoryConfiguration;
import strata.server.spring.unitofwork.ISpringUnitOfWorkManager;
import strata.server.spring.unitofwork.JpaUnitOfWork;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;
import strata.server.spring.unitofwork.SpringUnitOfWorkSynchronizationManager;

@Configuration
@EnableTransactionManagement
@Import({HealthCheckConfiguration.class,RequestScopeRepositoryConfiguration.class})
public
class PlatformConfiguration
{
    @Bean
    @RequestScoped
    public ISpringUnitOfWorkManager
    unitOfWorkManager(JpaUnitOfWork unitOfWork)
    {
        return new JpaUnitOfWorkManager(unitOfWork);
    }

    @Bean
    @RequestScoped
    public JpaUnitOfWork
    unitOfWork(EntityManagerFactory factory)
    {
        return new JpaUnitOfWork(factory);
    }

    @Bean
    @RequestScoped
    public IUnitOfWorkSynchronizationManager
    synchronizer()
    {
        return new SpringUnitOfWorkSynchronizationManager();
    }

}

//////////////////////////////////////////////////////////////////////////////
