package javax0.geci.record;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.Lex;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CaseTools;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax0.geci.lexeger.LexpressionBuilder.anyTill;
import static javax0.geci.lexeger.LexpressionBuilder.group;
import static javax0.geci.lexeger.LexpressionBuilder.identifier;
import static javax0.geci.lexeger.LexpressionBuilder.list;
import static javax0.geci.lexeger.LexpressionBuilder.match;
import static javax0.geci.lexeger.LexpressionBuilder.modifier;
import static javax0.geci.lexeger.LexpressionBuilder.type;
import static javax0.geci.lexeger.LexpressionBuilder.zeroOrMore;

/**
 * <p> This generator can be used to mimic the JEP 359 record feature of
 * Java language that will come some time later. The generator will
 * generate the constructor for the class listing all the fields in the
 * same order as they appear in the source file. </p>
 *
 * <p>If there is a {@code private} and {@code void} method in the class
 * that has the same name as the class (possible different casing) then
 * the constructor will invoke this method passing all the arguments of
 * the constructor to the method. If the method has different argument
 * list then it will be modified by the generator so that it will have
 * exactly the same argument signature as the constructor.</p>
 *
 * <p>If the class {@code RecordClass} has a method {@code private void
 * recordClass()} then the method will be modified to accept the same
 * arguments as the constructor. The name of the method is case
 * insensitive, it can be for example {@code RecordClass()} or {@code
 * ReCoRdClASs()}, but there should be no two of those in the class.</p>
 *
 * <p>The reason to name the validator to be the same as the class is to
 * make the migration easy when Java delivers the record feature.</p>
 *
 * <p>The generator also creates getters for the fields and generates
 * {@code hashCode()} and {@code equals()} methods. It also adds the
 * {@code final} modifier to the class as well as any non-static fields.
 * </p>
 *
 * <p> Note that altering the modifiers to be {@code final} is out of
 * editor-fold segment code generation.</p>
 *
 * <p>The generator will throw {@code GeciException} if the class
 * extends any class other than {@code Object} and also if the class is
 * {@code abstract}.</p>
 *
 * <p>The generator will throw {@code GeciException} if the class has
 * more than one {@code private} and {@code void} methods that have the
 * same name as the class.</p>
 */
@AnnotationBuilder
public class Record extends AbstractFilteredFieldsGenerator {

    private static final Selector<Field> NON_STATIC = Selector.compile("! static ");
    private static Selector<Field> NON_FINAL__NON_STATIC = Selector.compile("!final & ! static ");
    private static final Selector<Method> VOID = Selector.compile("void");

    private static class Config {
        private String filter;
    }

    public void process(Source source,
                        Class<?> klass,
                        CompoundParams global,
                        Field[] fields,
                        Segment segment
    ) {
        classAssertions(klass);

        final List<Field> sortedFields;
        final String validator;
        final String argumentDeclaration;
        try (final var javaLexed = new JavaLexed(source)) {
            validator = getValidatorMethodName(klass);
            makeClassFinal(klass, javaLexed);
            makeFieldsFinal(fields, javaLexed);
            sortedFields = getFieldsSorted(fields, javaLexed);
            argumentDeclaration = calculateArgsDeclaration(sortedFields);
            fixValidatorArguments(javaLexed, validator, argumentDeclaration);
        }
        generateConstructor(klass, segment, sortedFields, validator, argumentDeclaration);
        generateGetters(segment, sortedFields);
        generateHashCode(segment, sortedFields);
        generateEquals(segment, sortedFields, klass);
    }

    private void generateGetters(Segment segment, List<Field> sortedFields) {
        for (final var f : sortedFields) {
            segment.write_r("public " + GeciReflectionTools.getGenericTypeName(f.getGenericType()) + " get" + CaseTools.ucase(f.getName()) + "() {");
            segment.write("return " + f.getName() + ";");
            segment.write_l("}").newline();
        }
    }

    /**
     * Throw {@link GeciException} in case the class does not meet
     * preliminary requirements: extends some class other than {@code
     * Object} or is {@code abstract}.
     *
     * @param klass the class object to be checked
     */
    private void classAssertions(Class<?> klass) {
        if (!klass.getSuperclass().equals(Object.class)) {
            throw new GeciException("Class " + klass.getName()
                                        + " cannot be record because it has a superclass");
        }
        if ((klass.getModifiers() & Modifier.ABSTRACT) != 0) {
            throw new GeciException("Class " + klass.getName()
                                        + " cannot be record because it is abstract");
        }
    }

