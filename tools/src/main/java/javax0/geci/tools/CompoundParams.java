package javax0.geci.tools;

import java.util.Map;

/**
 * A parameter set that is composed from two or more parameter maps. The parameter maps are passed to the constructor
 * in the order they should be looked at from left to right. When a parameter is retrieved by any method first the
 * leftmost parameter map is consulted, then the next and so on.
 * <p>
 * A passed parameter map can be {@code null} or empty.
 */
public class CompoundParams {

    private final Map<String, String>[] params;


    public CompoundParams(Map<String, String>... params) {
        this.params = params;
    }

    /**
     * Get the parameter or return the {@code defaults} if the parameter is not defined.
     *
     * @param key      the name of the parameter
     * @param defaults to use when the parameter is not defined
     * @return the value of the parameter
     */
    public String get(String key, String defaults) {
        var s = get(key);
        if (s == null) {
            return defaults;
        } else {
            return s;
        }
    }

    /**
     * Get a parameter.
     *
     * @param key the name of the parameter.
     * @return the parameter or {@code null} if the parameter is not defined.
     */
    public String get(String key) {
        for (var param : params) {
            if (param != null && param.containsKey(key)) {
                return param.get(key);
            }
        }
        return null;
    }

    /**
     * Retrieves a parameter boolean value.
     *
     * @param key the name of the parameter
     * @return {@code true} if the parameter has a value {@code true}, {@code ok}, {@code 1} or {@code yes} (case
     * insensitive). In any other case the method returns {@code false}.
     */
    public boolean is(String key) {
        var s = get(key);
        return s != null && (
                s.equalsIgnoreCase("yes") ||
                        s.equalsIgnoreCase("ok") ||
                        s.equalsIgnoreCase("1") ||
                        s.equalsIgnoreCase("true")
        );
    }

}
