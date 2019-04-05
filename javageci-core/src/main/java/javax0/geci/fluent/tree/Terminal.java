package javax0.geci.fluent.tree;

import java.util.Objects;

/**
 * A {@link Node} that represents a terminal symbol, a.k.a. a specific method to be invoked in the fluent API.
 * A terminal {@link Node} has a method represented by the name of the method or ny the signature of the method
 * containing all the argument types in addition to the properties of the {@link Node}.
 */
public class Terminal extends Node {
    private final String method;

    public Terminal(int modifier, String method) {
        super(modifier);
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public Node clone(int modifier) {
        return new Terminal(modifier,method);
    }

    @Override
    public String toString() {
        return method + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terminal terminal = (Terminal) o;
        return method.equals(terminal.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }

    /**
     * Compare a Terminal to another Node. Tree nodes are always "greater than" terminal nodes.
     * Terminal nodes are string compared based on the name of the method.
     * @param node the other node to compare
     * @return see {@link Comparable#compareTo(Object)}
     */
    @Override
    public int compareTo(Node node) {
        if( node instanceof Tree){
            return -1;
        }
        Terminal terminal = (Terminal)node;
        return getMethod().compareTo(terminal.getMethod());
    }
}