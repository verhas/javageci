package javax0.geci.builder;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Builder extends AbstractFilteredFieldsGenerator {

    private static class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String filter = "private & !static & !final";
        private String builderName = "Builder";
        private String builderFactoryMethod = "builder";
        private String buildMethod = "build";
        private String aggregatorMethod = "add";
    }

    private final Config config = new Config();

    @Override
    public String mnemonic() {
        return "builder";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public static %s.%s %s() {", klass.getSimpleName(), local.builderName, local.builderFactoryMethod)
            .write("return new %s().new %s();", klass.getSimpleName(), local.builderName)
            .write_l("}")
            .newline()
            .write_r("public class %s {", local.builderName);
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var local = localConfig(params);
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName(), klass);
        if (!Modifier.isFinal(field.getModifiers())) {
            generateSetter(klass, segment, local.builderName, name, type);
        }
        if (config.aggregatorMethod != null && config.aggregatorMethod.length() > 0 &&
            Arrays.stream(field.getType().getDeclaredMethods()).anyMatch(m -> m.getName().equals(config.aggregatorMethod))) {
            generateAggregators(klass, segment, local.builderName, name, field);
        }
    }

    private void generateAggregators(Class<?> klass, Segment segment, String builder, String name, Field field) {
        final var aggMethod = config.aggregatorMethod + name.substring(0, 1).toUpperCase() + name.substring(1);
        Arrays.stream(GeciReflectionTools.getDeclaredMethodsSorted(field.getType()))
            .filter(m -> m.getName().equals(config.aggregatorMethod))
            .filter(m -> m.getParameterTypes().length == 1)
            .forEach(
                method -> {
                    var type = method.getParameterTypes()[0];
                    final String typeName;
                    Type genType;
                    Type[] parTypes;
                    Class parType;
                    if ((genType = field.getGenericType()) instanceof ParameterizedType &&
                        (parTypes = ((ParameterizedType) genType).getActualTypeArguments()).length == 1
                        && parTypes[0] instanceof Class
                        && type.isAssignableFrom(parType = (Class) parTypes[0])) {
                        typeName = GeciReflectionTools.normalizeTypeName(parType.getTypeName(), klass);
                    } else {
                        typeName = GeciReflectionTools.normalizeTypeName(type.getName(), klass);
                    }
                    writeGenerated(segment, config.generatedAnnotation);
                    segment.write_r("public %s %s(%s x) {", builder, aggMethod, typeName)
                        .write_r("if( %s.this.%s == null ) {", klass.getSimpleName(), name)
                        .write("throw new IllegalArgumentException(\"Collection field %s is null\");", name)
                        .write_l("}")
                        .write("%s.this.%s.%s(x);", klass.getSimpleName(), name, config.aggregatorMethod)
                        .write("return this;")
                        .write_l("}")
                        .newline();
                }
            );
    }


    private void generateSetter(Class<?> klass, Segment segment, String bn, String name, String type) {
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public %s %s(%s %s) {", bn, name, type, name)
            .write("%s.this.%s = %s;", klass.getSimpleName(), name, name)
            .write("return this;")
            .write_l("}")
            .newline();
    }

    @Override
    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public %s %s() {", klass.getSimpleName(), local.buildMethod)
            .write("return %s.this;", klass.getSimpleName())
            .write_l("}");
        segment.write_l("}"); // end of builder class
    }

    @Override
    protected String defaultFilterExpression() {
        return config.filter;
    }

    //<editor-fold id="configBuilder" builderName="ConfBuilder">
    public static Builder.ConfBuilder builder() {
        return new Builder().new ConfBuilder();
    }

    public class ConfBuilder {
        public ConfBuilder aggregatorMethod(String aggregatorMethod) {
            config.aggregatorMethod = aggregatorMethod;
            return this;
        }

        public ConfBuilder buildMethod(String buildMethod) {
            config.buildMethod = buildMethod;
            return this;
        }

        public ConfBuilder builderFactoryMethod(String builderFactoryMethod) {
            config.builderFactoryMethod = builderFactoryMethod;
            return this;
        }

        public ConfBuilder builderName(String builderName) {
            config.builderName = builderName;
            return this;
        }

        public ConfBuilder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public ConfBuilder generatedAnnotation(Class generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder build() {
            return Builder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.aggregatorMethod = params.get("aggregatorMethod",config.aggregatorMethod);
        local.buildMethod = params.get("buildMethod",config.buildMethod);
        local.builderFactoryMethod = params.get("builderFactoryMethod",config.builderFactoryMethod);
        local.builderName = params.get("builderName",config.builderName);
        local.filter = params.get("filter",config.filter);
        return local;
    }
    //</editor-fold>
}


