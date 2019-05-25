package javax0.geci.config;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.reflection.Selector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Predicate;


public class ConfigBuilder extends AbstractJavaGenerator {

    private static class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String filter = "private & !static & !final";
        private String builderName = "Builder";
        private String builderFactoryMethod = "builder";
        private String buildMethod = "build";
        private String aggregatorMethod = "add";
    }

    private final Config config = new Config();

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.filter = params.get("filter", config.filter);
        return local;
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var configClass = Class.forName(klass.getName() + ".Config");
        final Predicate<Field> predicate = (Field field) -> Selector.compile(config.filter).match(field);
        final var segment = source.open(global.id());
        segment.write_r("private Config localConfig(CompoundParams params){");
        Arrays.stream(configClass.getDeclaredFields()).filter(predicate).forEach(
            field -> segment.write("local.%s = params.get(\"%s\",config.%s);", field.getName(), field.getName(), field.getName())
        );
        segment.write_l("}");
    }

    @Override
    public String mnemonic() {
        return "configBuilder";
    }

    //<editor-fold id="configBuilder">
    //</editor-fold>
}
