package javax0.geci.jamal.macros.holders;

import javax0.geci.jamal.util.EntityStringer;
import javax0.jamal.api.BadSyntax;

import java.lang.reflect.Method;

public class MethodHolder extends Holder<Method> {

    private final String[] imports;
    public MethodHolder(Method method, String[] imports) {
        super(method);
        this.imports = imports;
    }


    @Override
    public int expectedNumberOfArguments() {
        return 1;
    }

    @Override
    public String evaluate(String... parameters) throws BadSyntax {
        if (parameters.length == 0) {
            return object.getName();
        }
        if (parameters.length == 1) {
            switch (parameters[0]) {
                case "name":
                case "modifiers":
                case "class":
                case "type":
                case "throws":
                case "fqThrows":
                case "exceptions":
                case "fqExceptions":
                case "args":
                    return EntityStringer.method2Fingerprint(object, "$" + parameters[0], ",",",",imports);
                default: // use the parameter as a template
                    return EntityStringer.method2Fingerprint(object,  parameters[0], ",", ",",imports);
            }
        }
        return super.evaluate(parameters);
    }

    @Override
    public String getId() {
        return object.getName();
    }

}
