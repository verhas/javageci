package javax0.geci.config;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generator that generates the code for a generator class to handle the configuration.
 * <p>
 * To use this generator the class has to contain a {@code private static class} named {@code Config}.
 */
public class ConfigBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String filter = "private & !static & !final";
        private String builderName = "Builder";
        private String builderFactoryMethod = "builder";
        private String buildMethod = "build";
    }

    private final Config config = new Config();

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var configClass = Class.forName(klass.getName() + "$Config");
        final var segment = source.open(global.id());
        final var allDeclaredFields = Arrays.asList(GeciReflectionTools.getDeclaredFieldsSorted(configClass));
        final var fields = collectConfigurableFields(global, allDeclaredFields);

        final var local = localConfig(global);
        generateBuilderFactoryMethod(klass, segment, local);
        generateConfigKeySet(segment, fields);
        startBuilderClass(segment, local);
        allDeclaredFields.forEach(field -> generateBuilderMethod(klass, segment, local, field));
        finishBuilderClass(klass, segment, local);
        generateLocalConfigMethod(segment, fields);
    }

    private void generateConfigKeySet(Segment segment, List<Field> fields) {
        segment.write_r("private static final java.util.Set<String> implementedKeys = java.util.Set.of(");
        fields.forEach(field -> {
            final var name = field.getName();
            segment.write("\"%s\",", name);
        });
        segment.write("\"id\"")
                .write_l(");")
        .newline();
        segment.write_l("@Override")
                .write_r("protected java.util.Set<String> implementedKeys() {")
                .write("return implementedKeys;")
                .write_l("}");
    }

    private void generateLocalConfigMethod(Segment segment, List<Field> fields) {
        segment.write_r("private Config localConfig(CompoundParams params){")
                .write("final var local = new Config();");
        fields.forEach(field -> {
            final var name = field.getName();
            segment.write("local.%s = params.get(\"%s\",config.%s);", name, name, name);
        });
        segment.write("return local;")
                .write_l("}");
    }

    private void finishBuilderClass(Class<?> klass, Segment segment, Config local) {
        segment.write_r("public %s %s() {", klass.getSimpleName(), local.buildMethod)
                .write("return %s.this;", klass.getSimpleName())
                .write_l("}");
        segment.write_l("}");

    }

    private void generateBuilderMethod(Class<?> klass, Segment segment, Config local, Field field) {
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName(), klass);
        if (!Modifier.isFinal(field.getModifiers())) {
            segment.write_r("public %s %s(%s %s) {", local.builderName, name, type, name)
                    .write("config.%s = %s;", name, name)
                    .write("return this;")
                    .write_l("}")
                    .newline();
        }
    }

    private void startBuilderClass(Segment segment, Config local) {
        segment.write_r("public class %s {", local.builderName);
    }

    private void generateBuilderFactoryMethod(Class<?> klass, Segment segment, Config local) {
        segment.write_r("public static %s.%s %s() {", klass.getSimpleName(), local.builderName, local.builderFactoryMethod)
                .write("return new %s().new %s();", klass.getSimpleName(), local.builderName)
                .write_l("}")
                .newline();
    }

    /**
     * Get the fields that can be configured on the Geci annotation.
     *
     * @param params            the global parameters that may configure the parameter {@code filter}
     * @param allDeclaredFields all declared fields of the configuration class
     * @return the fields that can be locally configured
     */
    private List<Field> collectConfigurableFields(CompoundParams params, List<Field> allDeclaredFields) {
        return allDeclaredFields.stream().filter(
                field -> {
                    var l = localConfig(new CompoundParams(GeciReflectionTools.getParameters(field, mnemonic()), params));
                    return Selector.compile(l.filter).match(field) && field.getType().equals(String.class);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public String mnemonic() {
        return "configBuilder";
    }

    //<editor-fold id="configBuilder">
    public static ConfigBuilder.Builder builder() {
        return new ConfigBuilder().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "buildMethod",
        "builderFactoryMethod",
        "builderName",
        "filter",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder buildMethod(String buildMethod) {
            config.buildMethod = buildMethod;
            return this;
        }

        public Builder builderFactoryMethod(String builderFactoryMethod) {
            config.builderFactoryMethod = builderFactoryMethod;
            return this;
        }

        public Builder builderName(String builderName) {
            config.builderName = builderName;
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public ConfigBuilder build() {
            return ConfigBuilder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.buildMethod = params.get("buildMethod",config.buildMethod);
        local.builderFactoryMethod = params.get("builderFactoryMethod",config.builderFactoryMethod);
        local.builderName = params.get("builderName",config.builderName);
        local.filter = params.get("filter",config.filter);
        return local;
    }
    //</editor-fold>
}
