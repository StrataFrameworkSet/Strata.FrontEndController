//////////////////////////////////////////////////////////////////////////////
// FrontEndInputMapper.ts
//////////////////////////////////////////////////////////////////////////////

import type {FrontEndInput} from "./FrontEndInput";
import      {Optional}      from "strata.foundation.core/Utility";

export
class FrontEndInputMapper
{
    mapFromInput<T extends FrontEndInput>(input: FrontEndInput): Optional<T>
    {
        try
        {
            return Optional.of<T>(input as T);
        }
        catch (e: unknown)
        {
            return Optional.empty<T>();
        }
    }

    mapFromRequest<T extends FrontEndInput>(request: { input: FrontEndInput }): Optional<T>
    {
        try
        {
            return this.mapFromInput<T>(request.input);
        }
        catch (e: unknown)
        {
            return Optional.empty<T>();
        }
    }

    mapFromReply<T extends FrontEndInput>(reply: { input: FrontEndInput }): Optional<T>
    {
        try
        {
            return this.mapFromInput<T>(reply.input);
        }
        catch (e: unknown)
        {
            return Optional.empty<T>();
        }
    }

    public static of(): FrontEndInputMapper
    {
        return new FrontEndInputMapper();
    }
}

//////////////////////////////////////////////////////////////////////////////
