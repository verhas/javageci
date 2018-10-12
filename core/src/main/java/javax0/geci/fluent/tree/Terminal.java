package javax0.geci.fluent.tree;

public class Terminal extends Node {
    private final String method;

    public Terminal(int modifier, String method) {
        super(modifier);
        this.method = method;
    }

    @Override
    public String toString() {
        return method + super.toString();
    }
}