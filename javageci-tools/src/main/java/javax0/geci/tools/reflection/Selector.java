package javax0.geci.tools.reflection;

import javax0.geci.tools.MethodTool;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * - head
 *
 * # Filter expressions
 *
 * Many of the generators use filter expressions that select certain
 * members from a class. For example you want to map an object to a
 * `Map` and you use the `mapper` generator. You want to control which
 * fields should be stored into the map. This can be done through the
 * configuration key `filter` specifying a filter expression. The
 * expression will select certain fields to be included and exclude
 * others.
 *
 * Generators are encouraged to use the configuration parameter `filter`
 * for the purpose. This configuration key is also supported by the
 * abstract generators `AbstractFilteredMethodsGenerator` and
 * `AbstractFilteredFieldsGenerator`. Generators extending one of these
 * abstract generators will get the methods or fields already filtered.
 *
 * ## What is a filter expression
 *
 * A filter expression is a string, a logical expression. For example
 * the filter expression
 *
 * `public | private`
 *
 * will select all members (fields or methods, whichever the code
 * generator works with) that are either `private` or `public`, but will
 * not select `protected` or package private members. You can use `|` to
 * express OR relation and `&` to express AND relation. For example the
 * filter expression
 *
 * `public | private & final`
 *
 * will select the members that are `public` or `private` but the
 * `private` fields also have to be `final` or else they will not be
 * selected. (The operator `&` has higher precedence than `|`.)
 *
 * The expressions can use the `!` character to negate the following
 * part and `(` and `)` can also be used to override the evaluation
 * order. The words and matchers are numerous, and are documented in the
 * following section.
 *
 * The filter expression compiler and matcher can also be extended with
 * specific words and matchers. If a generator does that it MUST
 * document these extensions. If the documentation does not mention any
 * extension then it uses the default set of words and matchers.
 *
 * ## Filter words and matchers
 *
 * Words are simple selectors, like `private` or `package` that will
 * select a member if the access protection of the member is private or
 * package private. Matchers are regular expression matchers that
 * identify certain feature of a member and compare it against a regular
 * expression. For example `simpleName ~ /Map$/` will match any class
 * that the simple name ends with the characters `M`, `a` and `p`.
 * Regular expressions are checked using the standard Java
 * `java.util.regex.Matcher` class' `find()` method. It means that the
 * regular expression may match a substring and does not need to match
 * the whole string. If you need to match the expression against the
 * whol string then start it with the character `^` which means the
 * start of the string in a regular expression and end the expression
 * with the character `$`, which matches the end of the string in a
 * regular expression.
 */
public class Selector<T> {

    private static final int SYNTHETIC = 0x00001000;
    private final Map<String, Function<T, Boolean>> selectors = new HashMap<>();
    private final Map<String, Function<T, Object>> converters = new HashMap<>();
    private final Map<String, BiFunction<T, Pattern, Boolean>> regexMemberSelectors = new HashMap<>();
    private SelectorNode top = null;

    private Selector() {

        defineConverters();

        methodAndClassOnlySelectors();

        universalSelectors();

        fieldOnlySelectors();

        methodOnlySelectors();

        classOnlySelectors();


        regexSelector("annotation", (m, regex) -> only(m, AnnotatedElement.class) &&
                matchAnnotations((AnnotatedElement) m, regex));
        selector("annotated", (m) -> only(m, AnnotatedElement.class) &&
                hasAnnotations((AnnotatedElement) m));
    }

    private void defineConverters() {
        converter("declaringClass", m -> only(m,Method.class, Field.class ) ? ((Member)m).getDeclaringClass() : null);
    }

    /**
     * Compile a string to the internal structure of the member selector that can later be used to match a member.
     *
     * @param expression a logical expression described as a string
     * @return {@code this} object to allow method chaining
     */
    public static Selector compile(String expression) {
        final var it = new Selector();
        it.top = SelectorCompiler.compile(expression);
        return it;
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
        throw new IllegalArgumentException("Selector cannot be applied to " + m.getClass());
    }

