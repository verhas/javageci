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

@Geci("annotationBuilder absolute='yes'")
public class AnnotationBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String module = "";
        private String in = "annotation";
        private String absolute = "no";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) {
        final var mnemonic = getMnemonic(klass);
        final var keys = getImplementedKeysSorted(klass);
        final var annotation = ucase(mnemonic);
        final var module = global.get("module", config.module);
        final var directory = module.equals("") ? "" : "../" + Source.maven().module(module).mainSource().getDirectories()[0];

        final var isRelative = global.is("absolute");
        String in = global.get("in", config.in);
        if(isRelative) {
            in = "/" + in;
        }
        in = normalizePackage(in);

        final var file = source.newSource(directory, in + annotation + ".java");
        writeContent(file, mnemonic, annotation, keys);
    }

    private String normalizePackage(String in) {
        if("".equals(in)) return in;
        var normalized = in;
        if(!normalized.endsWith("/")) {
            normalized = normalized + "/";
        }
        normalized = normalized.replaceAll("[.]", "/");
        normalized = normalized.replaceAll("//", "/");
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
        "absolute",
        "in",
        "module",
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
        local.absolute = params.get("absolute",config.absolute);
        local.in = params.get("in",config.in);
        local.module = params.get("module",config.module);
        return local;
    }
    //</editor-fold>

    //<editor-fold id="annotationBuilder" />
}
