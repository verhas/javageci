package javax0.geci.tools.reflection;

import javax0.geci.tools.MethodTool;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Reflection selector.
 *
 * @param <T> the type of the member to test. Field, Class, Method etc.
 */
@SuppressWarnings("DanglingJavadoc")
public class Selector<T> {

    private static final int SYNTHETIC = 0x00001000;
    private final Map<String, Function<T, Boolean>> selectors = new HashMap<>();
    private final Map<String, Function<T, Object>> converters = new HashMap<>();
    private final Map<String, BiFunction<T, Pattern, Boolean>> regexMemberSelectors = new HashMap<>();
    private SelectorNode top = null;
    /**
     * Store the original expression to be used in exceptions.
     */
    private final String expression;

    private Selector(String expression) {
        this.expression = expression;

        defineConversions();

        methodAndClassOnlySelectors();

        universalSelectors();

        fieldOnlySelectors();

        methodOnlySelectors();

        classOnlySelectors();

        /**
         * - head
         *
         * `annotation ~ /regex/` is `true` if the examined member has an annotation that matches the regular expression.
         */
        regexSelector("annotation", (m, regex) -> only(m, AnnotatedElement.class) &&
            matchAnnotations((AnnotatedElement) m, regex));
        /**
         * -
         *
         * `annotated` is `true` if the examined member has an annotation. (Any annotation.)
         */
        selector("annotated", (m) -> only(m, AnnotatedElement.class) &&
            hasAnnotations((AnnotatedElement) m));
    }

    /**
     * -
     * <p>
     * ### Conversion
     * <p>
     * Conversions are used to direct the next part of the expression to check something else instead of the member.
     * The conversion is on the same level as the `!` negation operator and the name of the conversion is separated from the following part of the expression by `->`.
     */
    private void defineConversions() {
        /**
         * -
         *
         * * `declaringClass` check the declaring class instead of the member.
         * This can be applied to methods, fields and classes.
         * Note that there is an `enclosingClass` that can be applied to classes.
         */
        converter("declaringClass", this::getDeclaringClass);
        converter("returnType", m -> only(m, Method.class) ? ((Method) m).getReturnType() : null);
        converter("type", m -> only(m, Field.class) ? ((Field) m).getType() : null);
        converter("superClass", m -> only(m, Class.class) ? ((Class) m).getSuperclass() : null);
        converter("enclosingClass", m -> only(m, Class.class) ? ((Class) m).getEnclosingClass() : null);
        converter("enclosingMethod", m -> only(m, Class.class) ? ((Class) m).getEnclosingMethod() : null);
        converter("componentType", m -> only(m, Class.class) ? ((Class) m).getComponentType() : null);
        converter("nestHost", m -> only(m, Class.class) ? ((Class) m).getNestHost() : null);
    }

    /**
     * Compile a string to the internal structure of the member selector that can later be used to match a member.
     *
     * @param expression a logical expression described as a string
     * @return {@code this} object to allow method chaining
     */
    public static Selector compile(String expression) {
        final var it = new Selector(expression);
        it.top = SelectorCompiler.compile(expression);
        return it;
    }

    private Class<?> getDeclaringClass(T m) {
        if (m instanceof Class) {
            return ((Class) m).getDeclaringClass();
        }
        if (m instanceof Method) {
            return ((Method) m).getDeclaringClass();
        }
        if (m instanceof Field) {
            return ((Field) m).getDeclaringClass();
        }
        if (m == null) {
            return null;
        }
        throw illegalArgumentException("Selector cannot be applied to " + m.getClass());
    }

    private Class<?> toClass(T m) {
        if (m instanceof Class) {
            return (Class<?>) m;
        }
        if (m instanceof Method) {
            return ((Method) m).getReturnType();
        }
        if (m instanceof Field) {
            return ((Field) m).getType();
        }
        throw illegalArgumentException("Selector cannot be applied to " + (m == null ? "null " : m.getClass()));
    }

