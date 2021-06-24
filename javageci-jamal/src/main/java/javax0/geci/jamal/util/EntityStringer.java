package javax0.geci.jamal.util;

import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.ModifiersBuilder;

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
     * Convert a possibly fully qualified class name to class name that can be used inside the source code of a class
     * that declares imports.
     *
     * @param fqClassName the name of the class to be converted
     * @param imports     the list of the imported classes as they stand after the {@code import} keyword. It can also
     *                    be null. In that case no conversion is done of the class name.
     * @return the converted class name, that does not contain the package or the original string in case this class was
     * not imported
     */
    private static String importedClass(String fqClassName, String[] imports) {
        if( imports != null ) {
            for (final var cn : imports) {
                if (cn != null && cn.equals(fqClassName)){
                    int i = fqClassName.lastIndexOf('.');
                    if( i == -1 ){
                        return fqClassName;
                    }
                    return fqClassName.substring(i+1);
                }
            }
        }
        return fqClassName;
    }

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
     * @param method       the method to format
     * @param format       the format string
     * @param argSep       the separator character in the list of arguments
     * @param exceptionSep the separator character in the list of exceptions
     * @return the formatted method representation
     */
    public static String method2Fingerprint(Method method, String format, String argSep, String exceptionSep, String[] imports) {
        final var className = importedClass(method.getDeclaringClass().getCanonicalName().replaceAll("^java.lang.", ""), imports);
        final var argList = getTypeList(method, argSep,imports);
        final var modifiers = GeciReflectionTools.modifiersString(method).trim();
        final var type = importedClass(GeciReflectionTools.getGenericTypeName(method.getReturnType()).replaceAll("^java.lang.", ""),imports);
        final var exceptions = Arrays.stream(method.getExceptionTypes())
            .map(Type::getTypeName)
            .map(s -> s.replaceAll("^java.lang.", ""))
            .map( tn -> importedClass(tn,imports))
            .collect(Collectors.joining(exceptionSep));
        final String throwExceptions;
        if (exceptions.length() > 0) {
            throwExceptions = "throw " + exceptions;
        } else {
            throwExceptions = "";
        }

        return format.replaceAll("\\$class", className)
            .replaceAll("\\$modifiers", modifiers + (modifiers.length() > 0 ? " " : ""))
            .replaceAll("\\$name", method.getName())
            .replaceAll("\\$exceptions", exceptions)
            .replaceAll("\\$throws", throwExceptions)
            .replaceAll("\\$args", argList)
            .replaceAll("\\$type", type)
            ;
    }

    private static String getTypeList(final Method method, final String argSep, String[] imports) {
        final var argList = Arrays.stream(method.getGenericParameterTypes())
            .map(Type::getTypeName)
            .map(tn -> importedClass(tn,imports))
            .collect(Collectors.joining(argSep));
        if (argList.endsWith("[]") && method.isVarArgs()) {
            return argList.substring(0, argList.length() - 2) + "...";
        } else {
            return argList;
        }
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
     * @param field  to convert to a fingerprint
     * @param format the format string
     * @return the fingerprint
     */
    public static String field2Fingerprint(Field field, String format) {
        final var className = field.getDeclaringClass().getCanonicalName();
        final var type = GeciReflectionTools.getGenericTypeName(field.getType());
        final var modifiers = new ModifiersBuilder(field.getModifiers()).field().toString().trim();
        return format.replaceAll("\\$class", className)
            .replaceAll("\\$name", field.getName())
            .replaceAll("\\$type", type)
            .replaceAll("\\$modifiers", modifiers + (modifiers.length() > 0 ? " " : ""))
            ;
    }
}
