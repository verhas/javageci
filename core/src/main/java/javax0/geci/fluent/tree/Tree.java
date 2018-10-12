package javax0.geci.fluent.tree;

import java.util.List;
import java.util.stream.Collectors;

public final class Tree extends Node {
    private final List<Node> tree;

    public Tree(int modifier, List<Node> tree) {
        super(modifier);
        this.tree = tree;
    }

    @Override
    public String toString() {
        return "(" + tree.stream().map(Node::toString).collect(Collectors.joining(",")) + ")" + super.toString();
    }
}
