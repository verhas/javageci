package javax0.geci.jamal.macros.holders;

import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.ObjectHolder;
import javax0.jamal.api.UserDefinedMacro;

import java.lang.reflect.Method;

public class MethodHolder implements UserDefinedMacro, ObjectHolder<Method> {
    final Method method;

    public MethodHolder(Method method) {
        this.method = method;
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
        return method.getName();
    }

    @Override
    public Method getObject() {
        return method;
    }
}
