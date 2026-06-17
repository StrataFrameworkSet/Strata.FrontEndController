//////////////////////////////////////////////////////////////////////////////
// ApplicationConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import strata.foundation.spring.inject.SingletonScoped;

@Configuration
public
class ApplicationConfiguration
{
    @Bean
    @SingletonScoped
    public ModelMapper
    mapper()
    {
        return new ModelMapper().registerModule(new MappingModule());
    }
}

//////////////////////////////////////////////////////////////////////////////
