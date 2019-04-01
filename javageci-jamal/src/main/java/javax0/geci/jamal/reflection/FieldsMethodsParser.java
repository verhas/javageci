package javax0.geci.jamal.reflection;

import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple parse utility for the macros {@link Methods} and {@link Fields} to parse the macro arguments.
 * In these macros the argument has to be formatted as
 * <pre>
 *     {@code
 *     className/selector
 *     }
 * </pre>
 * The class name separated from the selector using a / character. The selector part is optional. In this case the
 * {@code /} character should also be omitted.
 * <p>
 * The selector expression format is defined in {@link Selector} and it may contain {@code /} character.
 *
 * @param <T> is either Field or Method
 */
class FieldsMethodsParser<T> {

    private static final Pattern FIELD_MACRO_PATTERN = Pattern.compile("(.*?)(?:/(.*))?");

    final Selector<T> selector;
    final Class<?> klass;

    private FieldsMethodsParser(Selector<T> selector, Class<?> klass) {
        this.selector = selector;
        this.klass = klass;
    }

    /**
     * Parse the input and return the parse result.
     *
     * @param input     the macro argument that has the {@code class[ / selector ]} format
     * @param macroName the name of the macro using this method, used only to create exception when the format
     *                  is wrong
     * @return a new object that contains the selector and the klass available for the macro classes in the same package
     */
    static FieldsMethodsParser parse(String input, String macroName) {
        Matcher matcher = FIELD_MACRO_PATTERN.matcher(input);
        if (matcher.matches()) {
            try {
                final String className = matcher.group(1).trim();
                Selector<?> selector = Selector.compile(Optional.ofNullable(matcher.group(2)).orElse("true"));
                Class<?> klass = GeciReflectionTools.classForName(className);
                return new FieldsMethodsParser<>(selector, klass);
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
