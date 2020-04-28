package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax0.geci.api.CompoundParams.toBoolean;

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
public class CompoundParams implements javax0.geci.api.CompoundParams {

    private static final String Q = "\"";
    private final Map<String, List<String>>[] params;
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
    public CompoundParams(String id, Map<String, ?>... params) {
        this.params = new HashMap[params.length];
        for (int i = 0; i < params.length; i++) {
            this.params[i] = new HashMap<>();
            for (final var entry : params[i].entrySet()) {
                this.params[i]
                    .put(entry.getKey(),
                        valueToList(entry.getValue()));
            }
        }
        this.cparams = null;
        this.id = id;
    }

    /**
     * Convert an object that is supposed to be a list of strings or a
     * single string to a list of strings. The value is already a list
     * then return the list. If the value is a string then create a
     * mutable list that contains the string as a single element.
     *
     * @param value which is to be converted
     * @return the list that contains the value or the value itself if
     * value is already a list
     */
    private static List<String> valueToList(Object value) {
        if (value instanceof List) {
            return assertListOfStrings((List)value);
        } else if (value instanceof String) {
            return new ArrayList(Collections.singletonList((String) value));
        } else {
            throw new IllegalArgumentException(value.getClass()
                                                   + " cannot be used in "
                                                   + CompoundParams.class.getSimpleName()
                                                   + " as parameter value.");
        }
    }

