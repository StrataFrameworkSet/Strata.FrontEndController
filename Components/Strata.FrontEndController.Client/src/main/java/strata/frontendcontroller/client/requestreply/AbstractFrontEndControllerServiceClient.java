//////////////////////////////////////////////////////////////////////////////
// AbstractFrontEndControllerServiceClient.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.requestreply;

import jakarta.ws.rs.client.ClientBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import strata.client.core.service.AbstractRestClient;

import javax.net.ssl.SSLContext;

public abstract
class AbstractFrontEndControllerServiceClient
    extends AbstractRestClient
    implements IFrontEndControllerServiceClient
{
    protected
    AbstractFrontEndControllerServiceClient(String baseUrl,String servicePath)
    {
        super(
            createBuilder(),
            preprocessBaseUrl(baseUrl),
            servicePath);
    }

    public AbstractFrontEndControllerServiceClient
    setAuthorizationHeader(String accessToken)
    {
        setHeader("Authorization","Bearer " + accessToken);
        return this;
    }

    private static String
    preprocessBaseUrl(String baseUrl)
    {
        return
            baseUrl.endsWith("/")
                ? baseUrl
                : baseUrl + '/';
    }

    private static ClientBuilder
    createBuilder() throws RuntimeException
    {
        return
            ClientBuilder
                .newBuilder()
                .sslContext(createSslContext());
    }

    private static SSLContext
    createSslContext() throws RuntimeException
    {
        try
        {
            Resource keystore = new ClassPathResource("hello-service.p12");
            return
                new SSLContextBuilder()
                    .loadTrustMaterial(
                        keystore.getURL(),
                        "hello-service".toCharArray())
                    .build();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}

//////////////////////////////////////////////////////////////////////////////
