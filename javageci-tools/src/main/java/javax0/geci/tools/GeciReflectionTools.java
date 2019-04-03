package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.api.GeciException;
import javax0.geci.tools.reflection.ModifiersBuilder;
import javax0.geci.tools.reflection.Selector;
import javax0.geci.tools.syntax.GeciAnnotationTools;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GeciReflectionTools {

    public static final int PACKAGE = 0x00010000;
    private static final Selector inheritedField = Selector.compile("!static & !private");
    private static final Selector inheritedFieldDifferentPackage = Selector.compile("!static & !private & !package");
    private static final Map<String, Class<?>> PRIMITIVES = Map.of(
            "byte", byte.class,
            "char", char.class,
            "short", short.class,
            "int", int.class,
            "long", long.class,
            "float", float.class,
            "double", double.class,
            "boolean", boolean.class);


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
        final var strings = GeciAnnotationTools.getGecis(element);
        for (var string : strings) {
            var params = GeciAnnotationTools.getParameters(generatorMnemonic, string);
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
     * Get the type of a field or a method as string.
     *
     * @param member of which the type is needed
     * @return string containing the type as string with all the generics.
     */
    public static String typeAsString(Member member) {
        return getGenericTypeName(member instanceof Field ?
                ((Field) member).getGenericType()
                :
                ((Method) member).getGenericReturnType());
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
                .map(GeciReflectionTools::getGenericTypeName)
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
     * Get all the fields, declared and inherited fields sorted. About the sorting see the JavaDoc
     * of {@link #getDeclaredFieldsSorted(Class)}.
     *
     * @param klass of which the fields are collected
     * @return the sorted array of fields
     */
    public static Field[] getAllFieldsSorted(Class<?> klass) {
        Set<Field> fields = new HashSet<>(Arrays.asList(klass.getDeclaredFields()));
        var superClass = klass.getSuperclass();
        while (superClass != null) {
            collectFields(klass, superClass, fields);
            superClass = superClass.getSuperclass();
        }
        final var allFields = fields.toArray(new Field[0]);
        Arrays.sort(allFields, Comparator.comparing(Field::getName));
        return allFields;
    }

    /**
     * Collect all the fields from the actual class that are inherited by the base class assuming that the
     * base class extends directly or through other classes transitively the actual class.
     *
     * @param baseClass   the base class that we need the fields collected for
     * @param actualClass the class in which we look for the fields
     * @param fields      the collection of the fields where to put the fields
     */
    private static void collectFields(Class<?> baseClass, Class<?> actualClass, Set<Field> fields) {
        final var declaredFields = actualClass.getDeclaredFields();
        final var selector = baseClass.getPackage() == actualClass.getPackage()
                ? inheritedField : inheritedFieldDifferentPackage;
        for (final var field : declaredFields) {
            if (selector.match(field)) {
                fields.add(field);
            }
        }
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
     * The same as {@link #getDeclaredMethodsSorted(Class)} except it returns the methods and not the declared methods.
     * It means that only the methods that are available from outside but including the inherited methods are returned.
     *
     * @param klass the class of which we need the methods
     * @return the array of the methods of the class
     */
    public static Method[] getMethodsSorted(Class<?> klass) {
        final var methods = klass.getMethods();
        Arrays.sort(methods, Comparator.comparing(MethodTool::methodSignature));
        return methods;
    }

    /**
     * Get all the methods sorted: declared and inherited.
     *
     * @param klass the class of which we need the methods
     * @return the array of the methods of the class
     */
    public static Method[] getAllMethodsSorted(Class<?> klass) {
        final var methods = Arrays.stream(klass.getMethods()).collect(Collectors.toSet());
        final var declaredMethods = Arrays.stream(klass.getDeclaredMethods()).collect(Collectors.toSet());
        final var allMethods = new HashSet<>();
        allMethods.addAll(methods);
        allMethods.addAll(declaredMethods);
        final var methodArray = allMethods.toArray(new Method[0]);
        Arrays.sort(methodArray, Comparator.comparing(MethodTool::methodSignature));
        return methodArray;
    }

    public static Method getMethod(Class<?> klass, String methodName, Class<?>... classes) throws NoSuchMethodException {
        try {
            return klass.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException ignored) {
            return klass.getMethod(methodName, classes);
        }
    }

    public static Field getField(Class<?> klass, String fieldName) throws NoSuchFieldException {
        try {
            return klass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignored) {
            return klass.getField(fieldName);
        }
    }

    private static Class<?> classForNoArray(String className) throws ClassNotFoundException {
        if (PRIMITIVES.containsKey(className)) {
            return PRIMITIVES.get(className);
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return Class.forName("java.lang." + className);
        }
    }

    /**
     * Get the class that is represented by the name {@code className}. This functionality extends the basic
     * functionality provided by the static method {@link Class#forName(String)} so that it also works for
     * for input strings {@code int}, {@code byte} and so on for all the eight primitive types and also
     * it works for types that end with {@code []}, so when they are essentially arrays. This also works for primitives.
     * <p>
     * If the class cannot be found in the first round then this method tries it again prepending the {@code java.lang.}
     * in front of the name given as argument, so Java language types can be referenced as, for example {@code Integer}
     * and they do not need the fully qualified name.
     * <p>
     * Note that there are many everyday used types, like {@code Map}, which are NOT in the {@code java.lang} package.
     * They have to be specified with the fully qualified name.
     *
     * @param className the name of the class or a primitive type optionally one or more {@code []} pairs at the end.
     *                  The JVM limitation is that there can be at most 255 {@code []} pairs.
     * @return the class
     * @throws ClassNotFoundException if the class cannot be found
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        var arrayCounter = 0;
        while (className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
            arrayCounter++;
        }
        var klass = classForNoArray(className);
        while (arrayCounter-- > 0) {
            klass = Array.newInstance(klass, 0).getClass();
        }
        return klass;
    }

    /**
     * Convert an int containing modifiers bits to string containing the Java names of the modifiers space separated.
     *
     * @param modifiers to be converted to string
     * @return the space separated modifiers or empty string in case there is no modifier bit set in {@code modifiers}
     */
    public static String unmask(int modifiers) {
        final StringBuilder s = new StringBuilder();
        final BiConsumer<Predicate<Integer>, String> check = (Predicate<Integer> predicate, String text) -> {
            if (predicate.test(modifiers)) {
                s.append(text);
            }
        };
        check.accept(Modifier::isPrivate, "private ");
        check.accept(Modifier::isProtected, "protected ");
        check.accept(Modifier::isPublic, "public ");
        check.accept(Modifier::isFinal, "final ");
        check.accept(Modifier::isStatic, "static ");
        check.accept(Modifier::isSynchronized, "synchronized ");
        check.accept(Modifier::isVolatile, "volatile ");
        check.accept(Modifier::isStrict, "strictfp ");
        check.accept(Modifier::isAbstract, "abstract ");
        check.accept(Modifier::isTransient, "transient ");
        return s.toString().trim();
    }

    /**
     * Convert a string that contains lower case letter Java modifiers comma separated into an access mask.
     *
     * @param masks  is the comma separated list of modifiers. The list can also contain the word {@code package}
     *               that will be translated to {@link GeciReflectionTools#PACKAGE} since there is no modifier {@code package}
     *               in Java.
     * @param dfault the mask to return in case the {@code includes} string is empty.
     * @return the mask converted from String
     */
    public static int mask(String masks, int dfault) {
        int modMask = 0;
        if (masks == null) {
            return dfault;
        } else {
            for (var maskString : masks.split(",", -1)) {
                var maskTrimmed = maskString.trim();
                switch (maskTrimmed) {

                    case "private":
                        modMask |= Modifier.PRIVATE;
                        break;
                    case "public":
                        modMask |= Modifier.PUBLIC;
                        break;
                    case "protected":
                        modMask |= Modifier.PROTECTED;
                        break;
                    case "static":
                        modMask |= Modifier.STATIC;
                        break;
                    case "package":
                        modMask |= GeciReflectionTools.PACKAGE;//reuse the bit
                        break;
                    case "abstract":
                        modMask |= Modifier.ABSTRACT;
                        break;
                    case "final":
                        modMask |= Modifier.FINAL;
                        break;
                    case "interface":
                        modMask |= Modifier.INTERFACE;
                        break;
                    case "synchronized":
                        modMask |= Modifier.SYNCHRONIZED;
                        break;
                    case "native":
                        modMask |= Modifier.NATIVE;
                        break;
                    case "transient":
                        modMask |= Modifier.TRANSIENT;
                        break;
                    case "volatile":
                        modMask |= Modifier.VOLATILE;
                        break;
                    default:
                        throw new IllegalArgumentException(maskTrimmed + " can not be used as a modifier string");
                }
            }
            return modMask;
        }
    }
}
