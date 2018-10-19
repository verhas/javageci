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
    private final InterfaceNameProvider interfaceNameProvider;
    private final MethodCollection methods;
    private final FluentBuilderImpl fluent;
    private final Set<String> allInterfaces;
    private String interfaceName;

    /**
     * Create a ClassBuilder that builds the interface and class structure from the fluent definition.
     *
     * @param fluent the fluent definition.
     */
    public ClassBuilder(FluentBuilderImpl fluent) {
        this.interfaceNameProvider = new InterfaceNameProvider();
        this.fluent = fluent;
        this.methods = fluent.getMethods();
        this.allInterfaces = new HashSet<>();
    }

    /**
     * Create a new ClassBuilder that is essentially the clone of the other one.
     *
     * @param that the other class builder
     */
    private ClassBuilder(ClassBuilder that) {
        this.interfaceNameProvider = that.interfaceNameProvider;
        this.methods = that.methods;
        this.fluent = that.fluent;
        this.allInterfaces = that.allInterfaces;
    }

    /**
     * Create a new interface name and add it to the set of the interfaces.
     *
     * @return the name of the new interface
     */
    private String newInterfaceName() {
        var newInterfaceName = interfaceNameProvider.getNewInterfaceName();
        allInterfaces.add(newInterfaceName);
        return newInterfaceName;
    }

    /**
     * Get the return type of the last node in the fluent tree. The methods that are invoked inside the
     * chaned calls all return the same object (or a clone), except those that can be invoked last. There are usually
     * one of those in the typical fluent api, but there can be more. They cannot be present in the middle of the
     * call chain because they return usually the composed result that the whole chain was aiming to get.
     * <p>
     * This method seeks the last method of the structure and returns the type of that method. In case there are more
     * terminal methods then it also checks that they all have the same type.
     *
     * @param lastNode the last node of the node list of the fluent API.
     * @return the type of the last method/node
     */
    private String getLastNodeReturnType(Node lastNode) {
        if (lastNode instanceof Terminal) {
            return getLastNodeReturnType((Terminal) lastNode);
        } else {
            return getLastNodeReturnType((Tree) lastNode);
        }
    }

    /**
     * Get the return type of the last method in case that is a terminal type (simple method call and not a complex
     * fluent structure itself).
     *
     * @param lastNode the last node in the fluent structure
     * @return the type of the last method/node
     */
    private String getLastNodeReturnType(Terminal lastNode) {
        if (lastNode.getModifier() == Node.ONCE) {
            methods.exitNode(lastNode.getMethod());
            return methods.get(lastNode.getMethod()).getGenericReturnType().getTypeName();
        }
        if (lastNode.getModifier() == Node.ONE_OF) {
            throw new GeciException("Inconsistent fluent tree. The last node is ONE_OF terminal.");
        }
        if (lastNode.getModifier() == Node.ZERO_OR_MORE) {
            throw new GeciException("The last call can not be zeroOrMore(\""
                + lastNode.getMethod()
                + "\") in the fluent structure.");
        }
        if (lastNode.getModifier() == Node.OPTIONAL) {
            throw new GeciException("The last call can not be optional(\""
                + lastNode.getMethod()
                + "\") in the fluent structure.");
        }
        throw new GeciException("Inconsistent fluent tree, last method modifier is " + lastNode.getModifier());
    }

    /**
     * Get the return type of the last node/method in case the last node is a fluent structure.
     * In this case recursive calls may be needed to find the really last methods.
     *
     * @param lastNode the last node in the fluent structure
     * @return the type of the last method/node
     */
    private String getLastNodeReturnType(Tree lastNode) {
        if (lastNode.getModifier() == Node.ONCE) {
            var list = lastNode.getList();
            return getLastNodeReturnType(list.get(list.size() - 1));
        }
        if (lastNode.getModifier() == Node.ONE_OF || lastNode.getModifier() == Node.ONE_TERMINAL_OF) {
            var returnTypes = new HashSet<String>();
            var returnType = "";
            var list = lastNode.getList();
            for (var node : list) {
                returnType = getLastNodeReturnType(node);
                returnTypes.add(returnType);
            }
            if (returnTypes.size() != 1) {
                if (returnTypes.size() == 0) {
                    throw new GeciException("The structure has no return type");
                }
                throw new GeciException("The structure has several return types:\n" +
                    String.join("\n", returnTypes));
            }
            return returnType;
        }
        if (lastNode.getModifier() == Node.ZERO_OR_MORE) {
            throw new GeciException("The last call can not be zeroOrMore(substructure) in the fluent structure.");
        }
        if (lastNode.getModifier() == Node.OPTIONAL) {
            throw new GeciException("The last call can not be optional(substructure) in the fluent structure.");
        }
        throw new GeciException("Inconsistent fluent tree, last node structure modifier is " + lastNode.getModifier());
    }

    /**
     * Build the interface and class structure that implements the fluent interface.
     *
     * @return the string of the code that was built up.
     */
    public String build() {
        var list = fluent.getNodes();
        var tree = new Tree(Node.ONCE, list);
        var lastInterface = Tools.normalizeTypeName(getLastNodeReturnType(list.get(list.size() - 1)));
        var interfaces = build(tree, lastInterface);
        var code = new JavaSourceBuilder();
        writeStartMethod(code);
        writeWrapperClass(code);
        code.write(interfaces);
        return code.toString();
    }

    private void writeStartMethod(JavaSourceBuilder code) {
        var startMethod = fluent.getStartMethod() == null ? "start" : fluent.getStartMethod();
        try (var mtBl = code.open("public static %s %s()", interfaceNameProvider.getLastInterfaceName(), startMethod)) {
            mtBl.statement("return new Wrapper()");
        }
    }

    private void writeWrapperClass(JavaSourceBuilder code) {
        try (var klBl = code.open("public static class Wrapper implements %s", String.join(",", allInterfaces))) {
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
                        writeMethodWrapper(method, mtBl);
                    }
                }
            }
        }
    }

    private void writeMethodWrapper(Method method, JavaSourceBuilder mtBl) {
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
        interfaceName = newInterfaceName();
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
        this.interfaceName = newInterfaceName();
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
        this.interfaceName = newInterfaceName();
        try (var ifcB = code.open("interface %s extends %s", this.interfaceName
            , String.join(",", alternativeInterfaces))) {
        }
        return code.toString();
    }

    private String buildZeroOrMore(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSourceBuilder();
        ClassBuilder lastBuilder = null;
        this.interfaceName = newInterfaceName();
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
        this.interfaceName = newInterfaceName();
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
