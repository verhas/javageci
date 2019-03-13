package javax0.geci.jamal.reflection;

import javax0.geci.jamal.util.EntityStringer;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;
import javax0.jamal.api.Input;
import javax0.jamal.api.Macro;
import javax0.jamal.api.Processor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Methods implements Macro {
    /**
     * This METHOD_MACRO_PATTERN contains two parts: group(1) is the name of the class
     * group(2) is the selector expression, optionally empty
     */
    private static final Pattern METHOD_MACRO_PATTERN = Pattern.compile("(.*?)\\|(.*)");

    @Override
    public String evaluate(Input in, Processor processor) {
        Matcher matcher = METHOD_MACRO_PATTERN.matcher(in.toString());
        if (matcher.matches()) {
            try {
                final String className = matcher.group(1).trim();
                Selector selector = Selector.compile(matcher.group(2));
                Class<?> klass = GeciReflectionTools.classForName(className);
                var declaredMethods = GeciReflectionTools.getAllMethodsSorted(klass);
                var ret = new StringBuilder();
                var list = Arrays.stream(declaredMethods).filter(selector::match).collect(Collectors.toList());
                var sep = "";
                for (final var method : list) {
                    var fingerPrint = EntityStringer.method2Fingerprint(method);
                    ret.append(sep).append(fingerPrint);
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