package javax0.geci.record;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;
import javax0.geci.javacomparator.LexicalElement;
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
import java.util.stream.Collectors;

import static javax0.geci.lexeger.Lex.of;
import static javax0.geci.lexeger.LexpressionBuilder.group;
import static javax0.geci.lexeger.LexpressionBuilder.identifier;
import static javax0.geci.lexeger.LexpressionBuilder.list;
import static javax0.geci.lexeger.LexpressionBuilder.match;
import static javax0.geci.lexeger.LexpressionBuilder.modifier;
import static javax0.geci.lexeger.LexpressionBuilder.not;
import static javax0.geci.lexeger.LexpressionBuilder.type;
import static javax0.geci.lexeger.LexpressionBuilder.zeroOrMore;

/**
 * This generator can be used to mimic the JEP 359 record feature of
 * Java language that will come some time later. The generator will
 * generate the constructor for the class listing all the fields in
 * the same order as they appear in the source file.
 * <p>
 * The generator also creates getters for the fields and generates
 * {@code hashCode()} and {@code equals()} methods. It also adds the
 * {@code final} modifier to the class as well as any non-static fields.
 * <p>
 * Note that altering the modifiers to be {@code final} is out of
 * editor-fold segment code generation.
 * <p>
 * The generator will throw {@code GeciException} if the class extends
 * any class other than {@code Object} and also if the class is {@code
 * abstract}.
 * <p>
 * This setup is
 */
@AnnotationBuilder
public class Record extends AbstractFilteredFieldsGenerator {

    private static class Config {
        private String filter;
    }

    public void process(Source source, Class<?> klass, CompoundParams global, Field[] fields, Segment segment) {
        if (!klass.getSuperclass().equals(Object.class)) {
            throw new GeciException("Class " + klass.getName() + " cannot be record because it has a superclass");
        }
        if( (klass.getModifiers()&Modifier.ABSTRACT) != 0 ){
            throw new GeciException("Class " + klass.getName() + " cannot be record because it is abstract");
        }
        final List<Field> sortedFields;
        final String validator;
        try (final var javaLexed = new JavaLexed(source)) {
            makeClassFinal(klass, javaLexed);
            makeFieldsFinal(fields, javaLexed);
            sortedFields = getFieldsSorted(fields, javaLexed);
            validator = getValidatorMethodName(klass, sortedFields, javaLexed);
        }
        generateConstructor(klass, segment, sortedFields, validator);
        for (final var f : sortedFields) {
            segment.write_r("public " + GeciReflectionTools.getGenericTypeName(f.getGenericType()) + " get" + CaseTools.ucase(f.getName()) + "() {");
            segment.write("return " + f.getName() + ";");
            segment.write_l("}").newline();
        }
        generateHashCode(segment, sortedFields);
        generateEquals(segment, sortedFields, klass);
    }

    private String getValidatorMethodName(Class<?> klass, List<Field> fields, JavaLexed javaLexed) {
        final var methods = klass.getDeclaredMethods();
        final var validatorMethod = getValidatorMethod(klass, methods);
        if (validatorMethod == null) {
            return null;
        }
        javaLexed.find(list(
            match("private void " + validatorMethod.getName() + "("),
            zeroOrMore(not(match(")"))),
            match(")"))).fromStart();
        final var lexes = new ArrayList<LexicalElement>();
        lexes.addAll(Lex.of("private void " + validatorMethod.getName() + "("));
        var sep = "";
        for (final var field : fields) {
            lexes.addAll(Lex.of(sep));
            sep = ", ";
            lexes.addAll(Lex.of(GeciReflectionTools.getGenericTypeName(field.getGenericType())));
            lexes.addAll(Lex.of(" "));
            lexes.addAll(Lex.of(field.getName()));
        }
        lexes.addAll(Lex.of(")"));
        javaLexed.replaceWith(lexes);
        return validatorMethod.getName();
    }

    private Method getValidatorMethod(Class<?> klass, Method[] methods) {
        Method validatorMethod = null;
        for (final var method : methods) {
            if (method.getName().equalsIgnoreCase(klass.getSimpleName())) {
                if (validatorMethod != null) {
                    throw new GeciException("There are more than one methods mimicking the validator constructor of the record class " + klass.getName());
                }
                validatorMethod = method;
            }
        }
        return validatorMethod;
    }

    private void generateConstructor(Class<?> klass, Segment segment, List<Field> sortedFields, String validatorMethod) {
        segment.write_r("public " + klass.getSimpleName() + "("
                            + sortedFields.stream().map(f -> "final " +
                                                                 GeciReflectionTools.getGenericTypeName(f.getGenericType()) +
                                                                 " " +
                                                                 f.getName())
                                  .collect(Collectors.joining(", "))
                            + ") {");
        if (validatorMethod != null) {
            segment.write(validatorMethod + "(" + sortedFields.stream().map(Field::getName).collect(Collectors.joining(", ")) + ");");
        }
        for (final var field : sortedFields) {
            segment.write("this." + field.getName() + " = " + field.getName() + ";");
        }
        segment.write_l("}").newline();
    }

    private List<Field> getFieldsSorted(Field[] fields, JavaLexed javaLexed) {
        final var sortedFields = new ArrayList<Field>();
        final var fieldStart = new ArrayList<Integer>();
        Selector<Field> fieldSelector = Selector.compile("! static ");
        for (final var field : fields) {
            if (fieldSelector.match(field)) {
                final var start = javaLexed.find(
                    list(zeroOrMore(group("modifiers"), modifier(~Modifier.FINAL)),
                        type(group("fieldType")),
                        identifier(field.getName()))).fromStart().result().start;
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

    @SuppressWarnings("unchecked")
    private void makeFieldsFinal(Field[] fields, JavaLexed javaLexed) {
        Selector<Field> fieldSelector = Selector.compile("!final & ! static ");
        for (final var field : fields) {
            if (fieldSelector.match(field)) {
                javaLexed.find(
                    list(zeroOrMore(group("modifiers"), modifier(~Modifier.FINAL)),
                        type(group("fieldType")),
                        identifier(field.getName()))
                ).fromStart()
                    .replaceWith(of("final "),
                        javaLexed.group("modifiers"),
                        of(" "),
                        javaLexed.group("fieldType"),
                        of(" " + field.getName()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void makeClassFinal(Class<?> klass, JavaLexed javaLexed) {
        if (Selector.compile("!final").match(klass)) {
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

    private static final java.util.Set<String> implementedKeys = java.util.Set.of(
        "filter",
        "id"
    );

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

    private Config localConfig(CompoundParams params) {
        final var local = new Config();
        local.filter = params.get("filter", config.filter);
        return local;
    }
    //</editor-fold>

}