    /**
     * -
     * <p>
     * ### Class and method checking selectors
     *
     * <p> These conditions work on classes and on methods.
     * Applying them on a field will throw exception.
     */
    private void methodAndClassOnlySelectors() {
        /**
         * -
         *
         * * `abstract` is `true` if the type of method is abstract.
         *
         */
        selector("abstract", m -> only(m, Class.class, Method.class) && Modifier.isAbstract(getModifiers(m)));
        /**
         * -
         *
         * * `implements` is `true` if the class implements at least one interface.
         * When applied to a method it is `true` if the method implements a method of the same name and argument types in one of the interfaces the class directly or indirectly implements.
         * In other words it means that there is an interface that declares this method and this method is an implementation (not abstract).
         *
         */
        selector("implements", m -> only(m, Class.class, Method.class) && methodOrClassImplements(m));
    }

    private boolean notNull(T m) {
        return m != null;
    }

    /**
     * -
     * <p>
     * ### Class checking selectors
     * <p>
     * These conditions work on classes.
     * When used on a field then the type of the field is checked.
     * When used on a method then the return type of the method is checked.
     * When the documentation here says "... when the type is ..." it means that the class or interface itself or the type of the field or the return type of the method in case the condition is checked against a field or method.
     */
    private void classOnlySelectors() {
        /**
         * -
         *
         * * `interface` is `true` if the type is an interface
         */
        selector("interface", m -> notNull(m) && toClass(m).isInterface());
        /**
         * -
         *
         * * `primitive` is `true` when the type is a primitive type, a.k.a. `int`, `double`, `char` and so on.
         * Note that `String` is not a primitive type.
         */
        selector("primitive", m -> notNull(m) && toClass(m).isPrimitive());
        /**
         * -
         *
         * * `annotation` is `true` if the type is an annotation interface.
         */
        selector("annotation", m -> notNull(m) && toClass(m).isAnnotation());
        /**
         * -
         *
         * * `anonymous` is `true` if the type is anonymous.
         */
        selector("anonymous", m -> notNull(m) && toClass(m).isAnonymousClass());
        /**
         * -
         *
         * * `array` is `true` if the type is an array.
         */
        selector("array", m -> notNull(m) && toClass(m).isArray());
        /**
         * -
         *
         * * `enum` is `true` if the type is an enumeration.
         */
        selector("enum", m -> notNull(m) && toClass(m).isEnum());
        /**
         * -
         *
         * * `member` is `true` if the type is a member class, a.k.a. inner or nested class or interface
         */
        selector("member", m -> notNull(m) && toClass(m).isMemberClass());
        /**
         * -
         *
         * * `local` is `true` if the type is a local class. Local classes are defined inside a method.
         */
        selector("local", m -> notNull(m) && toClass(m).isLocalClass());
        /**
         * -
         *
         * * `extends` without any regular expression checks that the class explicitly extends some other class.
         * (Implicitly extending `Object` does not count.)
         *
         */
        selector("extends", m -> {
            if (m == null) {
                return false;
            }
            final var superClass = toClass(m).getSuperclass();
            return superClass != null && !"java.lang.Object".equals((superClass.getCanonicalName()));
        });
        /**
         * -
         *
         * * `extends ~ /regex/` is `true` if the canonical name of the superclass matches the regular expression.
         * In other words if the class extends directly the class given in the regular expression.
         */
        regexSelector("extends", (m, regex) -> notNull(m) && regex.matcher(toClass(m).getSuperclass().getCanonicalName()).find());
        /**
         * -
         *
         * * `simpleName ~ /regex/` is `true` if the simple name of the class (the name without the package) matches the regular expression.
         */
        regexSelector("simpleName", (m, regex) -> notNull(m) && regex.matcher(toClass(m).getSimpleName()).find());
        /**
         * -
         *
         * * `canonicalName ~ /regex/` is `true` if the canonical name of the class matches the regular expression.
         */
        regexSelector("canonicalName", (m, regex) -> notNull(m) && regex.matcher(toClass(m).getCanonicalName()).find());
        /**
         * -
         *
         * * `implements ~ /regex/` is `true` if the type directly implements an interface whose name matches the regular expression.
         * (Note: `implements` can also be used without a regular expression.
         * In that case the checking is different.)
         */
        regexSelector("implements", (m, regex) -> notNull(m) && classImplements(toClass(m), regex));
    }

