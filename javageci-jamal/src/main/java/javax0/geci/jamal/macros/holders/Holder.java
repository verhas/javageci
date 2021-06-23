package javax0.geci.jamal.macros.holders;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.UserDefinedMacro;

public abstract class Holder<T> implements UserDefinedMacro, ObjectHolder<T> {
    final protected T object;

    public Holder(T field) {
        this.object = field;
    }

    @Override
    public String evaluate(String... parameters) throws BadSyntax {
        return "";
    }

    /**
     * Individual implementations of {@link #evaluate(String...) evaluate()} can use different parameters.
     * @return -1 meaning any number of parameters
     */
    @Override
    public int expectedNumberOfArguments() {
        return -1;
    }

    @Override
    public T getObject() {
        return object;
    }
}
