package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
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
    private final FluentBuilder fluent;
    private String interfaceName;
    private Set<String> extendedInterfaces = Set.of();

    public ClassBuilder(FluentBuilder fluent) {
        this.classNameProvider = new ClassNameProvider();
        this.fluent = fluent;
        this.methods = fluent.getMethods();
    }

    private ClassBuilder(ClassNameProvider classNameProvider, MethodCollection methods, FluentBuilder fluent) {
        this.classNameProvider = classNameProvider;
        this.methods = methods;
        this.fluent = fluent;
    }

    private String getNodeReturnType(Node lastNode) {
        if (lastNode instanceof Terminal) {
            return getNodeReturnType((Terminal) lastNode);
        } else {
            return getNodeReturnType((Tree) lastNode);
        }
    }

    private String getNodeReturnType(Terminal lastNode) {
        if (lastNode.getModifier() == Node.ONCE) {
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

    private String getNodeReturnType(Tree lastNode) {
        if (lastNode.getModifier() == Node.ONCE) {
            var list = lastNode.getList();
            return getNodeReturnType(list.get(list.size() - 1));
        }
        if (lastNode.getModifier() == Node.ONE_OF) {
            var returnTypes = new HashSet<String>();
            var returnType = "";
            var list = lastNode.getList();
            for (var node : list) {
                returnType = getNodeReturnType(node);
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

    public String build() {
        var list = fluent.getNodes();
        var tree = new Tree(Node.ONCE, list);
        var lastInterface = getNodeReturnType(list.get(list.size() - 1));
        return build(tree, lastInterface, Set.of());
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
            var actualExtendedInterfaces = new HashSet<>(extendedInterfaces);
            actualExtendedInterfaces.add(nextInterface);
            code.append(" extends ").append(String.join(",", actualExtendedInterfaces));
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
        throw new GeciException("Internal error tree " + tree.toString() + " modifier is " + modifier);
    }

    private String buildOneOf(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        List<Node> list = tree.getList();
        var code = new StringBuilder();
        var alternativeInterfaces = new HashSet<String>();
        for (var node : list) {
            var builder = new ClassBuilder(classNameProvider, methods, fluent);
            code.append(builder.build(node, nextInterface, extendedInterfaces));
            alternativeInterfaces.add(builder.interfaceName);
        }
        this.interfaceName = classNameProvider.getNewClassName();
        code.append("interface ")
                .append(this.interfaceName)
                .append(" extends ")
                .append(String.join(",", alternativeInterfaces))
                .append("{}\n");
        return code.toString();
    }

    private String buildZeroOrMore(Tree tree, String nextInterface, Set<String> extendedInterfaces) {
        List<Node> list = tree.getList();
        var code = new StringBuilder();
        ClassBuilder lastBuilder = null;
        this.interfaceName = classNameProvider.getNewClassName();
        for (var i = list.size() - 1; i >= 0; i--) {
            final var node = list.get(i);
            var builder = new ClassBuilder(classNameProvider, methods, fluent);
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
                if (lastBuilder != null) {
                    actualExtendedInterfaces.add(lastBuilder.interfaceName);
                } else {
                    actualExtendedInterfaces.add(nextInterface);
                }
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
            var builder = new ClassBuilder(classNameProvider, methods, fluent);
            var actualExtendedInterfaces = extendedInterfaces;
            var actualNextInterface = nextInterface;
            if (lastBuilder != null) {
                actualExtendedInterfaces = lastBuilder.extendedInterfaces;
                this.extendedInterfaces = actualExtendedInterfaces;
                actualNextInterface = lastBuilder.interfaceName;
            }
            code.append(builder.build(node, actualNextInterface, actualExtendedInterfaces));
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
            var builder = new ClassBuilder(classNameProvider, methods, fluent);
            var actualExtendedInterfaces = extendedInterfaces;
            if (lastBuilder != null) {
                actualExtendedInterfaces = lastBuilder.extendedInterfaces;
                this.extendedInterfaces = actualExtendedInterfaces;
            }
            if (i == 0) {
                actualExtendedInterfaces = new HashSet<>(actualExtendedInterfaces);
                if (lastBuilder != null) {
                    actualExtendedInterfaces.add(lastBuilder.interfaceName);
                } else {
                    actualExtendedInterfaces.add(nextInterface);
                }
            }

            code.append(builder.build(node, nextInterface, actualExtendedInterfaces));
            lastBuilder = builder;
        }
        this.interfaceName = lastBuilder.interfaceName;
        return code.toString();
    }
}
