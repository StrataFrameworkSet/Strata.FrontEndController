import {AbstractServiceRequest} from "strata.foundation.core/Transfer";

export
interface GetOutputRequest
    extends AbstractServiceRequest
{
    userId?: number | null;
    source: string;
    command?: string | null;
    destination?: string | null;
}
