package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.InnerScopeDependent;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;
import javax0.jamal.tools.OptionsStore;

import static javax0.jamal.tools.InputHandler.getParts;

/**
 * This macro implements the Java String {@link String#replaceAll(String, String) replaceAll()} functionality. The
 * format is
 * <pre>{@code
 * {@code {%@replace stringInWhat regexFrom stringTo%}}
 * }</pre>
 * The arguments can be separated by space in case the {@code stringInWhat} and {@code regexFrom} does not contain any
 * space, using any non alpha numeric character or using `regex` as defined in {@link
 * javax0.jamal.tools.InputHandler#getParts(Input) getParts()}
 */
public class Replace implements Macro, InnerScopeDependent {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        InputHandler.skipWhiteSpaces(in);
        final var parts = InputHandler.getParts(in);
        if (parts.length < 2) {
            throw new BadSyntax("Marco 'replace' needs at least two arguments, gon only "
                + parts.length
                + ":\n" + String.join("\n", parts)
                + "\n----------");
        }
        final var isRegex = OptionsStore.getInstance(processor).is("regex");
        String string = parts[0];
        for (int i = 1; i < parts.length; i += 2) {
            final var from = parts[i];
            final String to;
            if (i < parts.length - 1) {
                to = parts[i + 1];
            } else {
                to = "";
            }
            if (isRegex) {
                try {
                    string = string.replaceAll(from, to);
                } catch (IllegalArgumentException e) {
                    throw new BadSyntax("There is a problem with the regular expression in macro 'replace' : "
                        + from + "\n" + to + "\n", e);
                }
            } else {
                string = string.replace(from, to);
            }
        }
        return string;
    }
}
