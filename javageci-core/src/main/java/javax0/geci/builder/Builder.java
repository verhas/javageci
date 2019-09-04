package javax0.geci.builder;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CaseTools;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;

import static javax0.geci.api.CompoundParams.toBoolean;

/**
 * - ClassDescription
 *
 * # Builder
 *
 * The builder generator generates an inner class into the target source
 * file. The Builder class has methods to configure the filtered fields
 * of the target class. There is also a static method `builder()`
 * created that returns the builder for the class. The `Builder` inner
 * class also has a method `build()` that returns the built class.
 *
 */
@AnnotationBuilder()
public class Builder extends AbstractFilteredFieldsGenerator {

    /**
     * - Config
     *
     * # Configuration
     */
    private static class Config {
        /**
         * -
         *
         * * `{{configVariableName}}` can define the class that will be
         * used to mark the generated methods and classes as generated.
         * By default it is the value if `{{configDefaultValue}}`
         */
        private Class<? extends Annotation> generatedAnnotation = javax0.geci.annotations.Generated.class;

        /**
         * -
         *
         * * `{{configVariableName}}` can define the filter expression for
         * the fields that will be included in the builder. The default
         * is `{{configDefaultValue}}`.
         */
        private String filter = "private & !static & !final";

        /**
         * -
         *
         * * `{{configVariableName}}` can define the name of the inner
         * class that implements the builder functionality. The default
         * value is `{{configDefaultValue}}`.
         */
        private String builderName = "Builder";

        /**
         * -
         *
         * * `{{configVariableName}}` can define the name of the method
         * that generates a new builder class instance. The default
         * value is `{{configDefaultValue}}`.
         */
        private String builderFactoryMethod = "builder";

        /**
         * -
         *
         * * `{{configVariableName}}` can define the name of the method
         * inside the builder class that closes the build chain and
         * returns the built class. The default value is
         * `{{configDefaultValue}}`.
         */
        private String buildMethod = "build";

        /**
         * -
         *
         * * `{{configVariableName}}` can define the name of the
         * aggregator method. The aggregator method is the one that can
         * add a new value to a field that is a collection type. The
         * default value is `{{configDefaultValue}}`.
         */
        private String aggregatorMethod = "add";

        /**
         * -
         *
         * * When an aggregator is generated the generated code checks
         * that the argument is not `null` if `{{configVariableName}}`
         * is "true". The default value is `"{{configDefaultValue}}"`.
         * If this value is configured to be false then this check will
         * be skipped and the generated code will call the underlying
         * aggregator method even when the argument is null.
         */
        private String checkNullInAggregator = "true";

        /**
         * -
         *
         * * `{{configVariableName}}` can define a setter prefix. The
         * default value is `"{{configDefaultValue}}"`. If this value is
         * not null and not an empty string then the name of the setter
         * method will start with this prefix and the name of the field
         * will be added with the first character capitalized.
         */
        private String setterPrefix = "";
    }

    @Override
    public String mnemonic() {
        return "builder";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public static %s.%s %s() {", klass.getSimpleName(), local.builderName, local.builderFactoryMethod)
                .write("return new %s().new %s();", klass.getSimpleName(), local.builderName)
                .write_l("}")
                .newline()
                .write_r("public class %s {", local.builderName);
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var local = localConfig(params);
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName(), klass);
        if (!Modifier.isFinal(field.getModifiers())) {
            generateSetter(klass, segment, local.builderName, name, type, local.setterPrefix);
        }
        if (config.aggregatorMethod != null && config.aggregatorMethod.length() > 0) {
            generateAggregators(klass, segment, local.builderName, name, field, toBoolean(local.checkNullInAggregator));
        }
    }

