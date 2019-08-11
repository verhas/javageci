package javax0.geci.api;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface CompoundParams {
    static boolean toBoolean(String s) {
        return s != null && (
            s.equalsIgnoreCase("yes") ||
                s.equalsIgnoreCase("ok") ||
                s.equalsIgnoreCase("1") ||
                s.equalsIgnoreCase("true")
        );
    }

    /**
     * Set the constraints that this {@code CompoundParameters} should
     * adhere. The constrain is simply the set of the allowed key
     * strings. The other argument are only used to construct a
     * meaningful exception during checking.
     *
     * <p> After the constraints are set a check is also performed and a
     * {@link GeciException} may be thrown.
     *
     * @param source      the source object from which the keys come from
     * @param mnemonic    the mnemonic of the generator
     * @param allowedKeys the set of the allowed keys
     */
    void setConstraints(Source source, String mnemonic, Set<String> allowedKeys);

    /**
     * Get the parameter or return the {@code defaults} if the parameter
     * is not defined.
     *
     * @param key      the name of the parameter
     * @param defaults to use when the parameter is not defined
     * @return the value of the parameter
     */
    String get(String key, String defaults);

    /**
     * Get the parameter or return the value supplied by the parameter
     * {@code defaultSupplier} if the parameter is not defined.
     *
     * <p> This method can be used instead of {@link #get(String,
     * String)} when the calculation of the default string costs a lot.
     *
     * @param key             the name of the parameter
     * @param defaultSupplier to use when the parameter is not defined
     * @return the value of the parameter
     */
    String get(String key, Supplier<String> defaultSupplier);

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
    String get(String key);

    /**
     * Get the list of the values associated with the key. In case there
     * are no values associated with the key then the return value is
     * {@code null}. This is radically different from the single string
     * value {@link #get(String)}, which returns empty string. This
     * method returns {@code null} in case there are no values.
     *
     * <p> This method should be used when there are multi-values keys
     * in the configuration.
     *
     * @param key we are looking for
     * @return the list of string values associated to the key
     */
    List<String> getValueList(String key);

    /**
     * Get the list of the values associated with the key. In case there
     * are no values associated with the key then the return value is
     * the list specified in the argument {@code defaults}.
     *
     * @param key we are looking for
     * @param defaults is the list to be used if there are no values associated with the key
     * @return the list of string values associated to the key
     */
    List<String> getValueList(String key, List<String> defaults);

    /**
     * Shortcut to {@code get("id")}
     *
     * @return the ID from the configuration
     */
    default String id() {
        return get("id");
    }

    /**
     * Get the id from the parameters or the default value, which is the
     * argument {@code defaulT} in case there is no id defined.
     *
     * @param defaulT the default value for the id in case it is not
     *                defined in the parameters. This is usually the
     *                mnemonic of the generator that is currently
     *                running.
     * @return the id
     */
    String id(String defaulT);

    /**
     * Retrieves a parameter boolean value.
     *
     * @param key the name of the parameter
     * @return {@code true} if the parameter has a value {@code true}, {@code ok}, {@code 1} or {@code yes} (case
     * insensitive). In any other case the method returns {@code false}.
     */
    boolean is(String key);

    /**
     * Retrieves a parameter boolean value if it exists. If it does not it returns with a default value.
     *
     * @param key          the name of the parameter
     * @param defaultValue the default value if the parameter does not exist.
     * @return the default value if the parameter does not exist. If it exists, it behaves as {@link #is(String)}.
     */
    boolean is(String key, boolean defaultValue);

    /**
     * Retrieves a parameter boolean value if it exists or tests a default value if it does not.
     *
     * @param key          the name of the parameter
     * @param defaultValue the default value if the parameter does not exist.
     * @return {@code true} if the parameter exists and fulfills the condition of {@link #is(String)} or if the parameter
     * does not exists and the default value fulfills the condition of {@link #is(String)}. In any other case the method returns {@code false}.
     */
    boolean is(String key, String defaultValue);

    default boolean isNot(String key) {
        return !is(key);

    }

    /**
     * Returns the set of queryable keys. The key {@code id} will only
     * be listed if it is explicitly contained in some of the maps or
     * underlying compound parameters.
     *
     * @return the set of the keys
     */
    Set<String> keySet();

}
