package javax0.geci.annotationbuilder;

import static javax0.geci.tools.CaseTools.ucase;

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
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        try {
            final var implementedKeysMethod = GeciReflectionTools.getMethod(klass, "implementedKeys");
            final var mnemonicMethod = GeciReflectionTools.getMethod(klass, "mnemonic");

            final var mnemonic = (String) mnemonicMethod.invoke(klass.getConstructor().newInstance());
            implementedKeysMethod.setAccessible(true);
            final var implementedKeys = (Set<String>) implementedKeysMethod.invoke(klass.getConstructor().newInstance());

            final var in = global.get("in","annotation");
            final String annotationName = ucase(mnemonic);
            final var newSource = source.newSource(in + "\\" + annotationName + ".java");
            final var annotationFile = newSource.open();

            //Package declaration
            annotationFile.write("package %s;", newSource.getPackageName());
            annotationFile.newline();
            //Import statements
            annotationFile.write("import java.lang.annotation.Retention;");
            annotationFile.write("import java.lang.annotation.RetentionPolicy;");
            annotationFile.write("import javax0.geci.annotations.Geci;");
            annotationFile.newline();
            //Write annotations
            annotationFile.write("@Geci(\"%s\")", mnemonic);
            annotationFile.write("@Retention(RetentionPolicy.RUNTIME)");
            //Interface declaration
            annotationFile.write_r("public @interface %s {", annotationName);
            //value() method declaration
            annotationFile.write(parameterMethod("value"));
            //implementedKeys method declarations
            for(final var key : implementedKeys) {
                annotationFile.write(parameterMethod(key));
            }
            annotationFile.write_l("}");
            annotationFile.close();
        } catch (NoSuchMethodException ex) {
            throw new GeciException("Cannot generate annotation for " + klass.getName() + " because it does not have a mnemonic or implementedKeys method.");
        }
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
