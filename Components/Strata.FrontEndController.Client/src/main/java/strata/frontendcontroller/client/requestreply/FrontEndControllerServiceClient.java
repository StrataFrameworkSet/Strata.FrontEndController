//////////////////////////////////////////////////////////////////////////////
// FrontEndControllerServiceClient.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.requestreply;

import strata.frontendcontroller.service.requestreply.*;
import jakarta.ws.rs.client.ClientBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import strata.client.core.service.AbstractRestClient;

import javax.net.ssl.SSLContext;
import java.util.concurrent.CompletionStage;

public
class FrontEndControllerServiceClient
    extends AbstractRestClient
    implements IFrontEndControllerServiceClient
{
    public FrontEndControllerServiceClient(String baseUrl)
    {
        super(
            createBuilder(),
            preprocessBaseUrl(baseUrl),
            "frontendcontroller-service/");
    }

    @Override
    public CompletionStage<HandleInputReply>
    handleInput(HandleInputRequest request)
    {
        return
            doPostAsync(
                "handle-input",
                HandleInputReply.class,
                request);
    }

    @Override
    public CompletionStage<GetOutputReply>
    getOutput(GetOutputRequest request)
    {
        return
            doPostAsync(
                "get-output",
                GetOutputReply.class,
                request);
    }

    public FrontEndControllerServiceClient
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
