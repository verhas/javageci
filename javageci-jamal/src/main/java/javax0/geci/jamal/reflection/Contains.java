package javax0.geci.jamal.reflection;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

/**
 * A simple implementation of the String.contains functionality. It can be used as
 *
 *  ((@contains abstract private abstract static))
 *
 *  This is mainly to check the
 *
 */
public class Contains implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var parts = InputHandler.getParts(in,2);
        if( parts.length < 2 ){
            throw new BadSyntax("{@contains A B} needs two arguments");
        }
        return ""+parts[1].contains(parts[0]);
    }
}
