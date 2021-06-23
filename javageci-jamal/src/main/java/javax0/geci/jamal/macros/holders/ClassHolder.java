package javax0.geci.jamal.macros.holders;

import javax0.geci.jamal.util.EntityStringer;
import javax0.jamal.api.BadSyntax;

public class ClassHolder extends Holder<Class> {

    public ClassHolder(Class field) {
        super(field);
    }

    @Override
    public String evaluate(String... parameters) throws BadSyntax {
        if (parameters.length == 0) {
            return object.getName();
        }
        if (parameters.length == 1) {
            switch (parameters[0]) {
                case "name":
                    return object.getName();
                case "simpleName":
                    return object.getSimpleName();
                case "canonical":
                case "canonicalName":
                    return object.getCanonicalName();
                case "type":
                case "typeName":
                    return object.getTypeName();
                case "generic":
                case "genericString":
                    return object.toGenericString();
                case "package":
                case "packageName":
                    return object.getPackageName();
                default: // use the name as a default
                    return object.getName();
            }
        }
        return super.evaluate(parameters);
    }

    @Override
    public String getId() {
        return object.getSimpleName();
    }
}
