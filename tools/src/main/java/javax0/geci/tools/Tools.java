package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.annotations.Gecis;
import javax0.geci.tools.reflection.ModifiersBuilder;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Tools {

    public static final int PACKAGE = 0x00010000;
    private static final Pattern pattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*'(.*?)'");

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

    public static String modifiersString(Method method) {
        return new ModifiersBuilder(method.getModifiers()).toString();
    }

    public static String typeAsString(Field field) {
        return normalizeTypeName(field.getGenericType().getTypeName());
    }

    public static String typeAsString(Method method) {
        return normalizeTypeName(method.getGenericReturnType().getTypeName());
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
        s = s.replace(" ", "");
        if (s.startsWith("java.lang.")) {
            s = s.substring("java.lang.".length());
        }
        s = s.replaceAll("([^\\w\\d.^])java.lang.", "$1");
        return s;
    }

    /**
     * Get the declared fields of the class sorted alphabetically. The actual order is usually not interesting
     * for the code generators, but deterministic order is. When a code generator generates code for all or
     * for some of the declared fields it is important that the order is always the same. If the order changes
     * from time to time then it may happen that the code generation creates the code every time differently and
     * breaking the build. It happends in practice, for example, when you have a different version of Java on the
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
     * method signature is created is document in the javadoc of the method {@link #methodSignature(Method, Function)}
     *
     * @param klass class of which the methods are returned
     * @return the sorted array of the methods
     */
    public static Method[] getDeclaredMethodsSorted(Class<?> klass) {
        final var methods = klass.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Tools::methodSignature));
        return methods;
    }

    /**
     * Same as {@link #methodSignature(Method, Function)} but the decorator function is {@code null}.
     *
     * @param method see {@link #methodSignature(Method, Function, String, boolean)}
     * @return see {@link #methodSignature(Method, Function, String, boolean)}
     */
    public static String methodSignature(Method method) {
        return methodSignature(method, null);
    }

    /**
     * Same as {@link #methodSignature(Method, Function, String, boolean)} but the
     * y{@code replaceReturnType} is {@code null}.
     *
     * @param method    see {@link #methodSignature(Method, Function, String, boolean)}
     * @param decorator see {@link #methodSignature(Method, Function, String, boolean)}
     * @return see {@link #methodSignature(Method, Function, String, boolean)}
     */
    public static String methodSignature(Method method, Function<String, String> decorator) {
        return methodSignature(method, decorator, null, false);
    }

    /**
     * Create the string representation of the signature of the method. The method signature
     * is the name of the class, then the arguments in parentheses with types comma separated and the exceptions
     * after the keyword {@code throws} if there is any exception thrown by the method. The argument names are
     * {@code arg1}, {@code arg2}, ... {@code argN}.
     *
     * @param method            of which the signature is needed
     * @param nameDecorator         can be {@code null} or can be used to alter the name of the method in the signature. This is
     *                          used when the code generation wants to create methods with different name and the same
     *                          signature as other methods. For example a proxy method in the same class that does something,
     *                          then calls the original method and then returns.
     * @param replaceReturnType when null then it has no effect. When is is not null then the prototype will use this
     *                          string in place of the method return type.
     * @param interfce          if {@code true} then the signature will not contain modifiers because in this case it
     *                          is assumed that the signature will be used in generation an interface.
     * @return the string of the method signature.
     */
    public static String methodSignature(Method method,
                                         Function<String, String> nameDecorator,
                                         String replaceReturnType, boolean interfce) {
        var argCounter = new AtomicInteger(0);
        var arglist = Arrays.stream(method.getGenericParameterTypes())
            .map(t -> normalizeTypeName(t.getTypeName()) + " arg" + argCounter.addAndGet(1))
            .collect(Collectors.joining(","));
        var exceptionlist = Arrays.stream(method.getGenericExceptionTypes())
            .map(t -> normalizeTypeName(t.getTypeName()))
            .collect(Collectors.joining(","));
        return (interfce ? "" : modifiersString(method)) +
            (replaceReturnType == null ? typeAsString(method) : replaceReturnType) +
            " " +
            decoratedName(method, nameDecorator) +
            "(" + arglist + ")" +
            (exceptionlist.length() == 0 ? "" : " throws " + exceptionlist);
    }

    /**
     * The same as {@link #methodCall(Method, Function)} but the decorator function is {@code null}.
     *
     * @param method see {@link #methodCall(Method, Function)}
     * @return see {@link #methodCall(Method, Function)}
     */
    public static String methodCall(Method method) {
        return methodCall(method, null);
    }

    /**
     * Creates the string that calls a method. The string will start with the name of the method and then the
     * arguments between parentheses comma separated. The argument names are
     * {@code arg1}, {@code arg2}, ... {@code argN}.
     *
     * @param method    of which the signature is needed
     * @param decorator a function to convert the name of the method. See {@link #methodSignature(Method, Function)}
     *                  for examples when it is useful.
     * @return the string that is the method call in Java syntax
     */
    public static String methodCall(Method method, Function<String, String> decorator) {
        var arglist = IntStream.range(1, method.getGenericParameterTypes().length + 1)
            .mapToObj(index -> " arg" + index)
            .collect(Collectors.joining(","));
        return new StringBuilder()
            .append(decoratedName(method, decorator))
            .append("(")
            .append(arglist)
            .append(")")
            .toString();
    }

    /**
     * Decorate the method name if {@code decorator} is not {@code null}.
     *
     * @param method    of which the name is retrieved
     * @param decorator converting the name or {@code null}
     * @return the decorated name
     */
    private static String decoratedName(Method method, Function<String, String> decorator) {
        if (decorator == null) {
            return method.getName();
        } else {
            return decorator.apply(method.getName());
        }
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
                if (maskTrimmed.equals("native")) {
                    modMask |= Modifier.TRANSIENT;
                }
                if (maskTrimmed.equals("native")) {
                    modMask |= Modifier.VOLATILE;
                }
            }
        }
        return modMask;
    }

}
