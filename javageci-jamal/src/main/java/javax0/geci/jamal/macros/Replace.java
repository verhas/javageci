package javax0.geci.jamal.macros;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import static javax0.jamal.tools.InputHandler.getParts;

public class Replace implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) throws BadSyntax {
        final var parts = getParts(in, 3);
        if (parts.length < 3) {
            throw new BadSyntax("replace needs three arguments");
        }
        return parts[0].replaceAll(parts[1], parts[2]);
    }
}
