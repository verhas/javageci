package javax0.geci.jamal;

import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class ReflectionMaps {

    /**
     * This FIELD_MACRO_PATTERN contains two parts: group(1) is the name of the class
     * group(2) is the selector expression, optionally empty
     */

    public static final WeakHashMap<String, Field> globalFieldsMap = new WeakHashMap<>();

    public static final WeakHashMap<String, Method> globalMethodMap = new WeakHashMap<>();

}
