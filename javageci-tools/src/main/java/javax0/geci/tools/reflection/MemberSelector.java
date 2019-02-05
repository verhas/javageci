package javax0.geci.tools.reflection;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
 * signature of the method contains a {@code }String} as the first argument.<li>
 * <li> "selector" will select a member if the configured {@code Function<Member,Boolean>} assigned to the name
 * "selector" returns {@code true}</li>
 * </ul>
 * <p>
 * Some of the conditions can only be applied to methods or only to fields. In that case using the expression for
 * a field or a method, for which it should not have been applied to will throw {@link IllegalArgumentException}.
 * <p>
 * The syntax of a selection expression formally:
 *
 * <ul>
 *     <li>EXPRESSION ::= TERMINAL | '!' EXPRESSION | '(' EXPRESSION ')' |
 *     EXPRESSION '|' EXPRESSION | EXPRESSION '&' EXPRESSION </li>
 * <li>TERMINAL ::= MODIFIER | PSEUDO_MODIFIER | name '~' REGEX | signature '~' REGEX | CALLER_DEFINED_SELECTOR</li>
 * <li>MODIFIER ::= private | protected | package | public | final | transient | volatile | static |
 * synthetic | synchronized | native | abstract | strict </li>
 * <li>PSEUDO_MODIFIER ::= default | implements | inherited | overrides | vararg</li>
 * </ul>
 */
public class MemberSelector {

    private MemberSelector() {
    }

    private final Map<String, Function<Member, Boolean>> functions = new HashMap<>();


    /**
     * Define a {@link Function} assigned to the name that can be referenced in the expression.
     *
     * @param name     the name to be used in the expression referencing the functin
     * @param function the function that will be executed when evaluating {@code #name}, where {@code name} is actually
     *                 the string provided in the argument {@code name}
     * @return {@code this} object to allow method chaining
     */
    public MemberSelector function(String name, Function<Member, Boolean> function) {
        functions.put(name, function);
        return this;
    }

    /**
     * Compile a string to the internal structure of the member selector that can later be used to match a member.
     *
     * @param expression a logical expression describeed as a string
     * @return a new prepared MemberSelector object
     */
    public static MemberSelector compile(String expression) {
        throw new IllegalArgumentException("not implented yet");
    }

    /**
     * Check that the member matches the selection criteria.
     *
     * @param member the member to check
     * @return {@code true} if the selection matches the
     */
    public boolean match(Member member) {
        int modifiers = member.getModifiers();
        return false;
    }
}
