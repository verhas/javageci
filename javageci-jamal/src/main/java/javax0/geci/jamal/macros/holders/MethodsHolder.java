package javax0.geci.jamal.macros.holders;

import java.util.Arrays;

/**
 * MethodsHolder holds an array of identifiers, which are all identify a MethodHolder.
 *
 * That way a macro can iterate through all the methods and can get the name of user defined macro that hold the method
 * and refer to that through the names.
 */
public class MethodsHolder extends Holder<String[]> {
    final String id;

    public MethodsHolder(MethodHolder[] methods, String id) {
        super(Arrays.stream(methods).map(MethodHolder::getId).toArray(String[]::new));
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String[] getObject() {
        return object;
    }
}
