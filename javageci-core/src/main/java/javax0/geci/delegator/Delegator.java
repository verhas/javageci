package javax0.geci.delegator;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractDeclaredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.MethodTool;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Delegator extends AbstractDeclaredFieldsGenerator {

    private final Class<? extends Annotation> generatedAnnotation;

    public Delegator() {
        generatedAnnotation = javax0.geci.annotations.Generated.class;
    }

    public Delegator(Class<? extends Annotation> generatedAnnotation) {
        this.generatedAnnotation = generatedAnnotation;
    }

    @Override
    public String mnemonic() {
        return "delegator";
    }

    public void process(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        final var id = params.get("id");
        final var filter = params.get("filter","public & !static");
        final var delClass = field.getType();
        final var methods = GeciReflectionTools.getDeclaredMethodsSorted(delClass);
        final var name = field.getName();
        try (final var segment = source.open(id)) {
            for (final var method : methods) {
                if (Selector.compile(filter).match(method) && !inClass(klass,method)) {
                    segment.write("@" + generatedAnnotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
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

    private boolean inClass(Class<?> klass, Method method) {
        try {
            var localMethod = klass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return localMethod.getDeclaredAnnotation(generatedAnnotation) == null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
