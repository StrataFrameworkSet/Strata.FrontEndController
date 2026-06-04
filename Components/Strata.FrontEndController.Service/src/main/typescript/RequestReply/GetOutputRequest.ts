import {AbstractServiceRequest} from "strata.foundation.core/Transfer";

export
interface GetOutputRequest
    extends AbstractServiceRequest
{
    userId: number;
    source: string;
    destination: string;
}
