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
import java.util.HashSet;

import static javax0.geci.api.CompoundParams.toBoolean;

/**
 * - ClassDescription
 *
 * # Builder
 *
 * The builder generator generates a builder as an inner class into a class.
 * The builder class will have a method to specify the values of the fields.
 * The fields taken into account are filtered the usual way using the `filter` configuration field.
 * Usually (by default) only `private` fields will have a builder method, which are not `private` or `static`.
 *
 * The generator is also capable generating aggregator methods when the field is a collection or some other type that can aggregate/collect several values.
 * A field is an aggregator type if the class of the field has at least one method named `add(x)` that has one argument.
 * The actual name is `add` by default but this is configurable.
 * For example if a field is of type `List` then it will be treated as aggregator type because the class `List` has a method `add`.
 * The name of the corresponding aggregator method in the builder will be `add` plus the name of the field with capitalized first letter.
 *
 * There are several values that can be configured for the generator in the generators builder pattern.
 * This can be done in the test code where the generator is registered into the `Geci` object, or on the class/field level using annotations.
 *
 */
@AnnotationBuilder()
public class Builder extends AbstractFilteredFieldsGenerator {

    /**
     * - Config
     *
     * # Configuration
     *
     * The configuration values can be configured on the builder of the generator in the test code where the generator object is registered into the `Geci` object that is used to run the generation.
     * The configuration items that are `String` can be configured on the [target class](TERMINOLOGY.md) and also on the fields individually.
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
         * * `{{configVariableName}}` can define the filter expression
         * for the fields that will be included in the builder. The
         * default is `{{configDefaultValue}}`. If there is a field that
         * you want to include into the builder individually in spite of
         * the fact that the "global" filter expression excludes the
         * field then you can annotate the field with `@Geci("builder
         * filter=true")`. This can be a good practice in case the field
         * is a collection or some other aggregator and you want to have
         * the aggregator methods, but the field itself is final
         * initialized on the declaration line or in the constructor of
         * the [target class](TERMINOLOGY.md). If a field is final the
         * generator never generates a builder method that sets the
         * field itself because that is not possible and would result a
         * code that does not compile.
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
         * default value is `{{configDefaultValue}}`. In the standard
         * collection types this method is called `add` therefore the
         * default value is `{{configDefaultValue}}`. There can be two
         * reasons to configure this value for a specific field to be
         * different. One reason is the obvious, when the method that
         * aggregates values is named differently. The other reason when
         * the aggregating method is named `{{configDefaultValue}}` but
         * you do not want the builder to create aggregator methods for
         * this fields into the builder. In this case the field should
         * be defined to be an empty string, because it is certain that
         * the class will not have a method that has empty name.
         */
        private String aggregatorMethod = "add";

        /**
         * -
         *
         * * When an aggregator is generated the generated code checks
         * that the field to which we want to add the argument value is
         * not `null` if `{{configVariableName}}` is "true". The default
         * value is `"{{configDefaultValue}}"`. If this value is
         * configured to be false then this check will be skipped and
         * the generated code will call the underlying aggregator method
         * even when the field is null. In this case there will be a
         * {code NullPointerException} thrown. If the value is true,
         * then the check is done and in case the field is `null` then
         * the generated code will throw `IllegalArgumentException`
         * naming the field.
         */
        private String checkNullInAggregator = "true";

        /**
         * -
         *
         * * `{{configVariableName}}` can define a setter prefix. The
         * default value is `"{{configDefaultValue}}"`. If this value is
         * not `null` and not an empty string then the name of the
         * setter method will start with this prefix and the name of the
         * field will be added with the first character capitalized.
         */
        private String setterPrefix = "";

        /**
         * -
         *
         * * The created `builder()` method returns a `Builder`
         * instance. The `Builder` class is a non-static inner class of
         * the [target class](TERMINOLOGY.md), because the build process
         * needs to access the fields of the [target
         * class](TERMINOLOGY.md) during the build process. Because of
         * this the method `builder()` (or whatever it is named in the
         * configuration `builderFactoryMethod`) needs to create a new
         * instance of the [target class](TERMINOLOGY.md). The default
         * is to invoke the default constructor. It is applied when
         * `{{configVariableName}}` is null or empty string. If this
         * configuration value is anything else then this string will be
         * used as it is to create a new instance of the [target
         * class](TERMINOLOGY.md). For example if there is a static
         * method called `factory()` that returns a new instance of the
         * [target class](TERMINOLOGY.md) then this configuration
         * parameter can be set to `"factory()". The default values is
         * `"{{configDefaultValue}}"`.
         */
        private String factory = "";