    private static List<String> assertListOfStrings(List value) {
        for (final var string : value) {
            if (!(string instanceof String)) {
                throw new IllegalArgumentException(value.getClass()
                                                       + " cannot be used in "
                                                       + CompoundParams.class.getSimpleName()
                                                       + " as parameter value as it contains non-String elements.");
            }
        }
        return value;
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
     * @param cparams the compound parameters array.
     */
    public CompoundParams(CompoundParams... cparams) {
        this.params = null;
        this.cparams = cparams;
        this.id = find(c -> c.id, cparams);
        this.source = find(c -> c.source, cparams);
        this.mnemonic = find(c -> c.mnemonic, cparams);
        this.allowedKeys = find(c -> c.allowedKeys, cparams);
        if (source != null && mnemonic != null && allowedKeys != null) {
            checkAllowedKeys();
        }
    }

    public void trace() {
        for (final var key : keySet()) {
            Tracer.log(key, get(key));
        }
    }

    /**
     * <ol>
     *
     * <li> Go through all non-null cparam array element, </li>
     *
     * <li> fetch the {@code id}, {@code source}, {@code mnemonic},
     * {@code allowedKeys} as defined by the mapper, </li>
     *
     * <li>find the first non-null</li>
     *
     * </ol>
     *
     * @param mapper  that fetches {@code id}, {@code source}, {@code
     *                mnemonic}, {@code allowedKeys} from the cparam
     * @param cparams the compound parameters from which the constructor
     *                calling this method wants to inherit the mapped
     *                field
     * @param <T>     the type of the inherited field selected by the
     *                mapper
     * @return the value that is to be inherited
     */
    private static <T> T find(Function<CompoundParams, T> mapper,
                              CompoundParams... cparams) {
        return Arrays.stream(cparams)
                   .filter(Objects::nonNull)
                   .map(mapper)
                   .filter(Objects::nonNull)
                   .limit(1)
                   .findFirst().orElse(null);
    }

    @Override
    public void setConstraints(Source source, String mnemonic, Set<String> allowedKeys) {
        this.source = source;
        this.mnemonic = mnemonic;
        this.allowedKeys = allowedKeys;
        if (source != null && allowedKeys != null) {
            checkAllowedKeys();
        }
    }

    /**
     * Check that the key set contains only strings that are in the
     * {@code allowedKeys} field. If there is any key, which is not
     * listed in the field then throw an exception. The exception will
     * try to find the one from the allowed keys that is the closest to
     * the one, which is not allowed. The error message in the exception
     * will list all the keys that are not allowed and after that at the
     * end it will also list the possible, allowed values.
     */
    private void checkAllowedKeys() {
        final StringBuilder errorMessage = new StringBuilder();
        for (final var key : keySet()) {
            if (!allowedKeys.contains(key)) {
                String closestKey = null;
                int closestDistance = Integer.MAX_VALUE;
                for (final var s : allowedKeys) {
                    final var d = Levenshtein.distance(key, s);
                    if (d == closestDistance) {
                        closestKey = null;
                    }
                    if (d < closestDistance) {
                        closestDistance = d;
                        closestKey = s;
                    }
                }
                errorMessage
                    .append("\nThe configuration '")
                    .append(key)
                    .append("' can not be used with the generator ")
                    .append(mnemonic)
                    .append(closestKey == null ?
                                "" :
                                ", did you mean '" + closestKey + "' ?");
            }
        }
        if (errorMessage.length() > 0) {
            throw new GeciException(errorMessage.append(
                "\nThe possible keys are:\n  ").append(
                String.join(", ", allowedKeys))
                                        .append("\nIn source code ")
                                        .append(source.getAbsoluteFile())
                                        .toString()
            );

        }
    }

    @Override
    public String get(String key, String defaults) {
        return Optional.ofNullable(get0(key)).orElse(defaults);
    }

    @Override
    public String get(String key, Supplier<String> defaultSupplier) {
        return Optional.ofNullable(get0(key)).orElse(defaultSupplier.get());
    }

    @Override
    public String get(String key) {
        final var value = get0(key);
        return value == null ? "" : value;
    }


    @Override
    public String id(String mnemonic) {
        var id = get("id");
        return id.length() == 0 ? mnemonic : id;
    }

    /**
     * Get the value for the key.
     *
     * <p>This method the actual implementation of the parameter search
     * traversing along the different sub structures that are stored in
     * a hierarchy in the the {@code CompoundParams}.
     *
     * <p>If the sub structure in this {@code CompoundParams} are hash
     * maps then the value is retrieved from the first that contains the
     * key.
     *
     * <p>If the sub structure in this {@code CompoundParams} are
     * further {@code CompoundParams} objects then the value is
     * retrieved from those using recursive calls. There is no check for
     * circular data structure that would cause infinite recursion.
     *
     * @param key the key we search for
     * @return the value String or an empty string in case there is no
     * such key or the value is empty string. It never returns {@code
     * null}.
     */
    private String get0(String key) {
        assertKeyAllowed(key);
        if (params != null) {
            return Arrays.stream(params)
                       .filter(Objects::nonNull)
                       .filter(p -> p.containsKey(key))
                       .map(p -> p.get(key).get(0))
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

    /**
     * The method checks that the key is listed in the {@code
     * allowedKeys} field unless the filed is {@code null}. If the key
     * is not listed in the {@code allowedKeys}, but there *is* an
     * {@code allowedKeys} set then the generator using the services of
     * {@code CompoundParams} has defined the allowed keys buit then
     * tries to access a key, which was not listed. This is an error in
     * the generator. In such a case this method will throw a {@link
     * GeciException}.
     *
     * @param key the key that we check
     */
    private void assertKeyAllowed(String key) {
        if (allowedKeys != null && !allowedKeys.contains(key)) {
            throw new GeciException("Generator is accessing key '"
                                        + key
                                        + "' which it does not list as an allowed key."
                                        + " This is a generator bug.");
        }
    }

    @Override
    public List<String> getValueList(String key, List<String> defaults) {
        final var list = getValueList(key);
        if (list != null) {
            return list;
        }
        return defaults;
    }

    @Override
    public List<String> getValueList(String key) {
        if (params != null) {
            return Arrays.stream(params)
                       .filter(Objects::nonNull)
                       .filter(p -> p.containsKey(key))
                       .map(p -> p.get(key))
                       .findFirst()
                       .orElse("id".equals(key) ? Collections.singletonList(id) : null);
        }
        if (cparams != null) {
            return Arrays.stream(cparams)
                       .filter(Objects::nonNull)
                       .map(p -> p.getValueList(key))
                       .filter(Objects::nonNull)
                       .findFirst()
                       .orElse("id".equals(key) ? Collections.singletonList(id) : null);
        }
        if ("id".equals(key)) {
            return Collections.singletonList(id);
        }
        return null;
    }


    @Override
    public boolean is(String key) {
        var s = get(key);
        return toBoolean(s);
    }

    @Override
    public boolean is(String key, boolean defaultValue) {
        var s = get(key);
        if (s.isEmpty()) {
            return defaultValue;
        } else {
            return toBoolean(s);
        }
    }

    @Override
    public boolean is(String key, String defaultValue) {
        var s = get(key);
        if (s.isEmpty()) {
            s = defaultValue;
        }
        return toBoolean(s);
    }


    @Override
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
