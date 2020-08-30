package javax0.geci.annotationbuilder;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CaseTools;
import javax0.geci.tools.CompoundParams;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@javax0.geci.core.annotations.AnnotationBuilder
public class AnnotationBuilder extends AbstractJavaGenerator {

    private static class Config {
        /**
         * - config
         * <p>
         * * `set='name-of-the-source-set'`
         * <p>
         * By default the annotations are generated into the same source set where the target generator is.
         * In case of a multi-module project you may want to separate the annotations from the generators into a different module.
         * The reason for that can be that the generators are test scope dependencies.
         * On the other hand the annotations, albeit not used during run-time are compile scope dependencies.
         * That is because these annotations have run-time retention and are put into the JVM byte code by the compiler.
         * Even though they are not used during non-test run-time, they are there and thus the JAR defining them must be on the class/module path.
         * <p>
         * Use this configuration either calling `set(""name-of-source-set")` in the test code when building the
         * annotation builder generator or `@AnnotationBuilder(module="name-of-source-set")` on the generator class to
         * define the name of the source set where the annotation will be generated.
         * <p>
         * Since the source set is defined in the test code it is reasonable to configure this parameter via the builder interface of the generator.
         */
        private String set = "";
        /**
         * -
         * <p>
         * * `in='name.of.package'`
         * <p>
         * This parameter can define the name of the package where the annotations will be created.
         * <p>
         * Use `@AnnotationBuilder(in="name.of.package")` to generate the annotation in a different package.
         * You can specify an absolute package with the full name of the package (e.g.: `com.example.package`).
         * Alternatively, you can specify a package that is relative to the package of the target generator starting the configuration value with a dot.
         * For example, the default value for this parameter is `.annotation` that will direct the annotation builder to generate the annotation in the subpackage `annotation` right below the target generator.
         * <p>
         * Using empty string, or only a `.` will generate the annotation to the same package where the generator is.
         * Note, however, when you separate the annotations from the generators to different modules the different modules are not allowed to define classes in the same package. The Java module system will not load such modules.
         */
        private String in = ".annotation";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) {
        if (Generator.class.isAssignableFrom(klass)) {
            final var local = localConfig(global);
            final var mnemonic = getMnemonic(klass);
            final var annotation = CaseTools.ucase(mnemonic);

            final String directory = getPackageDirectory(local);

            final var newSource = source.newSource(Source.Set.set(local.set), directory + "/" + annotation + ".java");
            writeContent(newSource, mnemonic, annotation, getImplementedKeysSorted(klass));
        }
    }

    private String getPackageDirectory(Config local) {
        if (local.in.isEmpty()) {
            return "";
        } else {
            final boolean packageIsAbsolute = !local.in.startsWith(".");
            return (packageIsAbsolute ? "/" : "") +
                local.in.substring(packageIsAbsolute ? 0 : 1).replaceAll("\\.", "/");
        }
    }

    private List<String> getImplementedKeysSorted(final Class<?> klass) {
        try {
            final var implementedKeys =  ((AbstractJavaGenerator) klass.getConstructor().newInstance()).implementedKeys();
            if( implementedKeys == null ){
                return Collections.emptyList();
            }
            return implementedKeys.stream().sorted().collect(Collectors.toList());
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

    private void writeContent(Source source, String mnemonic, String annotation, List<String> keys) {
        try (final var segment = source.open()) {
            segment.write("package %s;", source.getPackageName())
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
            keys.stream().filter(key -> !key.equals("id")).map(this::parameterMethod).forEach(segment::write);
            segment.write_l("}");
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

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "in",
        "set",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder implements javax0.geci.api.GeneratorBuilder {
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
        local.in = params.get("in", config.in);
        local.set = params.get("set", config.set);
        return local;
    }
    //</editor-fold>
}
