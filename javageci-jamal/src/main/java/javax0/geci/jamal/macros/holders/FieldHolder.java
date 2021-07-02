package javax0.geci.jamal.macros.holders;

import javax0.geci.jamal.util.EntityStringer;
import javax0.jamal.api.BadSyntax;

import java.lang.reflect.Field;

public class FieldHolder extends Holder<Field> {
private final String[] imports;
    public FieldHolder(Field field, String[] imports) {
        super(field);
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
                case "class": // the class name converted to simple name if the same package or is imported
                case "fqClass": // the fully qualified class name
                case "type": // the type converted to simple name if the same package or is imported
                case "fqType": // the fully qualified type
                    return EntityStringer.field2Fingerprint(object, "$" + parameters[0], imports);
                default: // use the parameter as a template
                    return EntityStringer.field2Fingerprint(object,  parameters[0], imports);
            }
        }
        return super.evaluate(parameters);
    }

    @Override
    public String getId() {
        return object.getName();
    }
}
