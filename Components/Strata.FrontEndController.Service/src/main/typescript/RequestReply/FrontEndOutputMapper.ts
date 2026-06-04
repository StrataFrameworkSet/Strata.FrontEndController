//////////////////////////////////////////////////////////////////////////////
// FrontEndOutputMapper.ts
//////////////////////////////////////////////////////////////////////////////

import type {FrontEndOutput} from "./FrontEndOutput";
import      {Optional}       from "strata.foundation.core/Utility";

export
class FrontEndOutputMapper
{
    mapFromOutput<T extends FrontEndOutput>(output: FrontEndOutput): Optional<T>
    {
        try
        {
            return Optional.of<T>(output as T);
        }
        catch (e: unknown)
        {
            return Optional.empty<T>();
        }
    }

    mapFromReply<T extends FrontEndOutput>(reply: { output: FrontEndOutput }): Optional<T>
    {
        try
        {
            return this.mapFromOutput<T>(reply.output);
        }
        catch (e: unknown)
        {
            return Optional.empty<T>();
        }
    }

    public static of(): FrontEndOutputMapper
    {
        return new FrontEndOutputMapper();
    }
}

//////////////////////////////////////////////////////////////////////////////
