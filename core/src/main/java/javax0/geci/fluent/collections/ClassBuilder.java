package javax0.geci.fluent.collections;

import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;
import javax0.geci.tools.Tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassBuilder {
    private final ClassNameProvider classNameProvider;
    private final MethodCollection methods;

    private String interfaceName;
    private Set<String> extendedInterfaces;

    public ClassBuilder(ClassNameProvider classNameProvider, MethodCollection methods) {
        this.classNameProvider = classNameProvider;
        this.methods = methods;
    }

    public String build(Tree tree) {
        // TODO add starting and ending to the fluent builder and then here and code this
        return null;
    }

    private String build(Node node, String nextInterface, Set<String> extendedInterfaces) {
        if (node instanceof Terminal) {
            return build((Terminal) node, nextInterface, extendedInterfaces);
        } else {
            return build((Tree) node, nextInterface, extendedInterfaces);
        }
    }

    private String build(Terminal terminal, String nextInterface, Set<String> extendedInterfaces) {
        this.extendedInterfaces = extendedInterfaces;
        interfaceName = classNameProvider.getNewClassName();
        var code = new StringBuilder();
        code.append("interface ").append(interfaceName);
        if ((terminal.getModifier() & (Node.OPTIONAL | Node.ZERO_OR_MORE)) != 0) {
            code.append(" extends ").append(extendedInterfaces);
        }
        code.append("{\n  ");
        code.append(Tools.methodSignature(methods.get(terminal.getMethod()), null,
            (terminal.getModifier() & Node.ZERO_OR_MORE) != 0 ? interfaceName : nextInterface));
        code.append(";\n");
        code.append("}\n");
        return code.toString();
    }

    private String build(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        int modifier = tree.getModifier();
        if (modifier == Node.ONCE) {
            return buildOnce(tree, nextInterface, extendedInterfaces);
        }
        if (modifier == Node.OPTIONAL) {
            return buildOptional(tree, nextInterface, extendedInterfaces);
        }
        if (modifier == Node.ZERO_OR_MORE) {
            return buildZeroOrMore(tree, nextInterface, extendedInterfaces);
        }
        if (modifier == Node.ONE_OF) {
            return buildOneOf(tree, nextInterface, extendedInterfaces);
        }
        return null;
    }

    private String buildOneOf(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        List<Node> list = tree.getList();
        var code = new StringBuilder();
        var alternativeInterfaces = new HashSet<String>();
        for (var node : list) {
            var builder = new ClassBuilder(classNameProvider, methods);
            code.append(builder.build(node, nextInterface, extendedInterfaces));
            alternativeInterfaces.add(builder.interfaceName);
        }
        this.interfaceName = classNameProvider.getNewClassName();
        code.append("interface ")
            .append(this.interfaceName)
            .append(" extends ")
            .append(String.join(",", alternativeInterfaces))
            .append("{}");
        return code.toString();
    }

    private String buildZeroOrMore(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        List<Node> list = tree.getList();
        var code = new StringBuilder();
        ClassBuilder lastBuilder = null;
        this.interfaceName = classNameProvider.getNewClassName();
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(classNameProvider, methods);
            var actualExtendedInterfaces = extendedInterfaces;
            var actualNextInterface = nextInterface;
            if (lastBuilder != null) {
                actualExtendedInterfaces = lastBuilder.extendedInterfaces;
            } else {
                this.extendedInterfaces = actualExtendedInterfaces;
                actualNextInterface = this.interfaceName;
            }
            if (i == 0) {
                actualExtendedInterfaces = new HashSet<>(actualExtendedInterfaces);
                actualExtendedInterfaces.add(lastBuilder.interfaceName);
            }

            code.append(builder.build(node, actualNextInterface, actualExtendedInterfaces));
            lastBuilder = builder;
        }
        code.append("interface ")
            .append(this.interfaceName)
            .append(" extends ")
            .append(lastBuilder.interfaceName)
            .append("{}");
        return code.toString();
    }

    private String buildOnce(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        List<Node> list = tree.getList();
        var code = new StringBuilder();
        ClassBuilder lastBuilder = null;
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(classNameProvider, methods);
            var actualExtendedInterfaces = extendedInterfaces;
            if (lastBuilder != null) {
                actualExtendedInterfaces = lastBuilder.extendedInterfaces;
                this.extendedInterfaces = actualExtendedInterfaces;
            }
            code.append(builder.build(node, nextInterface, actualExtendedInterfaces));
            lastBuilder = builder;
        }
        this.interfaceName = lastBuilder.interfaceName;
        return code.toString();
    }

    private String buildOptional(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        List<Node> list = tree.getList();
        var code = new StringBuilder();
        ClassBuilder lastBuilder = null;
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(classNameProvider, methods);
            var actualExtendedInterfaces = extendedInterfaces;
            if (lastBuilder != null) {
                actualExtendedInterfaces = lastBuilder.extendedInterfaces;
                this.extendedInterfaces = actualExtendedInterfaces;
            }
            if (i == 0) {
                actualExtendedInterfaces = new HashSet<>(actualExtendedInterfaces);
                actualExtendedInterfaces.add(lastBuilder.interfaceName);
            }

            code.append(builder.build(node, nextInterface, actualExtendedInterfaces));
            lastBuilder = builder;
        }
        this.interfaceName = lastBuilder.interfaceName;
        return code.toString();
    }
}
