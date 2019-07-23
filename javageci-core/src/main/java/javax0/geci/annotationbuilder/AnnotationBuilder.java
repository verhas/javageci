package javax0.geci.annotationbuilder;

import static javax0.geci.tools.CaseTools.ucase;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import javax0.geci.annotations.Geci;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;

@Geci("annotationBuilder")
public class AnnotationBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String module = "";
        private String in = "";
        private String maven = "true";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) {
        final var mnemonic = getMnemonic(klass);
        final var keys = getImplementedKeysSorted(klass);
        final var annotation = ucase(mnemonic);
        if(global.is("maven", config.maven)) {
            final var module = global.get("module", config.module);
            final var in = normalizePackage(global.get("in", config.in));
            final var directory = Source.maven().module(module).mainSource().getDirectories()[0];
            final var file = source.newSource(directory, in + annotation + ".java");
            writeContent(file, mnemonic, annotation, keys);
        }

    }

    private String normalizePackage(String in) {
        var normalized = in;
        if(!in.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if(!in.endsWith("/")) {
            normalized = normalized + "/";
        }
        normalized = normalized.replaceAll("[.]", "/");
        return normalized;
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
        "in",
        "maven",
        "module",
        "id"
    );

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder in(String in) {
            config.in = in;
            return this;
        }

        public Builder maven(String maven) {
            config.maven = maven;
            return this;
        }

        public Builder module(String module) {
            config.module = module;
            return this;
        }

        public AnnotationBuilder build() {
            return AnnotationBuilder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.in = params.get("in",config.in);
        local.maven = params.get("maven",config.maven);
        local.module = params.get("module",config.module);
        return local;
    }
    //</editor-fold>

    //<editor-fold id="annotationBuilder" />
}
