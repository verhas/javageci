package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import static javax0.jamal.tools.InputHandler.getParts;

/**
 * A simple implementation of the String {@link String#equals(Object) equals()} functionality. It can be used as
 *
 * <pre>{@code
 *  {%@equals /something/something%}
 * }</pre>
 * <p>
 * The return value of the macro is either the string literal {@code true} or the string literal {@code false}.
 * <p>
 * The arguments can be separated by space in case the {@code something} does not contain any space, using any
 * non-alphanumeric character or using `regex` as defined in {@link javax0.jamal.tools.InputHandler#getParts(Input)
 * getParts()}
 */
public class Equals implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var parts = getParts(in, 2);
        if (parts.length < 2) {
            throw new BadSyntax("Macro equals needs two argument");
        }
        return "" + parts[0].equals(parts[1]);
    }
}
