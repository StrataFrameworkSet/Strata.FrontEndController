//////////////////////////////////////////////////////////////////////////////
// IEchoFrontEndControllerService.ts
//////////////////////////////////////////////////////////////////////////////

import {ICompletionStage}                from "strata.foundation.core/Concurrent";
import type {IFrontEndControllerService} from "../RequestReply/IFrontEndControllerService";
import type {HandleEchoInputRequest}     from "./HandleEchoInputRequest";
import type {HandleEchoInputReply}       from "./HandleEchoInputReply";
import type {GetEchoOutputRequest}       from "./GetEchoOutputRequest";
import type {GetEchoOutputReply}         from "./GetEchoOutputReply";

export
interface IEchoFrontEndControllerService
    extends IFrontEndControllerService
{
    handleEchoInput(request: HandleEchoInputRequest):
        ICompletionStage<HandleEchoInputReply>;

    getEchoOutput(request: GetEchoOutputRequest):
        ICompletionStage<GetEchoOutputReply>;
}

//////////////////////////////////////////////////////////////////////////////
