package javax0.geci.fluent.syntax;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.tree.FluentNodeCreator;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.tools.syntax.Lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax0.geci.tools.syntax.Lexeme.Type.EOF;
import static javax0.geci.tools.syntax.Lexeme.Type.SPACE;
import static javax0.geci.tools.syntax.Lexeme.Type.SYMBOL;
import static javax0.geci.tools.syntax.Lexeme.Type.WORD;

/**
 * This class implements the syntax analyzer that can process fluent api grammar definition.
 * <p>
 * The definition of the syntax of an expression is the following in lazy BNF:
 * <pre>
 *     expression ::= alternate ... alternate
 *     alternate ::= simple '|' ... '|' simple
 *     simple ::= terminal | terminal '*' | terminal '+' | terminal '?'
 *     terminal := method | '(' expression ')
 * </pre>
 * <p>
 * An {@code expression} is one or more '{@code alternate}'s separated by spaces.
 * An {@code alternate} is one or more {@code simple} separated by the character {@code |}. These are alternatives,
 * and alternatives have higher precendence so {@code a b|c} means either {@code a().b()} or {@code a().c()} in the
 * final fluent API and NOT {@code a().b()} or {@code c()}.
 * A {@code simple} is a {@code terminal} followed by one of the modifier characters {@code *+?} denoting zero or more,
 * one or more and optional occurrence of the {@code terminal}. A {@code terminal} can be a method name or signature
 * or an expression enclosed in parentheses.
 */
public class Syntax {
    private final Lexer lexer;
    private final FluentNodeCreator nodeCreator;

    public Syntax(Lexer lexer, FluentNodeCreator nodeCreator) {
        this.lexer = lexer;
        this.nodeCreator = nodeCreator;
    }

    private static boolean isOneOrMore(List<Node> nodes) {
        if (nodes.size() != 2) {
            return false;
        }
        if (nodes.get(0).getModifier() != Node.ONCE ||
                nodes.get(1).getModifier() != Node.ZERO_OR_MORE) {
            return false;
        }
        if (nodes.get(0).getClass() != nodes.get(1).getClass()) {
            return false;
        }
        if (nodes.get(0) instanceof Terminal) {
            final var t0 = (Terminal) nodes.get(0);
            final var t1 = (Terminal) nodes.get(1);
            return t0.getMethod().equals(t1.getMethod());
        } else {
            final var t0 = (Tree) nodes.get(0);
            final var t1 = (Tree) nodes.get(1);
            return t0.getList() == t1.getList();
        }
    }

    /**
     * Creates a mutable list of the arguments. This is needed to fill in the list of the nodes in a Tree node.
     * Optimization later changes the list.
     *
     * @param ts  the array of nodes to make list of
     * @param <T> the type of the elements
     * @return the modifiable list of nodes
     */
    @SafeVarargs
    private static <T> List<T> listOf(T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }

    /**
     * Analyze an expression and return the list of nodes it created.
     * <p>
     * The syntax of an expression is defined in the following way:
     *
     * <pre>
     *     expression ::= alternate ... alternate
     * </pre>
     *
     * @return the list of nodes that were created during the syntax analysis.
     */
    public List<Node> expression() {
        final var nodes = subExpression();
        if (lexer.peek().type != EOF) {
            throw new GeciException("Extra characters at the end: '" + lexer.rest() + "'");
        }
        return nodes;
    }

    public List<Node> subExpression() {
        final var nodes = new ArrayList<>(alternate());
        while (lexer.peek().type == SPACE) {
            lexer.get();
            nodes.addAll(alternate());
        }
        return nodes;
    }

    /**
     * Analyze an "alternate" and return the node it created.
     * <p>
     * The syntax of an "alternate" is defined in the following way:
     *
     * <pre>
     *     alternate ::= simple '|' ... '|' simple
     * </pre>
     * <p>
     * The name "alternate" comes from the fact that what it returns is a node that is an alternative of list
     * of sub nodes. If there is only one "simple" in the expression then it returns that and not a ONE_OF node
     * containing a "simple".
     * <p>
     * Usually the returned list has one element. It may happen that the returned value is the actual returned value
     * from {@link #simple()}, which means that on this level there are no alternatives. In this case the list of nodes
     * returned by {@link #simple()} is returned. In other cases the list will contain only one element, which is a
     * ONE_OF element sublisting the alternatives.
     *
     * @return the list of nodes that were created during the syntax analysis.
     */
    public List<Node> alternate() {
        final var first = simple();

        if (lexer.peek().type != SYMBOL || !lexer.peek().string.equals("|")) {
            return first;
        }

        final var nodes = new ArrayList<Node>();
        nodes.add(box(first));
        while (lexer.peek().type == SYMBOL && lexer.peek().string.equals("|")) {
            lexer.get();
            nodes.add(box(simple()));
        }
        if (nodes.size() == 1) {
            return nodes;
        } else {
            return listOf(nodeCreator.oneOfNode(nodes));
        }
    }