    /**
     * -
     * <p>
     * ### Method checking selectors
     * <p>
     * These conditions work on methods.
     * If applied to anything else than a method the checking will throw an exception.
     */
    private void methodOnlySelectors() {
        /**
         * -
         *
         * * `synthetic` is `true` if the method is synthetic.
         * Synthetic methods are generated by the Javac compiler in some special situation.
         * These methods do not appear in the source code.
         */
        selector("synthetic", m -> only(m, Method.class) && (getModifiers(m) & SYNTHETIC) != 0);
        /**
         * -
         *
         * * `synchronized` is `true` if the method is synchronized.
         */
        selector("synchronized", m -> only(m, Method.class) && Modifier.isSynchronized(getModifiers(m)));
        /**
         * -
         *
         * * `native` is `true` if the method is native.
         */
        selector("native", m -> only(m, Method.class) && Modifier.isNative(getModifiers(m)));
        /**
         * -
         *
         * * `strict` is `true` if the method has the `strict` modifier.
         * This is a rarely used modifier and affects the floating point calculation.
         */
        selector("strict", m -> only(m, Method.class) && Modifier.isStrict(getModifiers(m)));
        /**
         * -
         *
         * * `default` is `true` if the method is defined as a default method in an interface.
         */
        selector("default", m -> only(m, Method.class) &&
            ((Member) m).getDeclaringClass().isInterface() && !Modifier.isAbstract(getModifiers(m)));
        /**
         * -
         *
         * * `bridge` is `true` if the method is a bridge method.
         * Bridge methods are generated by the Javac compiler in some special situation.
         * These methods do not appear in the source code.
         */
        selector("bridge", m -> only(m, Method.class) && ((Method) m).isBridge());
        /**
         * -
         *
         * * `vararg` is `true` if the method is a variable argument method.
         */
        selector("vararg", m -> only(m, Method.class) && ((Method) m).isVarArgs());
        /**
         * -
         *
         * * `overrides` is `true` if the method is overriding another method in the superclass of the method's declaring method or a method in the superclass of the superclass and so on.
         * Implementing a method declared in an interface alone will not result `true`, even though methods implementing an interface method are annotated using the compile time `@Override` annotation.
         * This check is not the same.
         */
        selector("overrides", m -> only(m, Method.class) && methodOverrides((Method) m));
        /**
         * -
         *
         * * `void` is `true` if the method has no return value.
         */
        selector("void", m -> only(m, Method.class) && ((Method) m).getReturnType().equals(Void.TYPE));
        /**
         * -
         *
         * * `returns ~ /regex/` is `true` if the method return type's canonical name matches the regular expression.
         */
        regexSelector("returns", (m, regex) -> only(m, Method.class) && regex.matcher(
            ((Method) m).getReturnType().getCanonicalName()).find());
        /**
         * -
         *
         * * `throws ~ /regex/` is `true` if the method throws a declared exception that matches the regular expression.
         */
        regexSelector("throws", (m, regex) -> only(m, Method.class) &&
            Arrays.stream(((Method) m).getGenericExceptionTypes())
                .anyMatch(exception -> regex.matcher(exception.getTypeName()).find()));
        /**
         * -
         *
         * * `signature ~ /regex/` checks that the signature of the method matches the regular expression.
         * The signature of the method uses the formal argument names `arg0` ,`arg1`,...,`argN`.
         */
        regexSelector("signature", (m, regex) ->
            only(m, Method.class) && regex.matcher(MethodTool.methodSignature((Method) m)).find());
    }

    /**
     * -
     * <p>
     * ### Field checking selectors
     * <p>
     * These conditions work on fields.
     * If applied to anything else than a field the checking will throw an exception.
     */
    private void fieldOnlySelectors() {
        /**
         * -
         *
         * * `transient` is `true` if the field is transient.
         */
        selector("transient", m -> only(m, Field.class) && Modifier.isTransient(getModifiers(m)));
        /**
         * -
         *
         * * `volatile` is `true` if the field is declared volatile.
         */
        selector("volatile", m -> only(m, Field.class) && Modifier.isVolatile(getModifiers(m)));
    }

