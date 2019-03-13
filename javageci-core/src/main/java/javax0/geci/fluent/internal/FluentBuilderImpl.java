package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.fluent.syntax.Syntax;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.tools.Tools;
import javax0.geci.tools.syntax.Lexer;

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
            return Tools.classForName(interfaceName);
        } catch (ClassNotFoundException e1) {
            throw new GeciException(interfaceName + " interface can not be found");
        }
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

    public FluentBuilder optional(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(newTerminal(Node.OPTIONAL, method));
        return next;
    }

    public FluentBuilder optional(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(newTree(Node.OPTIONAL, nodesOf(sub)));
        return next;
    }

    public FluentBuilder oneOrMore(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(newTerminal(Node.ONCE, method));
        next.nodes.add(newTerminal(Node.ZERO_OR_MORE, method));
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

    public FluentBuilder oneOrMore(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(newTree(Node.ONCE, nodesOf(sub)));
        next.nodes.add(newTree(Node.ZERO_OR_MORE, nodesOf(sub)));
        return next;
    }

    public FluentBuilder zeroOrMore(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(newTerminal(Node.ZERO_OR_MORE, method));
        return next;
    }

    public FluentBuilder zeroOrMore(FluentBuilder sub) {
        assertClass(sub);
        var next = copy();
        next.nodes.add(newTree(Node.ZERO_OR_MORE, nodesOf(sub)));
        return next;
    }

    public FluentBuilder oneOf(String... methods) {
        assertMethod(methods);
        var next = copy();

        next.nodes.add(newTree(Node.ONE_TERMINAL_OF, Arrays.stream(methods)
                .map(method -> newTerminal(Node.ONCE, method)).collect(Collectors.toList())));
        return next;
    }

    private Class<?> classOf(FluentBuilder builder) {
        return ((FluentBuilderImpl) builder).klass;
    }

    private List<Node> nodesOf(FluentBuilder builder) {
        return ((FluentBuilderImpl) builder).nodes;
    }

    public FluentBuilder oneOf(FluentBuilder... subs) {
        assertClass(subs);
        var next = copy();
        next.nodes.add(newTree(Node.ONE_OF, Arrays.stream(subs)
                .map(sub -> newTree(Node.ONCE, nodesOf(sub))).collect(Collectors.toList())));
        return next;
    }

    private FluentBuilder append(FluentBuilder that) {
        var next = copy();
        next.nodes.addAll(((FluentBuilderImpl) that).nodes);
        return next;
    }

    public FluentBuilder syntax(String syntaxDef) {
        final var lexer = new Lexer(syntaxDef);
        var next = copy();
        next.nodes.clear();
        final var syntaxAnalyzer = new Syntax(lexer, next);
        return append(syntaxAnalyzer.expression());
    }

    public FluentBuilder one(String method) {
        assertMethod(method);
        var next = copy();
        next.nodes.add(newTerminal(Node.ONCE, method));
        return next;
    }

    public FluentBuilder one(FluentBuilder sub) {
        var nodes = nodesOf(sub);
        var next = copy();
        var tree = newTree(Node.ONCE, nodes);
        next.nodes.add(tree);
        return next;
    }

    public FluentBuilder name(String interfaceName) {
        if (interfaceName == null || interfaceName.length() == 0) {
            return this;
        }
        var next = copy();
        next.lastName = interfaceName;
        return next;
    }

    @Override
    public String toString() {
        return nodes.stream().map(Node::toString).collect(Collectors.joining(","));
    }
}
