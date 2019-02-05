package javax0.geci.delegator;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractDeclaredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.MethodTool;
import javax0.geci.tools.Tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
                if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !implemented(method).inClass(klass)) {
                    segment.write("@javax0.geci.annotations.Generated(\"" + mnemonic() + "\")");
                    segment.write_r(MethodTool.with(method).signature() + " {");
                    if ("void".equals(method.getReturnType().getName())) {
                        segment.write(name + "." + MethodTool.with(method).call() + ";");
                    } else {
                        segment.write("return " + name + "." + MethodTool.with(method).call() + ";");
                    }
                    segment.write_l("}");
                    segment.newline();
                }
            }
        }
    }

    private static class MethodHolder {
        final private Method method;

        private MethodHolder(Method method) {
            this.method = method;
        }

        public boolean inClass(Class<?> klass) {
            try {
                var localMethod = klass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return !Tools.isGenerated(localMethod);
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
    }

    /**
     * Returns a new instance of a method holder, but the proper use is to chain it via
     * {@link MethodHolder#inClass(Class)} which at the end returns true if the method is implemented in
     * the class and there is no need to implement.
     *
     * @param method the method
     * @return the new method holder
     */
    private MethodHolder implemented(Method method) {
        return new MethodHolder(method);
    }
}
