package javax0.geci.annotationbuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CaseTools;
import javax0.geci.tools.CompoundParams;

@javax0.geci.core.annotations.AnnotationBuilder(absolute = "yes")
public class AnnotationBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String set = "";
        private String in = "annotation";
        private String absolute = "no";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) {
        if(Generator.class.isAssignableFrom(klass)) {
            final var local = localConfig(global);
            final var mnemonic = getMnemonic(klass);
            final var keys = getImplementedKeysSorted(klass);
            final var annotation = CaseTools.ucase(mnemonic);

            boolean isAbsolute = CompoundParams.toBoolean(local.absolute);
            if (isAbsolute) {
                local.in = "/" + local.in.replaceAll("[.]", "/");
            }

            if (!local.in.isEmpty() || isAbsolute) {
                final var file = source.newSource(Source.Set.set(local.set), local.in + "/" + annotation + ".java");
                writeContent(file, mnemonic, annotation, keys);
            }
        }
    }

    private List<String> getImplementedKeysSorted(final Class<?> klass) {
        try {
            return ((AbstractJavaGenerator) klass.getConstructor().newInstance()).implementedKeys().stream().sorted().collect(Collectors.toList());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            throw new GeciException("Cannot generate annotation for " + klass.getName() + " because it does not have an implementedKeys() method.", ex);
        }
    }

    private String getMnemonic(Class<?> klass) {
        try {
            return ((AbstractJavaGenerator) klass.getConstructor().newInstance()).mnemonic();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new GeciException("Cannot generate annotation for " + klass.getName() + " because it does not have a mnemonic() method.", ex);
        }
    }

    private void writeContent(Source file, String mnemonic, String annotation, List<String> keys) {
        final var content = file.open();
        content.write("package %s;", file.getPackageName())
        .newline()
        .write("import java.lang.annotation.Retention;")
        .write("import java.lang.annotation.RetentionPolicy;")
        .write("import javax0.geci.annotations.Geci;")
        .newline()
        .write("@Geci(\"%s\")", mnemonic)
        .write("@Retention(RetentionPolicy.RUNTIME)")
        .write_r("public @interface %s {", annotation)
        .newline()
        .write(parameterMethod("value"));
        keys.stream().filter(key -> !key.equals("id")).map(this::parameterMethod).forEach(content::write);
        content.write_l("}");
    }

    private String parameterMethod(String param) {
        return "String " + param + "() default \"\";";
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static AnnotationBuilder.Builder builder() {
        return new AnnotationBuilder().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "absolute",
        "in",
        "set",
        "id"
    );

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder absolute(String absolute) {
            config.absolute = absolute;
            return this;
        }

        public Builder in(String in) {
            config.in = in;
            return this;
        }

        public Builder set(String set) {
            config.set = set;
            return this;
        }

        public AnnotationBuilder build() {
            return AnnotationBuilder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.absolute = params.get("absolute",config.absolute);
        local.in = params.get("in",config.in);
        local.set = params.get("set",config.set);
        return local;
    }
    //</editor-fold>

    //<editor-fold id="annotationBuilder" />
}
