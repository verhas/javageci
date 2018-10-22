package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.tools.JavaSourceBuilder;
import javax0.geci.tools.Tools;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassBuilder {
    private final InterfaceNameFactory ifNameFactory;
    private final MethodCollection methods;
    private final FluentBuilderImpl fluent;
    private String interfaceName;

    /**
     * Create a ClassBuilder that builds the interface and class structure from the fluent definition.
     *
     * @param fluent the fluent definition.
     */
    public ClassBuilder(FluentBuilderImpl fluent) {
        this.ifNameFactory = new InterfaceNameFactory();
        this.fluent = fluent;
        this.methods = fluent.getMethods();
    }

    /**
     * Create a new ClassBuilder that is essentially the clone of the other one.
     *
     * @param that the other class builder
     */
    private ClassBuilder(ClassBuilder that) {
        this.ifNameFactory = that.ifNameFactory;
        this.methods = that.methods;
        this.fluent = that.fluent;
        this.interfaceName = that.interfaceName;
    }


    /**
     * Build the interface and class structure that implements the fluent interface.
     *
     * @return the string of the code that was built up.
     */
    public String build() {
        var list = fluent.getNodes();
        if (list.size() == 0) {
            throw new GeciException("There are no actual calls in the fluent structure.");
        }
        final var tree = new Tree(Node.ONCE, list);
        final var calculator = new LastNodeTypeCalculator(methods);
        final var exitType = calculator.getLastNodeReturnType(list.get(list.size() - 1));
        final var lastInterface = Tools.normalizeTypeName(exitType);
        final var interfaces = build(tree, lastInterface);
        final var code = new JavaSourceBuilder();
        writeStartMethod(code);
        writeWrapperClass(code);
        code.write(interfaces);
        return code.toString();
    }

    /**
     * Write the source code of the start method into the source builder.
     * <p>
     * The start method is a public static method with no argument that creates a new instance of the wrapper
     * class and returns it as the interface type that can be used to start the fluent API structure.
     *
     * @param code to write the start method into
     */
    private void writeStartMethod(JavaSourceBuilder code) {
        final var startMethod = fluent.getStartMethod() == null ? "start" : fluent.getStartMethod();
        try (final var mtBl = code.open("public static %s %s()", ifNameFactory.getLastName(), startMethod)) {
            mtBl.statement("return new Wrapper()");
        }
    }

    /**
     * Write the source code of the wrapper class into the source builder.
     *
     * @param code to write the start method into
     */
    private void writeWrapperClass(JavaSourceBuilder code) {
        try (var klBl = code.open("public static class Wrapper implements %s", setJoin(ifNameFactory.getAllNames()))) {
            klBl.statement("private final %s that", fluent.getKlass().getCanonicalName());
            if (fluent.getCloner() != null) {
                try (var coBl = klBl.open("public Wrapper(%s that)", fluent.getKlass().getCanonicalName())) {
                    coBl.statement("this.that = that");
                }
            }
            try (var coBl = klBl.open("public Wrapper()")) {
                coBl.statement("this.that = new %s()", fluent.getKlass().getCanonicalName());
            }
            writeWrapperMethods(code);
        }
    }


    private void writeWrapperMethods(JavaSourceBuilder code) {
        for (var signature : methods.methodSignatures()) {
            var method = methods.get(signature);
            if (fluent.getCloner() == null || !fluent.getCloner().equals(method)) {
                var isExitNode = methods.isExitNode(signature);
                var replaceReturnType = isExitNode ? null : "Wrapper";
                try (var mtBl = code.open(Tools.methodSignature(method, null, replaceReturnType, false))) {
                    if (isExitNode) {
                        writeExitMethodWrapper(method, mtBl);
                    } else {
                        writeWrapperMethodBody(method, mtBl);
                    }
                }
            }
        }
    }

    private void writeWrapperMethodBody(Method method, JavaSourceBuilder mtBl) {
        if (fluent.getCloner() != null) {
            mtBl.statement("var next = new Wrapper(that.%s)", Tools.methodCall(fluent.getCloner()))
                .statement("next.%s", Tools.methodCall(method))
                .returnStatement("next");

        } else {
            mtBl.statement("that.%s", Tools.methodCall(method))
                .returnStatement("this");
        }
    }

    private void writeExitMethodWrapper(Method method, JavaSourceBuilder mtBl) {
        if (method.getReturnType() == Void.class) {
            mtBl.statement("that.%s", Tools.methodCall(method));
        } else {
            mtBl.statement("return that.%s", Tools.methodCall(method));
        }
    }

    private String build(Node node, String nextInterface) {
        if (node instanceof Terminal) {
            return build((Terminal) node, nextInterface);
        } else {
            return build((Tree) node, nextInterface);
        }
    }

    private String build(Terminal terminal, String nextInterface) {
        interfaceName = ifNameFactory.getNewName();
        var code = new JavaSourceBuilder();
        var extendsList = (terminal.getModifier() & (Node.OPTIONAL | Node.ZERO_OR_MORE)) != 0 ?
            " extends " + nextInterface : "";
        try (var ifcB = code.open("interface %s %s ", interfaceName, extendsList)) {
            ifcB.statement(Tools.methodSignature(methods.get(terminal.getMethod()), null,
                (terminal.getModifier() & Node.ZERO_OR_MORE) != 0 ? interfaceName : nextInterface, true));
        }
        return code.toString();
    }

    private String build(Tree tree, String nextInterface) {
        int modifier = tree.getModifier();
        if (modifier == Node.ONCE) {
            return buildOnce(tree, nextInterface);
        }
        if (modifier == Node.OPTIONAL) {
            return buildOptional(tree, nextInterface);
        }
        if (modifier == Node.ZERO_OR_MORE) {
            return buildZeroOrMore(tree, nextInterface);
        }
        if (modifier == Node.ONE_OF) {
            return buildOneOf(tree, nextInterface);
        }
        if (modifier == Node.ONE_TERMINAL_OF) {
            return buildOneTerminalOf(tree, nextInterface);
        }
        throw new GeciException("Internal error tree " + tree.toString() + " modifier is " + modifier);
    }

    private String buildOneTerminalOf(Tree tree, String nextInterface) {
        this.interfaceName = ifNameFactory.getNewName();
        var code = new JavaSourceBuilder();
        try (
            var ifcB = code.open("interface %s", this.interfaceName)) {
            List<Node> list = tree.getList();
            for (var node : list) {
                if (node instanceof Tree) {
                    throw new GeciException("Internal error, ON_TERMINAL_OF contains a non-terminal sub.");
                } else {
                    var terminal = (Terminal) node;
                    ifcB.statement(Tools.methodSignature(methods.get(terminal.getMethod()), null, nextInterface, true));
                }
            }
        }
        return code.toString();
    }

    private String buildOneOf(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSourceBuilder();
        var alternativeInterfaces = new HashSet<String>();
        for (var node : list) {
            var builder = new ClassBuilder(this);
            code.write(builder.build(node, nextInterface));
            alternativeInterfaces.add(builder.interfaceName);
        }
        this.interfaceName = ifNameFactory.getNewName();
        try (var ifcB = code.open("interface %s extends %s", this.interfaceName, setJoin(alternativeInterfaces))) {
        }
        return code.toString();
    }

    private String setJoin(Set<String> set) {
        return String.join(",", set);
    }

    private String buildZeroOrMore(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSourceBuilder();
        ClassBuilder lastBuilder = null;
        this.interfaceName = ifNameFactory.getNewName();
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(this);
            var actualNextInterface = this.interfaceName;
            if (lastBuilder != null) {
                actualNextInterface = lastBuilder.interfaceName;
            }
            code.write(builder.build(node, actualNextInterface));
            lastBuilder = builder;
        }
        code.statement("interface %s extends %s,%s {}", this.interfaceName, nextInterface, lastBuilder.interfaceName);
        return code.toString();
    }

    private String buildOptional(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSourceBuilder();
        ClassBuilder lastBuilder = null;
        this.interfaceName = ifNameFactory.getNewName();
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(this);
            var actualNextInterface = nextInterface;
            if (lastBuilder != null) {
                actualNextInterface = lastBuilder.interfaceName;
            }
            code.write(builder.build(node, actualNextInterface));
            lastBuilder = builder;
        }
        code.statement("interface %s extends %s,%s {}", this.interfaceName, nextInterface, lastBuilder.interfaceName);
        return code.toString();
    }

    private String buildOnce(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSourceBuilder();
        ClassBuilder lastBuilder = null;
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(this);
            var actualNextInterface = nextInterface;
            if (lastBuilder != null) {
                actualNextInterface = lastBuilder.interfaceName;
            }
            code.write(builder.build(node, actualNextInterface));
            lastBuilder = builder;
        }
        this.interfaceName = lastBuilder.interfaceName;
        return code.toString();
    }
}
