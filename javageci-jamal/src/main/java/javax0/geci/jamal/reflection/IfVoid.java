package javax0.geci.jamal.reflection;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;
import javax0.jamal.tools.InputHandler;

import static javax0.jamal.tools.InputHandler.skipWhiteSpaces;

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
        return "ifNotVoid";
    }
}
