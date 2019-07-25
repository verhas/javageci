package javax0.geci.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Context implements javax0.geci.api.Context {

    private final Map<Object, Object> map = new HashMap<>();

    @Override
    public <Z> Z get(Object key, Supplier<Z> ini) {
        return (Z) map.computeIfAbsent(key, k -> ini.get());
    }
}
