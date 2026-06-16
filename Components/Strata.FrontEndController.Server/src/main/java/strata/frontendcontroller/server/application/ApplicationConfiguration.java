//////////////////////////////////////////////////////////////////////////////
// ApplicationConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@EnableTransactionManagement
public
class ApplicationConfiguration
{
    @Bean
    @RequestScope
    public ITransactionalFrontEndControllerService
    frontendcontrollerService(
        IRequestProcessor processor,
        ModelMapper       mapper)
    {
        return new FrontEndControllerService(processor,mapper);
    }

    @Bean
    @Scope("singleton")
    public ModelMapper
    mapper()
    {
        return new ModelMapper().registerModule(new MappingModule());
    }
}

//////////////////////////////////////////////////////////////////////////////
