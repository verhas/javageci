package javax0.geci.jamal;

import javax0.geci.tools.Tools;
import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javax0.geci.tools.MethodTool.with;

public class Reflection {


    public static class Type implements Macro {
        @Override
        public String evaluate(Input in, Processor processor) {
            final var entityName = in.toString().trim();
            final Class<?> type;
            if (globalFieldsMap.containsKey(entityName)) {
                var field = globalFieldsMap.get(entityName);
                type = field.getType();
            } else if (globalMethodMap.containsKey(entityName)) {
                var method = globalMethodMap.get(entityName);
                type = method.getReturnType();
            } else {
                throw new IllegalArgumentException("Entiry identified with " + entityName + " was not found.");
            }
            return Tools.getGenericTypeName(type);
        }
    }

    public static class Name implements Macro {
        @Override
        public String evaluate(Input in, Processor processor) {
            final var entityName = in.toString().trim();
            final String name;
            if (globalFieldsMap.containsKey(entityName)) {
                var field = globalFieldsMap.get(entityName);
                name = field.getName();
            } else if (globalMethodMap.containsKey(entityName)) {
                var method = globalMethodMap.get(entityName);
                name = method.getName();
            } else {
                throw new IllegalArgumentException("Entiry identified with " + entityName + " was not found.");
            }
            return name;
        }
    }

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
    private static final Pattern FIELD_MACRO_PATTERN = Pattern.compile("(.*?)\\|(.*)");
    private static WeakHashMap<String, Field> globalFieldsMap = new WeakHashMap<>();

    public static class Fields implements Macro {
        private Map<String, Field> fieldMap = new HashMap<>();

        @Override
        public String evaluate(Input in, Processor processor) {
            Matcher matcher = METHOD_MACRO_PATTERN.matcher(in.toString());
            if (matcher.matches()) {
                try {
                    final String className = matcher.group(1).trim();
                    Selector selector = Selector.compile(matcher.group(2));
                    Class<?> klass = Class.forName(className);
                    var declaredFields = Tools.getDeclaredFieldsSorted(klass);
                    var ret = new StringBuilder();
                    var list = Arrays.stream(declaredFields).filter(selector::match).collect(Collectors.toList());
                    var sep = "";
                    for (final var field : list) {
                        final var fingerPrint = Tools.getGenericTypeName(klass) + "." + field.getName();
                        fieldMap.put(fingerPrint, field);
                        globalFieldsMap.put(fingerPrint, field);
                        ret.append(sep).append(fingerPrint);
                        sep = ",";
                    }
                    return ret.toString();
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Class name in macro 'methods "
                        + matcher.group(1)
                        + " cannot be found");
                }
            } else {
                throw new IllegalArgumentException("Macro 'fields class|selection' bad syntax");
            }
        }
    }

    private static WeakHashMap<String, Method> globalMethodMap = new WeakHashMap<>();
    /**
     * This METHOD_MACRO_PATTERN contains two parts: group(1) is the name of the class
     * group(2) is the selector expression, optionally empty
     */
    private static final Pattern METHOD_MACRO_PATTERN = Pattern.compile("(.*?)\\|(.*)");

    public static class Methods implements Macro {
        private Map<String, Method> methodMap = new HashMap<>();

        @Override
        public String evaluate(Input in, Processor processor) {
            Matcher matcher = METHOD_MACRO_PATTERN.matcher(in.toString());
            if (matcher.matches()) {
                try {
                    final String className = matcher.group(1).trim();
                    Selector selector = Selector.compile(matcher.group(2));
                    Class<?> klass = Class.forName(className);
                    var declaredMethods = Tools.getDeclaredMethodsSorted(klass);
                    var ret = new StringBuilder();
                    var list = Arrays.stream(declaredMethods).filter(selector::match).collect(Collectors.toList());
                    var sep = "";
                    for (final var method : list) {
                        var fingerPrint = with(method).fingerprint();
                        methodMap.put(fingerPrint, method);
                        globalMethodMap.put(fingerPrint, method);
                        ret.append(sep).append(fingerPrint);
                        sep = ",";
                    }
                    return ret.toString();
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Class name in macro 'methods "
                        + matcher.group(1)
                        + " cannot be found");
                }
            } else {
                throw new IllegalArgumentException("Macro 'methods class|selection' bad syntax");
            }
        }
    }
}
