package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parameter set that is composed from two or more parameter maps. The
 * parameter maps are passed to the constructor in the order they should
 * be looked at from left to right. When a parameter is retrieved by any
 * method first the leftmost parameter map is consulted, then the next
 * and so on.
 *
 * <p> A CompoundParams can also contain many other CompoundParams
 * instead of string maps. In that case the underlying compound param
 * objects are scanned.
 *
 * <p> A compound parameters object also contains the ID of the
 * parameter set. This is used as the default value for the key "id".
 *
 * <p> According to the conventions the parameter "id" is used to
 * identify the editor-fold section where the generated code is to be
 * placed. Many times code generators use only one segment as their
 * output. In such cases the use of the "id" is an extra and a not
 * needed indirection. Instead the code can use the name of the
 * generator as identifier and can omit the key "id" from the
 * annotation.
 *
 * <p> For example the generator {@code accessor} generates setters and
 * getters into one single editor-fold (unless som field annotation
 * specifies different segment id, but usually it is not the case). If
 * that editor-fold segment is named {@code "accessor"} then there is no
 * need to specify this separately in the annotation.
 */
public class CompoundParams {

    private static final String Q = "\"";
    private final Map<String, String>[] params;
    private final CompoundParams[] cparams;
    private final String id;
    private Set<String> allowedKeys = null;
    private Source source = null;
    private String mnemonic = null;

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
     * Create a new {@code CompoundParams} object with the given {@code
     * id} and with the underlying {@code CompoundParameters} array.
     *
     * <p> The identifier of the parameters will be copied from the
     * first non-null element of the array.
     *
     * <p> If any of the argument {@code CompoundParameters} have
     * defined constraints (a set of allowed keys) then the constraints
     * will be checked and in case the constraints are violated then it
     * will throw {@link GeciException}.
     *
     *
     * @param cparams the compound parameters array.
     */
    public CompoundParams(CompoundParams... cparams) {
        this.params = null;
        this.cparams = cparams;
        this.id = find(cparams,c -> c.id);
        this.source = find(cparams,c -> c.source);
        this.mnemonic = find(cparams,c -> c.mnemonic);
        this.allowedKeys = find(cparams,c -> c.allowedKeys);
        if( source != null &&  mnemonic != null && allowedKeys != null ){
            checkAllowedKeys();
        }
    }

    private <T> T find(CompoundParams[] cparams, Function<CompoundParams,T> mapper){
        return Arrays.stream(cparams)
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(Objects::nonNull)
                .limit(1)
                .findFirst().orElse(null);
    }

    /**
     * Set the constraints that this {@code CompoundParameters} should
     * adhere. The constrain is simply the set of the allowed key
     * strings. The other parameters are used to construct a meaningful
     * exception during checking.
     *
     * <p> After the constraints are set a check is also performed and a
     * {@link GeciException} may be thrown.
     *
     * @param source the source object from which the keys come from
     * @param mnemonic the mnemonic of the generator
     * @param allowedKeys the set of the allowed keys
     */
    public void setConstrains(Source source, String mnemonic, Set<String> allowedKeys) {
        this.source = source;
        this.mnemonic = mnemonic;
        this.allowedKeys = allowedKeys;
        if (source != null && allowedKeys != null) {
            checkAllowedKeys();
        }
    }

    private void checkAllowedKeys() {
        for (final var key : keySet()) {
            if (!allowedKeys.contains(key)) {
                throw new GeciException("The configuration '"
                        + key
                        + "' can not be used with the generator "
                        + mnemonic
                        + " in source code "
                        + source.getAbsoluteFile());
            }
        }
    }

    /**
     * Get the parameter or return the {@code defaults} if the parameter
     * is not defined.
     *
     * @param key      the name of the parameter
     * @param defaults to use when the parameter is not defined
     * @return the value of the parameter
     */
    public String get(String key, String defaults) {
        return Optional.ofNullable(get0(key)).orElse(defaults);
    }

