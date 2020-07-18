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
     * <p>Calculate a new interface name.</p>
     *
     * <p>The name calculation converts a serial number to characters. The serial number starts at 119, just to give
     * a kind of random. This is because the counter runs from zero and that way the names start kind of "boring"
     * not really variable.</p>
     *
     * <p>A number is converted to a name alternating consonants and vowels. The algorithm uses four consonants and 16
     * vowels.</p>
     *
     * <p>The vowels:</p>
     *
     * <ul>
     *     <li>a is used</li>
     *     <li>e is used</li>
     *     <li>i is not used, because it may be confusing when pronounced in some languages instead of 'e'</li>
     *     <li>o is used</li>
     *     <li>u is used</li>
     * </ul>
     *
     * <p>Consonants:</p>
     *
     * <ul>
     *     <li>b is used</li>
     *     <li>c is used</li>
     *     <li>d is used</li>
     *     <li>f is used</li>
     *     <li>g is used</li>
     *     <li>h is used</li>
     *     <li>j is used</li>
     *     <li>k is used</li>
     *     <li>l is used</li>
     *     <li>m is used</li>
     *     <li>n is used</li>
     *     <li>p is not used because it may be mixed up with 'b' when pronounced</li>
     *     <li>q is not used, because the pronunciation may be problematic, and in most
     *     of the languages where it is used it is supposed to be followed an 'u'.</li>
     *     <li>r is used</li>
     *     <li>s is used</li>
     *     <li>t is used</li>
     *     <li>v is not used because it may be mixed with 'b' for some languages.</li>
     *     <li>w is used</li>
     *     <li>x is not used because pronunciation may be difficult in some languages.</li>
     *     <li>y is not used because different languages pronounce it differently</li>
     *     <li>z is used</li>
     * </ul>
     *
     * @param node for which the interface name is created. In case the node has a name then the name of the node is used
     *             as interface name.
     * @return the new interface name calculated from the counter numeric value and the first character upper cased.
     */
    public String getNewName(Node node) {
        if (node.hasName()) {
            lastInterface = node.getName();
        } else {
            final var sb = new StringBuilder();
            int z = counter;
            counter += 119;
            while( z > 0 ){
                sb.append("aeou", z&3, (z&3)+1);
                z = z >> 2;
                if( z > 0 ){
                    sb.append("bcdfghjklmnrstwz", z&7, (z&7)+1);
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
