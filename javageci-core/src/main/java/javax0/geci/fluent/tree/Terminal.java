package javax0.geci.fluent.tree;

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
}