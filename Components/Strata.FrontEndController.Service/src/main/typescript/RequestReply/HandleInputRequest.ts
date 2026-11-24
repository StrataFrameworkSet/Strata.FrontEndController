import {AbstractServiceRequest} from "strata.foundation.core/Transfer";
import {FrontEndInput} from "./FrontEndInput";

export
interface HandleInputRequest<I extends FrontEndInput>
    extends AbstractServiceRequest
{
    input: I;
}
