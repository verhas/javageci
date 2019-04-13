package javax0.geci.fluent.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A set of interface names. This class is used to maintain the names of the interfaces that we generate during
 * fluent API generation.
 */
public class InterfaceSet {
    /**
     * Name of the wrapper interface.
     */
    static final String WRAPPER_INTERFACE_NAME = "WrapperInterface";
    private final Set<String> interfaceSet = new HashSet<>();
    private boolean lastWhen; // see {@link #when(boolean)}

    private InterfaceSet(boolean needsWrapperInterface) {
        if (needsWrapperInterface) {
            interfaceSet.add(WRAPPER_INTERFACE_NAME);
        }
    }

    /**
     * Create a new interface set for the method collection.
     *
     * @param methods is the method collection for which we create the interface.
     * @return the new "set" that can be used to collect the interface names
     */
    public static InterfaceSet builderFor(MethodCollection methods) {
        return new InterfaceSet(methods.needWrapperInterface());
    }

    /**
     * Add all interface names to the set.
     *
     * @param interfaces the names of the interfaces to be added
     * @return {@code this}
     */
    public InterfaceSet set(Set<String> interfaces) {
        this.interfaceSet.addAll(interfaces);
        return this;
    }

    /**
     * Add all interface names to the set.
     *
     * @param interfaces the names of the interfaces to be added
     * @return {@code this}
     */
    public InterfaceSet set(String... interfaces) {
        this.interfaceSet.addAll(Arrays.stream(interfaces).filter(Objects::nonNull).collect(Collectors.toSet()));
        return this;
    }

    /**
     * A fluent method that should be used together with the method {@link #then(String...)}. Caller can add
     * interafce names conditionally to the set using the format
     *
     * <pre>
     *     ...when(condition).then(if1,if2,...)
     * </pre>
     * <p>
     * {@link #then(String...)} adds the interface names only when the last {@code #when(boolean)} argument was true.
     *
     * @param flag the flag to control the execution of the next {@link #then(String...)}.
     * @return {@code this}
     */
    public InterfaceSet when(boolean flag) {
        lastWhen = flag;
        return this;
    }

    /**
     * Adds the names of the interfaces to the set if the last flag passed to {@link #when(boolean)} was true.
     *
     * @param interfaces the names of the interfaces to be added
     * @return {@code this}
     */
    public InterfaceSet then(String... interfaces) {
        if (lastWhen) {
            set(interfaces);
        }
        return this;
    }

    /**
     * @return the list of the interfaces comma delimited with the prefix {@code " extends "}. This can directly
     * used in code generation.
     */
    public String buildList() {
        if (interfaceSet.isEmpty()) {
            return "";
        } else {
            return " extends " + String.join(",", interfaceSet);
        }
    }
}
