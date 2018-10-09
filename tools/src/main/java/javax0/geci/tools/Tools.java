package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Gecis;
import javax0.geci.tools.reflection.ModifiersBuilder;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Tools {

    private static final Pattern pattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*'(.*?)'");

    public static String[] getGecis(AnnotatedElement element) {
        final var annotations = element.getDeclaredAnnotation(Gecis.class);
        if (annotations == null) {
            final var annotation = element.getDeclaredAnnotation(Geci.class);
            if (annotation == null) {
                return new String[0];
            } else {
                return new String[]{annotation.value()};
            }
        }
        final var gecis = new String[annotations.value().length];
        var i = 0;
        for (final var annotation : annotations.value()) {
            gecis[i++] = annotation.value();
        }
        return gecis;
    }

    public static Map<String, String> getParameters(String s) {
        var pars = new HashMap<String, String>();
        var matcher = pattern.matcher(s);
        while (matcher.find()) {
            var key = matcher.group(1);
            var value = matcher.group(2);
            pars.put(key, value);
        }
        return pars;
    }

    public static CompoundParams getParameters(AnnotatedElement element, String generatorMnemonic) {
        final var strings = getGecis(element);
        for (var string : strings) {
            if (string.startsWith(generatorMnemonic + " ")) {
                var parametersString = string.substring(generatorMnemonic.length() + 1);
                return new CompoundParams(generatorMnemonic, Map.copyOf(getParameters(parametersString)));
            } else if (string.equals(generatorMnemonic)) {
                return new CompoundParams(generatorMnemonic, Map.of());
            }
        }
        return null;
    }

    public static String modifiersString(Method method) {
        return new ModifiersBuilder(method.getModifiers()).toString();
    }

    public static String typeAsString(Field field) {
        var s = field.getGenericType().getTypeName();
        s = normalizeTypeName(s);
        return s;
    }

    public static String typeAsString(Method method) {
        var s = method.getReturnType().getTypeName();
        s = normalizeTypeName(s);
        return s;
    }

    private static String normalizeTypeName(String s) {
        if (s.startsWith("java.lang.")) {
            s = s.substring("java.lang.".length());
        }
        return s;
    }

}
