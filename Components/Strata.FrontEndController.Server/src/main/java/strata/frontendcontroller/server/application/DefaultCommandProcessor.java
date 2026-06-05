//////////////////////////////////////////////////////////////////////////////
// DefaultCommandProcessor.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.server.application;

import strata.frontendcontroller.service.requestreply.FrontEndInput;
import strata.frontendcontroller.service.requestreply.FrontEndOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public
class DefaultCommandProcessor
    implements ICommandProcessor
{
    private final Map<String,ICommand> commands;

    public
    DefaultCommandProcessor()
    {
        commands = new HashMap<>();
    }

    @Override
    public FrontEndOutput
    process(FrontEndInput input)
    {
        return null;
    }

    @Override
    public FrontEndOutput
    getOutput(String key)
    {
        return null;
    }

    protected Optional<ICommand>
    getCommand(String key)
    {
        return Optional.ofNullable(commands.get(key));
    }
}

//////////////////////////////////////////////////////////////////////////////
