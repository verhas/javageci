package javax0.geci.jamal;

import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class Reflection {



    public static class Modifiers implements Macro {
        @Override
        public String evaluate(Input in, Processor processor) {
            final var entityName = in.toString().trim();
            final int modifiers;
            if (globalFieldsMap.containsKey(entityName)) {
                var field = globalFieldsMap.get(entityName);
                modifiers = field.getModifiers();
            } else if (globalMethodMap.containsKey(entityName)) {
                var method = globalMethodMap.get(entityName);
                modifiers = method.getModifiers();
            } else {
                throw new IllegalArgumentException("Entiry identified with " + entityName + " was not found.");
            }
            return new ModifiersBuilder(modifiers).toString();
        }
    }

    /**
     * This FIELD_MACRO_PATTERN contains two parts: group(1) is the name of the class
     * group(2) is the selector expression, optionally empty
     */

    public static WeakHashMap<String, Field> globalFieldsMap = new WeakHashMap<>();



    public static WeakHashMap<String, Method> globalMethodMap = new WeakHashMap<>();



}
