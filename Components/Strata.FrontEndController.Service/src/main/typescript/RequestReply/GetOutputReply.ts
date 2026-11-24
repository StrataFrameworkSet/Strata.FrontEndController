import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {FrontEndOutput} from "./FrontEndOutput";

export
interface GetOutputReply<O extends FrontEndOutput>
    extends AbstractServiceReply
{
    output: O;
}
