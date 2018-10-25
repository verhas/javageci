package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;

import java.lang.reflect.Method;
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
public class FluentBuilderImpl implements FluentBuilder {

    private final Class<?> klass;
    private final List<Node> nodes = new ArrayList<>();
    private final MethodCollection methods;
    private Method cloner = null;
    private String startMethod = null;
    private String interfaces = null;
    private String lastType = null;

    public FluentBuilderImpl(Class<?> klass) {
        methods = new MethodCollection(klass);
        this.klass = klass;
    }
    private FluentBuilderImpl(FluentBuilderImpl that){
        this.klass = that.klass;
        this.nodes.addAll(that.nodes);
        this.methods = that.methods;
        this.cloner = that.cloner;
        this.startMethod = that.startMethod;
        this.interfaces = that.interfaces;
        this.lastType = that.lastType;
    }

    public String getInterfaces() {
        return interfaces;
    }

    public String getLastType() {
        return lastType;
    }

    public String getStartMethod() {
        return startMethod;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public MethodCollection getMethods() {
        return methods;
    }

    public Method getCloner() {
        return cloner;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public FluentBuilder start(String method) {
        var next = copy();
        next.startMethod = method;
        return next;
    }

    public FluentBuilder cloner(String method) {
        assertMethod(method);
        Method clonerMethod = methods.get(method);
        if (clonerMethod.getGenericExceptionTypes().length > 0) {
            throw new GeciException("The cloner method should not have parameters");
        }
        if (clonerMethod.getReturnType() != klass) {
            throw new GeciException("The cloner method should return the type of the class it is in.");
        }
        var next = copy();
        next.cloner = clonerMethod;
        return next;
    }

    public Tree get() {
        return new Tree(Node.ONCE, nodes);
    }

    private FluentBuilderImpl copy() {
        return new FluentBuilderImpl(this);
    }


    private void assertClass(FluentBuilder... subs) {
        for (var sub : subs) {
            if (!(sub instanceof FluentBuilderImpl)) {
                throw new GeciException("FluentBuilderImpl can not handle other FluentBuilder implementations");
            }
            var subi = (FluentBuilderImpl) sub;
            if (subi.klass != klass) {
                throw new GeciException("Cannot compose fluent API from different classes.");
            }
        }
    }

    private void assertMethod(String... methodArr) {
        for (var method : methodArr) {
            if (methods.get(method) == null) {
                throw new GeciException("Method '" + method + "' is not found in class " + klass);
            }
        }
    }

    public FluentBuilder implement(String interfaces) {
        var next = copy();
        next.interfaces = interfaces;
        return next;
    }

    public FluentBuilder fluentType(String type) {
        var next = copy();
        next.lastType = type;
        return next;
    }

    public FluentBuilder exclude(String method) {
        methods.exclude(method);
        return this;
    }

    public FluentBuilder optional(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(new Terminal(Node.OPTIONAL, method));
        return next;
    }

    public FluentBuilder optional(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(new Tree(Node.OPTIONAL, ((FluentBuilderImpl) sub).nodes));
        return next;
    }

    public FluentBuilder oneOrMore(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(new Terminal(Node.ONCE, method));
        next.nodes.add(new Terminal(Node.ZERO_OR_MORE, method));
        return next;
    }

    public FluentBuilder oneOrMore(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        var subi = (FluentBuilderImpl) sub;
        next.nodes.add(new Tree(Node.ONCE, subi.nodes));
        next.nodes.add(new Tree(Node.ZERO_OR_MORE, subi.nodes));
        return next;
    }

    public FluentBuilder zeroOrMore(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(new Terminal(Node.ZERO_OR_MORE, method));
        return next;
    }

    public FluentBuilder zeroOrMore(FluentBuilder sub) {
        assertClass(sub);
        var subi = (FluentBuilderImpl) sub;
        var next = copy();
        next.nodes.add(new Tree(Node.ZERO_OR_MORE, subi.nodes));
        return next;
    }

    public FluentBuilder oneOf(String... methods) {
        assertMethod(methods);
        var next = copy();

        next.nodes.add(new Tree(Node.ONE_TERMINAL_OF, Arrays.stream(methods)
                .map(method -> new Terminal(Node.ONCE, method)).collect(Collectors.toList())));
        return next;
    }

    public FluentBuilder oneOf(FluentBuilder... subs) {
        assertClass(subs);
        var next = copy();
        next.nodes.add(new Tree(Node.ONE_OF, Arrays.stream(subs)
                .map(sub -> new Tree(Node.ONCE, ((FluentBuilderImpl) sub).nodes)).collect(Collectors.toList())));
        return next;
    }

    public FluentBuilder one(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(new Terminal(Node.ONCE, method));
        return next;
    }

    public FluentBuilder one(FluentBuilder builder) {
        return builder;
    }

    @Override
    public String toString() {
        return nodes.stream().map(Node::toString).collect(Collectors.joining(","));
    }
}
