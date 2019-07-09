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

    void setConstraints(Source source, String mnemonic, Set<String> allowedKeys);

    String get(String key, String defaults);

    String get(String key, Supplier<String> defaultSupplier);

    String get(String key);

    List<String> getValueList(String key);

    List<String> getValueList(String key, List<String> defaults);

    String id();

    String id(String mnemonic);

    boolean is(String key);

    boolean is(String key, boolean defaultValue);

    boolean is(String key, String defaultValue);

    boolean isNot(String key);

    Set<String> keySet();

}
