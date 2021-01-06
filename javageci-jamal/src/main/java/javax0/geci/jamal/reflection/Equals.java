package javax0.geci.jamal.reflection;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

import static javax0.jamal.tools.InputHandler.getParts;

public class Equals implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var parts = getParts(in,2);
        if( parts.length < 2 ){
            throw new BadSyntax("Macro equals needs two argument");
        }
        return ""+parts[0].equals(parts[1]);
    }
}
