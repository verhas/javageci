package javax0.geci.annotationbuilder;

import static javax0.geci.tools.CaseTools.ucase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import javax0.geci.annotations.Geci;
import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

@Geci("annotationBuilder")
public class AnnotationBuilder extends AbstractJavaGenerator {

    private static class Config {
        private String in = "";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try {

            final String mnemonic = getMnemonic(klass);
            final ArrayList<String> keys = getImplementedKeys(klass);
            final String annotation = ucase(mnemonic);
            final var in = config.in;

            final var newSource = source.newSource(Source.Set.set(in), annotation + ".java");
            final var content = createContent(newSource, mnemonic, annotation, keys);
            final Segment annotationFile = newSource.open();
            annotationFile.write(content);
        } catch (NoSuchMethodException ex) {
            throw new GeciException(
                "Cannot generate annotation for " + klass.getName() + " because it does not have a mnemonic or implementedKeys method.");
        }
    }

    private ArrayList<String> getImplementedKeys(final Class<?> klass)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Method implementedKeysMethod = GeciReflectionTools.getMethod(klass, "implementedKeys");
        implementedKeysMethod.setAccessible(true);
        final var unorderedKeySet = (Set<String>) implementedKeysMethod.invoke(klass.getConstructor().newInstance());
        final var implementedKeys = new ArrayList<>(unorderedKeySet);
        implementedKeys.sort(String::compareTo);
        return implementedKeys;
    }

    private String getMnemonic(Class<?> klass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Method mnemonicMethod = GeciReflectionTools.getMethod(klass, "mnemonic");
        return (String) mnemonicMethod.invoke(klass.getConstructor().newInstance());
    }

    private Segment createContent(Source file, String mnemonic, String annotation, Iterable<String> keys) {
        Segment content = new javax0.geci.engine.Segment(0);
        content.write("package %s;", file.getPackageName());
        content.newline();
        content.write("import java.lang.annotation.Retention;");
        content.write("import java.lang.annotation.RetentionPolicy;");
        content.write("import javax0.geci.annotations.Geci;");
        content.newline();
        content.write("@Geci(\"%s\")", mnemonic);
        content.write("@Retention(RetentionPolicy.RUNTIME)");
        content.write_r("public @interface %s {", annotation);
        content.write(parameterMethod("value"));
        for(final var key : keys) {
            if(!key.equals("id")) {
                content.write(parameterMethod(key));
            }
        }
        content.write_l("}");
        return content;
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
}
