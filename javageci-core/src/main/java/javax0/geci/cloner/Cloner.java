package javax0.geci.cloner;

import javax0.geci.annotations.Generated;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static javax0.geci.api.CompoundParams.toBoolean;
import static javax0.geci.tools.CaseTools.ucase;

/**
 * //snippet Cloner_head
 * This code generator will generate a `copy()` method that returns the clone of the object referenced by `this` in the class body and also methods named `withXyzAbc()` for every `xyzAbc` field.
 * For example the sample class `NeedCloner` has the fields
 * //end snippet
 */
@AnnotationBuilder
public class Cloner extends AbstractFilteredFieldsGenerator {

    public Cloner() {
        declaredOnly = false;
    }

    private class Config {
        // this documentation is markdown and is copied automatically
        // during build time to the file CLONER.md

        /* snippet Cloner_Config_001
        ### `generatedAnnotation = javax0.geci.annotations.Generated.class`

        This builder-only parameter defines the class that is used to annotate the generated classes.
        By default the Geci generated annotation class is used as shown above.
        If this parameter is set to `null` in the builder then there will be no `Generated` annotation placed on the class.

        end snippet */
        private Class<? extends Annotation> generatedAnnotation = Generated.class;

        // snippet Cloner_Config_002
        private String filter = "!static & !final";
        /*
        The filter expression that selects the fields that are to be used during the cloning process.
        By default all the fields that are not static and are not final are copied to the new object.
        Static fields are shared by the different instances and thus any copy operation would just copy the value from a field back to the same field.
        Final fields can not be written after the constructor has finished it's work, therefore they cannot be altered and copied.

        The set of the fields is also controlled by the other option `declaredOnly` (see later).

        end snippet */

        // snippet Cloner_Config_003
        private String cloneMethod = "copy";
        /*
        The name of the method that creates a clone of the object.
        The default name is `copy()`.
        Although it seems to be reasonable to name this method `clone()` it is recommended not to.
        There is a `clone()` method defined in the class `Object` and this name collisions will cause obnoxious inconveniences.

        end snippet */

        // snippet Cloner_Config_004
        private String cloneMethodProtection = "public";
        /*
        The protection of the copy method.
        end snippet */

        // snippet Cloner_Config_005
        private String copyMethod = "copy";
        /*
        The name of the generated method that copies the values of the fields.

        end snippet */

        // snippet Cloner_Config_006
        private String superCopyMethod = null;
        /*
        The name of the copy method of the parent class that the copy method calls.
        This has only significance if `copyCallsCuper` is `true`.
        There is no check that the parent has this method or not.
        It may happen that the parent does not have this method at the time of code generation because the code generation creates this method.

        If the value is `null` or empty string then the configured value for `copyMethod` is used.
        end snippet */

        // snippet Cloner_Config_007
        private String copyMethodProtection = "protected";
        /*
        The protection of the generated copy method.

        end snippet */

        // snippet Cloner_Config_008
        private String cloneWith = "true";
        /*
        The `withXXX()` methods calls `copy()` to make a fresh copy of the object before setting the field.
        In case you want to use the generated code where usually many fields are altered for the new object then it is better to set this configuration value to `false`.
        In this case the `withXXX()` methods alter the actual object referenced by `this`.
        When the cloning code was generated that way then call `copy()` explicitly on the original object before calling `withXXX()` methods presumably chained for many different fields.

        end snippet */

        // snippet Cloner_Config_009
        private String copyCallsSuper = "false";
        /*
        Set this configuration value to `true` to make the copying method call the same method in the parent class through the reference `super`.

        end snippet */

        // note that this variable is never ever used. It is here to trigger code generation only. The setter is used
        // and the setter sets the code generator class 'declaredOnly' field
        private final boolean declaredOnly = false;

        /* snippet Cloner_Config_010
        ### `declaredOnly = null`

        When you work with class hierarchy where each class in the inheritance chain then specifying this configuration parameter to true will limit the working to use only the fields that are declared in the actual class.

        end snippet */
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
        if (toBoolean(local.copyCallsSuper)) {
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
        if (toBoolean(local.cloneWith)) {
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

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "cloneMethod",
        "cloneMethodProtection",
        "cloneWith",
        "copyCallsSuper",
        "copyMethod",
        "copyMethodProtection",
        "filter",
        "superCopyMethod",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder implements javax0.geci.api.GeneratorBuilder {
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
        local.cloneMethod = params.get("cloneMethod", config.cloneMethod);
        local.cloneMethodProtection = params.get("cloneMethodProtection", config.cloneMethodProtection);
        local.cloneWith = params.get("cloneWith", config.cloneWith);
        local.copyCallsSuper = params.get("copyCallsSuper", config.copyCallsSuper);
        local.copyMethod = params.get("copyMethod", config.copyMethod);
        local.copyMethodProtection = params.get("copyMethodProtection", config.copyMethodProtection);
        local.setDeclaredOnly(config.declaredOnly);
        local.filter = params.get("filter", config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.superCopyMethod = params.get("superCopyMethod", config.superCopyMethod);
        return local;
    }
    //</editor-fold>
}
