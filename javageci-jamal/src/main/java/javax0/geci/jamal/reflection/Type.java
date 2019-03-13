package javax0.geci.jamal.reflection;

import javax0.geci.jamal.Reflection;
import javax0.geci.tools.Tools;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

/**
 * Gets the name of a method or field, which was discovered formerly using by the macro methods or fields and
 * returns the return / type (class) of the field or method.
 */
public class Type implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) {
        final var entityName = in.toString().trim();
        final Class<?> type;
        if (Reflection.globalFieldsMap.containsKey(entityName)) {
            var field = Reflection.globalFieldsMap.get(entityName);
            type = field.getType();
        } else if (Reflection.globalMethodMap.containsKey(entityName)) {
            var method = Reflection.globalMethodMap.get(entityName);
            type = method.getReturnType();
        } else {
            throw new IllegalArgumentException("Entity identified with " + entityName + " was not found.");
        }
        return Tools.getGenericTypeName(type);
    }
}
