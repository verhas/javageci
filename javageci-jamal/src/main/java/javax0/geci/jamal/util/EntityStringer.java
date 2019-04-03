package javax0.geci.jamal.util;

import javax0.geci.tools.GeciReflectionTools;
import javax0.jamal.api.BadSyntax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * EntityString as per entity=field or method and stringer as something that converts to and from string.
 * Methods in this class convert fields or methods to string and back. The macros that list fields or methods
 * return comma separated list of fields, or methods. When other macros in a loop work with these strings they
 * need to get the actual methods and fields to work with during code generation.
 * <p>
 * The string format was designed so that this is also human readable, easy to parse, unique for each field and method
 * and do not interfere with the comma separated macro list handling.
 */
public class EntityStringer {

    /**
     * Creates a proprietary string representation of a method. This representation is passed back as a string by
     * macros that search methods. Later this string can be passed to other macros that work with methods.
     * <p>
     * The representation is
     * <pre>
     *     class canonical name|method name| arg1 type | arg2 type | ... | argN type
     * </pre>
     * <p>
     * The class is the declaring class.
     *
     * <p>
     * The representation was created so that this is easy to parse (just split along the | characters), does not
     * contain comma, so it can be used as a member of comma separated list in a "for" macro and can also be readable
     * and last but not least have all information needed to look up the method via reflection.
     * <p>
     * Important that there are at least two {@code |} characters in the representation even if there are no arguments
     * of the method. In that case the last character of the fingerprint is {@code |}. This helps the method
     * {@link #isFingerPrintAField(String)} tell a field fingerprint from a method fingerprint.
     *
     * @param method to convert to a fingerprint
     * @return the fingerprint
     */
    public static String method2Fingerprint(Method method) {
        final var className = method.getDeclaringClass().getCanonicalName();
        final var argList = Arrays.stream(method.getGenericParameterTypes())
                .map(Type::getTypeName)
                .collect(Collectors.joining("|"));
        return className + "|" + method.getName() + "|" + argList;
    }

    /**
     * Creates a proprietary string representation of a field. This representation is passed back as a string by
     * macros that search fields. Later this string can be passed to other macros that work with fields.
     * <p>
     * The representation is
     * <pre>
     *     class canonical name|field name
     * </pre>
     * <p>
     * The class is the declaring class.
     *
     * <p>
     * Important that there is exactly one {@code |} character in the representation.
     * This helps the method {@link #isFingerPrintAField(String)} tell a field fingerprint from a method fingerprint.
     * <p>
     * Read the explanation at {@link #method2Fingerprint(Method)}
     *
     * @param field to convert to a fingerprint
     * @return the fingerprint
     */
    public static String field2Fingerprint(Field field) {
        final var className = field.getDeclaringClass().getCanonicalName();
        return className + "|" + field.getName();
    }

    private static Class<?> forName(String klassName) {
        try {
            return GeciReflectionTools.classForName(klassName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Search for the method, which is represented by the fingerprint that was presumably created by the method
     * {@link #method2Fingerprint(Method)}. If the method can be found using reflection then return the method.
     *
     * @param fingerprint the string fingerprint of the method.
     * @return the method
     * @throws BadSyntax if the method cannot be found
     */
    public static Method fingerprint2Method(String fingerprint) throws BadSyntax {
        final var parts = fingerprint.split("\\|", -1);
        if (parts.length < 2) {
            throw new BadSyntax("Method fingerprint '" + fingerprint +
                    "' is badly formatted, does not have 'class|name' parts.");
        }
        try {
            final var klass = GeciReflectionTools.classForName(parts[0]);
            final var name = parts[1];
            if (parts.length > 2) {
                final var argClasses = Arrays.stream(parts).skip(2).map(EntityStringer::forName).toArray(Class[]::new);
                return GeciReflectionTools.getMethod(klass, name, argClasses);
            } else {
                return GeciReflectionTools.getMethod(klass, name);
            }
        } catch (ClassNotFoundException e) {
            throw new BadSyntax("Class in fingerprint '" + fingerprint + "' cannot be found.", e);
        } catch (NoSuchMethodException e) {
            throw new BadSyntax("Method in fingerprint '" + fingerprint + "' cannot be found.", e);
        }
    }

    /**
     * Decides if a fingerprint string represents a method or a field.
     * <p>
     * The decision is fairly simple, as field fingerprints have two | separated parts, the class and the name of the
     * field. Methods have more: class, name of the method, arguments and if there is no argument even then the end
     * of the fingerprint is a | character.
     *
     * @param fingerPrint a method or field fingerprint string
     * @return {@code true} if the fingerprint represents a method, and false otherwise
     */
    public static boolean isFingerPrintAField(String fingerPrint) {
        return fingerPrint.split("\\|", -1).length < 3;
    }

    /**
     * Search for the field, which is represented by the fingerprint that was presumably created by the method
     * {@link #field2Fingerprint(Field)} . If the field can be found using reflection then return the method.
     *
     * @param fingerprint is the string representation of the field that was created
     *                    by {@link #field2Fingerprint(Field)}.
     * @return the field
     * @throws BadSyntax if the argument has no | in it or uses a class or field that is not available
     */
    public static Field fingerprint2Field(String fingerprint) throws BadSyntax {
        final var parts = fingerprint.split("\\|", -1);
        if (parts.length < 2) {
            throw new BadSyntax("Fields fingerprint '" + fingerprint +
                    "' is badly formatted, does not have 'class|name' parts.");
        }
        try {
            final var klass = GeciReflectionTools.classForName(parts[0]);
            final var name = parts[1];
            return GeciReflectionTools.getField(klass, name);
        } catch (ClassNotFoundException e) {
            throw new BadSyntax("Class in fingerprint '" + fingerprint + "' cannot be found.", e);
        } catch (NoSuchFieldException e) {
            throw new BadSyntax("Field in fingerprint '" + fingerprint + "' cannot be found.", e);
        }
    }
}
