package javax0.geci.delegator;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractDeclaredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.MethodTool;
import javax0.geci.tools.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Delegator extends AbstractDeclaredFieldsGenerator {
    @Override
    public String mnemonic() {
        return "delegator";
    }

    public void processField(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        var id = params.get("id");
        var delClass = field.getType();
        var methods = Tools.getDeclaredMethodsSorted(delClass);
        var name = field.getName();
        try (var segment = source.open(id)) {
            for (var method : methods) {
                var modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    segment.write_r(MethodTool.with(method).signature() + " {");
                    if (!"void".equals(method.getReturnType().getName())) {
                        segment.write("return " + name + "." + MethodTool.with(method).call() + ";");
                    } else {
                        segment.write(name + "." + MethodTool.with(method).call() + ";");
                    }
                    segment.write_l("}");
                    segment.newline();
                }
            }
        }
    }
}
