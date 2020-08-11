package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.JavaSource;
import javax0.geci.tools.MethodTool;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassBuilder {
    private final InterfaceNameFactory ifNameFactory;
    private final MethodCollection methods;
    private final FluentBuilderImpl fluent;
    private String interfaceName;
    private static final Logger LOG = LoggerFactory.getLogger();

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
     * @throws Exception when there is an error in the grammar
     */
    public String build() throws Exception {
        LOG.debug("Class building started for the class %s", fluent.getKlass().getSimpleName());
        var list = fluent.getNodes();
        if (list.size() == 0) {
            throw new GeciException("There are no actual calls in the fluent structure.");
        }
        LOG.debug("There are %d nodes on the top level", list.size());
        final var tree = new Tree(Node.ONCE, list);
        final var exitType = NodeTypeCalculator.from(methods).getReturnType(getLastNode(list));
        final var lastInterface = GeciReflectionTools.normalizeTypeName(exitType);
        LOG.debug("The last type is %s", lastInterface);
        final var interfaces = build(tree, lastInterface);
        final var code = JavaSource.builder();
        writeStartMethod(code);
        writeWrapperInterface(code);
        writeWrapperClass(code);
        code.write(interfaces);
        return code.toString();
    }

    /**
     * Get the last node of the list of nodes.
     *
     * @param list a non-empty list of nodes
     * @return the last node
     */
    private Node getLastNode(List<Node> list) {
        return list.get(list.size() - 1);
    }


    /**
     * Write the source code of the start method into the source builder.
     * <p>
     * The start method is a public static method with no argument that creates a new instance of the wrapper
     * class and returns it as the interface type that can be used to start the fluent API structure.
     *
     * @param code to write the start method into
     * @throws Exception never, signature inherited from {@code AutoCloseable}
     */
    private void writeStartMethod(JavaSource.Builder code) throws Exception {
        final var startMethod = fluent.getStartMethod() == null ? "start" : fluent.getStartMethod();
        LOG.debug("Creating start method %s()", startMethod);
        final String lastType;
        if (fluent.getLastType() != null) {
            lastType = fluent.getLastType();
            code.write("public interface %s extends %s {}", lastType, this.interfaceName);
        } else {
            lastType = ifNameFactory.getLastName();
        }
        try (final var mtBl = code.method(startMethod).modifiers("public static").returnType(lastType).noArgs()) {
            mtBl.returnStatement("new Wrapper()");
        }
    }

    private void writeWrapperInterface(JavaSource.Builder code) throws Exception {
        if (methods.needWrapperInterface()) {
            try (var ignored = code.open("public interface WrapperInterface")) {
            }
        }
    }

    /**
     * Write the source code of the wrapper class into the source builder.
     *
     * @param code to write the start method into
     */
    private void writeWrapperClass(JavaSource.Builder code) throws Exception {
        try (var klBl = code.open("public static class Wrapper implements %s",
            setJoin(ifNameFactory.getAllNames(), fluent.getLastType(), fluent.getInterfaces()))) {
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


    private void writeWrapperMethods(JavaSource.Builder code) throws Exception {
        for (var signature : methods.methodSignatures()) {
            var method = methods.get(signature);
            if (fluent.getCloner() == null || !fluent.getCloner().equals(method)) {
                var notFluent = methods.isExitNode(signature) || !methods.isFluentNode(signature);
                var actualReturnType = notFluent ? null : "Wrapper";
                var signatureString = FluentMethodTool
                    .from(fluent.getKlass())
                    .asPublic()
                    .forThe(method)
                    .withType(actualReturnType)
                    .signature();
                try (var methodBody = (JavaSource.MethodBody) code.open(signatureString)) {
                    if (notFluent) {
                        writeNonFluentMethodWrapper(method, methodBody);
                    } else {
                        writeWrapperMethodBody(method, methodBody);
                    }
                }
            }
        }
    }

    private void writeWrapperMethodBody(Method method, JavaSource.MethodBody mtBl) {
        var callString = FluentMethodTool.from(fluent.getKlass()).forThe(method).call();
        if (fluent.getCloner() != null) {
            mtBl.statement("var next = new Wrapper(that.%s)", MethodTool.with(fluent.getCloner()).call())
                .statement("next.that.%s", callString)
                .returnStatement("next");

        } else {
            mtBl.statement("that.%s", callString)
                .returnStatement("this");
        }
    }

    private void writeNonFluentMethodWrapper(Method method, JavaSource.MethodBody mtBl) {
        var callString = FluentMethodTool.from(fluent.getKlass()).forThe(method).call();
        if (method.getReturnType() == Void.TYPE) {
            mtBl.statement("that.%s", callString);
        } else {
            mtBl.returnStatement("that.%s", callString);
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
        interfaceName = ifNameFactory.getNewName(terminal);
        var code = new JavaSource();
        var list = InterfaceSet.builderFor(methods)
            .when((terminal.getModifier() & (Node.OPTIONAL | Node.ZERO_OR_MORE)) != 0).then(nextInterface, fluent.getInterfaces())
            .buildList();
        try (var ifcB = code.open("public interface %s%s ", interfaceName, list)) {
            ifcB.statement(FluentMethodTool
                .from(fluent.getKlass())
                .forThe(methods.get(terminal.getMethod()))
                .withType((terminal.getModifier() & Node.ZERO_OR_MORE) != 0 ? interfaceName : nextInterface)
                .asInterface()
                .signature());
        }
        return code.toString();
    }

    private String build(Tree tree, String nextInterface) {
        int modifier = tree.getModifier();
        switch (modifier) {
            case Node.ONCE:
                return buildOnce(tree, nextInterface);
            case Node.OPTIONAL:
                return buildOptional(tree, nextInterface);
            case Node.ZERO_OR_MORE:
                return buildZeroOrMore(tree, nextInterface);
            case Node.ONE_OF:
                return buildOneOf(tree, nextInterface);
            case Node.ONE_TERMINAL_OF:
                return buildOneTerminalOf(tree, nextInterface);
            default:
                throw new GeciException("Internal error tree " + tree.toString() + " modifier is " + modifier);
        }
    }

    private String buildOneTerminalOf(Tree tree, String nextInterface) {
        this.interfaceName = ifNameFactory.getNewName(tree);
        var code = new JavaSource();
        try (
            final var ifcB = code.open("public interface %s", this.interfaceName)) {
            for (var node : tree.getList()) {
                if (node instanceof Tree) {
                    throw new GeciException("Internal error, ON_TERMINAL_OF contains a non-terminal sub.");
                } else {
                    var terminal = (Terminal) node;
                    ifcB.statement(FluentMethodTool
                        .from(fluent.getKlass())
                        .forThe(methods.get(terminal.getMethod()))
                        .withType(nextInterface)
                        .asInterface()
                        .signature());
                }
            }
        }
        return code.toString();
    }

    private String buildOneOf(Tree tree, String nextInterface) {
        final var list = tree.getList();
        final var code = new JavaSource();
        final var alternativeInterfaces = new HashSet<String>();
        for (var node : list) {
            var builder = new ClassBuilder(this);
            code.write(builder.build(node, nextInterface));
            alternativeInterfaces.add(builder.interfaceName);
        }
        this.interfaceName = ifNameFactory.getNewName(tree);
        final var ifs = InterfaceSet.builderFor(methods)
            .set(fluent.getInterfaces())
            .set(alternativeInterfaces)
            .buildList();
        try (final var ignored = code.open("public interface %s%s", this.interfaceName, ifs)) {
        }
        return code.toString();
    }

    private String setJoin(Set<String> set, String... other) {
        if (other != null && other.length > 0) {
            var local = new HashSet<>(set);
            local.addAll(Arrays.stream(other).filter(Objects::nonNull).collect(Collectors.toSet()));
            return String.join(",", local);
        } else {
            return String.join(",", set);
        }
    }

    private String buildZeroOrMore(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSource();
        this.interfaceName = ifNameFactory.getNewName(tree);
        ClassBuilder lastBuilder = buildNodeList(this.interfaceName, list, code);
        var ifs = InterfaceSet.builderFor(methods).set(nextInterface, lastBuilder.interfaceName, fluent.getInterfaces()).buildList();
        code.write("public interface %s%s {}", this.interfaceName, ifs);
        return code.toString();
    }

    private String buildOptional(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSource();
        this.interfaceName = ifNameFactory.getNewName(tree);
        ClassBuilder lastBuilder = buildNodeList(nextInterface, list, code);
        var ifs = InterfaceSet.builderFor(methods).set(nextInterface, lastBuilder.interfaceName, fluent.getInterfaces()).buildList();
        code.write("public interface %s%s {}", this.interfaceName, ifs);
        return code.toString();
    }

    private String buildOnce(Tree tree, String nextInterface) {
        List<Node> list = tree.getList();
        var code = new JavaSource();
        ClassBuilder lastBuilder = buildNodeList(nextInterface, list, code);
        this.interfaceName = lastBuilder.interfaceName;
        return code.toString();
    }

    /**
     * Used to build the internal nodes of other nodes.
     * <p>
     * For example: If you have (a|b)*, which is a {@code Node.ZERO_OR_MORE} Node,
     * containing a {@code NODE.ONE_OF} Node, we call {@code buildZeroOrMore()} to
     * build the parent node ({@code Node.ZERO_OR_MORE}, (a|b)*), which calls this method
     * to build the contained node ({@code Node.ONE_OF}, a|b).
     *
     * @param nextInterface the name of the next interface
     * @param list          the nodes contained by the parent node
     * @param code          the source code we write the generated interfaces into
     * @return the last builder clone
     */
    private ClassBuilder buildNodeList(String nextInterface, List<Node> list, JavaSource code) {
        String actualNextInterface = nextInterface;
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(this);
            code.write(builder.build(node, actualNextInterface));
            if (i == 0) {
                return builder;
            }
            actualNextInterface = builder.interfaceName;
        }
        throw new GeciException("Internal error");
    }
}
