package javax0.geci.fluent;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A fluent API can be best described using a finite state automaton. A finite state automaton can be bes described
 * using regular expressions. Regular expressions can be described using a fluent API designed to describe regular
 * expressions. This fluent builder is the basic builder for the fluent API facade for the fluent API regular
 * expressions.
 */
public class FluentBuilder {

    private final Class<?> klass;
    private final List<Node> nodes = new ArrayList<>();

    private FluentBuilder(Class<?> klass) {
        this.klass = klass;
    }

    public static FluentBuilder from(Class<?> klass) {
        return new FluentBuilder(klass);
    }

    public Tree get() {
        return new Tree(Node.ONCE, nodes);
    }

    private FluentBuilder copy() {
        var klone = new FluentBuilder(klass);
        klone.nodes.addAll(nodes);
        return klone;
    }


    private void assertClass(FluentBuilder sub) {
        if (sub.klass != klass) {
            throw new GeciException("Cannot compose fluent API from different classes.");
        }
    }

    public FluentBuilder optional(String method) {
        var next = copy();
        next.nodes.add(new Terminal(Node.OPTIONAL, method));
        return next;
    }

    public FluentBuilder optional(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(new Tree(Node.OPTIONAL, sub.nodes));
        return next;
    }

    public FluentBuilder many(String method) {
        var next = copy();
        next.nodes.add(new Terminal(Node.MANY, method));
        return next;
    }

    public FluentBuilder many(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(new Tree(Node.MANY, sub.nodes));
        return next;
    }

    public FluentBuilder zeroOrMore(String method) {
        var next = copy();
        next.nodes.add(new Terminal(Node.ZERO_OR_MORE, method));
        return next;
    }

    public FluentBuilder zeroOrMore(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(new Tree(Node.ZERO_OR_MORE, sub.nodes));
        return next;
    }

    public FluentBuilder oneOf(String... methods) {
        var next = copy();

        next.nodes.add(new Tree(Node.ONE_OF, Arrays.stream(methods)
                .map(method -> new Terminal(Node.ONCE, method)).collect(Collectors.toList())));
        return next;
    }

    public FluentBuilder oneOf(FluentBuilder... subs) {
        for (var sub : subs) {
            assertClass(sub);
        }
        var next = copy();
        next.nodes.add(new Tree(Node.ONE_OF, Arrays.stream(subs)
                .map(sub -> new Tree(Node.ONCE, sub.nodes)).collect(Collectors.toList())));
        return next;
    }

    public FluentBuilder call(String method) {
        var next = copy();
        next.nodes.add(new Terminal(Node.ONCE, method));
        return next;
    }

    @Override
    public String toString() {
        return nodes.stream().map(Node::toString).collect(Collectors.joining(","));
    }
}
