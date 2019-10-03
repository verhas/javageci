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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import static javax0.geci.lexeger.Lex.of;
import static javax0.geci.lexeger.LexpressionBuilder.group;
import static javax0.geci.lexeger.LexpressionBuilder.identifier;
import static javax0.geci.lexeger.LexpressionBuilder.list;
import static javax0.geci.lexeger.LexpressionBuilder.modifier;
import static javax0.geci.lexeger.LexpressionBuilder.type;
import static javax0.geci.lexeger.LexpressionBuilder.zeroOrMore;

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
        try (final var javaLexed = new JavaLexed(source)) {
            if (Selector.compile("!final").match(klass)) {
                javaLexed.find(list("class", klass.getSimpleName())).fromStart()
                    .replaceWith(Lex.of("final class " + klass.getSimpleName()));
            }
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
        segment.write_r("public " + klass.getSimpleName() + "("
                            + Arrays.stream(fields).map(f -> GeciReflectionTools.getGenericTypeName(f.getGenericType()) + " " + f.getName()).collect(Collectors.joining(", "))
                            + ") {");
        for (final var field : fields) {
            segment.write("this." + field.getName() + " = " + field.getName() + ";");
        }
        segment.write_l("}").newline();
        for (final var f : fields) {
            segment.write_r("public " + GeciReflectionTools.getGenericTypeName(f.getGenericType()) + " get" + CaseTools.ucase(f.getName()) + "() {");
            segment.write("return " + f.getName() + ";");
            segment.write_l("}").newline();
        }
        final var nonStaticFields = Arrays.stream(fields).filter(f -> (f.getModifiers() & Modifier.STATIC) == 0).toArray(Field[]::new);
        generateHashCode(segment, nonStaticFields);
        generateEquals(segment, nonStaticFields, klass);
    }

    private void generateHashCode(Segment segment, Field[] fields) {
        segment.write("@Override");
        segment.write_r("public int hashCode() {");
        segment.write("return java.util.Objects.hash(%s);",
            Arrays.stream(fields).map(Field::getName).collect(Collectors.joining(", ")));
        segment.write_l("}").newline();
    }


    private void generateEquals(Segment segment, Field[] fields, Class<?> klass) {
        segment.write("@Override")
            .write_r("public boolean equals(Object o) {")
            .write("if (this == o) return true;")
            .write("if (o == null || getClass() != o.getClass()) return false;")
            .write("%s that = (%s) o;", klass.getSimpleName(), klass.getSimpleName())
            .write("return " +
                       Arrays.stream(fields)
                           .map(Field::getName)
                           .map(fn -> "java.util.Objects.equals(that." + fn + ", " + fn + ")")
                           .collect(Collectors.joining(" && ")) + ";")
            .write_l("}").newline();
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
