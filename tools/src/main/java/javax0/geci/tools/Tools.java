package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Gecis;
import javax0.geci.annotations.Generated;
import javax0.geci.api.GeciException;
import javax0.geci.tools.reflection.ModifiersBuilder;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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
     *
     * @param element the class, method or field that is annotated.
     * @return the array of string that contains the values of the annotations. If the element is not annotated
     * then the returned array will have zero elements. If there is one }{@link Geci} annotation then the
     * returned String array will have one element. If there are many annotations then the array will contains each
     * of the values.
     */
    public static String[] getGecis(AnnotatedElement element) {
        final var annotations = element.getDeclaredAnnotation(Gecis.class);
        if (annotations == null) {
            final var annotation = element.getDeclaredAnnotation(Geci.class);
            if (annotation == null) {
                return new String[0];
            } else {
                return new String[]{annotation.value()};
            }
        } else {
            final var gecis = new String[annotations.value().length];
            var i = 0;
            for (final var annotation : annotations.value()) {
                gecis[i++] = annotation.value();
            }
            return gecis;
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

    /**
     * Get the parameters from the {@code element} from the {@link Geci} annotation that stands for the
     * generator that has the mnemonic {@code generatorMnemonic}.
     *
     * @param element           the method, class etc. that has the }{@link Geci} annotation.
     * @param generatorMnemonic the parameters parsed into a {@link CompoundParams} object.
     * @return the new {@link CompoundParams} object or {@code null} in case there is no annotation matching the
     * generator mnemonic.
     */
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
     * removed.
     *
     * @param s generic type name to be normalized
     * @return the normalized type name
     */
    public static String normalizeTypeName(String s) {
        //s = s.replace(" ", "");
        s = s.replaceAll("\\s*<\\s*", "<")
                .replaceAll("\\s*>\\s*", ">")
                .replaceAll("\\s*\\.\\s*", ".")
                .replaceAll("\\s*\\,\\s*", ",")
                .replaceAll("\\s+", " ");
        if (s.startsWith("java.lang.")) {
            s = s.substring("java.lang.".length());
        }
        s = s.replaceAll("([^\\w\\d.^])java.lang.", "$1");
        return s;
    }

    private static String removeJavaLang(String s) {
        if (s.matches("^java\\.lang\\.\\w+(\\.\\.\\.|\\[\\])?$")) {
            return s.substring("java.lang.".length());
        } else {
            return s;
        }
    }

    /**
     * Get the generic type name of the type passed as argument. The JDK {@link Type#getTypeName()} returns
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
        var wt = t;
        var ub = joinTypes(wt.getUpperBounds());
        var lb = joinTypes(wt.getLowerBounds());
        normalizedName = "?" +
                (lb.length() > 0 && !lb.equals("Object") ? " super " + lb : "") +
                (ub.length() > 0 && !ub.equals("Object") ? " extends " + ub : "");
        return normalizedName;
    }

    private static String getGenericParametrizedTypeName(ParameterizedType t) {
        String normalizedName;
        var pt = t;
        var types = pt.getActualTypeArguments();
        if (!(pt.getRawType() instanceof Class<?>)) {
            throw new GeciException("'getRawType()' returned something that is not a class : " + pt.getClass().getTypeName());
        }
        final var klass = (Class) pt.getRawType();
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
        return element.getDeclaredAnnotation(Generated.class) != null;
    }

}
