package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

/**
 * A simple implementation of the String {@link String#contains(CharSequence) contains()} functionality.
 * It can be used as
 *
 * <pre>{@code
 *  {%@contains /something/ in a string%}
 * }</pre>
 * <p>
 * The arguments can be separated by space in case the {@code something} does not contain any space, using any
 * non-alphanumeric character or using `regex` as defined in {@link javax0.jamal.tools.InputHandler#getParts(Input)
 * getParts()}
 */
public class Contains implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var parts = InputHandler.getParts(in, 2);
        if (parts.length < 2) {
            throw new BadSyntax("{@contains A B} needs two arguments");
        }
        return "" + parts[1].contains(parts[0]);
    }
}
