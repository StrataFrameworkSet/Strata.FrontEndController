import {ICompletionStage} from "strata.foundation.core/Concurrent";
import {HandleInputRequest} from "./HandleInputRequest";
import {HandleInputReply} from "./HandleInputReply";
import {GetOutputRequest} from "./GetOutputRequest";
import {GetOutputReply} from "./GetOutputReply";

export
interface IFrontEndControllerService
{
    handleInput(request: HandleInputRequest):
        ICompletionStage<HandleInputReply>;

    getOutput(request: GetOutputRequest):
        ICompletionStage<GetOutputReply>;
}