    /**
     * Analyze an "simple" and return the node it created.
     * <p>
     * The syntax of an "simple" is defined in the following way:
     * <p>
     * A
     * <pre>
     *     simple ::= terminal | terminal '*' | terminal '+' | terminal '?'
     * </pre>
     * <p>
     * Usually the method returns a list of a single element. The exception is when it returns a ONE_OR_MORE modified
     * terminal. In this case it returns a list of a ONE and a ZERO_OR_MORE node list. The higher layers, namely the
     * {@link #alternate()} interpret it appropriately putting it into a ONE node or passing it up to
     * {@link #expression()}.
     *
     * @return the list of nodes that were created during the syntax analysis.
     */
    public List<Node> simple() {
        final var nodes = terminal();
        final var lexeme = lexer.peek();
        if (lexeme.type == SYMBOL) {
            final Node node = box(nodes);
            final var modifierCharacter = lexeme.string;
            switch (modifierCharacter) {
                case "*":
                    lexer.get();
                    if (isOneOrMore(nodes)) {
                        return listOf(box(nodes.get(0), Node.ZERO_OR_MORE));
                    }
                    return listOf(box(node, Node.ZERO_OR_MORE));
                case "?":
                    lexer.get();
                    if (isOneOrMore(nodes)) {
                        return listOf(box(nodes.get(0), Node.ZERO_OR_MORE));
                    }
                    return listOf(box(node, Node.OPTIONAL));
                case "+":
                    lexer.get();
                    if (isOneOrMore(nodes)) {
                        return nodes;
                    }
                    if (node.getModifier() == Node.OPTIONAL) {
                        return listOf(node.clone(Node.ZERO_OR_MORE));
                    }
                    if (node.getModifier() == Node.ZERO_OR_MORE) {
                        return nodes;
                    }
                    return listOf(node, box(node, Node.ZERO_OR_MORE));
            }
        }
        return nodes;
    }

    /**
     * Analyze a terminal part of the expression. This is essentially a method name or method signature, or a whole
     * expression enclosed between parenthesis.
     * <p>
     * The definition of the syntax of a terminal is the following in lazy BNF:
     * <pre>
     *     terminal := method | '(' expression ')
     * </pre>
     *
     * @return the list of nodes that were created during the syntax analysis.
     */
    public List<Node> terminal() {
        if (lexer.peek().type == WORD) {
            return listOf(nodeCreator.oneNode(lexer.get().string));
        }
        if (lexer.peek().type == SYMBOL && lexer.peek().string.equals("(")) {
            lexer.get();
            final var nodes = subExpression();
            if (lexer.peek().type != SYMBOL || !lexer.peek().string.equals(")")) {
                throw new GeciException("Fluent expression syntax error after ( ... ) missing closing parenthesis at '"
                        + lexer.rest() + "'");
            }
            lexer.get();
            return nodes;
        }
        throw new GeciException("Fluent expression syntax error at '" + lexer.rest() + "'");
    }

    /**
     * Create a node from the list of nodes. In case the node list contains one single element then just return that
     * single element. If there are more elements then create a ONE tree node and put the elements into that one and
     * return the tree.
     *
     * @param nodes the list of the nodes to box
     * @return the single node of the list or a boxing node containing all nodes in the list
     */
    private Node box(List<Node> nodes) {
        final Node node;
        if (nodes.size() == 1) {
            node = nodes.get(0);
        } else {
            node = nodeCreator.oneNode(nodes);
        }
        return node;
    }

    /**
     * Box a node into another node which has the {@code modifier}. When the node is a ONE_OF type then a new node is
     * created with the required modifier and the ONE_OF node is a sibling under it. In other cases the original node
     * is cloned with the new modifier. This method is used to process the '?', '*' postfix modifiers.
     * <p>
     * When the node is already '*' ({@link Node#ZERO_OR_MORE} then the node itself is returned, even if
     * {@link Node#OPTIONAL} modifier is requested. That is because {@code (xxx*)?} is the same as {@code xxx*}.
     *
     * @param node     the node to box with the
     * @param modifier that can be {@link Node#OPTIONAL} or {@link Node#ZERO_OR_MORE}. Other modifiers are not allowed.
     * @return the new node with the modifier
     */
    private Node box(Node node, int modifier) {
        if (node.getModifier() == Node.ONE_OF || node.getModifier() == Node.ONE_TERMINAL_OF) {
            return nodeCreator.oneNode(listOf(node)).clone(modifier);
        }
        if (node.getModifier() == Node.ZERO_OR_MORE) {
            return node;
        }
        return node.clone(modifier);
    }
}
