package javax0.geci.jamal.macros.holders;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.UserDefinedMacro;

import java.lang.reflect.Field;

public class FieldsHolder implements UserDefinedMacro, ObjectHolder<Field[]> {
    final Field[] fields;
    final String id;

    public FieldsHolder(Field[] fields, String id) {
        this.fields = fields;
        this.id = id;
    }

    @Override
    public String evaluate(String... parameters) throws BadSyntax {
        return "";
    }

    @Override
    public int expectedNumberOfArguments() {
        return 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Field[] getObject() {
        return fields;
    }
}
