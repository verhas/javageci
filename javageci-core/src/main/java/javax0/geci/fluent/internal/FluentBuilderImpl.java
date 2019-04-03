package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.fluent.tree.FluentNodeCreator;
import javax0.geci.fluent.syntax.Syntax;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.syntax.Lexer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A fluent API can be best described using a finite state automaton. A finite state automaton can be bes described
 * using regular expressions. Regular expressions can be described using a fluent API designed to describe regular
 * expressions. This fluent builder is the basic builder for the fluent API facade for the fluent API regular
 * expressions.
 */
public class FluentBuilderImpl implements FluentBuilder, FluentNodeCreator {

    private final Class<?> klass;
    private final List<Node> nodes = new ArrayList<>();
    private final MethodCollection methods;
    private Method cloner = null;
    private String startMethod = null;
    private String interfaces = null;
    private String lastType = null;
    private String lastName = null;

    public FluentBuilderImpl(Class<?> klass) {
        methods = new MethodCollection(klass);
        this.klass = klass;
    }

    private FluentBuilderImpl(FluentBuilderImpl that) {
        this.lastName = that.lastName;
        this.klass = that.klass;
        this.nodes.addAll(that.nodes);
        this.methods = that.methods;
        this.cloner = that.cloner;
        this.startMethod = that.startMethod;
        this.interfaces = that.interfaces;
        this.lastType = that.lastType;
    }

    private static Class<?> classOf(FluentBuilder builder) {
        return ((FluentBuilderImpl) builder).klass;
    }

