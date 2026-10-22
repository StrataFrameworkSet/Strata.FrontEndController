//////////////////////////////////////////////////////////////////////////////
// PlatformConfiguration.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.platform;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import strata.server.spring.health.HealthCheckConfiguration;

@Configuration
@Import({HealthCheckConfiguration.class})
public
class PlatformConfiguration
{
}

//////////////////////////////////////////////////////////////////////////////
