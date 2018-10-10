package javax0.geci.delegator;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractDeclaredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Delegator extends AbstractDeclaredFieldsGenerator {
    @Override
    public String mnemonic() {
        return "delegator";
    }


    public void processField(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        var id = params.get("id");
        var delClass = field.getType();
        var methods = delClass.getDeclaredMethods();
        var name = field.getName();
        try (var segment = source.open(id)) {
            for (var method : methods) {
                var modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    var counter = new AtomicInteger(0);
                    var arglist = Arrays.stream(method.getGenericParameterTypes()).map(t -> t.toString() + " i" + counter.addAndGet(1)).collect(Collectors.joining(","));
                    var callCounter = new AtomicInteger(0);
                    var callArglist = Arrays.stream(method.getGenericParameterTypes()).map(t -> " i" + callCounter.addAndGet(1)).collect(Collectors.joining(","));
                    segment.write_r("" + Tools.modifiersString(method) +
                            Tools.typeAsString(method) +
                            " " + method.getName() + "(" +
                            arglist + "){");
                    if (!"void".equals(method.getReturnType().getName())) {
                        segment.write("return " + name + "." + method.getName() + "(" + callArglist + ");");
                    } else {
                        segment.write(name + "." + method.getName() + "(" + callArglist + ");");
                    }
                    segment.write_l("}");
                }
            }
        }
    }
}
