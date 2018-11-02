package javax0.geci.fluent.tree;

import java.util.List;
import java.util.stream.Collectors;

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
    public String toString() {
        return "(" + list.stream().map(Node::toString).collect(Collectors.joining(",")) + ")" + super.toString();
    }
}
