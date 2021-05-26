package javax0.geci.jamal.macros.holders;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.UserDefinedMacro;

import java.lang.reflect.Field;

public class FieldHolder implements UserDefinedMacro, ObjectHolder<Field> {
    final Field field;

    public FieldHolder(Field field) {
        this.field = field;
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
        return field.getName();
    }

    @Override
    public Field getObject() {
        return field;
    }
}
