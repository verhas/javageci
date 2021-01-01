package javax0.geci.jamal.util;

import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.jamal.api.BadSyntax;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Entity is a field or a method. Stringer is something that converts to and from string. This class can convert a field
 * (not the value of the field, but the field as in reflection) or a method to string and it can also do the conversion
 * the other way.
 *
 * <p> Methods in this class convert fields or methods to string and back. The Jamal macros that list fields or methods
 * return comma separated list of fields, or methods. When other macros in a loop work with these strings they need to
 * get the actual methods and fields to work with during code generation.
 *
 * <p> The string format was designed so that this is also human readable, easy to parse, unique for each field and
 * method and do not interfere with the comma separated macro list handling.
 */
public class EntityStringer {


    /**
     * Format the method. The returned string will replace
     *
     * <ul>
     *     <li>{@code $name}</li>
     *     <li>{@code $class}</li>
     *     <li>{@code $args}</li>
     *     <li>{@code $modifiers}</li>
     *     <li>{@code $exceptions}</li>
     * </ul>
     * <p>
     * with the actual name, class and argument types, modifiers of the method.
     *
     * @param method the method to format
     * @param format the format string
     * @return the formatted method representation
     */
    public static String method2Fingerprint(Method method, String format, String argSep, String exceptionSep) {
        final var className = method.getDeclaringClass().getCanonicalName();
        final var argList = Arrays.stream(method.getGenericParameterTypes())
            .map(Type::getTypeName)
            .collect(Collectors.joining(argSep));
        final var modifiers = new ModifiersBuilder(method.getModifiers()).toString().trim();
        final var type = GeciReflectionTools.getGenericTypeName(method.getReturnType());
        final var exceptions = Arrays.stream(method.getExceptionTypes())
            .map(Type::getTypeName)
            .collect(Collectors.joining(exceptionSep));

        return format.replaceAll("\\$class", className)
            .replaceAll("\\$modifiers", modifiers)
            .replaceAll("\\$name", method.getName())
            .replaceAll("\\$exceptions", exceptions)
            .replaceAll("\\$args", argList)
            .replaceAll("\\$type", type)
            ;
    }

    /**
     * Creates a proprietary string representation of a field. This representation is passed back as a string by macros
     * that search fields. Later this string can be passed to other macros that work with fields.
     * <p>
     * The representation is
     * <pre>
     *     class canonical name|field name
     * </pre>
     * <p>
     * The class is the declaring class.
     *
     * <p>
     *
     * @param field to convert to a fingerprint
     * @return the fingerprint
     */
    public static String field2Fingerprint(Field field, String format) {
        final var className = field.getDeclaringClass().getCanonicalName();
        final var type = GeciReflectionTools.getGenericTypeName(field.getType());
        final var modifiers = new ModifiersBuilder(field.getModifiers()).field().toString().trim();
        return format.replaceAll("$$class", className)
            .replaceAll("\\$name", field.getName())
            .replaceAll("\\$type", type)
            .replaceAll("\\$modifiers", modifiers)
            ;
    }
}
