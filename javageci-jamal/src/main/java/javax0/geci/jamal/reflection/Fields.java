package javax0.geci.jamal.reflection;

import javax0.geci.jamal.Reflection;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Fields implements Macro {
    private Map<String, Field> fieldMap = new HashMap<>();
    private static final Pattern FIELD_MACRO_PATTERN = Pattern.compile("(.*?)\\|(.*)");
    @Override
    public String evaluate(Input in, Processor processor) {
        Matcher matcher = FIELD_MACRO_PATTERN.matcher(in.toString());
        if (matcher.matches()) {
            try {
                final String className = matcher.group(1).trim();
                Selector selector = Selector.compile(matcher.group(2));
                Class<?> klass = GeciReflectionTools.classForName(className);
                var declaredFields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
                var ret = new StringBuilder();
                var list = Arrays.stream(declaredFields).filter(selector::match).collect(Collectors.toList());
                var sep = "";
                for (final var field : list) {
                    final var fingerPrint = GeciReflectionTools.getGenericTypeName(klass) + "." + field.getName();
                    fieldMap.put(fingerPrint, field);
                    Reflection.globalFieldsMap.put(fingerPrint, field);
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