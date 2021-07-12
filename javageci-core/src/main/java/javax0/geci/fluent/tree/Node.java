package javax0.geci.fluent.tree;

import java.util.Map;

/**
 * A node that describes one element in the syntax tree that describes a fluent API. A node has a modifier, like
 * {@code ONCE}, {@code OPTIONAL} that denotes that the node represents a structure that has to be called in the
 * final fluent interface exactly once or optionally. There are five different modifiers all together.
 * <p>
 * A node also has a name that can be set, reset and queried that it was set or not.
 * <p>
 * {@code Node} is abstract because the implementations can be either {@link Tree} or {@link Terminal} representing
 * a sub-tree in the syntax tree or a terminal leaf in the syntax tree.
 */
public abstract class Node implements Comparable<Node> {
    public static final int ONCE = 0x00000001;
    public static final int OPTIONAL = 0x00000002;
    public static final int ZERO_OR_MORE = 0x00000004;
    public static final int ONE_OF = 0x00000008;
    public static final int ONE_TERMINAL_OF = 0x00000010;
    private static final Map<Integer, String> stringMap =
        Map.of(ONCE, "",
            OPTIONAL, "?",
            ZERO_OR_MORE, "*",
            ONE_OF, "{OR}",
            ONE_TERMINAL_OF, "{OR}");
    private final int modifier;
    private String name = null;

    Node(int modifier) {
        this.modifier = modifier;
    }

    public abstract Node clone(int modifier);

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModifier() {
        return modifier;
    }

    @Override
    public String toString() {
        return stringMap.get(modifier);
    }
}
