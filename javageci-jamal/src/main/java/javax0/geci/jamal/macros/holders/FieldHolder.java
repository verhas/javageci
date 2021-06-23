package javax0.geci.jamal.macros.holders;

import javax0.geci.jamal.util.EntityStringer;
import javax0.jamal.api.BadSyntax;

import java.lang.reflect.Field;

public class FieldHolder extends Holder<Field> {

    public FieldHolder(Field field) {
        super(field);
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
                    return EntityStringer.field2Fingerprint(object, "$" + parameters[0]);
                default: // use the parameter as a template
                    return EntityStringer.field2Fingerprint(object,  parameters[0]);
            }
        }
        return super.evaluate(parameters);
    }

    @Override
    public String getId() {
        return object.getName();
    }
}
