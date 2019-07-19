package javax0.geci.annotationbuilder;

import static javax0.geci.api.Source.Set.set;
import static javax0.geci.tools.CaseTools.ucase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax0.geci.api.GeciException;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

public class AnnotationBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String in = "";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) {
        final var mnemonic = getMnemonic(klass);
        final var keys = getImplementedKeysSorted(klass);
        final var annotation = ucase(mnemonic);

        final var file = source.newSource(set(config.in), annotation + ".java");
        writeContent(file, mnemonic, annotation, keys);
    }

    private List<String> getImplementedKeysSorted(final Class<?> klass) {
        try {
            final var getKeys = GeciReflectionTools.getMethod(klass, "implementedKeys");
            getKeys.setAccessible(true);
            final var keySet = (Set<String>) getKeys.invoke(klass.getConstructor().newInstance());
            final var keyList = new ArrayList<>(keySet);
            keyList.sort(String::compareTo);
            return keyList;
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
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder in(String in) {
            config.in = in;
            return this;
        }

        public AnnotationBuilder build() {
            return AnnotationBuilder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.in = params.get("in",config.in);
        return local;
    }
    //</editor-fold>

    //<editor-fold id="annotationBuilder" />
}
