package javax0.geci.tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A parameter set that is composed from two or more parameter maps. The parameter maps are passed to the constructor
 * in the order they should be looked at from left to right. When a parameter is retrieved by any method first the
 * leftmost parameter map is consulted, then the next and so on.
 * <p>
 * A CompoundParams can also contain many other CompoundParams instead of string maps. In that case the underlying
 * compound param objects are scanned.
 * <p>
 * A compound parameters object also contains the ID of the parameter set. This is used as the default value for
 * the key "id".
 * <p>
 * According to the conventions the parameter "id" is used to identify the editor-fold section where the generated
 * code is to be placed. Many times code generators use only one segment as their output. In such cases the use of
 * the "id" is an extra and not needed indirection. Instead the code can use the name of the generator as identifier
 * and can omit the key "id" from the annotation.
 * <p>
 * For example the generator {@code accessor} generates setters and getters into one single editor-fold (unless
 * som field annotation specifies different segment id, but usually it is not the case). If that editor-fold segment
 * is named {@code "accessor"} then there is no need to specify this separately in the annotation.
 */
public class CompoundParams {

    private final Map<String, String>[] params;
    private final CompoundParams[] cparams;
    private final String id;

    /**
     * Create a new {@code CompoundParams} object with the given {@code id} and with the underlying parameter
     * map array.
     *
     * @param id     the identifier of the parameter set
     * @param params the array of parameter maps
     */
    @SafeVarargs
    public CompoundParams(String id, Map<String, String>... params) {
        this.params = params;
        this.cparams = null;
        this.id = id;
    }

    /**
     * Create a new {@code CompoundParams} object with the given {@code id} and with the underlying
     * {@code CompoundParameters} array.
     * <p>
     * The identifier of the parameters will be copied from the first non-null element of the array.
     *
     * @param cparams the compound parameters array.
     */
    public CompoundParams(CompoundParams... cparams) {
        this.params = null;
        this.cparams = cparams;
        String id = null;
        for (var cparam : cparams) {
            if (cparam != null) {
                id = cparam.id;
                break;
            }
        }
        this.id = id;
    }

    /**
     * Get the parameter or return the {@code defaults} if the parameter is not defined.
     *
     * @param key      the name of the parameter
     * @param defaults to use when the parameter is not defined
     * @return the value of the parameter
     */
    public String get(String key, String defaults) {
        var s = get0(key);
        if (s == null) {
            return defaults;
        } else {
            return s;
        }
    }

    /**
     * Get a parameter. The implementation looks through the underlying map array or compound parameters array in the
     * order they were specified in the constructor. If the key is not found then {@code ""} is returned.
     * <p>
     * The key "id" is handled in a special way. In case there is no "id" defined in the parameters then the
     * identifier of the parameter set is returned. In the nomal use case that is the mnemonic of the actual
     * generator calling this method. That way generators can get the "id" of the segment they are supposed to
     * write that has the same name as the generator and the using code does not need to specify it in the "id"
     * parameter. This is a simple convention over configuration simplification that is implemented by all the
     * generators, which use this method to get the "id" to identify the segment where to write the generated code.
     *
     * @param key the name of the parameter.
     * @return the parameter or {@code ""} if the parameter is not defined. In case the key is {@code "id"} and is
     * not defined in the underlying array then the parameter set identifier is returned.
     */
    public String get(String key) {
        return Objects.requireNonNullElse(get0(key), "");
    }

    private String get0(String key) {
        if (params != null) {
            for (var param : params) {
                if (param != null && param.containsKey(key)) {
                    return param.get(key);
                }
            }
        }
        if (cparams != null) {
            for (var param : cparams) {
                if (param != null && param.get0(key) != null) {
                    return param.get0(key);
                }
            }
        }
        if ("id".equals(key)) {
            return id;
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

    public boolean isNot(String key) {
        return !is(key);
    }


    public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        if (params != null) {
            for (var param : params) {
                if (param != null) {
                    keys.addAll(param.keySet());
                }
            }
        } else {
            if (cparams != null) {
                for (var cparam : cparams) {
                    if (cparam != null) {
                        keys.addAll(cparam.keySet());
                    }
                }
            }
        }
        return keys;
    }

    private static final String Q = "\"";

    @Override
    public String toString() {
        return "{ " +
                keySet().stream().map(k -> Q + k + Q + ":" + Q + get(k) + Q)
                        .collect(Collectors.joining(","))
                + " }";
    }
}
