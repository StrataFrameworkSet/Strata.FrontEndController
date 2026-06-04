import {AbstractServiceReply} from "strata.foundation.core/Transfer";
import {FrontEndInput} from "./FrontEndInput";
import {FrontEndOutput} from "./FrontEndOutput";

export
interface HandleInputReply
    extends AbstractServiceReply
{
    input: FrontEndInput;
    output: FrontEndOutput;
}
