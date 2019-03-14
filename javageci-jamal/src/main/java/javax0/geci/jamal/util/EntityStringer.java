package javax0.geci.jamal.util;

import javax0.geci.tools.GeciReflectionTools;
import javax0.jamal.api.BadSyntax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static boolean isFingerPrintAField(String fingerPrint) {
        return fingerPrint.split("\\|", -1).length < 3;
    }

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
