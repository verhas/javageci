package javax0.geci.api;

import java.util.function.Supplier;

/**
 * <p>The context is a global object available during the run-time of the
 * generators that are executed under the same Geci engine instance.
 * Context makes it possible to share information between different
 * generators.</p>
 *
 * <p>A context is something like a {@code Map<Object,Object>} object.
 * Similar to, for example a servlet session object. It can be used to
 * share objects associated to other objects that are usually strings.</p>
 *
 * <p>The context is automatically created by the Geci object when none
 * was injected. On the other hand when the created context object is
 * retrieved from the Geci object it can be injected into a different
 * Geci object and that way different Geci instances can share context.</p>
 */
public interface Context {

    /**
     * Get the object associated with the key. In case there is no
     * object associated with the key then the {@code ini}, initial
     * object supplier should provide an instance of the object.
     *
     * @param key to which the associated object is requested. Currently
     *            I believe that generators using this feature will use
     *            mainly {@code String} values as keys, but at the same
     *            time I also see no reason to restrict the type of the
     *            key. Different generators can use different key types.
     * @param ini the supplier that creates the object for the key if
     *            the object does not exist yet.
     * @param <T> the type of the value
     * @return the object associated with the key.
     */
    <T> T get(Object key, Supplier<T> ini);

    /**
     * Get the object associated with the key. This version of the
     * method is to be used when the generator want to get the object
     * and is absolutely sure that there is already an object associated
     * with the key.
     *
     * @param key see {@link #get(Object, Supplier)}
     * @param <T> the type of the value
     * @return the object associated with the key or null if there is no
     * such object
     */
    default <T> T get(Object key) {
        return get(key, () -> null);
    }
}
