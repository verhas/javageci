package javax0.geci.jamal.macros.holders;

import java.util.Arrays;

public class FieldsHolder extends Holder<String[]> {
    final String id;

    public FieldsHolder(FieldHolder[] fields, String id) {
        super(Arrays.stream(fields).map(FieldHolder::getId).toArray(String[]::new));
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
