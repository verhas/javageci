package javax0.geci.delegator;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.MethodTool;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Delegator extends AbstractFilteredFieldsGenerator {
    private static class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String filter = "!static";
        private String methods = "public & !static";
    }

    private Delegator() {
    }

    @Override
    public String mnemonic() {
        return "delegator";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var name = field.getName();
        final var local = localConfig(params);
        final List<Method> methods = Arrays.stream(GeciReflectionTools.getDeclaredMethodsSorted(field.getType()))
                .filter(Selector.compile(local.methods)::match)
                .collect(Collectors.toList());
        for (final var method : methods) {
            if (!manuallyCoded(klass, method)) {
                writeGenerated(segment, config.generatedAnnotation);
                segment.write_r(MethodTool.with(method).signature() + " {")
                        .write((isVoid(method) ? "" : "return ") + name + "." + MethodTool.with(method).call() + ";")
                        .write_l("}")
                        .newline();
            }
        }
    }

    private static boolean isVoid(Method method) {
        return "void".equals(method.getReturnType().getName());
    }

    private boolean manuallyCoded(Class<?> klass, Method method) {
        try {
            var localMethod = klass.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return localMethod.getDeclaredAnnotation(config.generatedAnnotation) == null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    protected String defaultFilterExpression() {
        return config.filter;
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static Delegator.Builder builder() {
        return new Delegator().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "filter",
        "methods",
        "id"
    );

    @Override public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder generatedAnnotation(Class<? extends java.lang.annotation.Annotation> generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder methods(String methods) {
            config.methods = methods;
            return this;
        }

        public Delegator build() {
            return Delegator.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.filter = params.get("filter",config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.methods = params.get("methods",config.methods);
        return local;
    }
    //</editor-fold>
}
