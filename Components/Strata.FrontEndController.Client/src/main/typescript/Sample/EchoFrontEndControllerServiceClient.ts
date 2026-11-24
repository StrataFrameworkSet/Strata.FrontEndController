//////////////////////////////////////////////////////////////////////////////
// EchoFrontEndControllerServiceClient.ts
//////////////////////////////////////////////////////////////////////////////

import {AbstractFrontEndControllerServiceClient} from "../RequestReply/AbstractFrontEndControllerServiceClient";
import type {IEchoFrontEndControllerServiceClient} from "./IEchoFrontEndControllerServiceClient";
import {ICompletionStage} from "strata.foundation.core/Concurrent";
import {
    HandleEchoInputRequest,
    HandleEchoInputReply,
    GetEchoOutputRequest,
    GetEchoOutputReply
} from "strata.frontendcontroller.service/Sample";

export
class EchoFrontEndControllerServiceClient
    extends AbstractFrontEndControllerServiceClient
    implements IEchoFrontEndControllerServiceClient
{
    constructor(baseUrl: string)
    {
        super(baseUrl);
    }

    handleEchoInput(request: HandleEchoInputRequest): ICompletionStage<HandleEchoInputReply>
    {
        return this.doPost<HandleEchoInputReply,HandleEchoInputRequest>(
            "handle-echo-input",
            this.initializeRequest(request));
    }

    getEchoOutput(request: GetEchoOutputRequest): ICompletionStage<GetEchoOutputReply>
    {
        return this.doPost<GetEchoOutputReply,GetEchoOutputRequest>(
            "get-echo-output",
            this.initializeRequest(request));
    }
}

//////////////////////////////////////////////////////////////////////////////
