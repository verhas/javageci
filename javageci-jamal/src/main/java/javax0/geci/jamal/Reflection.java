package javax0.geci.jamal;

import javax0.geci.tools.MethodTool;
import javax0.geci.tools.Tools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.BadSyntax;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Reflection {


    /**
     * This macro is NOT registered in the META-INF or in the module-info.java. Instances of this macro are instantiated
     * when methods are listed. One new instance for each method, and they are registered with a name
     * {@code Method_%08X} where the {@code %08X} is the hex counter starting from zero.
     */
    private static class MethodMacro implements Macro {
        private static final Pattern PATTERN = Pattern.compile("\\s*(\\d+)\\s+(\\w+)");
        private final String id;
        private final List<Method> method;

        @Override
        public String getId() {
            return id;
        }

        MethodMacro(String id, List<Method> method) {
            this.id = id;
            this.method = method;
        }

        @Override
        public String evaluate(Input in, Processor processor) throws BadSyntax {
            final var matcher = PATTERN.matcher(in.toString());
            if (matcher.matches()) {
                var index = Integer.parseInt(matcher.group(1));
                return MethodTool.methodSignature(method.get(index));
            } else {
                throw new BadSyntax("");
            }
        }
    }

    /**
     * This PATTERN contains two parts: group(1) is the name of the class
     * group(2) is the selector expression, optionally empty
     */
    private static final Pattern PATTERN = Pattern.compile("(.*)\\|(.*?)\\|(.*)");

    public static class Methods implements Macro {
        AtomicInteger count = new AtomicInteger(0);

        @Override
        public String evaluate(Input in, Processor processor) {
            Matcher matcher = PATTERN.matcher(in.toString());
            if (matcher.matches()) {
                try {
                    final String macroName = matcher.group(1).trim();
                    final String className = matcher.group(2).trim();
                    Selector selector = Selector.compile(matcher.group(3));
                    Class<?> klass = Class.forName(className);
                    var declaredMethods = Tools.getDeclaredMethodsSorted(klass);
                    var ret = new StringBuilder();
                    var list = Arrays.stream(declaredMethods).filter(selector::match).collect(Collectors.toList());
                    var sep = "";
                    var methodMacro = new MethodMacro(macroName, list);
                    processor.getRegister().define(methodMacro);
                    int i = 0;
                    for (final var method : list) {
                        ret.append(sep).append(i++);
                        sep = ",";
                    }
                    return ret.toString();
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Class name in macro 'methods "
                        + matcher.group(1)
                        + " cannot be found");
                }
            } else {
                throw new IllegalArgumentException("Macro 'methods class|selection' bad syntax");
            }
        }
    }
}
