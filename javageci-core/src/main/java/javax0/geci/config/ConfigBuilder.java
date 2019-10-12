package javax0.geci.config;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CaseTools;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javax0.geci.api.CompoundParams.toBoolean;

/**
 * Generator that generates the code for a generator class to handle the configuration.
 * <p>
 * To use this generator the class has to contain a {@code private static class} named {@code Config}.
 */
@AnnotationBuilder
public class ConfigBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String filter = "private & !static";
        private String builderName = "Builder";
        private String builderFactoryMethod = "builder";
        private String buildMethod = "build";
        private String configAccess = "private";
        private String generateImplementedKeys = "true";
        private String localConfigMethod = "localConfig";
        private String configurableMnemonic = "";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final Class<?> configClass;
        try {
            configClass = Class.forName(klass.getName() + "$Config");
        } catch (ClassNotFoundException cnfe) {
            throw new GeciException("There is no class 'Config' in " + klass.getName(), cnfe);
        }
        try (final var segment = source.open(global.id())) {
            final var allDeclaredFields = Arrays.asList(GeciReflectionTools.getDeclaredFieldsSorted(configClass));
            final var fields = configurableFields(global, allDeclaredFields);

            final var local = localConfig(global);
            segment.param("klass", klass.getSimpleName(),
                "access", local.configAccess,
                "build", local.buildMethod,
                "builder", local.builderFactoryMethod,
                "Builder", local.builderName,
                "localConfig", local.localConfigMethod);
            generateMnemonic(segment, local, klass);
            generateConfigField(segment);
            generateBuilderFactoryMethod(segment, klass);
            if (toBoolean(local.generateImplementedKeys)) {
                generateConfigKeySet(segment, fields);
            }
            startBuilderClass(segment, klass);
            allDeclaredFields.forEach(field -> generateBuilderMethod(segment, klass, configClass, field));
            generateMnemonicConfiguration(segment, local);
            finishBuilderClass(segment);
            if (local.localConfigMethod.length() > 0) {
                generateLocalConfigMethod(segment, allDeclaredFields, fields, configClass);
            }
        }
    }

    private void generateMnemonicConfiguration(Segment segment, Config local) {
        if (mnemonicIsConfigurable(local)) {
            segment.write_r("public {{Builder}} mnemonic(String mnemonic) {")
                    .write("configuredMnemonic = mnemonic;")
                    .write("return this;")
                    .write_l("}")
                    .newline();
        }
    }

    private void generateMnemonic(Segment segment, Config local, Class klass) {
        if (mnemonicIsConfigurable(local)) {
            segment.write("private String configuredMnemonic = \"%s\";", local.configurableMnemonic)
                    .newline();
            try {
                klass.getSuperclass().getMethod("mnemonic");
                segment.write("@Override");
            } catch (NoSuchMethodException ignored) {
            }
            segment.write_r("public String mnemonic(){")
                    .write("return configuredMnemonic;")
                    .write_l("}")
                    .newline();
        }
    }

    private boolean mnemonicIsConfigurable(Config local) {
        return local.configurableMnemonic != null && local.configurableMnemonic.length() > 0;
    }

    private void generateConfigField(Segment segment) {
        segment.write("{{access}} final Config config = new Config();");
    }

    private void generateConfigKeySet(Segment segment, List<Field> fields) {
        segment.write_r("private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(");
        fields.forEach(field -> {
            final var name = field.getName();
            segment.write("\"%s\",", name);
        });
        segment.write("\"id\"")
                .write_l("));")
                .newline();
        segment.write("@Override")
                .write_r("public java.util.Set<String> implementedKeys() {")
                .write("return implementedKeys;")
                .write_l("}");
    }

    private void generateLocalConfigMethod(Segment segment, List<Field> allDeclaredFields, List<Field> fields, Class<?> configClass) {
        segment.write_r("private Config {{localConfig}}(CompoundParams params){")
                .write("final var local = new Config();");
        for (final var field : allDeclaredFields) {
            final var name = field.getName();
            final var setterName = "set" + CaseTools.ucase(name);
            segment.param("name", name,
                    "setterName", setterName);
            final var hasSetter = doesTheFieldHaveSetter(configClass, field, setterName);
            if (fields.contains(field)) {
                if (hasSetter) {
                    segment.write("local.{{setterName}}(params.get(\"{{name}}\", config.{{name}}));");
                } else {
                    segment.write("local.{{name}} = params.get(\"{{name}}\", config.{{name}});");
                }
            } else {
                if (hasSetter) {
                    segment.write("local.{{setterName}}(config.{{name}});");
                } else {
                    if (!Modifier.isFinal(field.getModifiers())) {
                        segment.write("local.{{name}} = config.{{name}};");
                    }
                }
            }
        }
        segment.write("return local;")
                .write_l("}");
    }

    private void finishBuilderClass(Segment segment) {
        segment.write_r("public {{klass}} {{build}}() {")
                .write("return {{klass}}.this;")
                .write_l("}");
        segment.write_l("}");

    }

    private void generateBuilderMethod(Segment segment, Class<?> klass, Class<?> configClass, Field field) {
        final var name = field.getName();
        final var type = GeciReflectionTools.getGenericTypeName(field.getGenericType());
        final var setterName = "set" + CaseTools.ucase(name);
        segment.param("name", name,
                "setter", setterName,
                "type", type);
        final var hasSetter = doesTheFieldHaveSetter(configClass, field, setterName);
        if (!Modifier.isFinal(field.getModifiers()) || hasSetter) {
            segment.write_r("public {{Builder}} {{name}}({{type}} {{name}}) {");
            if (hasSetter) {
                segment.write("config.{{setter}}({{name}});");
            } else {
                segment.write("config.{{name}} = {{name}};");
            }
            segment.write("return this;")
                    .write_l("}")
                    .newline();
        }
    }

    private static boolean doesTheFieldHaveSetter(Class<?> configClass, Field field, String setterName) {
        try {
            GeciReflectionTools.getMethod(configClass, setterName, field.getType());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private void startBuilderClass(Segment segment, Class klass) {
        try {
            final var superBuilder = Class.forName(klass.getSuperclass().getName() + "$Builder");
            segment.write_r("public class {{Builder}} extends %s implements javax0.geci.api.GeneratorBuilder {", superBuilder.getCanonicalName());
        } catch (ClassNotFoundException cnfe) {
            segment.write_r("public class {{Builder}} implements javax0.geci.api.GeneratorBuilder {");
        }
    }

    private void generateBuilderFactoryMethod(Segment segment, Class klass) {
        if (!Modifier.isAbstract(klass.getModifiers())) {
            segment.write_r("public static {{klass}}.{{Builder}} {{builder}}() {")
                    .write("return new {{klass}}().new {{Builder}}();")
                    .write_l("}")
                    .newline();
        }
    }

    /**
     * Get the fields that can be configured on the Geci annotation.
     * They can be configured if they match the filter (by default it is
     * {@code "private & !static"} and are `String` type and not final.
     *
     * @param params            the global parameters that may configure
     *                          the parameter {@code filter}
     * @param allDeclaredFields all declared fields of the configuration
     *                          class
     * @return the fields that can be locally configured
     */
    private List<Field> configurableFields(CompoundParams params, List<Field> allDeclaredFields) {
        return allDeclaredFields.stream().filter(
                field -> {
                    var local = localConfig(
                            new CompoundParams(
                                    GeciReflectionTools.getParameters(field, mnemonic()
                                    ),
                                    params)
                    );
                    return Selector.compile(local.filter).match(field)
                            &&
                            !Modifier.isFinal(field.getModifiers())
                            &&
                            field.getType().equals(String.class);
                }
        ).collect(Collectors.toList());
    }

    @Override
    public String mnemonic() {
        return "configBuilder";
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static ConfigBuilder.Builder builder() {
        return new ConfigBuilder().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "buildMethod",
        "builderFactoryMethod",
        "builderName",
        "configAccess",
        "configurableMnemonic",
        "filter",
        "generateImplementedKeys",
        "localConfigMethod",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder implements javax0.geci.api.GeneratorBuilder {
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

        public Builder configAccess(String configAccess) {
            config.configAccess = configAccess;
            return this;
        }

        public Builder configurableMnemonic(String configurableMnemonic) {
            config.configurableMnemonic = configurableMnemonic;
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder generateImplementedKeys(String generateImplementedKeys) {
            config.generateImplementedKeys = generateImplementedKeys;
            return this;
        }

        public Builder localConfigMethod(String localConfigMethod) {
            config.localConfigMethod = localConfigMethod;
            return this;
        }

        public ConfigBuilder build() {
            return ConfigBuilder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.buildMethod = params.get("buildMethod", config.buildMethod);
        local.builderFactoryMethod = params.get("builderFactoryMethod", config.builderFactoryMethod);
        local.builderName = params.get("builderName", config.builderName);
        local.configAccess = params.get("configAccess", config.configAccess);
        local.configurableMnemonic = params.get("configurableMnemonic", config.configurableMnemonic);
        local.filter = params.get("filter", config.filter);
        local.generateImplementedKeys = params.get("generateImplementedKeys", config.generateImplementedKeys);
        local.localConfigMethod = params.get("localConfigMethod", config.localConfigMethod);
        return local;
    }
    //</editor-fold>
}
