import {IFrontEndControllerService} from "strata.frontendcontroller.service/RequestReply";

export
interface IFrontEndControllerServiceClient
    extends IFrontEndControllerService
{
    setAuthorization(accessToken: string): IFrontEndControllerServiceClient;

    close(): void;
}