    /**
     * -
     * <p>
     * ### Universal selectors
     * <p>
     * These conditions work on fields, classes and methods.
     */
    private void universalSelectors() {
        /**
         * -
         *
         * * `true` is `true` always.
         */
        selector("true", m -> true);
        /**
         * -
         *
         * * `false` is `false` always.
         */
        selector("false", m -> false);

        /**
         * -
         *
         * * `null` is `true` when the tested something is null.
         * This can be used to test when a field, class or method has a parent, enclosing class or something else that we can examine with a `->` operator.
         */
        selector("null", Objects::isNull);
        /**
         * -
         *
         * * `private` is `true` if the examined member has private protection.
         */
        selector("private", m -> notNull(m) && Modifier.isPrivate(getModifiers(m)));
        /**
         * -
         *
         * * `protected` is `true` if the examined member has protected protection.
         */
        selector("protected", m -> notNull(m) && Modifier.isProtected(getModifiers(m)));
        /**
         * -
         *
         * * `package` is `true` if the examined member has package private protection.
         */
        selector("package", m ->
            notNull(m) &&
                !Modifier.isPublic(getModifiers(m)) &&
                !Modifier.isProtected(getModifiers(m)) &&
                !Modifier.isPrivate(getModifiers(m)));
        /**
         * -
         *
         * * `public` is `true` if the examined member is public.
         */
        selector("public", m -> m != null && Modifier.isPublic(getModifiers(m)));
        /**
         * -
         *
         * * `static` is `true` if the examined member is static.
         */
        selector("static", m -> m != null && Modifier.isStatic(getModifiers(m)));
        /**
         * -
         *
         * * `static` is `true` if the examined member is final.
         */
        selector("final", m -> m != null && Modifier.isFinal(getModifiers(m)));

        /**
         * -
         *
         * * `name ~ /regex/` is `true` if the examined member's name matches the regular expression.
         */
        regexSelector("name", (m, regex) -> m != null && regex.matcher(getName(m)).find());
    }

    private String getName(T m) {
        if (m instanceof Member) {
            return ((Member) m).getName();
        }
        if (m instanceof Class) {
            return ((Class) m).getName();
        }
        throw illegalArgumentException("Cannot get the name for " + m.getClass().getCanonicalName());
    }

    private int getModifiers(T m) {
        if (m instanceof Member) {
            return ((Member) m).getModifiers();
        }
        if (m instanceof Class) {
            return ((Class) m).getModifiers();
        }
        throw illegalArgumentException("Cannot get the modifiers for " + m.getClass().getCanonicalName());
    }

    /**
     * Checks if a certain method is the implementation of a method declared in one of the interfaces that the
     * class the method is in implements either directly or through the transitive closures of the interfaces
     * extending each other.
     * <p>
     * This method is not the opposite of {@link #methodOverrides(Method)}. It can happen that a method overrides
     * another method and also implements the one defined in an interface.
     *
     * @param m the method to check
     * @return {@code true} if the method implements a method defined in an interface. If the method itself is
     * declared in an interface then it does not implement anything and {@code methodOrClassImplements()} returns {@code false}
     * unless this is a default method that implements another method declared in an interface that the declaring
     * interface extends directly or through transitive closure of the interfaces extending each other.
     */
    private boolean methodImplements(Method m) {
        if (m.getDeclaringClass().isInterface() && !m.isDefault() || Modifier.isAbstract(m.getModifiers())) {
            return false;
        }
        final var interfaces = collectInterfaces(m.getDeclaringClass());
        for (final var intarface : interfaces) {
            final var args = m.getParameterTypes();
            final var name = m.getName();
            if (classHas(intarface, name, args)) return true;
        }
        return false;
    }

    private boolean methodOrClassImplements(Object m) {
        if (m instanceof Method) {
            return methodImplements((Method) m);
        }
        if (m instanceof Class) {
            return classImplements((Class) m);
        }
        return false;
    }

    private static boolean classImplements(Class<?> klass) {
        if (klass.isInterface()) {
            return false;
        }
        Class[] interfaces = klass.getInterfaces();
        return interfaces != null && interfaces.length > 0;
    }

