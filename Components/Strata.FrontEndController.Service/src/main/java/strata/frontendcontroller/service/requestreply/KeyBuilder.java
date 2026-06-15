//////////////////////////////////////////////////////////////////////////////
// KeyBuilder.java
//////////////////////////////////////////////////////////////////////////////

package strata.frontendcontroller.service.requestreply;

import java.util.Optional;

public
class KeyBuilder 
{
    public static final String KEY_SEPARATOR = ".";

    public
    KeyBuilder() {}

    public String
    build(
        Optional<String> source,
        Optional<String> destination,
        Optional<String> command)
    {
        StringBuilder builder = new StringBuilder();

        source.ifPresent(s -> append(builder,s));
        destination.ifPresent(d -> append(builder,d));
        command.ifPresent(c -> append(builder,c));
        return builder.toString();
    }

    private void
    append(StringBuilder builder,String element)
    {
        if (!builder.isEmpty())
            builder.append(KEY_SEPARATOR);

        builder.append(element);
    }

    public static String
    build(String source,String destination,String command)
    {
        return
            new KeyBuilder()
                .build(
                    Optional.ofNullable(source),
                    Optional.ofNullable(destination),
                    Optional.ofNullable(command));
    }
}

//////////////////////////////////////////////////////////////////////////////