    /**
     * <p>Generate an aggregator method.</p>
     *
     * <p>if the type of the field is something that is an aggregation
     * of other values, like a {@code List<String>}, which is a list of
     * strings then this code will generate a method that can add a
     * value to the field.</p>
     *
     * <p>To add a value there has to be an aggregarot method name been
     * defined. The default value is {@code add}. In this case the
     * aggregator method for a field with the name {@code fieldName}
     * will be {@code addFieldName()}.</p>
     *
     * <p>To have an aggregator builder method the type of the field has
     * to have at least one aggregator method. In other words it has to
     * have at least one method that has the name configured in the
     * configuration field {@code aggregatorMethod} (default is {@code
     * add}) that has only one parameter.</p>
     *
     * <p>For each such method there will be an {@code
     * addFieldName(XXX)} bulder method generated, where {@code add} at
     * the start will be the name of the aggregator method, the  {@code
     * FieldName} is the actual field name and {@code XXX} is the type
     * of the argument of the generated aggregator method.</p>
     *
     * <p>The type {@code XXX} is calculated the way that it can be the
     * type of the method argument or it can be the parameter type of
     * the field type. The selection algorithm will select the one that
     * is the broader. In case they are not compatible then the
     * algorithm will select the type of the argument of the method and
     * in that case it is up to the aggregator method of the files type
     * how it converts the added value to the type of the field
     * itself.</p>
     *
     * @param klass     the class in which the fields in. It is mainly
     *                  used to normalize the type.
     * @param segment   the segment into which the code will be
     *                  generated.
     * @param builder   the name of the builder class
     * @param name      the name of the aggregator method
     * @param field     the field for which we generate an aggregator
     *                  method
     * @param checkNull if {@code true} the generated code will throw
     *                  {@link IllegalArgumentException} if the value is
     *                  {@code null}
     */
    private void generateAggregators(Class<?> klass, Segment segment, String builder, String name, Field field, boolean checkNull) {
        final var aggMethod = config.aggregatorMethod + CaseTools.ucase(name);
        final var argumentTypesDone = new HashSet<String>();
        Arrays.stream(GeciReflectionTools.getDeclaredMethodsSorted(field.getType()))
                .filter(m -> m.getName().equals(config.aggregatorMethod) && m.getParameterTypes().length == 1)
                .forEach(
                        method -> {
                            final String argumentTypeName = calculateTypeName(klass, field, method.getParameterTypes()[0]);
                            if (argumentTypeName != null && !argumentTypesDone.contains(argumentTypeName)) {
                                argumentTypesDone.add(argumentTypeName);
                                writeGenerated(segment, config.generatedAnnotation);
                                segment.write_r("public %s %s(%s x) {", builder, aggMethod, argumentTypeName);
                                if (checkNull) {
                                    segment.write_r("if( %s.this.%s == null ) {", klass.getSimpleName(), name)
                                            .write("throw new IllegalArgumentException(\"Collection field %s is null\");", name)
                                            .write_l("}");
                                }
                                segment.write("%s.this.%s.%s(x);", klass.getSimpleName(), name, config.aggregatorMethod)
                                        .write("return this;")
                                        .write_l("}")
                                        .newline();
                            }
                        }
                );
    }

    /**
     * <p>Calculate the name for the type of the filed to be used as a
     * method argument declaration. If the type of the field is a
     * generic type then the it has only one type parameter and it is
     * assignable from the type given as the third argument then this
     * type will be used. In other cases the name of the given type will
     * be used directly.</p>
     *
     * <p>The reason for this complex approach is that in case the field
     * type is something line {@code List<CharSequence>} and the type of
     * the aggregator function is {@code String} then the returned type
     * will be {@code CharSequence} and not the more restrictive {@code
     * String}.</p>
     *
     * @param klass in which the code will be used. In case the type is
     *              inside the same class then the package names can be
     *              removed from the start of the the name and
     *              normalization will do that.
     * @param field is the field for which we search the actual type
     * @param type  the type of the argument of the aggregator method
     * @return the final argument type to be used in the builder
     * aggregator.
     */
    private String calculateTypeName(Class<?> klass, Field field, Class<?> type) {
        Type genType;
        Type[] parTypes;
        Class parType;
        if ((genType = field.getGenericType()) instanceof ParameterizedType &&
                (parTypes = ((ParameterizedType) genType).getActualTypeArguments()).length == 1
                && parTypes[0] instanceof Class && type.isAssignableFrom(parType = (Class) parTypes[0])) {
            return GeciReflectionTools.normalizeTypeName(parType.getTypeName(), klass);
        } else {
            return GeciReflectionTools.normalizeTypeName(type.getName(), klass);
        }
    }


