package javax0.geci.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p>A simple implementation of the context {@link javax0.geci.api.Context} interface. This implementation contains an
 * empty map and you can query keys against the empty map. Eventually there will be no values in the map. Subclasses
 * extending this class can add more features, they have access to the map itself as the map is {@code protected}.</p>
 */
public class Context implements javax0.geci.api.Context {

    /**
     * <p>This class is simply an envelop over an empty map and it is immutable thus there is no need to have more than one
     * instance of it.</p>
     */
    public static javax0.geci.api.Context singletonInstance = new Context();

    /**
     * <p>Implementation may decide the extend this class and those implementation can use the Map.</p>
     */
    protected final Map<Object, Object> map = new HashMap<>();

    @Override
    public <Z> Z get(Object key, Supplier<Z> ini) {
        return (Z) map.computeIfAbsent(key, k -> ini.get());
    }
}
