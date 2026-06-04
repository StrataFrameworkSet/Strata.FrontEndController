//////////////////////////////////////////////////////////////////////////////
// FrontEndInputMapper.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import java.util.Optional;

public
class FrontEndInputMapper
{
    public <T extends FrontEndInput> Optional<T>
    map(Class<T> type,FrontEndInput input)
    {
        try
        {
            return Optional.of(type.cast(input));
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public <T extends FrontEndInput> Optional<T>
    map(Class<T> type,HandleInputRequest request)
    {
        try
        {
            return map(type,request.getInput());
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public <T extends FrontEndInput> Optional<T>
    map(Class<T> type,HandleInputReply reply)
    {
        try
        {
            return map(type,reply.getInput());
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public static FrontEndInputMapper
    of() { return new FrontEndInputMapper(); }
}

//////////////////////////////////////////////////////////////////////////////
