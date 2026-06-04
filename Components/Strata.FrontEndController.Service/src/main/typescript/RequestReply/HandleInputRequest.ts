import {AbstractServiceRequest} from "strata.foundation.core/Transfer";
import {FrontEndInput} from "./FrontEndInput";

export
interface HandleInputRequest
    extends AbstractServiceRequest
{
    input: FrontEndInput;
}
