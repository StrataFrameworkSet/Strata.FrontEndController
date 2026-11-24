//////////////////////////////////////////////////////////////////////////////
// IEchoFrontEndControllerServiceClient.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.sample;

import strata.frontendcontroller.client.requestreply.IFrontEndControllerServiceClient;
import strata.frontendcontroller.service.sample.IEchoFrontEndControllerService;

public
interface IEchoFrontEndControllerServiceClient
    extends IFrontEndControllerServiceClient,
            IEchoFrontEndControllerService
{
    @Override
    IEchoFrontEndControllerServiceClient
    setAuthorizationHeader(String accessToken);
}

//////////////////////////////////////////////////////////////////////////////
