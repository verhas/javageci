package javax0.geci.factory;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Factory extends AbstractFilteredFieldsGenerator {
    private Class<? extends Annotation> generatedAnnotation = Generated.class;
    private String filter = "private & !static & !final";
    private String builderName = "Builder";
    private String builderFactoryMethod = "builder";
    private String buildMethod = "build";

    @Override
    public String mnemonic() {
        return "builder";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var bn = global.get("builderName", builderName);
        final var bfm = global.get("builderFactoryMethod", builderFactoryMethod);
        writeGenerated(segment, generatedAnnotation);
        segment.write_r("public static %s.%s %s() {", klass.getSimpleName(), bn, bfm)
            .write("return new %s().new %s();", klass.getSimpleName(), bn)
            .write_l("}")
            .newline()
            .write_r("public class %s {", bn);
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) throws Exception {
        final var bn = params.get("builderName", builderName);
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName());
        writeGenerated(segment, generatedAnnotation);
        segment.write_r("public %s %s(%s %s){", bn, name, type, name)
            .write("%s.this.%s = %s;", klass.getSimpleName(), name, name)
            .write("return this;")
            .write_l("}")
            .newline();
    }

    @Override
    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var bm = global.get("buildMethod", buildMethod);
        writeGenerated(segment, generatedAnnotation);
        segment.write_r("public %s %s() {", klass.getSimpleName(), bm)
            .write("return %s.this;", klass.getSimpleName())
            .write_l("}");
        segment.write_l("}"); // end of builder class
    }

    @Override
    protected String defaultFilterExpression() {
        return filter;
    }

    //<editor-fold id="builder" builderName="BuilderBuilder">
    public static Factory.BuilderBuilder builder() {
        return new Factory().new BuilderBuilder();
    }

    public class BuilderBuilder {
        public BuilderBuilder buildMethod(final String x) {
            Factory.this.buildMethod = x;
            return this;
        }

        public BuilderBuilder builderFactoryMethod(final String x) {
            Factory.this.builderFactoryMethod = x;
            return this;
        }

        public BuilderBuilder builderName(final String x) {
            Factory.this.builderName = x;
            return this;
        }

        public BuilderBuilder filter(final String x) {
            Factory.this.filter = x;
            return this;
        }

        public BuilderBuilder generatedAnnotation(final Class x) {
            Factory.this.generatedAnnotation = x;
            return this;
        }

        public Factory build() {
            return Factory.this;
        }
    }
    //</editor-fold>
}
