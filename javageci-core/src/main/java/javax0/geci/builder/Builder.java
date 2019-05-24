package javax0.geci.builder;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Builder extends AbstractFilteredFieldsGenerator {
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
    public static Builder.BuilderBuilder builder() {
        return new Builder().new BuilderBuilder();
    }

    public class BuilderBuilder {
        public BuilderBuilder buildMethod(String buildMethod) {
            Builder.this.buildMethod = buildMethod;
            return this;
        }

        public BuilderBuilder builderFactoryMethod(String builderFactoryMethod) {
            Builder.this.builderFactoryMethod = builderFactoryMethod;
            return this;
        }

        public BuilderBuilder builderName(String builderName) {
            Builder.this.builderName = builderName;
            return this;
        }

        public BuilderBuilder filter(String filter) {
            Builder.this.filter = filter;
            return this;
        }

        public BuilderBuilder generatedAnnotation(Class generatedAnnotation) {
            Builder.this.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder build() {
            return Builder.this;
        }
    }
    //</editor-fold>
}

