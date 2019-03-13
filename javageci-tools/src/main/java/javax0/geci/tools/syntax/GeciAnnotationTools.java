package javax0.geci.tools.syntax;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Generated;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GeciAnnotationTools {
    private static final Pattern ANNOTATTION_PATTERN = Pattern.compile("@Geci\\(\"(.*)\"\\)");
    private static final Pattern pattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*'(.*?)'");

    /**
     * Get the strings of the values of the {@link Geci} annotations that are on the element parameter.
     * The {@link Geci} annotation has a single value parameter that is a string.
     * <p>
     * The method takes care of the special case when there is only one {@link Geci} annotation on the element and
     * also when there are many.
     * <p>
     * Note that the annotation does not need to be the one, which is defined in the javageci annotation library.
     * It can be any annotation interface so long as long the name is {@code Geci} and the method {@code value()}
     * returns {@code java.lang.String}.
     *
     * @param element the class, method or field that is annotated.
     * @return the array of strings that contains the values of the annotations. If the element is not annotated
     * then the returned array will have zero elements. If there is one {@link Geci} annotation then the
     * returned String array will have one element. If there are many annotations then the array will contains each
     * of the values.
     */
    public static String[] getGecis(AnnotatedElement element) {
        final var allAnnotations = element.getDeclaredAnnotations();
        var annotations =
                Arrays.stream(allAnnotations)
                        .filter(a -> annotationName(a).equals("Gecis"))
                        .flatMap(g -> Arrays.stream((getValueGecis(g))))
                        .collect(Collectors.toSet());
        if (annotations.isEmpty()) {
            annotations = Arrays.stream(allAnnotations)
                    .filter(a -> annotationName(a).equals("Geci"))
                    .collect(Collectors.toSet());
        }
        return annotations.stream()
                .map(GeciAnnotationTools::getValue)
                .toArray(String[]::new);
    }

    private static String annotationName(Annotation a) {
        return a.annotationType().getSimpleName();
    }

    /**
     * Get the value string from the annotation and in case there are other parameters that
     * return a String value and are defined on the annotation then append the "key='value'" after
     * the value string. That way the annotation parameters become part of the configuration.
     *
     * @param annotation the annotation that contains the configuration
     * @return the configuration string
     */
    private static String getValue(Object annotation) {
        try {
            Method valueMethod = annotation.getClass().getDeclaredMethod("value");
            final var value = new StringBuilder((String) valueMethod.invoke(annotation));
            for (final var method : annotation.getClass().getDeclaredMethods()) {
                if (method.getReturnType().equals(String.class) &&
                        !method.getName().equals("value") &&
                        !method.getName().equals("toString")) {
                    final var param = (String) method.invoke(annotation);
                    if (param != null && param.length() > 0) {
                        value.append(" ")
                                .append(method.getName())
                                .append("='")
                                .append(param)
                                .append("'");
                    }
                }
            }
            return value.toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            throw new GeciException("Can not use '" + annotation.getClass().getCanonicalName()
                    + "' as generator annotation.", e);
        }
    }

    /**
     * Get the annotations that are in the {@code @Gecis} annotation.
     *
     * @param annotation the collection annotation
     * @return the array of the underlying annotations
     */
    private static Annotation[] getValueGecis(Object annotation) {
        try {
            Method value = annotation.getClass().getDeclaredMethod("value");
            return (Annotation[]) value.invoke(annotation);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
            throw new GeciException("Can not use '" + annotation.getClass().getCanonicalName()
                    + "' as generator annotation.", e);
        }
    }

    /**
     * Checks if the element is real source code or was generated.
     * <p>
     * Generators are encouraged to annotate the generated elements with the annotation {@link Generated}. This is
     * good for the human reader and the same time some generators can decide if an element is in the compiled class
     * because it was generated or because the programmer provided a version for the element manually. For example the
     * delegator generator does not generate the delegating methods that are provided by the programmer manually
     * but it regenerates all methods that are needed and have the {@link Generated} annotation.
     *
     * @param element that needs the decision if it is generated or manually programmed
     * @return {@code true} if the element was generated (has the annotation {@link Generated}).
     */
    public static boolean isGenerated(AnnotatedElement element) {
        return Selector.compile("annotation ~ /Generated/").match(element);
    }

    /**
     * Get the parameters from the source file directly reading the source. When a generator uses this method the
     * project may not need {@code com.javax0.geci:javageci-annotation:*} as a {@code compile} time dependency
     * when the "annotation" is commented out. This configuration tool can also be used when the source is not
     * Java, as it does not depend on Java annotations.
     * <p>
     * The lines of the source are read from the start and the parameters composed from the first line that is
     * successfully processed are returned.
     *
     * @param source            the source object holding the code lines
     * @param generatorMnemonic the name of the generator that needs the parameters. Only the parameters that are
     *                          specific for this generator are read.
     * @param prefix            characters that should prefix the annotation. In case of Java it is {@code //}.
     *                          The line is first trimmed from leading and trailing space, then the {@code prefix}
     *                          characters are removed from the start, {@code postfix} characters are removed from
     *                          the end and then it has to match the annotation syntax. If this parameter is
     *                          {@code null} then it is treated as empty string, a.k.a. there is no prefix.
     * @param postfix           the postfix that may follow the parameter definition at the end of the line. If this
     *                          parameter is {@code null} it is treated as empty string, a.k.a. there is no postfix.
     * @param nextLine          is a regular expression that should match the line after the successfully matched
     *                          configuration line. If the next line does not match the pattern then the previous line
     *                          is ignored. Typically this is something line {@code /final\s*int\s*xx} when the
     *                          generator wants to get the parameters for the {@code final int xx} variable.
     *                          If this variable is {@code null} then there is no pattern matching performed, and all
     *                          parameter holding line that looks like a {@code Geci} annotation is accepted and
     *                          processed.
     *                          <p>
     *                          Note also that if one or more lines looks like {@code Geci} annotations then they are
     *                          skipped and the {@code nextLine} pattern is matched against the next line that is not
     *                          a configuration line. This allows the program to have multiple configuration lines
     *                          for different generators preceding the same source line.
     * @return the new {@link CompoundParams} object or {@code null} in case there is no configuration found in the
     * file for the specific generator with the specified conditions.
     */
    public static CompoundParams getParameters(Source source,
                                               String generatorMnemonic,
                                               String prefix,
                                               String postfix,
                                               Pattern nextLine) {
        CompoundParams paramConditional = null;
        for (var line : source.getLines()) {
            if (paramConditional != null) {
                if (nextLine == null || nextLine.matcher(line).matches()) {
                    return paramConditional;
                }
            }

            final Matcher match = getMatch(prefix, postfix, line);
            if (match.matches()) {
                if (paramConditional == null) {
                    var string = match.group(1);
                    paramConditional = getParameters(generatorMnemonic, string);
                }
            } else {
                paramConditional = null;
            }
        }
        return null;
    }

    public static CompoundParams getParameters(String generatorMnemonic, String string) {
        if (string.startsWith(generatorMnemonic + " ")) {
            var parametersString = string.substring(generatorMnemonic.length() + 1);
            return new CompoundParams(generatorMnemonic, Map.copyOf(getParameters(parametersString)));
        } else if (string.equals(generatorMnemonic)) {
            return new CompoundParams(generatorMnemonic, Map.of());
        } else {
            return null;
        }
    }

    /**
     * Get a matcher of the line against the {@code @Geci( ... ) } pattern to extract the configuration parameters
     * from a comment line. Before the regular expression matching the line is tirmmed, prefix and postfix
     * is chopped off from the start and the end of the line and then the remaining line is trimmed again.
     *
     * @param prefix  the string that is chopped off from the start of the line if it is there
     * @param postfix this string that is chopped off from the end of the line it it is there
     * @param line    the line to match
     * @return the matcher of regular expression matching
     */
    private static Matcher getMatch(String prefix, String postfix, String line) {
        final var trimmedLine = line.trim();
        final var leftChopped = prefix != null && trimmedLine.startsWith(prefix) ?
                trimmedLine.substring(prefix.length()) : trimmedLine;
        final var rightChopped = postfix != null && leftChopped.endsWith(postfix) ?
                leftChopped.substring(0, leftChopped.length() - postfix.length()) : leftChopped;
        final var matchLine = rightChopped.trim();
        return ANNOTATTION_PATTERN.matcher(matchLine);
    }

    /**
     * Get the parameters into a map from the string. The {@link Geci} annotation has one single value that is a string.
     * This string is supposed to have the format:
     *
     * <pre>
     *
     *     generator_menomic key='value' ... key='value'
     * </pre>
     * <p>
     * The key can be anything that is more or less an identifier (contains only alphanumeric characters, underscore
     * and {@code $} charater, but can also start with any of those, thus it could be '{@code 1966}').
     * <p>
     * The value is enclosed between single quotes, that makes it easier to type and read as single quotes do not need
     * escaping in strings. These quotes can not be skipped.
     *
     * @param s the string parameter
     * @return the map composed from the string
     */
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
}