        /**
         * -
         *
         * * In the setter and aggregator methods the argument is
         * `{{configDefaultValue}}`. If you do not like this naming then
         * you can use `{{configVariableName}}` to specify a different
         * name.
         */
        private String argumentVariable = "x";
    }

    @Override
    public String mnemonic() {
        return "builder";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        final var local = localConfig(global);
        segment.param("class", klass.getSimpleName());
        final String factory;
        if (local.factory != null && !local.factory.isEmpty()) {
            factory = local.factory;
        } else {
            factory = "new " + klass.getSimpleName() + "()";
        }
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public static %s.%s %s() {", klass.getSimpleName(), local.builderName, local.builderFactoryMethod)
                .write("return %s.new %s();", factory, local.builderName)
                .write_l("}")
                .newline();
        writeGenerated(segment, config.generatedAnnotation);
        segment.write_r("public class %s {", local.builderName);
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) {
        final var local = localConfig(params);
        final var name = field.getName();
        final var type = GeciReflectionTools.normalizeTypeName(field.getType().getName(), klass);
        segment.param(
                "field", name,
                "type", type,
                "Builder", local.builderName,
                "x", local.argumentVariable);
        if (!Modifier.isFinal(field.getModifiers())) {
            generateSetter(klass, segment, name, local.setterPrefix);
        }
        if (local.aggregatorMethod != null && local.aggregatorMethod.length() > 0) {
            generateAggregators(klass, segment, field, local);
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
     * @param field     the field for which we generate an aggregator
     *                  method
     * @param local     the local configuration
     */
    private void generateAggregators(Class<?> klass, Segment segment, Field field, Config local) {
        final var argumentTypesDone = new HashSet<String>();
        final var name = field.getName();
        segment.param(
                "aggregatorMethod", local.aggregatorMethod + CaseTools.ucase(name),
                "remoteAggregatorMethod", local.aggregatorMethod);
        for (final var method : GeciReflectionTools.getDeclaredMethodsSorted(field.getType())) {
            if (method.getName().equals(local.aggregatorMethod) &&
                    method.getParameterTypes().length == 1) {
                final String argumentTypeName = calculateTypeName(klass, field, method.getParameterTypes()[0]);
                if (argumentTypeName != null && !argumentTypesDone.contains(argumentTypeName)) {

                    argumentTypesDone.add(argumentTypeName);
                    segment.param("argumentType", argumentTypeName);
                    writeGenerated(segment, local.generatedAnnotation);
                    segment.write_r("public {{Builder}} {{aggregatorMethod}}(final {{argumentType}} {{x}}) {");

                    if (toBoolean(local.checkNullInAggregator)) {
                        segment.write_r("if( {{class}}.this.{{field}} == null ) {")
                                .write("throw new IllegalArgumentException(\"Collection field {{field}} is null\");")
                                .write_l("}");
                    }

                    segment.write("{{class}}.this.{{field}}.{{remoteAggregatorMethod}}({{x}});")
                            .write("return this;")
                            .write_l("}")
                            .newline();
                }
            }
        }
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
     * @param name        the name of the field
     */
    private void generateSetter(Class<?> klass,
                                Segment segment,
                                String name,
                                String prefix) {
        writeGenerated(segment, config.generatedAnnotation);
        final String setterName;
        if (prefix != null && prefix.length() > 0) {
            setterName = prefix + CaseTools.ucase(name);
        } else {
            setterName = name;
        }
        segment.param(
                "setter", setterName,
                "field", name);
        segment.write_r("public {{Builder}} {{setter}}(final {{type}} {{x}}) {")
                .write("{{class}}.this.{{field}} = {{x}};")
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

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "aggregatorMethod",
        "argumentVariable",
        "buildMethod",
        "builderFactoryMethod",
        "builderName",
        "checkNullInAggregator",
        "factory",
        "filter",
        "setterPrefix",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class ConfBuilder implements javax0.geci.api.GeneratorBuilder {
        public ConfBuilder aggregatorMethod(String aggregatorMethod) {
            config.aggregatorMethod = aggregatorMethod;
            return this;
        }

        public ConfBuilder argumentVariable(String argumentVariable) {
            config.argumentVariable = argumentVariable;
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

        public ConfBuilder factory(String factory) {
            config.factory = factory;
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
        local.aggregatorMethod = params.get("aggregatorMethod", config.aggregatorMethod);
        local.argumentVariable = params.get("argumentVariable", config.argumentVariable);
        local.buildMethod = params.get("buildMethod", config.buildMethod);
        local.builderFactoryMethod = params.get("builderFactoryMethod", config.builderFactoryMethod);
        local.builderName = params.get("builderName", config.builderName);
        local.checkNullInAggregator = params.get("checkNullInAggregator", config.checkNullInAggregator);
        local.factory = params.get("factory", config.factory);
        local.filter = params.get("filter", config.filter);
        local.generatedAnnotation = config.generatedAnnotation;
        local.setterPrefix = params.get("setterPrefix", config.setterPrefix);
        return local;
    }
    //</editor-fold>
}


