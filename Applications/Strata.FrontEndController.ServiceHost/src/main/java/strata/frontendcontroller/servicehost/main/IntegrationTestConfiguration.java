//////////////////////////////////////////////////////////////////////////////
// IntegrationTestConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.servicehost.main;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import strata.frontendcontroller.server.application.IGetOutputAction;
import strata.frontendcontroller.service.requestreply.GetOutputRequest;

import java.util.concurrent.CompletableFuture;

@Configuration
@Profile("integration-test")
public
class IntegrationTestConfiguration
{
    @Bean("test-source")
    @Scope("singleton")
    @Qualifier(HostUserConfiguration.AUDIENCE)
    public IGetOutputAction
    testSourceAction()
    {
        return (GetOutputRequest request) -> CompletableFuture.completedFuture(null);
    }
}

//////////////////////////////////////////////////////////////////////////////
