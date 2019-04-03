package javax0.geci.fluent.tree;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link Node} that represents an internal, non-leaf node of the syntax tree of the fluent API defined. It can be
 * the whole tree, when this is the top level {@link Node} of the syntax tree or just an internal {@link Node} that has
 * {@link Node} elements below it, each beaing either a {@code Tree} themselves or a {@link Terminal} representing
 * a concrete method.
 * <p>
 * A {@code Tree} has a list of nodes that are under it in addition to the properties defined in the abstract class
 * {@link Node}.
 */
public final class Tree extends Node {
    private final List<Node> list;

    public Tree(int modifier, List<Node> tree) {
        super(modifier);
        this.list = tree;
    }

    public List<Node> getList() {
        return list;
    }

    @Override
    public Node clone(int modifier) {
        return new Tree(modifier, list);
    }

    @Override
    public String toString() {
        if (getModifier() == ONE_OF || getModifier() == ONE_TERMINAL_OF) {
            return "(" + list.stream().map(Node::toString).collect(Collectors.joining("|")) + ")";
        } else {
            if (list.size() == 1) {
                return list.stream().map(Node::toString).collect(Collectors.joining(" ")) + super.toString();
            } else {
                return "(" + list.stream().map(Node::toString).collect(Collectors.joining(" ")) + ")" + super.toString();
            }
        }
    }
}
