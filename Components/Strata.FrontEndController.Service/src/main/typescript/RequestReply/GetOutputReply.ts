import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {FrontEndOutput} from "./FrontEndOutput";

export
interface GetOutputReply
    extends AbstractServiceReply
{
    output: FrontEndOutput;
}
