package javax0.geci.fluent.internal;

import javax0.geci.fluent.tree.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Create new interface names and remember the names that were created.
 */
public class InterfaceNameFactory {
    private final Set<String> allInterfaces = new HashSet<>();
    private int counter = 1000;
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
     * @param node for which the interface name is created. In case the node has a name then the name of the node is used
     *             as interface name.
     * @return the new interface name.
     */
    public String getNewName(Node node) {
        if (node.hasName()) {
            lastInterface = node.getName();
        } else {
            final var sb = new StringBuilder();
            int z = counter;
            counter += 119;
            while( z > 0 ){
                sb.append("aeou".substring(z&3,(z&3)+1));
                z = z >> 2;
                if( z > 0 ){
                    sb.append("bcdfghjklmnpqrst".substring(z&7,(z&7)+1));
                    z = z >> 4;
                }
            }
            sb.setCharAt(0,Character.toUpperCase(sb.charAt(0)));
            lastInterface = sb.toString();
        }
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