    /**
     * <p>Get the name of the validator method and in case the validator
     * method has different arguments than the constructor then replace
     * the argument list of the validator to be the same as that of the
     * constructor.</p>
     *
     * @param klass the class we are generating code into
     * @return the name of the validator method or {@code null} in case
     * there is no validator method.
     */
    private String getValidatorMethodName(Class<?> klass) {
        return getValidatorMethod(klass).map(Method::getName).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private void fixValidatorArguments(JavaLexed javaLexed,
                                       String validatorMethodName,
                                       String argumentDeclaration
    ) {
        javaLexed.find(
            list(
                match("void "
                          + validatorMethodName
                          + "("),
                anyTill(")"),
                match(")")
            )
        ).fromStart()
            .replaceWith(Lex.of("void "
                                    + validatorMethodName
                                    + "(" + argumentDeclaration + ")")
            );
    }

    private Optional<Method> getValidatorMethod(Class<?> klass) {
        final var methods = klass.getDeclaredMethods();
        Method validatorMethod = null;
        for (final var method : methods) {
            if (method.getName().equalsIgnoreCase(klass.getSimpleName()) && VOID.match(method)) {
                if (validatorMethod != null) {
                    throw new GeciException("There are more than one methods mimicking the validator constructor of the record class " + klass.getName());
                }
                validatorMethod = method;
            }
        }
        return Optional.ofNullable(validatorMethod);
    }

    private void generateConstructor(Class<?> klass, Segment segment, List<Field> sortedFields, String validatorMethod, String argumentDeclaration) {
        segment.write_r("public " + klass.getSimpleName() + "("
                            + argumentDeclaration
                            + ") {");
        if (validatorMethod != null) {
            segment.write(validatorMethod + "(" + sortedFields.stream().map(Field::getName).collect(Collectors.joining(", ")) + ");");
        }
        for (final var field : sortedFields) {
            segment.write("this." + field.getName() + " = " + field.getName() + ";");
        }
        segment.write_l("}").newline();
    }

    private String calculateArgsDeclaration(List<Field> sortedFields) {
        return sortedFields.stream().map(f -> "final " +
                                                  GeciReflectionTools.getGenericTypeName(f.getGenericType()) +
                                                  " " +
                                                  f.getName())
                   .collect(Collectors.joining(", "));
    }


    private List<Field> getFieldsSorted(Field[] fields, JavaLexed javaLexed) {
        final var sortedFields = new ArrayList<Field>();
        final var fieldStart = new ArrayList<Integer>();
        for (final var field : fields) {
            if (NON_STATIC.match(field)) {
                final var start = getDeclarationStartOfField(javaLexed, field);
                int i = 0;
                while (i < sortedFields.size()) {
                    if (start > fieldStart.get(i)) {
                        i++;
                    } else {
                        break;
                    }
                }
                fieldStart.add(i, start);
                sortedFields.add(i, field);
            }
        }
        return sortedFields;
    }

    /**
     * @param javaLexed the java lexical elements list
     * @param field     the field that we search the declaration of
     * @return the index of the lexical element in the list that starts
     * the declaration of the field.
     */
    private int getDeclarationStartOfField(JavaLexed javaLexed, Field field) {
        return javaLexed.find(
            list(zeroOrMore(modifier(~Modifier.FINAL)),
                type(),
                identifier(field.getName()))).fromStart().result().start;
    }


    /**
     * Find all field declarations that are not final and not static and
     * modify the declarations so that these fields will become final.
     *
     * @param fields    all the declared fields of the class
     * @param javaLexed the class source lexical elements
     */
    @SuppressWarnings("unchecked")
    private void makeFieldsFinal(Field[] fields, JavaLexed javaLexed) {
        for (final var field : fields) {
            if (NON_FINAL__NON_STATIC.match(field)) {
                javaLexed.find(
                    list(
                        zeroOrMore(group("modifiers"), modifier(~Modifier.FINAL)),
                        type(group("fieldType")),
                        identifier(field.getName())
                    )
                ).fromStart()
                    .replaceWith(Lex.of("final "),
                        javaLexed.group("modifiers"),
                        Lex.of(" "),
                        javaLexed.group("fieldType"),
                        Lex.of(" " + field.getName()));
            }
        }
    }

    private static final Selector<Class<?>> NOT_FINAL = Selector.compile("!final");

    /**
     * If the class is not final then insert the {@code final} modifier
     * in front of the class declaration.
     *
     * @param klass     the class to be modified
     * @param javaLexed the class source lexical elements
     */
    @SuppressWarnings("unchecked")
    private void makeClassFinal(Class<?> klass, JavaLexed javaLexed) {
        if (NOT_FINAL.match(klass)) {
            javaLexed.find(list("class", klass.getSimpleName())).fromStart()
                .replaceWith(Lex.of("final class " + klass.getSimpleName()));
        }
    }

    private void generateHashCode(Segment segment, List<Field> fields) {
        segment.write("@Override");
        segment.write_r("public int hashCode() {");
        segment.write("return java.util.Objects.hash(%s);",
            fields.stream().map(Field::getName).collect(Collectors.joining(", ")));
        segment.write_l("}").newline();
    }


    private void generateEquals(Segment segment, List<Field> fields, Class<?> klass) {
        segment.write("@Override")
            .write_r("public boolean equals(Object o) {")
            .write("if (this == o) return true;")
            .write("if (o == null || getClass() != o.getClass()) return false;")
            .write("%s that = (%s) o;", klass.getSimpleName(), klass.getSimpleName())
            .write("return " +
                       fields.stream()
                           .map(Field::getName)
                           .map(fn -> "java.util.Objects.equals(that." + fn + ", " + fn + ")")
                           .collect(Collectors.joining(" && ")) + ";")
            .write_l("}");
    }

    @Override
    public String mnemonic() {
        return "record";
    }

    //<editor-fold id="configBuilder">
    private final Config config = new Config();
    public static Record.Builder builder() {
        return new Record().new Builder();
    }

    private static final java.util.Set<String> implementedKeys = new java.util.HashSet<>(java.util.Arrays.asList(
        "filter",
        "id"
    ));

    @Override
    public java.util.Set<String> implementedKeys() {
        return implementedKeys;
    }
    public class Builder implements javax0.geci.api.GeneratorBuilder {
        public Builder filter(String filter) {
            config.filter = filter;
            return this;
        }

        public Record build() {
            return Record.this;
        }
    }
    private Config localConfig(CompoundParams params){
        final var local = new Config();
        local.filter = params.get("filter", config.filter);
        return local;
    }
    //</editor-fold>

}
