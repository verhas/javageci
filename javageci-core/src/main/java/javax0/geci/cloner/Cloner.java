package javax0.geci.cloner;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static javax0.geci.tools.CaseTools.ucase;

public class Cloner extends AbstractFilteredFieldsGenerator {

    public Cloner() {
        declaredOnly = false;
    }

    private class Config {
        private Class<? extends Annotation> generatedAnnotation = Generated.class;
        private String filter = "!static & !final";
        private String cloneMethod = "copy";
        private String cloneMethodProtection = "public";
        private String copyMethod = "copy";
        private String superCopyMethod = null;
        private String copyMethodProtection = "protected";
        private String cloneWith = "true";
        private String copyCallsSuper = "false";
        // note that this variable is never ever used. It is here to trigger code generation only. The setter is used
        // and the setter sets the code generator class 'declaredOnly' field
        private final boolean declaredOnly = false;

        private void setDeclaredOnly(boolean b) {
            Cloner.this.declaredOnly = b;
        }

        private String getSuperCopyMethod() {
            return superCopyMethod != null && superCopyMethod.length() != 0 ?
                superCopyMethod : copyMethod;
        }

    }

    @Override
    protected String defaultFilterExpression() {
        return config.filter;
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var log = source.getLogger();
        final var local = localConfig(global);
        Field[] fields = Arrays.stream(GeciReflectionTools.getAllFieldsSorted(klass))
            .filter(field -> !Modifier.isFinal(field.getModifiers()))
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .toArray(Field[]::new);
        writeGenerated(segment, config.generatedAnnotation);
        final var fullyQualified = GeciReflectionTools.getSimpleGenericClassName(klass);
        log.info("Creating %s %s %s()",local.cloneMethodProtection, fullyQualified, local.cloneMethod);
        segment.write_r("%s %s %s() {", local.cloneMethodProtection, fullyQualified, local.cloneMethod);
        segment.write("final var it = new %s();", klass.getSimpleName())
            .write("%s(it);",local.copyMethod)
            .write("return it;")
            .write_l("}");

        log.info("Creating %s void %s(%s it)",local.copyMethodProtection, local.copyMethod, fullyQualified);
        segment.write_r("%s void %s(%s it) {", local.copyMethodProtection, local.copyMethod, fullyQualified);
        if (CompoundParams.toBoolean(local.copyCallsSuper)) {
            segment.write("super.%s(it);", local.getSuperCopyMethod());
        }
        segment.newline();

        for (final var field : fields) {
            segment.write("it.%s = %s;", field.getName(), field.getName());
        }
        segment.write_l("}").newline();
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var local = localConfig(params);
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName(), klass);
        final var fullyQualified = GeciReflectionTools.getSimpleGenericClassName(klass);
        segment.write_r("%s with%s(%s %s) {", fullyQualified, ucase(name), type, name);
        if (CompoundParams.toBoolean(local.cloneWith)) {
            segment.write("final var it = %s();", local.cloneMethod)
                .write("it.%s = %s;", name, name)
                .write("return it;");
        } else {
            segment.write("this.%s = %s;", name, name)
                .write("return this;");
        }
        segment.write_l("}")
            .newline();
    }

    //<editor-fold id="configBuilder" configurableMnemonic="cloner">
    private String configuredMnemonic = "cloner";

    @Override
    public String mnemonic(){
        return configuredMnemonic;
    }

    private final Config config = new Config();
    public static Cloner.Builder builder() {
        return new Cloner().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "cloneMethod",
        "cloneMethodProtection",
        "cloneWith",
        "copyCallsSuper",
        "copyMethod",
        "copyMethodProtection",
        "filter",
        "superCopyMethod",
        "id"
    );

    @Override
    protected java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder {
        public Builder cloneMethod(String cloneMethod) {
            config.cloneMethod = cloneMethod;
            return this;
        }

        public Builder cloneMethodProtection(String cloneMethodProtection) {
            config.cloneMethodProtection = cloneMethodProtection;
            return this;
        }

        public Builder cloneWith(String cloneWith) {
            config.cloneWith = cloneWith;
            return this;
        }

        public Builder copyCallsSuper(String copyCallsSuper) {
            config.copyCallsSuper = copyCallsSuper;
            return this;
        }

        public Builder copyMethod(String copyMethod) {
            config.copyMethod = copyMethod;
            return this;
        }

        public Builder copyMethodProtection(String copyMethodProtection) {
            config.copyMethodProtection = copyMethodProtection;
            return this;
        }

        public Builder declaredOnly(boolean declaredOnly) {
            config.setDeclaredOnly(declaredOnly);
            return this;
        }

        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Builder generatedAnnotation(Class<? extends java.lang.annotation.Annotation> generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public Builder superCopyMethod(String superCopyMethod) {
            config.superCopyMethod = superCopyMethod;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            configuredMnemonic = mnemonic;
            return this;
        }

        public Cloner build() {
            return Cloner.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.cloneMethod = params.get("cloneMethod",config.cloneMethod);
        local.cloneMethodProtection = params.get("cloneMethodProtection",config.cloneMethodProtection);
        local.cloneWith = params.get("cloneWith",config.cloneWith);
        local.copyCallsSuper = params.get("copyCallsSuper",config.copyCallsSuper);
        local.copyMethod = params.get("copyMethod",config.copyMethod);
        local.copyMethodProtection = params.get("copyMethodProtection",config.copyMethodProtection);
        local.setDeclaredOnly(config.declaredOnly);
        local.filter = params.get("filter",config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.superCopyMethod = params.get("superCopyMethod",config.superCopyMethod);
        return local;
    }
    //</editor-fold>
}