    private static boolean classImplements(Class<?> klass, Pattern regex) {
        if (klass.isInterface()) {
            return false;
        }
        Class[] interfaces = klass.getInterfaces();
        for (final var iface : interfaces) {
            if (regex.matcher(iface.getName()).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Collect all the interfaces that this class implements including all the interfaces that are transitively extended
     * by any of the directly or transitively indirectly implemented interfaces.
     *
     * @param klass the class for which we need all the interfaces
     * @return the set of the interfaces the class implements directly or transitively
     */
    private Set<Class<?>> collectInterfaces(Class<?> klass) {
        final Set<Class<?>> returnSet = new HashSet<>();
        for (final var interfAce : klass.getInterfaces()) {
            collectInterfaces(interfAce, returnSet);
        }
        return returnSet;
    }

    /**
     * Collects all the interfaces into the {@code returnSet}. The set is also used to avoid double
     * collection. Since there cannot be cyclic interface extensions this is  only a simple speed-up.
     *
     * @param klass     the interface to collect and also the interfaces that this extends
     * @param returnSet the set into which collect the interfaces
     */
    private void collectInterfaces(Class<?> klass, Set<Class<?>> returnSet) {
        if (returnSet.contains(klass)) {
            return;
        }
        returnSet.add(klass);
        for (final var interfAce : klass.getInterfaces()) {
            collectInterfaces(interfAce, returnSet);
        }
    }


    /**
     * Checks if this method overrides a method in one of the super
     * classes.
     *
     * @param m the method to be checked
     * @return {@code true} if the method is overriding a method in the
     * superclass or in the superclass of the superclass and so on.
     */
    private boolean methodOverrides(Method m) {
        final var args = m.getParameterTypes();
        final var name = m.getName();
        for (var klass = m.getDeclaringClass().getSuperclass(); klass != null; klass = klass.getSuperclass()) {
            if (classHas(klass, name, args)) return true;
        }
        return false;
    }

    /**
     * @param klass the class in which we are looking for a declared method
     * @param name  the name of the method
     * @param args  the argument types of the method
     * @return {@code true} is the class has a declared method that
     * matches the name and the argument types. Otherwise it returns
     * {@code false}.
     */
    private boolean classHas(Class<?> klass, String name, Class<?>[] args) {
        try {
            klass.getDeclaredMethod(name, args);
            return true;
        } catch (NoSuchMethodException ignored) {
        }
        return false;
    }

    /**
     * Check that the argument {@code m} is a Method, Field, Class or whatever it is.
     *
     * @param m       the reflective object to be checked for the listed types
     * @param classes the different types
     * @return {@code true} if a type is found for {@code m} or throws exception
     */
    private boolean only(T m, Class<?>... classes) {
        if (m == null) {
            return false;
        }
        for (final var klass : classes) {
            if (klass.isAssignableFrom(m.getClass())) return true;
        }
        throw illegalArgumentException("Selector cannot be applied to " + m.getClass());
    }

    /**
     * Define a converter and allow redefinition.
     *
     * @param name     the name of the converter
     * @param function the function that performs the conversion
     * @return {@code this}
     */
    public Selector converterRe(String name, Function<T, Object> function) {
        converters.put(name, function);
        return this;
    }

    /**
     * Define a converter but it does not allow redefinition.
     *
     * @param name     the name of the converter
     * @param function the function that performs the conversion
     * @return {@code this}
     */
    public Selector converter(String name, Function<T, Object> function) {
        if (converters.containsKey(name)) {
            throw illegalArgumentException("The converter '" + name + "' is already defined, can not be redefined");
        }
        return converterRe(name, function);
    }

    /**
     * Define a {@link Function} assigned to the name that can be referenced in the expression.
     *
     * @param name     the name to be used in the expression referencing
     *                 the function
     * @param function the function that will be executed when
     *                 evaluating {@code #name}, where {@code name} is
     *                 actually the string provided in the argument
     *                 {@code name}
     * @return {@code this} object to allow method chaining
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public Selector selector(String name, Function<T, Boolean> function) {
        if (selectors.containsKey(name)) {
            throw illegalArgumentException("The selector '" + name + "' is already defined, can not be redefined");
        }
        return selectorRe(name, function);
    }

    /**
     * Define a {@link Function} assigned to the name that can be
     * referenced in the expression. Essentially the same as {@link
     * #selector(String, Function)} but this version will not throw an
     * exception in case the function is already defined. It will
     * overwrite the function assigned to the name.
     *
     * @param name     the name to be used in the expression referencing
     *                 the function
     * @param function the function that will be executed when evaluating {@code #name}, where {@code name} is actually
     *                 the string provided in the argument {@code name}
     * @return {@code this} object to allow method chaining
     */
    public Selector selectorRe(String name, Function<T, Boolean> function) {
        selectors.put(name, function);
        return this;
    }

    /**
     * Define a {@link BiFunction} assigned to the name that can be referenced in the expression.
     *
     * @param name     the name to be used in the expression referencing
     *                 the function
     * @param function the function that will be executed when
     *                 evaluating {@code #name}, where {@code name} is
     *                 actually the string provided in the argument
     *                 {@code name}. The bi-function will get two
     *                 arguments: 1.) the checked object and 2.) the
     *                 regular expression {@link Pattern}. It is up to
     *                 the bi-function to decide if the pattern is used
     *                 to find only or to match the whole string
     *                 extracted from the object some way. It is also
     *                 the responsibility of the bi-function to extract
     *                 some string from the object calling getName,
     *                 getSimpleName or whatever the object provides.
     *                 For examples see the built-in bi-functions in the
     *                 constructor of this class.
     * @return {@code this} object to allow method chaining
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public Selector regexSelector(String name, BiFunction<T, Pattern, Boolean> function) {
        regexMemberSelectors.put(name, function);
        return this;
    }

    /**
     * Check that the object matches the selection criteria.
     *
     * @param member the member to check
     * @return {@code true} if the selection matches the
     */
    @SuppressWarnings("WeakerAccess")
    public boolean match(Object member) {
        //noinspection unchecked
        return match((T) member, top);
    }

    private boolean matchOr(T m, SelectorNode.Or node) {
        for (final var sub : node.subNodes) {
            if (match(m, sub)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchAnd(T m, SelectorNode.And node) {
        for (final var sub : node.subNodes) {
            if (!match(m, sub)) {
                return false;
            }
        }
        return true;
    }

    private boolean match(T m, SelectorNode node) {
        if (node instanceof SelectorNode.Or) {
            return matchOr(m, (SelectorNode.Or) node);
        }
        if (node instanceof SelectorNode.And) {
            return matchAnd(m, (SelectorNode.And) node);
        }
        if (node instanceof SelectorNode.Converted) {
            final var converter = ((SelectorNode.Converted) node).converter;
            final Function<T, Object> function = converters.get(converter);
            if (function == null) {
                throw illegalArgumentException("There is no converter for '" + converter + "'");
            } else {
                return match((T) function.apply(m), ((SelectorNode.Converted) node).subNode);
            }
        }
        if (node instanceof SelectorNode.Not) {
            return !match(m, ((SelectorNode.Not) node).subNode);
        }
        if (node instanceof SelectorNode.Regex) {
            final var regexNode = (SelectorNode.Regex) node;
            if (!regexMemberSelectors.containsKey(regexNode.name)) {
                throw illegalArgumentException("There is no regex matcher functionality for '" + regexNode.name + "'");
            }
            return regexMemberSelectors.get(regexNode.name).apply(m, regexNode.regex);
        }
        if (node instanceof SelectorNode.Terminal) {
            final var terminalNode = (SelectorNode.Terminal) node;
            if (!selectors.containsKey(terminalNode.terminal)) {
                throw illegalArgumentException("The selector '" + terminalNode.terminal + "' is not known.");
            }
            return selectors.get(terminalNode.terminal).apply(m);
        }
        throw illegalArgumentException("Invalid node type in the compiled structure");
    }

    private boolean hasAnnotations(AnnotatedElement m) {
        final var ann = m.getAnnotations();
        return ann != null && ann.length > 0;
    }

    /**
     * Check that the member has annotation that matches the regex
     *
     * @param m       the member that has or does not have the annotation
     * @param pattern the annotation has to match
     * @return {@code true} if the member has at least one annotation so that the canonical name of the annotation
     * matches the regular expression pattern
     */
    private boolean matchAnnotations(AnnotatedElement m, Pattern pattern) {
        return Arrays.stream(m.getAnnotations()).anyMatch(a ->
            pattern.matcher(a.annotationType().getCanonicalName()).find());
    }

    private IllegalArgumentException illegalArgumentException(final String message) {
        final var exception = new IllegalArgumentException(message + " in expression '" + expression + "'");
        final var elements = exception.getStackTrace();
        final var trace = new StackTraceElement[elements.length - 1];
        System.arraycopy(elements, 1, trace, 0, trace.length);
        exception.setStackTrace(trace);
        return exception;
    }
}
