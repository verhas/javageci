package javax0.geci.tools.reflection;

import javax0.geci.tools.MethodTool;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An {@code MemberSelector} can select or deselect a member based on the selection expression.
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
 * <li>{@code inherited} will select a member if it is inherited from a parent class or from an interface and is
 * not implemented in the class itself</li>
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
 * "selector" returns {@code true}. Selector functions can be added calling the method {@link #function(String, Function)}.</li>
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
public class MemberSelector {

    private static final int SYNTHETIC = 0x00001000;
    private final Map<String, Function<Member, Boolean>> functions = new HashMap<>();
    private SelectorNode top = null;

    public MemberSelector() {
        function("abstract", m -> Modifier.isAbstract(m.getModifiers()));
        function("private", m -> Modifier.isPrivate(m.getModifiers()));
        function("protected", m -> Modifier.isProtected(m.getModifiers()));
        function("package", m ->
                !Modifier.isPublic(m.getModifiers())
                        && !Modifier.isProtected(m.getModifiers())
                        && !Modifier.isPrivate(m.getModifiers()));
        function("public", m -> Modifier.isPublic(m.getModifiers()));
        function("final", m -> Modifier.isFinal(m.getModifiers()));
        function("transient", m -> fieldOnly(m) && Modifier.isTransient(m.getModifiers()));
        function("volatile", m -> fieldOnly(m) && Modifier.isVolatile(m.getModifiers()));
        function("static", m -> Modifier.isStatic(m.getModifiers()));
        function("synthetic", m -> methodOnly(m) && (m.getModifiers() & SYNTHETIC) != 0);
        function("synchronized", m -> methodOnly(m) && Modifier.isSynchronized(m.getModifiers()));
        function("native", m -> methodOnly(m) && Modifier.isNative(m.getModifiers()));
        function("abstract", m -> methodOnly(m) && Modifier.isAbstract(m.getModifiers()));
        function("strict", m -> methodOnly(m) && Modifier.isStrict(m.getModifiers()));

        function("default", m -> methodOnly(m) &&
                m.getDeclaringClass().isInterface() && !Modifier.isAbstract(m.getModifiers()));
        function("implements", m -> methodOnly(m) && notImplemented());
        function("inherited", m -> methodOnly(m) && notImplemented());
        function("overrides", m -> methodOnly(m) && notImplemented());
        function("vararg", m -> methodOnly(m) && ((Method) m).isVarArgs());
        function("true", m -> true);
        function("false", m -> false);
    }

    private static boolean notImplemented() {
        throw new IllegalArgumentException("Not implemented");
    }

    @SuppressWarnings("SameReturnValue")
    private static boolean methodOnly(Member m) {
        if (!(m instanceof Method)) {
            throw new IllegalArgumentException("A method only selector was applied to a field.");
        }
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    private static boolean fieldOnly(Member m) {
        if (!(m instanceof Field)) {
            throw new IllegalArgumentException("A field only selector was applied to a method.");
        }
        return true;
    }

    /**
     * Define a {@link Function} assigned to the name that can be referenced in the expression.
     *
     * @param name     the name to be used in the expression referencing the functin
     * @param function the function that will be executed when evaluating {@code #name}, where {@code name} is actually
     *                 the string provided in the argument {@code name}
     * @return {@code this} object to allow method chaining
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public MemberSelector function(String name, Function<Member, Boolean> function) {
        functions.put(name, function);
        return this;
    }

    /**
     * Compile a string to the internal structure of the member selector that can later be used to match a member.
     *
     * @param expression a logical expression described as a string
     * @return {@code this} object to allow method chaining
     */
    public MemberSelector compile(String expression) {
        final var compiler = new SelectorCompiler(functions);
        top = compiler.compile(expression);
        return this;
    }

    /**
     * Check that the member matches the selection criteria.
     *
     * @param member the member to check
     * @return {@code true} if the selection matches the
     */
    @SuppressWarnings("WeakerAccess")
    public boolean match(Member member) {
        return match(member, top);
    }


    private boolean matchOr(Member m, SelectorNode.Or node) {
        for (final var sub : node.subNodes) {
            if (match(m, sub)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchAnd(Member m, SelectorNode.And node) {
        for (final var sub : node.subNodes) {
            if (!match(m, sub)) {
                return false;
            }
        }
        return true;
    }

    private boolean match(Member m, SelectorNode node) {
        if (node instanceof SelectorNode.Or) {
            return matchOr(m, (SelectorNode.Or) node);
        }
        if (node instanceof SelectorNode.And) {
            return matchAnd(m, (SelectorNode.And) node);
        }
        if (node instanceof SelectorNode.Not) {
            return !match(m, ((SelectorNode.Not) node).subNode);
        }
        if (node instanceof SelectorNode.Name) {
            return ((SelectorNode.Regex) node).regex.matcher(m.getName()).find();
        }
        if (node instanceof SelectorNode.Annotation) {
            return matchAnnotations(m, ((SelectorNode.Regex) node).regex);
        }
        if (node instanceof SelectorNode.Returns) {
            methodOnly(m);
            return ((SelectorNode.Regex) node).regex.matcher(
                    ((Method) m).getReturnType().getCanonicalName()).find();
        }
        if (node instanceof SelectorNode.Signature) {
            methodOnly(m);
            return ((SelectorNode.Regex) node).regex.matcher(
                    MethodTool.methodSignature((Method) m)).find();
        }
        if (node instanceof SelectorNode.Terminal) {
            return ((SelectorNode.Terminal) node).terminal.apply(m);
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
    private boolean matchAnnotations(Member m, Pattern pattern) {
        final Annotation[] annotations;
        if (m instanceof Method) {
            annotations = ((Method) m).getAnnotations();
        } else if (m instanceof Field) {
            annotations = ((Field) m).getAnnotations();
        } else {
            throw new IllegalArgumentException("It should not happen " + m.getName() + "/" +
                    m.getClass().getCanonicalName() + " is neither a method nor a field.");
        }
        return Arrays.stream(annotations).anyMatch(a ->
                pattern.matcher(a.annotationType().getCanonicalName()).find());
    }
}
