//////////////////////////////////////////////////////////////////////////////
// DomainConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.annotation.RequestScope;
import strata.server.core.unitofwork.IUnitOfWork;

@Configuration
@EnableTransactionManagement
public
class DomainConfiguration
{
    @Bean
    @RequestScope
    public IFrontEndControllerRepository
    frontendcontrollerRepository(IUnitOfWork unitOfWork)
    {
        return new FrontEndControllerRepository(unitOfWork);
    }
}

//////////////////////////////////////////////////////////////////////////////
