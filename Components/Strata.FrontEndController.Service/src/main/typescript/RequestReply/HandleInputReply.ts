import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {FrontEndInput} from "./FrontEndInput";
import {FrontEndOutput} from "./FrontEndOutput";

export
interface HandleInputReply<I extends FrontEndInput,O extends FrontEndOutput>
    extends AbstractServiceReply
{
    input: I;
    output: O;
}
