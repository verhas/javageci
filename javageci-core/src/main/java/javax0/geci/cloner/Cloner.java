package javax0.geci.cloner;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static javax0.geci.tools.CaseTools.ucase;

public class Cloner extends AbstractFilteredFieldsGenerator {

    public Cloner() {
        declaredOnly = false;
    }

    private static class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String filter = "!static & !final";
        private String cloneMethod = "clone";
    }

    @Override
    protected String defaultFilterExpression() {
        return config.filter;
    }

    @Override
    public String mnemonic() {
        return "cloner";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        Field[] fields = Arrays.stream(GeciReflectionTools.getAllFieldsSorted(klass))
                .filter(field -> !Modifier.isFinal(field.getModifiers()))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        writeGenerated(segment, config.generatedAnnotation);
        final var fullyQualified = GeciReflectionTools.getSimpleGenericClassName(klass);
        segment.write_r("public %s %s() {", fullyQualified, local.cloneMethod)
                .write("final var it = new %s();", klass.getSimpleName())
                .newline();

        for (final var field : fields) {
            segment.write("it.%s = %s;", field.getName(), field.getName());
        }
        segment.write("return it;").write_l("}").newline();
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var local = localConfig(params);
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName(), klass);
        final var fullyQualified = GeciReflectionTools.getSimpleGenericClassName(klass);
        segment.write_r("%s with%s(%s %s) {", fullyQualified, ucase(name), type, name)
                .write("final var it = clone();")
                .write("it.%s = %s;", name, name)
                .write("return it;")
                .write_l("}")
                .newline();
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static Cloner.Builder builder() {
        return new Cloner().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "cloneMethod",
        "filter",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder cloneMethod(String cloneMethod) {
            config.cloneMethod = cloneMethod;
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder generatedAnnotation(Class generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Cloner build() {
            return Cloner.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.cloneMethod = params.get("cloneMethod",config.cloneMethod);
        local.filter = params.get("filter",config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        return local;
    }
    //</editor-fold>
}
