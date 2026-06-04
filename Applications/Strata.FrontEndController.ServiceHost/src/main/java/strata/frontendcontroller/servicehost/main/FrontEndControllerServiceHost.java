//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceHost.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.servicehost.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class,SecurityAutoConfiguration.class})
public
class FrontEndControllerServiceHost
{
    public static void
    main(String[] args)
    {
        SpringApplication application =
            new SpringApplication(FrontEndControllerServiceHost.class);

        application.setAdditionalProfiles("production");
        application.run(args);
    }
}

//////////////////////////////////////////////////////////////////////////////
