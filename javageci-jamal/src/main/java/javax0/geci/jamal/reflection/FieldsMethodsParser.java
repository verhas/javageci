package javax0.geci.jamal.reflection;

import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FieldsMethodsParser<T> {

    private static final Pattern FIELD_MACRO_PATTERN = Pattern.compile("(.*?)(?:/(.*))?");

    final Selector<T> selector;
    final Class<?> klass;

    private FieldsMethodsParser(Selector<T> selector, Class<?> klass) {
        this.selector = selector;
        this.klass = klass;
    }

    static FieldsMethodsParser parse(String input, String macroName) {
        Matcher matcher = FIELD_MACRO_PATTERN.matcher(input);
        if (matcher.matches()) {
            try {
                final String className = matcher.group(1).trim();
                Selector<?> selector = Selector.compile(Optional.ofNullable(matcher.group(2)).orElse("true"));
                Class<?> klass = GeciReflectionTools.classForName(className);
                return new FieldsMethodsParser<>(selector,klass);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class name in macro '" + macroName + " "
                        + matcher.group(1)
                        + " cannot be found");
            }
        } else {
            throw new IllegalArgumentException("Macro '" + macroName + " class/selection' bad syntax");
        }
    }
}
