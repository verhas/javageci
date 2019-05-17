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
        final var tree = new Tree(modifier, list);
        tree.setName(getName());
        return tree;
    }

    public Node clone(int modifier,final List<Node> newList) {
        final var tree = new Tree(modifier, newList);
        tree.setName(getName());
        return tree;
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

    /**
     * Compares a Tree node to another node. Terminal nodes are always "less-than" a Tree node.
     * Tree nodes that have less number of sub nodes in their list are also "less-than" the longer list tree.
     * When two tree nodes have the same size list then the one is "smaller-than", which has the first "smaller-than"
     * element in the list. If all elements in the list are "equal" then the return value is zero.
     *
     * @param node the other node to compare
     * @return see {@link Comparable#compareTo(Object)}
     */
    @Override
    public int compareTo(Node node) {
        if( node instanceof Terminal){
            return +1;
        }
        Tree tree = (Tree)node;
        if( tree.getList().size() > getList().size()){
            return +1;
        }
        if( tree.getList().size() < getList().size()){
            return -1;
        }
        for(int i = 0 ; i < getList().size(); i ++){
            final var res = getList().get(i).compareTo(tree.getList().get(i));
            if( res != 0 ){
                return res;
            }
        }
        return 0;
    }
}
