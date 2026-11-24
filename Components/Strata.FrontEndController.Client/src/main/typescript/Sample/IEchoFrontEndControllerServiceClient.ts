//////////////////////////////////////////////////////////////////////////////
// IEchoFrontEndControllerServiceClient.ts
//////////////////////////////////////////////////////////////////////////////

import type {IFrontEndControllerServiceClient} from "../RequestReply/IFrontEndControllerServiceClient";
import type {IEchoFrontEndControllerService}   from "strata.frontendcontroller.service/Sample";

export
interface IEchoFrontEndControllerServiceClient
    extends IFrontEndControllerServiceClient,
            IEchoFrontEndControllerService
{
}

//////////////////////////////////////////////////////////////////////////////
