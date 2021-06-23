package javax0.geci.jamal.macros.holders;

import java.util.Arrays;

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
