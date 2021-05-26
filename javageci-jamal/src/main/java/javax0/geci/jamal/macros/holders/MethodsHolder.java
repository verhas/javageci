package javax0.geci.jamal.macros.holders;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.UserDefinedMacro;

import java.lang.reflect.Method;

public class MethodsHolder implements UserDefinedMacro, ObjectHolder<Method[]> {
    final Method[] methods;
    final String id;

    public MethodsHolder(Method[] methods, String id) {
        this.methods = methods;
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
    public Method[] getObject() {
        return methods;
    }
}