    /**
     * - <p> ### Class and method checking selectors <p> These
     * conditions work on classes and on methods. Applying them on a
     * field will throw exception.
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
         * * `implements` is `true` if the class implements at least one
         * interface. When applied to a method it is `true` if the
         * method implements a method of the same name and argument
         * types in one of the interfaces the class directly or
         * indirectly implements. In other words it means that there is
         * an interface that declares this method and this method is an
         * implementation (not abstract).
         *
         */
        selector("implements", m -> only(m, Class.class, Method.class) && methodOrClassImplements(m));
    }

    /**
     * -
     * <p>
     * ### Class checking selectors
     * <p>
     * These conditions work on classes. When used on a field then
     * the type of the field is checked. When used on a method then
     * the return type of the method is checked. When the
     * documentation here says "... when the type is ..." it means
     * that the class or interface itself or the type of the field
     * or the return type of the method in case the condition is
     * checked against a field or method.
     */
    private void classOnlySelectors() {
        /**
         * -
         *
         * * `interface` is `true` if the type is an interface
         */
        selector("interface", m -> toClass(m).isInterface());
        /**
         * -
         *
         * * `primitive` is `true` when the type is a primitive type,
         * a.k.a. `int`, `double`, `char` and so on. Note that `String`
         * is not a primitive type.
         */
        selector("primitive", m -> toClass(m).isPrimitive());
        /**
         * -
         *
         * * `annotation` is `true` if the type is an annotation
         * interface.
         */
        selector("annotation", m -> toClass(m).isAnnotation());
        /**
         * -
         *
         * * `anonymous` is `true` if the type is anonymous.
         */
        selector("anonymous", m -> toClass(m).isAnonymousClass());
        /**
         * -
         *
         * * `array` is `true` if the type is an array.
         */
        selector("array", m -> toClass(m).isArray());
        /**
         * -
         *
         * * `enum` is `true` if the type is an enumeration.
         */
        selector("enum", m -> toClass(m).isEnum());
        /**
         * -
         *
         * * `member` is `true` if the type is a member class, a.k.a.
         * inner or nested class or interface
         */
        selector("member", m -> toClass(m).isMemberClass());
        /**
         * -
         *
         * * `local` is `true` if the type is a local class. Local
         * classes are defined inside a method.
         */
        selector("local", m -> toClass(m).isLocalClass());
        /**
         * -
         *
         * * `extends` without any regular expression checks that the
         * class explicitly extends some other class. (Implicitly
         * extending `Object` does not count.)
         *
         */
        selector("extends", m -> {
            final var superClass = toClass(m).getSuperclass();
            return superClass != null && !"java.lang.Object".equals((superClass.getCanonicalName()));
        });
        /**
         * -
         *
         * * `extends ~ /regex/` is `true` if the canonical name of the
         * superclass matches the regular expression. In other words if
         * the class extends directly the class given in the regular
         * expression.
         */
        regexSelector("extends", (m, regex) -> regex.matcher(toClass(m).getSuperclass().getCanonicalName()).find());
        /**
         * -
         *
         * * `simpleName ~ /regex/` is `true` if the simple name of the
         * class (the name without the package) matches the regular
         * expression.
         */
        regexSelector("simpleName", (m, regex) -> regex.matcher(toClass(m).getSimpleName()).find());
        /**
         * -
         *
         * * `canonicalName ~ /regex/` is `true` if the canonical name of
         * the class matches the regular expression.
         */
        regexSelector("canonicalName", (m, regex) -> regex.matcher(toClass(m).getCanonicalName()).find());
        /**
         * -
         *
         * * `implements ~ /regex/` is `true` if the type directly
         * implements an interface whose name matches the regular
         * expression. (Note: `implements` can also be used without a
         * regular expression. In that case the checking is different.)
         */
        regexSelector("implements", (m, regex) -> classImplements(toClass(m), regex));
    }

    private void methodOnlySelectors() {
        selector("synthetic", m -> only(m, Method.class) && (getModifiers(m) & SYNTHETIC) != 0);
        selector("synchronized", m -> only(m, Method.class) && Modifier.isSynchronized(getModifiers(m)));
        selector("native", m -> only(m, Method.class) && Modifier.isNative(getModifiers(m)));
        selector("strict", m -> only(m, Method.class) && Modifier.isStrict(getModifiers(m)));
        selector("default", m -> only(m, Method.class) &&
                ((Member) m).getDeclaringClass().isInterface() && !Modifier.isAbstract(getModifiers(m)));
        selector("vararg", m -> only(m, Method.class) && ((Method) m).isVarArgs());
        selector("overrides", m -> only(m, Method.class) && methodOverrides((Method) m));
        selector("void", m -> only(m, Method.class) && ((Method) m).getReturnType().equals(Void.TYPE));

        regexSelector("returns", (m, regex) -> only(m, Method.class) && regex.matcher(
                ((Method) m).getReturnType().getCanonicalName()).find());
        regexSelector("throws", (m, regex) -> only(m, Method.class) &&
                Arrays.stream(((Method) m).getGenericExceptionTypes())
                        .anyMatch(exception -> regex.matcher(exception.getTypeName()).find()));
        regexSelector("signature", (m, regex) ->
                only(m, Method.class) && regex.matcher(MethodTool.methodSignature((Method) m)).find());
    }

    private void fieldOnlySelectors() {
        selector("transient", m -> only(m, Field.class) && Modifier.isTransient(getModifiers(m)));
        selector("volatile", m -> only(m, Field.class) && Modifier.isVolatile(getModifiers(m)));
    }

    private void universalSelectors() {
        selector("true", m -> true);
        selector("false", m -> false);

        selector("private", m -> Modifier.isPrivate(getModifiers(m)));
        selector("protected", m -> Modifier.isProtected(getModifiers(m)));
        selector("package", m ->
                !Modifier.isPublic(getModifiers(m)) &&
                        !Modifier.isProtected(getModifiers(m)) &&
                        !Modifier.isPrivate(getModifiers(m)));
        selector("static", m -> Modifier.isStatic(getModifiers(m)));
        selector("public", m -> Modifier.isPublic(getModifiers(m)));
        selector("final", m -> Modifier.isFinal(getModifiers(m)));

        regexSelector("name", (m, regex) -> regex.matcher(getName(m)).find());
    }

    private String getName(T m) {
        if (m instanceof Member) {
            return ((Member) m).getName();
        }
        if (m instanceof Class) {
            return ((Class) m).getName();
        }
        throw new IllegalArgumentException("Cannot get the name for " + m.getClass().getCanonicalName());
    }

    private int getModifiers(T m) {
        if (m instanceof Member) {
            return ((Member) m).getModifiers();
        }
        if (m instanceof Class) {
            return ((Class) m).getModifiers();
        }
        throw new IllegalArgumentException("Cannot get the modifiers for " + m.getClass().getCanonicalName());
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
        for (final var klass : classes) {
            if (klass.isAssignableFrom(m.getClass())) return true;
        }
        throw new IllegalArgumentException("Selector cannot be applied to " + m.getClass());
    }

    /**
     * Define a converter and allow redefinition.
     * @param name
     * @param function
     * @return
     */
    public Selector converterRe(String name, Function<T, Object> function) {
        converters.put(name, function);
        return this;
    }

    /**
     * Define a converter.
     *
     * @param name
     * @param function
     * @return
     */
    public Selector converter(String name, Function<T, Object> function) {
        if (converters.containsKey(name)) {
            throw new IllegalArgumentException("The converter '" + name + "' is already defined, can not be redefined");
        }
        return converterRe(name, function);
    }

    /**
     * Define a {@link Function} assigned to the name that can be referenced in the expression.
     *
     * @param name     the name to be used in the expression referencing the function
     * @param function the function that will be executed when evaluating {@code #name}, where {@code name} is actually
     *                 the string provided in the argument {@code name}
     * @return {@code this} object to allow method chaining
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public Selector selector(String name, Function<T, Boolean> function) {
        if (selectors.containsKey(name)) {
            throw new IllegalArgumentException("The selector '" + name + "' is already defined, can not be redefined");
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
     *
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
            return match((T)converters.get(converter).apply(m), ((SelectorNode.Converted) node).subNode);
        }
        if (node instanceof SelectorNode.Not) {
            return !match(m, ((SelectorNode.Not) node).subNode);
        }
        if (node instanceof SelectorNode.Regex) {
            final var regexNode = (SelectorNode.Regex) node;
            if (!regexMemberSelectors.containsKey(regexNode.name)) {
                throw new IllegalArgumentException("There is no regex matcher functionality for '" + regexNode.name + "'");
            }
            return regexMemberSelectors.get(regexNode.name).apply(m, regexNode.regex);
        }
        if (node instanceof SelectorNode.Terminal) {
            final var terminalNode = (SelectorNode.Terminal) node;
            if (!selectors.containsKey(terminalNode.terminal)) {
                throw new IllegalArgumentException("The selector '" + terminalNode.terminal + "' is not known.");
            }
            return selectors.get(terminalNode.terminal).apply(m);
        }
        throw new IllegalArgumentException("Invalid node type in the compiled structure");
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
}
