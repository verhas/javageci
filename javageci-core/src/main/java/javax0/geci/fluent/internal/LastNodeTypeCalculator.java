package javax0.geci.fluent.internal;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.tree.Node;
import javax0.geci.fluent.tree.Terminal;
import javax0.geci.fluent.tree.Tree;

import java.util.HashSet;

public class LastNodeTypeCalculator {
    private final MethodCollection methods;

    public LastNodeTypeCalculator(MethodCollection methods) {
        this.methods = methods;
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
    String getLastNodeReturnType(Node lastNode) {
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
}
