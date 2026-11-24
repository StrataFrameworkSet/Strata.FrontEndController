//////////////////////////////////////////////////////////////////////////////
// FrontEndOutputMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import java.util.Optional;

public
class FrontEndOutputMapper
{
    public <T extends FrontEndOutput> Optional<T>
    map(Class<T> type,FrontEndOutput output)
    {
        try
        {
            return Optional.of(type.cast(output));
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public <T extends FrontEndOutput> Optional<T>
    map(Class<T> type,GetOutputReply<?> reply)
    {
        try
        {
            return map(type,reply.getOutput());
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public <T extends FrontEndOutput> Optional<T>
    map(Class<T> type,HandleInputReply<?,?> reply)
    {
        try
        {
            return map(type,reply.getOutput());
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public static FrontEndOutputMapper
    of() { return new FrontEndOutputMapper(); }
}

//////////////////////////////////////////////////////////////////////////////
