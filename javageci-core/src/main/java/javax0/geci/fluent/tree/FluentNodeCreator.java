package javax0.geci.fluent.tree;

import javax0.geci.fluent.FluentBuilder;

import java.util.List;

public interface FluentNodeCreator {

    /**
     * The method may be called zero or one time in the fluent API at the defined point.
     *
     * @param method the name or the prototype/signature of the method. For more information see the note in the documentation
     *               of the class {@link FluentBuilder}
     * @return the newly created Node for the sub-tree
     */
    Node optionalNode(String method);

    /**
     * The method may be called zero or more time in the fluent API at the defined point.
     *
     * @param method the name or the prototype/signature of the method. For more information see the note in the documentation
     *               of the class {@link FluentBuilder}
     * @return the newly created Node for the sub-tree
     */
    Node zeroOrMoreNode(String method);

    /**
     * The fluent API using code may call one of the methods at this point.
     *
     * @param methods the names of the methods. For more information see the note in the documentation
     *                of the class {@link FluentBuilder}
     * @return the newly created Node for the sub-tree
     */
    Node oneOfNode(String... methods);

    /**
     * The fluent API using code may call one of the sub structures at this point.
     *
     * @param subs the sub structures from which one may be selected by the caller
     * @return the newly created Node for the sub-tree
     */
    Node oneOfNode(List<Node> subs);

    /**
     * The method can be called exactly once at the point.
     *
     * @param method the name or the prototype/signature of the method. For more information see the note in the documentation
     *               of the class {@link FluentBuilder}
     * @return the newly created Node for the sub-tree
     */
    Node oneNode(String method);

    /**
     * The sub structure can be called exactly once at the point.
     *
     * @param sub substructures
     * @return the newly created Node for the sub-tree
     */
    Node oneNode(List<Node> sub);

}
