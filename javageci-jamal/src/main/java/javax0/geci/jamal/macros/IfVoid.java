package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

import static javax0.jamal.tools.InputHandler.skipWhiteSpaces;

/**
 * This argument fetches an identifier from the start of its input and returns the rest of it if it is {@code void}. If
 * the identifier is anything else but {@code void} then it returns the empty string.
 */
public class IfVoid implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        skipWhiteSpaces(in);
        final String type;
        type = InputHandler.fetchId(in);
        skipWhiteSpaces(in);
        if( "void".equals(type)){
            return in.toString();
        }
        return "";
    }

    @Override
    public String getId() {
        return "ifVoid";
    }
}
