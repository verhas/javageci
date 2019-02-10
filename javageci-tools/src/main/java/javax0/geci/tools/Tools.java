package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Generated;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Tools {

    public static final int PACKAGE = 0x00010000;
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
                .map(Tools::getValue)
                .toArray(String[]::new);
    }

    private static String annotationName(Annotation a) {
        return a.annotationType().getSimpleName();
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


    private static final Pattern ANNOTATTION_PATTERN = Pattern.compile("@Geci\\(\"(.*)\"\\)");

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

    private static CompoundParams getParameters(String generatorMnemonic, String string) {
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
     * Get the parameters from the {@code element} from the {@link Geci} annotation that stands for the
     * generator that has the mnemonic {@code generatorMnemonic}.
     *
     * @param element           the method, class etc. that has the }{@link Geci} annotation.
     * @param generatorMnemonic the name of the generator that needs the parameters. Only the parameters that are
     *                          specific for this generator are read.
     * @return the new {@link CompoundParams} object or {@code null} in case there is no annotation matching the
     * generator mnemonic.
     */
    public static CompoundParams getParameters(AnnotatedElement element, String generatorMnemonic) {
        final var strings = getGecis(element);
        for (var string : strings) {
            var params = getParameters(generatorMnemonic, string);
            if (params != null) {
                return params;
            }
        }
        return null;
    }

    /**
     * Get the modifiers as string.
     *
     * @param method for which the modifiers are needed
     * @return the string containing the modifiers space separated
     */
    public static String modifiersString(Method method) {
        return new ModifiersBuilder(method.getModifiers()).toString();
    }

    /**
     * Get the modifiers as string except access modifier.
     *
     * @param method for which the modifiers are needed
     * @return the string containing the modifiers space separated, except the access modifier
     */
    public static String modifiersStringNoAccess(Method method) {
        return new ModifiersBuilder(method.getModifiers()
                & ~Modifier.PROTECTED & Modifier.PRIVATE & Modifier.PUBLIC).toString();
    }

    /**
     * Get the type of the field as string.
     *
     * @param field of which the type is needed
     * @return string containing the type as string with all the generics.
     */
    public static String typeAsString(Field field) {
        return getGenericTypeName(field.getGenericType());
    }

    /**
     * Get the return type of the method as string.
     *
     * @param method of which the type is needed
     * @return string containing the type as string with all the generics.
     */
    public static String typeAsString(Method method) {
        return getGenericTypeName(method.getGenericReturnType());
    }

    /**
     * Normalize a generic type name removing all {@code java.lang.} from the type names.
     * <p>
     * Even the generated code should be human readable, especially when you debug the working of the code. In that
     * case the generic names with all the {@code java.lang.String}, {@code java.lang.Integer} and so on are disturbing.
     * This method removes those prefixes.
     * <p>
     * Note that the prefixes {@code java.util.} and similar others that are usually imported by the class are NOT
     * removed, because we cannot know that the class imports those or not.
     * <p>
     * Since there is no API in JDK to get the canonical name of a {@link Type} the inner classes before normalization
     * will contain {@code $} in the names and not dot. As a last resort here we replace all {@code $} character in the
     * name to {@code .} (dot). This has the consequence that the application that uses fluent API generator must not
     * use {@code $} in the name of the classes.
     *
     * @param s generic type name to be normalized
     * @return the normalized type name
     */
    public static String normalizeTypeName(String s) {
        s = s.replaceAll("\\s*<\\s*", "<")
                .replaceAll("\\s*>\\s*", ">")
                .replaceAll("\\s*\\.\\s*", ".")
                .replaceAll("\\s*,\\s*", ",")
                .replaceAll("\\s+", " ");
        if (s.startsWith("java.lang.")) {
            s = s.substring("java.lang.".length());
        }
        s = s.replaceAll("([^\\w\\d.^])java.lang.", "$1");
        s = s.replaceAll("\\$", ".");
        return s;
    }

    private static String removeJavaLang(String s) {
        if (s.matches("^java\\.lang\\.\\w+(\\.\\.\\.|\\[])?$")) {
            return s.substring("java.lang.".length());
        } else {
            return s;
        }
    }

    /**
     * Get the generic type name of the type passed as argument. The JDK {@code Type#getTypeName()} returns
     * a string that contains the classes with their names and not with the canonical names (inner classes
     * have {@code $} in the names instead of dot). This method goes through the type structure and converts
     * the names (generic types also) to
     *
     * @param t the type
     * @return the type as string
     */
    public static String getGenericTypeName(Type t) {
        final String normalizedName;
        if (t instanceof ParameterizedType) {
            normalizedName = getGenericParametrizedTypeName((ParameterizedType) t);
        } else if (t instanceof Class<?>) {
            normalizedName = removeJavaLang(((Class) t).getCanonicalName());
        } else if (t instanceof WildcardType) {
            normalizedName = getGenericWildcardTypeName((WildcardType) t);
        } else if (t instanceof GenericArrayType) {
            var at = (GenericArrayType) t;
            normalizedName = getGenericTypeName(at.getGenericComponentType()) + "[]";
        } else if (t instanceof TypeVariable) {
            normalizedName = t.getTypeName();
        } else {
            throw new GeciException("Type is something not handled. It is '" + t.getClass() + "' for the type '" + t.getTypeName() + "'");
        }
        return normalizedName;
    }

    private static String getGenericWildcardTypeName(WildcardType t) {
        String normalizedName;
        var ub = joinTypes(t.getUpperBounds());
        var lb = joinTypes(t.getLowerBounds());
        normalizedName = "?" +
                (lb.length() > 0 && !lb.equals("Object") ? " super " + lb : "") +
                (ub.length() > 0 && !ub.equals("Object") ? " extends " + ub : "");
        return normalizedName;
    }

    private static String getGenericParametrizedTypeName(ParameterizedType t) {
        String normalizedName;
        var types = t.getActualTypeArguments();
        if (!(t.getRawType() instanceof Class<?>)) {
            throw new GeciException("'getRawType()' returned something that is not a class : " + t.getClass().getTypeName());
        }
        final var klass = (Class) t.getRawType();
        final String klassName = removeJavaLang(klass.getCanonicalName());
        if (types.length > 0) {
            normalizedName = klassName + "<" +
                    joinTypes(types) +
                    ">";
        } else {
            normalizedName = klassName;
        }
        return normalizedName;
    }

    /**
     * Join the type names also removing the {@code java.lang. } prefixes if any.
     *
     * @param types the types to join. These are the generic types, or the super or extends types in a wildcard.
     * @return the string of the list comma separated.
     */
    private static String joinTypes(Type[] types) {
        return Arrays.stream(types)
                .map(Tools::getGenericTypeName)
                .collect(Collectors.joining(","));
    }

    /**
     * Get the declared fields of the class sorted alphabetically. The actual order is usually not interesting
     * for the code generators, but a deterministic order is. When a code generator generates code for all or
     * for some of the declared fields it is important that the order is always the same. If the order changes
     * from time to time then it may happen that the code generation creates the code every time differently and
     * breaking the build. It happens in practice, for example, when you have a different version of Java on the
     * development machine and on the build server. In development you run the build, generate the code, run the build
     * again, commit the code. You expect that on the build server the code generation will not fail because all the
     * generated code is there in the repository in the files. However, you may use a different version of Java
     * (even if only different build) on the build server and because of that the order of the fields is different and
     * the generated code is different, although functionally it is the same.
     * <p>
     * This method and also the {@link #getDeclaredMethodsSorted(Class)} can and should be used by code generators to
     * have a deterministic output.
     *
     * @param klass of which the fields are collected
     * @return the sorted array of fields
     */
    public static Field[] getDeclaredFieldsSorted(Class<?> klass) {
        final var fields = klass.getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));
        return fields;
    }

    /**
     * Get the declared methods of the class sorted.
     * <p>
     * See the notes at the javadoc of the method {@link #getDeclaredFieldsSorted(Class)}
     * <p>
     * The methods are sorted according to the string representation of the signature. How the
     * method signature is created is document in the javadoc of the method {@link MethodTool#methodSignature(Method)}
     *
     * @param klass class of which the methods are returned
     * @return the sorted array of the methods
     */
    public static Method[] getDeclaredMethodsSorted(Class<?> klass) {
        final var methods = klass.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(MethodTool::methodSignature));
        return methods;
    }


    public static class Separator {
        private Separator(String sep) {
            this.sep = sep;
        }

        private final String sep;
        private boolean first = true;

        public String get() {
            if (first) {
                first = false;
                return "";
            } else {
                return sep;
            }
        }

    }

    public static Separator separator(String sep) {
        return new Separator(sep);
    }

    /**
     * Convert a string that contains lower case letter Java modifiers comma separated into an access mask.
     *
     * @param masks  is the comma separated list of modifiers. The list can also contain the word {@code package}
     *               that will be translated to {@link Tools#PACKAGE} since there is no modifier {@code package}
     *               in Java.
     * @param dfault the mask to return in case the {@code includes} string is empty.
     * @return the mask converted from String
     */
    public static int mask(String masks, int dfault) {
        int modMask = 0;
        if (masks == null) {
            modMask = dfault;
        } else {
            for (var maskString : masks.split(",")) {
                var maskTrimmed = maskString.trim();
                if (maskTrimmed.equals("private")) {
                    modMask |= Modifier.PRIVATE;
                }
                if (maskTrimmed.equals("public")) {
                    modMask |= Modifier.PUBLIC;
                }
                if (maskTrimmed.equals("protected")) {
                    modMask |= Modifier.PROTECTED;
                }
                if (maskTrimmed.equals("static")) {
                    modMask |= Modifier.STATIC;
                }
                if (maskTrimmed.equals("package")) {
                    modMask |= Tools.PACKAGE;//reuse the bit
                }
                if (maskTrimmed.equals("abstract")) {
                    modMask |= Modifier.ABSTRACT;
                }
                if (maskTrimmed.equals("final")) {
                    modMask |= Modifier.FINAL;
                }
                if (maskTrimmed.equals("interface")) {
                    modMask |= Modifier.INTERFACE;
                }
                if (maskTrimmed.equals("synchronized")) {
                    modMask |= Modifier.SYNCHRONIZED;
                }
                if (maskTrimmed.equals("native")) {
                    modMask |= Modifier.NATIVE;
                }
                if (maskTrimmed.equals("transient")) {
                    modMask |= Modifier.TRANSIENT;
                }
                if (maskTrimmed.equals("volatile")) {
                    modMask |= Modifier.VOLATILE;
                }
            }
        }
        return modMask;
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

}