    private static List<Node> nodesOf(FluentBuilder builder) {
        return ((FluentBuilderImpl) builder).nodes;
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

    @Override
    public FluentBuilder start(String method) {
        final var next = copy();
        next.startMethod = method;
        return next;
    }

    @Override
    public FluentBuilder cloner(String method) {
        assertThatMethodExistsInTheClass(method);
        Method clonerMethod = methods.get(method);
        if (clonerMethod.getGenericExceptionTypes().length > 0) {
            throw new GeciException("The cloner method should not have parameters");
        }
        if (clonerMethod.getReturnType() != klass) {
            throw new GeciException("The cloner method should return the type of the class it is in.");
        }
        final var next = copy();
        next.cloner = clonerMethod;
        return next;
    }

    public Tree get() {
        return newTree(Node.ONCE, nodes);
    }

    private FluentBuilderImpl copy() {
        return new FluentBuilderImpl(this);
    }

    private void assertClass(FluentBuilder... subs) {
        for (var sub : subs) {
            if (!(sub instanceof FluentBuilderImpl)) {
                throw new GeciException("FluentBuilderImpl can not handle other FluentBuilder implementations");
            }
            if (classOf(sub) != klass) {
                throw new GeciException("Cannot compose fluent API from different classes.");
            }
        }
    }

    private void assertThatMethodExistsInTheClass(String... methodArr) {
        for (var method : methodArr) {
            if (methods.get(method) == null) {
                throw new GeciException("Method '" + method + "' is not found in class " + klass);
            }
        }
    }

    @Override
    public FluentBuilder implement(String interfaces) {
        final var next = copy();
        next.interfaces = interfaces;
        referenceTheMethodsIn(interfaces);
        return next;
    }

    private void referenceTheMethodsIn(String interfaces) {
        var interfaceNames = Arrays.stream(interfaces.split(",")).map(String::trim).collect(Collectors.toList());
        for (final var interfaceName : interfaceNames) {
            var intrface = getInterfaceClass(interfaceName);
            if (!intrface.isInterface()) {
                throw new GeciException(interfaceName + " is not an interface");
            }
            for (var method : intrface.getMethods()) {
                exclude(method.getName());
                methods.get(method.getName());
            }
        }
    }

    private Class getInterfaceClass(String interfaceName) {
        try {
            return GeciReflectionTools.classForName(interfaceName);
        } catch (ClassNotFoundException e1) {
            throw new GeciException(interfaceName + " interface can not be found");
        }
    }

    @Override
    public FluentBuilder fluentType(String type) {
        final var next = copy();
        next.lastType = type;
        return next;
    }

    @Override
    public FluentBuilder exclude(String method) {
        methods.exclude(method);
        return this;
    }

    @Override
    public FluentBuilder include(String method) {
        methods.get(method);
        methods.include(method);
        return this;
    }

    private Terminal newTerminal(int modifiers, String method) {
        var terminal = new Terminal(modifiers, method);
        if (lastName != null) {
            terminal.setName(lastName);
        }
        lastName = null;
        return terminal;
    }

    @Override
    public Node optionalNode(String method) {
        assertThatMethodExistsInTheClass(method);
        return newTerminal(Node.OPTIONAL, method);
    }

    private <T> FluentBuilder buildWith(Function<T, Node> buildNode, T method) {
        final var next = copy();
        next.nodes.add(buildNode.apply(method));
        return next;
    }

    @Override
    public FluentBuilder optional(String method) {
        return buildWith(this::optionalNode, method);
    }

    private Node optionalNode(FluentBuilder sub) {
        return newTree(Node.OPTIONAL, nodesOf(sub));
    }

    @Override
    public FluentBuilder optional(FluentBuilder sub) {
        assertClass(sub);
        return buildWith(this::optionalNode, sub);
    }

    @Override
    public FluentBuilder oneOrMore(String method) {
        assertThatMethodExistsInTheClass(method);
        final var next = copy();
        next.nodes.add(oneNode(method));
        next.nodes.add(zeroOrMoreNode(method));
        return next;
    }

    private Tree newTree(int modifiers, List<Node> nodes) {
        var tree = new Tree(modifiers, nodes);
        if (lastName != null) {
            tree.setName(lastName);
        }
        lastName = null;
        return tree;
    }

    @Override
    public FluentBuilder oneOrMore(FluentBuilder sub) {
        assertClass(sub);
        final var next = copy();
        next.nodes.add(oneNode(sub));
        next.nodes.add(zeroOrMoreNode(sub));
        return next;
    }

    @Override
    public Node zeroOrMoreNode(String method) {
        assertThatMethodExistsInTheClass(method);
        return newTerminal(Node.ZERO_OR_MORE, method);
    }

    @Override
    public FluentBuilder zeroOrMore(String method) {
        return buildWith(this::zeroOrMoreNode, method);
    }

    private Node zeroOrMoreNode(FluentBuilder sub) {
        return newTree(Node.ZERO_OR_MORE, nodesOf(sub));
    }

    @Override
    public FluentBuilder zeroOrMore(FluentBuilder sub) {
        assertClass(sub);
        return buildWith(this::zeroOrMoreNode, sub);
    }

    @Override
    public Node oneOfNode(String... methods) {
        assertThatMethodExistsInTheClass(methods);
        return newTree(Node.ONE_TERMINAL_OF, Arrays.stream(methods)
                .map(method -> newTerminal(Node.ONCE, method)).collect(Collectors.toList()));
    }

    @Override
    public FluentBuilder oneOf(String... methods) {
        return buildWith(this::oneOfNode, methods);
    }

    @Override
    public Node oneOfNode(List<Node> subs) {
        return newTree(Node.ONE_OF, subs);
    }

    private Node oneOfNode(FluentBuilder... subs) {
        return newTree(Node.ONE_OF, Arrays.stream(subs)
                .map(sub -> newTree(Node.ONCE, nodesOf(sub))).collect(Collectors.toList()));
    }

    @Override
    public FluentBuilder oneOf(FluentBuilder... subs) {
        assertClass(subs);
        return buildWith(this::oneOfNode, subs);
    }

    @Override
    public FluentBuilder syntax(String syntaxDef) {
        final var syntaxAnalyzer = new Syntax(new Lexer(syntaxDef), this);
        final var next = copy();
        next.nodes.addAll(syntaxAnalyzer.expression());
        return next;
    }

    @Override
    public Node oneNode(String method) {
        assertThatMethodExistsInTheClass(method);
        return newTerminal(Node.ONCE, method);
    }

    @Override
    public FluentBuilder one(String method) {
        return buildWith(this::oneNode, method);
    }

    @Override
    public Node oneNode(List<Node> sub) {
        return new Tree(Node.ONCE, sub);
    }

    private Node oneNode(FluentBuilder sub) {
        final var nodes = nodesOf(sub);
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return newTree(Node.ONCE, nodesOf(sub));
    }

    @Override
    public FluentBuilder one(FluentBuilder sub) {
        return buildWith(this::oneNode, sub);
    }

    @Override
    public FluentBuilder name(String interfaceName) {
        if (interfaceName == null || interfaceName.length() == 0) {
            return this;
        }
        final var next = copy();
        next.lastName = interfaceName;
        return next;
    }

    @Override
    public String toString() {
        return nodes.stream().map(Node::toString).collect(Collectors.joining(" "));
    }
}
