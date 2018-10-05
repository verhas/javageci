package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Gecis;
import javax0.geci.api.Source;
import javax0.geci.tools.reflection.ModifiersBuilder;

import java.lang.reflect.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Tools {

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

    private static final Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*'(.*?)'");

    public static Map<String, String> getParameters(AnnotatedElement element, String generatorMnemonic) {
        final var strings = getGecis(element);
        for (var string : strings) {
            if (string.startsWith(generatorMnemonic + " ")) {
                var pars = new HashMap<String,String>();
                var matcher = pattern.matcher(string.substring(generatorMnemonic.length()+1));
                while (matcher.find()) {
                    var key = matcher.group(1);
                    var value = matcher.group(2);
                    pars.put(key, value);
                }
                return Map.copyOf(pars);
            }
        }
        return null;
    }

    public static Class<?> getClass(Source source) {
        var file = source.file();
        String className = source.file().replace("\\",".").replace("/",".")
                .replaceAll("\\.java$","");
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException|NoClassDefFoundError e) {
            return null;
        }
    }

    public static String modifiersString(Method method){
        return new ModifiersBuilder(method.getModifiers()).toString();
    }

    public static String typeAsString(Field field) {
        var s = field.getGenericType().getTypeName();
        if (s.startsWith("java.lang.")) {
            s = s.substring("java.lang.".length());
        }
        return s;
    }

    public static String typeAsString(Method method) {
        var s = method.getReturnType().getTypeName();
        if (s.startsWith("java.lang.")) {
            s = s.substring("java.lang.".length());
        }
        return s;
    }

    public static String modifiersString(int modifiers){
        return new ModifiersBuilder(modifiers).toString();
    }

}
