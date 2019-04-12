package javax0.geci.tools.reflection;

import javax0.geci.tools.MethodTool;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An {@code Selector} can select or deselect a member based on the selection expression.
 * <p>
 * A selection expression can be as simple as {@code final} which will match a member if the member if final.
 * Expressions can refer to the modifiers, the name of the member using regular expression and can contain {@code |}
 * and {@code &} to form logical expressions. Also the expression can use parentheses and subexpressions or the whole
 * expression can be negated using the {@code !} character.
 * <p>
 * Example:
 * <ul>
 * <li>{@code final & public} will select a member if it is final and public</li>
 * <li>{@code final | public} will select a member if it is final or public</li>
 * <li>{@code !final & (protected | public)} will select a member if it is final and protected or public</li>
 * <li>{@code !final & (package | public)} will select a member if it is final
 * and package private or public</li>
 * <li>{@code implements} will select a member if it is the implementation of an interface method</li>
 * <li>{@code overrides} will select a member if it is the overriding implementation of an parent class method</li>
 * <li>{@code default} will select a member if it is a default method in an interface</li>
 * <li>{@code overloaded} will select a member if there are multiple methods with the same name in the class</li>
 * <li>{@code vararg} will select a member if it is a variable argument method</li>
 * <li>{@code name ~ /^has/} will select a member if the name of the member starts with {@code has}</li>
 * <li>{@code signature ~ /^has.*\(String,.*\)/} will select a member if the name starts with {@code has} and the
 * signature of the method contains a {@code }String} as the first argument.</li>
 * <li>{@code returns ~ /String/} will select a method if the return value is {@code String}</li>
 * <li>{@code annotation ~ /Generated/} will select a member if it is annotation with an annotation that
 * starts with {@code Generated}</li>
 * <li> any other identifier "selector" will select a member if the configured {@code Function<Member,Boolean>} assigned to the name
 * "selector" returns {@code true}. Selector functions can be added calling the method {@link #selector(String, Function)}.</li>
 * </ul>
 * <p>
 * Some of the conditions can only be applied to methods or only to fields. In that case using the expression for
 * a field or a method, for which it should not have been applied to will throw {@link IllegalArgumentException}.
 * <p>
 * The syntax of a selection expression formally:
 *
 * <ul>
 * <li>EXPRESSION :== TERMINAL | '!' EXPRESSION | '(' EXPRESSION ')' | EXPRESSION '&amp;' EXPRESSION ... |
 * EXPRESSION '|' EXPRESSION ... </li>
 * <li>TERMINAL ::= MODIFIER | PSEUDO_MODIFIER | name '~' REGEX | signature '~' REGEX | annotation ~ REGEX |
 * CALLER_DEFINED_SELECTOR</li>
 * <li>MODIFIER ::= private | protected | package | public | final | transient | volatile | static |
 * synthetic | synchronized | native | abstract | strict </li>
 * <li>PSEUDO_MODIFIER ::= default | implements | inherited | overrides | vararg | true | false</li>
 * </ul>
 * <p>
 * 'and' operation has higher priority than 'or' ust like in Java. Expressions are evaluated short circuit, although
 * it has no vital importance since the sub expressions cannot have side effects.
 */
public class Selector<T> {

    private static final int SYNTHETIC = 0x00001000;
    private final Map<String, Function<T, Boolean>> selectors = new HashMap<>();
    private final Map<String, BiFunction<T, Pattern, Boolean>> regexMemberSelectors = new HashMap<>();
    private SelectorNode top = null;

    public Selector() {

        selector("abstract", m -> only(m, Class.class, Method.class) && Modifier.isAbstract(getModifiers(m)));

        universalSelectors();

        fieldOnlySelectors();

        methodOnlySelectors();

        classOnlySelectory();


        regexSelector("annotation", (m, regex) -> only(m, AnnotatedElement.class) &&
            matchAnnotations((AnnotatedElement) m, regex));
    }

    private void classOnlySelectory() {//TODO fields and methods could be matched with their type/return type
        selector("interface", m -> only(m, Class.class) && ((Class<?>) m).isInterface());
        selector("primitive", m -> only(m, Class.class) && ((Class<?>) m).isPrimitive());
        selector("annotation", m -> only(m, Class.class) && ((Class<?>) m).isAnnotation());
        selector("anonymous", m -> only(m, Class.class) && ((Class<?>) m).isAnonymousClass());
        selector("array", m -> only(m, Class.class) && ((Class<?>) m).isArray());
        selector("enum", m -> only(m, Class.class) && ((Class<?>) m).isEnum());
        selector("member", m -> only(m, Class.class) && ((Class<?>) m).isMemberClass());
        selector("local", m -> only(m, Class.class) && ((Class<?>) m).isLocalClass());

        regexSelector("simpleName", (m, regex) -> only(m, Class.class) && regex.matcher(((Class) m).getSimpleName()).find());
        regexSelector("canonicalName", (m, regex) -> only(m, Class.class) && regex.matcher(((Class) m).getCanonicalName()).find());
        regexSelector("extends", (m, regex) -> only(m, Class.class) && regex.matcher(((Class) m).getSuperclass().getCanonicalName()).find());
    }

    private void methodOnlySelectors() {
        selector("synthetic", m -> only(m, Method.class) && (getModifiers(m) & SYNTHETIC) != 0);
        selector("synchronized", m -> only(m, Method.class) && Modifier.isSynchronized(getModifiers(m)));
        selector("native", m -> only(m, Method.class) && Modifier.isNative(getModifiers(m)));
        selector("strict", m -> only(m, Method.class) && Modifier.isStrict(getModifiers(m)));
        selector("default", m -> only(m, Method.class) &&
            ((Member) m).getDeclaringClass().isInterface() && !Modifier.isAbstract(getModifiers(m)));
        selector("vararg", m -> only(m, Method.class) && ((Method) m).isVarArgs());
        selector("implements", m -> only(m, Method.class) && methodImplements((Method) m));
        selector("overrides", m -> only(m, Method.class) && methodOverrides((Method) m));

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
     * declared in an interface then it does not implement anything and {@code methodImplements()} returns {@code false}
     * unless this is a default method that implements another method declared in an interface that the declaring
     * interface extends directly or through transitive closure of the interfaces extending each other.
     */
    private boolean methodImplements(Method m) {
        final var args = m.getParameterTypes();
        final var name = m.getName();
        if (m.getDeclaringClass().isInterface() && !m.isDefault() || Modifier.isAbstract(m.getModifiers())) {
            return false;
        }
        final var interfaces = collectInterfaces(m.getDeclaringClass());
        for (final var interfAce : interfaces) {
            if (classHas(interfAce, name, args)) return true;
        }
        return false;
    }

    /**
     * Collect all the interaces that this class implements including all the interfaces that are transitively extended
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

    private boolean classHas(Class<?> klass, String name, Class<?>[] args) {
        try {
            klass.getDeclaredMethod(name, args);
            return true;
        } catch (NoSuchMethodException ignored) {
        }
        return false;
    }

    private static boolean notImplemented() {
        throw new IllegalArgumentException("Not implemented");
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
     * Define a {@link Function} assigned to the name that can be referenced in the expression. Essentially the same
     * as {@link #selector(String, Function)} but this version will not throw an exception in case the function is
     * already defined. It will overwrite the function assigned to the name.
     *
     * @param name     the name to be used in the expression referencing the function
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
     * @param name     the name to be used in the expression referencing the function
     * @param function the function that will be executed when evaluating {@code #name}, where {@code name} is actually
     *                 the string provided in the argument {@code name}. The bi-function will get two arguments: 1.) the
     *                 checked object and 2.) the regular expresison {@link Pattern}. It is up to the bi-function to decide
     *                 if the pattern is used to find only or to match the whole string extracted from the object some
     *                 way. It is also the responsibility of the bi-function to extract some string from the object
     *                 calling getName, getSimpleName or whatever the object provides. For examples see the built-in
     *                 bi-functions in the constructor of this class.
     * @return {@code this} object to allow method chaining
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public Selector regexSelector(String name, BiFunction<T, Pattern, Boolean> function) {
        regexMemberSelectors.put(name, function);
        return this;
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

    /**
     * Check that the object matches the selection criteria.
     *
     * @param member the member to check
     * @return {@code true} if the selection matches the
     */
    @SuppressWarnings("WeakerAccess")
    public boolean match(T member) {
        return match(member, top);
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
