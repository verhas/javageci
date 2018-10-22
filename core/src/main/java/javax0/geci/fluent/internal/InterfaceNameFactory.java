package javax0.geci.fluent.internal;

import java.util.HashSet;
import java.util.Set;

/**
 * Create new interface names and remember the names that were created.
 */
public class InterfaceNameFactory {
    private final Set<String> allInterfaces = new HashSet<>();
    private int counter = 0;
    private String lastInterface = null;

    /**
     * Get the names of all the interface names that were created.
     *
     * @return the set of the names
     */
    public Set<String> getAllNames() {
        return allInterfaces;
    }

    /**
     * Calculate a new interface name.
     *
     * @return the new interface name.
     */
    public String getNewName() {
        lastInterface = "If" + counter++;
        allInterfaces.add(lastInterface);
        return lastInterface;
    }

    /**
     * Get the name of the interface that was last calculated.
     *
     * @return the last interface name that was created.
     */
    public String getLastName() {
        return lastInterface;
    }
}