    /**
     * Get the parameter or return the value supplied by the parameter
     * {@code defaultSupplier} if the parameter is not defined. <p> This
     * method can be used instead of {@link #get(String, String)} when
     * the calculation of the default string costs a lot.
     *
     * @param key             the name of the parameter
     * @param defaultSupplier to use when the parameter is not defined
     * @return the value of the parameter
     */
    public String get(String key, Supplier<String> defaultSupplier) {
        return Optional.ofNullable(get0(key)).orElse(defaultSupplier.get());
    }

    /**
     * Get a parameter. The implementation looks through the underlying
     * map array or compound parameters array in the order they were
     * specified in the constructor. If the key is not found then {@code
     * ""} is returned.
     *
     * <p> The key "id" is handled in a special way. In case there is no
     * "id" defined in the parameters then the identifier of the
     * parameter set is returned. In the normal use case that is the
     * mnemonic of the actual generator calling this method. That way
     * generators can get the "id" of the segment they are supposed to
     * write that has the same name as the generator and the using code
     * does not need to specify it in the "id" parameter. This is a
     * simple convention over configuration simplification that is
     * implemented by all the generators, which use this method to get
     * the "id" to identify the segment where to write the generated
     * code.
     *
     * @param key the name of the parameter.
     * @return the parameter or {@code ""} if the parameter is not
     * defined. In case the key is {@code "id"} and is not defined in
     * the underlying array then the parameter set identifier is
     * returned.
     */
    public String get(String key) {
        if( allowedKeys != null && ! allowedKeys.contains(key)){
            throw new GeciException("Generator is accessing key '"+key+"' which is not allowed. This is a generator bug.");
        }
        return Objects.requireNonNullElse(get0(key), "");
    }

    /**
     * Shortcut to {@code get("id")}
     *
     * @return the ID from the configuration
     */
    public String id() {
        return get("id");
    }

    private String get0(String key) {
        if (params != null) {
            return Arrays.stream(params)
                    .filter(Objects::nonNull)
                    .filter(p -> p.containsKey(key))
                    .map(p -> p.get(key))
                    .findFirst()
                    .orElse("id".equals(key) ? id : null);
        }
        if (cparams != null) {
            return Arrays.stream(cparams)
                    .filter(Objects::nonNull)
                    .map(p -> p.get0(key))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("id".equals(key) ? id : null);
        }
        if ("id".equals(key)) {
            return id;
        }
        return null;
    }


    public static boolean toBoolean(String s){
        return s != null && (
            s.equalsIgnoreCase("yes") ||
                s.equalsIgnoreCase("ok") ||
                s.equalsIgnoreCase("1") ||
                s.equalsIgnoreCase("true")
        );
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
        return toBoolean(s);
    }

    public boolean is(String key, boolean defaultValue) {
        var s = get(key);
        if (s == null) {
            return defaultValue;
        } else {
            return is(key);
        }
    }

    public boolean is(String key, String defaultValue) {
        var s = get(key);
        if (s == null) {
            return is(defaultValue);
        } else {
            return is(key);
        }
    }

    public boolean isNot(String key) {
        return !is(key);
    }

    /**
     * Returns the set of queryable keys. The key {@code id} will only
     * be listed if it is explicitly contained in some of the maps or
     * underlying compound parameters.
     *
     * @return the set of the keys
     */
    public Set<String> keySet() {
        final Stream<Set<String>> keyStream;
        if (params != null) {
            keyStream = Arrays.stream(params).filter(Objects::nonNull)
                    .map(Map::keySet);
        } else if (cparams != null) {
            keyStream = Arrays.stream(cparams).filter(Objects::nonNull)
                    .map(CompoundParams::keySet);
        } else {
            keyStream = Stream.of();
        }
        return keyStream.filter(Objects::nonNull).flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "{ " +
                keySet().stream().map(k -> Q + k + Q + ":" + Q + get(k) + Q)
                        .collect(Collectors.joining(","))
                + " }";
    }
}
