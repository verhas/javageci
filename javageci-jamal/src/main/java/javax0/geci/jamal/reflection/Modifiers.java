package javax0.geci.jamal.reflection;

import javax0.geci.jamal.Reflection;
import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

public class Modifiers implements Macro {
    @Override
    public String evaluate(Input in, Processor processor) {
        final var entityName = in.toString().trim();
        final int modifiers;
        if (Reflection.globalFieldsMap.containsKey(entityName)) {
            var field = Reflection.globalFieldsMap.get(entityName);
            modifiers = field.getModifiers();
        } else if (Reflection.globalMethodMap.containsKey(entityName)) {
            var method = Reflection.globalMethodMap.get(entityName);
            modifiers = method.getModifiers();
        } else {
            throw new IllegalArgumentException("Entiry identified with " + entityName + " was not found.");
        }
        return new ModifiersBuilder(modifiers).toString();
    }
}