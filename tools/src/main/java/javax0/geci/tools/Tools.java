package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Gecis;
import javax0.geci.api.Source;

import java.lang.reflect.AnnotatedElement;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
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

    public static Map<String, String> getParameters(AnnotatedElement element, String id) {
        final var strings = getGecis(element);
        for (var string : strings) {
            if (string.startsWith(id + " ")) {
                var pars = new HashMap<String,String>();
                var matcher = pattern.matcher(string);
                while (matcher.find()) {
                    var key = matcher.group(1);
                    var value = matcher.group(2);
                    pars.put(key, value);
                }
                return Map.copyOf(pars);
            }
        }
        return Map.of();
    }

    public static Class<?> getClass(Source source) throws ClassNotFoundException {
        var file = source.file();
        String className = String.join(".", source.file().split("[\\/]"))
                .replaceAll("\\.java$","");
        return Class.forName(className);
    }
}
