//////////////////////////////////////////////////////////////////////////////
// IFrontEndControllerServiceClient.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.client.requestreply;

import strata.frontendcontroller.service.requestreply.IFrontEndControllerService;

public
interface IFrontEndControllerServiceClient
    extends IFrontEndControllerService
{
    IFrontEndControllerServiceClient
    setAuthorizationHeader(String accessToken);

    void
    close();
}

//////////////////////////////////////////////////////////////////////////////
