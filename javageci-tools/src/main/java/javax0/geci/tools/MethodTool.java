package javax0.geci.tools;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MethodTool {
    final protected AtomicInteger argCounter = new AtomicInteger(0);
    protected Method method;
    private String type = null;
    private boolean isInterface = false;
    private boolean isPublic = false;
    private Function<String, String> decorator;

    public static MethodTool with(Method method) {
        var it = new MethodTool();
        it.method = method;
        return it;
    }

    public MethodTool forThe(Method method) {
        this.method = method;
        return this;
    }

    public MethodTool withType(String type) {
        this.type = type;
        return this;
    }

    public MethodTool decorateNameWith(Function<String, String> decorator) {
        this.decorator = decorator;
        return this;
    }

    public MethodTool asInterface() {
        this.isInterface = true;
        return this;
    }

    public MethodTool asPublic() {
        this.isPublic = true;
        return this;
    }

    public static String methodSignature(Method method) {
        return with(method).signature();
    }

    public String signature() {
        return signature(false);
    }

    public String signature(boolean concrete) {
        final var types = method.getGenericParameterTypes();
        final var sb = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            if (i == types.length - 1 && method.isVarArgs()) {
                sb.append(getVarArg(types[i]));
            } else {
                sb.append(getArg(types[i]));
            }
        }
        var arglist = sb.toString();
        var exceptionlist = Arrays.stream(method.getGenericExceptionTypes())
            .map(GeciReflectionTools::getGenericTypeName)
            .collect(Collectors.joining(","));
        final String modifiers;
        if (isPublic) {
            if (concrete) {
                modifiers = (isInterface ? "" : "public " + GeciReflectionTools.modifiersStringNoAccessConcrete(method));
            } else {
                modifiers = (isInterface ? "" : "public " + GeciReflectionTools.modifiersStringNoAccess(method));
            }
        } else {
            if (concrete) {
                modifiers = (isInterface ? "" : (GeciReflectionTools.modifiersStringConcrete(method)));
            } else {
                modifiers = (isInterface ? "" : (GeciReflectionTools.modifiersString(method)));
            }
        }
        return modifiers +
            (type == null ? GeciReflectionTools.typeAsString(method) : type) +
            " " +
            decoratedName(method) +
            "(" + arglist + ")" +
            (exceptionlist.length() == 0 ? "" : " throws " + exceptionlist);
    }

    public String call() {
        var arglist = Arrays.stream(method.getGenericParameterTypes())
            .map(this::getArgCall)
            .collect(Collectors.joining(","));

        return decoratedName(method) + "(" + arglist + ")";
    }

    public String getArgCall(Type t) {
        final var normType = GeciReflectionTools.getGenericTypeName(t);
        return "arg" + argCounter.addAndGet(1);
    }

    public String getVarArg(Type t) {
        final var normType = GeciReflectionTools.getGenericTypeName(t);
        final String actualType = normType.substring(0, normType.length() - 2) + "... ";
        return actualType + " arg" + argCounter.addAndGet(1);
    }

    public String getArg(Type t) {
        final var normType = GeciReflectionTools.getGenericTypeName(t);
        return normType + " arg" + argCounter.addAndGet(1);
    }

    /**
     * Decorate the method name if {@code decorator} is not {@code null}.
     *
     * @param method of which the name is retrieved
     * @return the decorated name
     */
    private String decoratedName(Method method) {
        if (decorator == null) {
            return method.getName();
        } else {
            return decorator.apply(method.getName());
        }
    }
}