    /**
     * <p>Generate a method in the builder class that sets the value of
     * the field. The name of the method is the same as the name of the
     * field. It does not have a {@code set} or {@code with} prefix.</p>
     *
     * @param klass       in which the builder is created
     * @param segment     the segment where the builder code is created
     * @param builderName the name of the builder class
     * @param name        the name of the field
     * @param type        is the type of the field that will also be the type of the setter argument
     */
    private void generateSetter(Class<?> klass,
                                Segment segment,
                                String builderName,
                                String name,
                                String type,
                                String prefix) {
        writeGenerated(segment, config.generatedAnnotation);
        final String setterName;
        if (prefix != null && prefix.length() > 0) {
            setterName = prefix + CaseTools.ucase(name);
        } else {
            setterName = name;
        }
        segment.write_r("public %s %s(%s %s) {", builderName, setterName, type, name)
                .write("%s.this.%s = %s;", klass.getSimpleName(), name, name)
                .write("return this;")
                .write_l("}")
                .newline();
    }

    @Override
    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public %s %s() {", klass.getSimpleName(), local.buildMethod)
                .write("return %s.this;", klass.getSimpleName())
                .write_l("}");
        segment.write_l("}"); // end of builder class
    }

    @Override
    protected String defaultFilterExpression() {
        return config.filter;
    }

    //<editor-fold id="configBuilder" builderName="ConfBuilder">
    private final Config config = new Config();
    public static Builder.ConfBuilder builder() {
        return new Builder().new ConfBuilder();
    }

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "aggregatorMethod",
        "buildMethod",
        "builderFactoryMethod",
        "builderName",
            "checkNullInAggregator",
        "filter",
            "setterPrefix",
        "id"
    );

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class ConfBuilder {
        public ConfBuilder aggregatorMethod(String aggregatorMethod) {
            config.aggregatorMethod = aggregatorMethod;
            return this;
        }

        public ConfBuilder buildMethod(String buildMethod) {
            config.buildMethod = buildMethod;
            return this;
        }

        public ConfBuilder builderFactoryMethod(String builderFactoryMethod) {
            config.builderFactoryMethod = builderFactoryMethod;
            return this;
        }

        public ConfBuilder builderName(String builderName) {
            config.builderName = builderName;
            return this;
        }

        public ConfBuilder checkNullInAggregator(String checkNullInAggregator) {
            config.checkNullInAggregator = checkNullInAggregator;
            return this;
        }

        public ConfBuilder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public ConfBuilder generatedAnnotation(Class<? extends java.lang.annotation.Annotation> generatedAnnotation) {
            config.generatedAnnotation = generatedAnnotation;
            return this;
        }

        public ConfBuilder setterPrefix(String setterPrefix) {
            config.setterPrefix = setterPrefix;
            return this;
        }

        public Builder build() {
            return Builder.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.aggregatorMethod = params.get("aggregatorMethod",config.aggregatorMethod);
        local.buildMethod = params.get("buildMethod",config.buildMethod);
        local.builderFactoryMethod = params.get("builderFactoryMethod",config.builderFactoryMethod);
        local.builderName = params.get("builderName",config.builderName);
        local.checkNullInAggregator = params.get("checkNullInAggregator", config.checkNullInAggregator);
        local.filter = params.get("filter",config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.setterPrefix = params.get("setterPrefix", config.setterPrefix);
        return local;
    }
    //</editor-fold>
}


