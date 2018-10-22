package javax0.geci.fluent.internal;

import javax0.geci.tools.Tools;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MethodTool {
    final private AtomicInteger argCounter = new AtomicInteger(0);
    final private String klassName;
    private Method method;
    private String type = null;
    private boolean isInterface = false;

    private MethodTool(Class klass) {
        this.klassName = Tools.normalizeTypeName(klass.getName());
    }

    public static MethodTool from(Class klass) {
        return new MethodTool(klass);
    }

    public MethodTool forThe(Method method) {
        this.method = method;
        return this;
    }

    public MethodTool withType(String type) {
        this.type = type;
        return this;
    }

    public MethodTool asInterface() {
        this.isInterface = true;
        return this;
    }

    public String signature() {
        var arglist = Arrays.stream(method.getGenericParameterTypes())
                .map(this::getArg)
                .collect(Collectors.joining(","));
        var exceptionlist = Arrays.stream(method.getGenericExceptionTypes())
                .map(t -> Tools.normalizeTypeName(t.getTypeName()))
                .collect(Collectors.joining(","));
        return (isInterface ? "" : (Tools.modifiersString(method))) +
                (type == null ? Tools.typeAsString(method) : type) +
                " " +
                method.getName() +
                "(" + arglist + ")" +
                (exceptionlist.length() == 0 ? "" : " throws " + exceptionlist);
    }

    public String call() {
        var arglist = Arrays.stream(method.getGenericParameterTypes())
                .map(this::getArgCall)
                .collect(Collectors.joining(","));

        return method.getName() + "(" + arglist + ")";
    }

    private String getArgCall(Type t) {
        final var normType = Tools.normalizeTypeName(t.getTypeName());
        final String arg;
        if (normType.equals(klassName)) {
            arg = "((Wrapper)arg" + argCounter.addAndGet(1) + ").that";
        } else {
            arg = "arg" + argCounter.addAndGet(1);
        }
        return arg;
    }

    private String getArg(Type t) {
        final var normType = Tools.normalizeTypeName(t.getTypeName());
        final String actualType;
        if (normType.equals(klassName)) {
            actualType = "WrapperInterface";
        } else {
            actualType = normType;
        }
        return actualType + " arg" + argCounter.addAndGet(1);
    }
}
