package javax0.geci.jamal.reflection;

import javax0.geci.jamal.Reflection;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

/**
 * Gets the name of a method or field, which was discovered formerly using by the macro methods or fields and
 * returns the name the field or method.
 */
public class Name implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) {
        final var entityName = in.toString().trim();
        final String name;
        if (Reflection.globalFieldsMap.containsKey(entityName)) {
            var field = Reflection.globalFieldsMap.get(entityName);
            name = field.getName();
        } else if (Reflection.globalMethodMap.containsKey(entityName)) {
            var method = Reflection.globalMethodMap.get(entityName);
            name = method.getName();
        } else {
            throw new IllegalArgumentException("Entity identified with " + entityName + " was not found.");
        }
        return name;
    }
}
