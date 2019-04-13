package javax0.geci.equals;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.syntax.GeciAnnotationTools;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Sample generator that generates {@code equals()} and {@code hashCode()} methods. The implementation extends the
 * {@link AbstractFilteredFieldsGenerator} class. There are two different ways to extend this abstract class and for
 * the sake of the demonstration this implementation uses both: one for creating the {@code equals()} method, and the
 * other one to create the {@code hashCode()} method.
 * <p>
 * The code for {@code equals()} is created overriding the methods
 * {@link AbstractFilteredFieldsGenerator#preprocess(Source, Class, CompoundParams)},
 * {@link AbstractFilteredFieldsGenerator#process(Source, Class, CompoundParams, Field)}, and
 * {@link AbstractFilteredFieldsGenerator#postprocess(Source, Class, CompoundParams)}.
 * </p>
 * <p>
 * The code for {@code hashCode()} is created overriding the method
 * {@link AbstractFilteredFieldsGenerator#process(Source, Class, CompoundParams, Field[])}
 * </p>
 * (Note that in this case the last parameter is an array and not a single field.)
 */
public class Equals extends AbstractFilteredFieldsGenerator {

    private final Class<? extends Annotation> generatedAnnotation;
    private final List<Field> fields = new ArrayList<>();
    private Segment equalsSegment;
    private Field lastField;
    private CompoundParams lastParams;
    private boolean generateEquals;

    public Equals() {
        generatedAnnotation = javax0.geci.annotations.Generated.class;
    }

    public Equals(Class<? extends Annotation> generatedAnnotation) {
        this.generatedAnnotation = generatedAnnotation;
    }

    @Override
    public String mnemonic() {
        return "equals";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global) {
        equalsSegment = source.temporary();
        generateEqualsHeader(equalsSegment, klass, global);
        lastField = null;
        fields.clear();
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field) {
        generateEqualsForField(equalsSegment, params, field);
        fields.add(field);
    }

    @Override
    public void postprocess(Source source, Class<?> klass, CompoundParams global) throws IOException {
        if (lastField != null) {
            generateEqualsForField(equalsSegment, lastParams, lastField, this::retLast);
        }
        generateEqualsTail(equalsSegment);
        final var gid = global.get("id");
        var segment = source.open(gid);
        if (generateEquals) {
            segment.write(equalsSegment);
        }
    }

    private void generateEqualsHeader(Segment segment, Class<?> klass, CompoundParams global) {
        var equalsMethod = getMethodOrNull(klass, "equals", Object.class);
        var subclassingAllowed = global.is("subclass");
        generateEquals = equalsMethod == null || GeciAnnotationTools.isGenerated(equalsMethod);
        segment.write("@" + generatedAnnotation.getCanonicalName() + "(\"" + mnemonic() + "\")");
        segment.write("@Override");
        segment.write_r("public %sboolean equals(Object o) {", subclassingAllowed ? "final " : "");
        segment.write("if (this == o) return true;");
        if (subclassingAllowed) {
            segment.write("if (!(o instanceof %s)) return false;", klass.getSimpleName());
        } else {
            segment.write("if (o == null || getClass() != o.getClass()) return false;");
        }
        segment.newline();
        segment.write("%s that = (%s) o;", klass.getSimpleName(), klass.getSimpleName());
    }

    private void generateEqualsTail(Segment segment) {
        segment.write_l("}");
        segment.newline();
    }

    private void generateEqualsForField(Segment segment, CompoundParams params, Field field) {
        if (lastField != null) {
            generateEqualsForField(segment, lastParams, lastField, this::ret);
        }
        lastParams = params;
        lastField = field;
    }

    private void generateEqualsForField(Segment segment, CompoundParams params, Field field, Function<String, String> convert) {
        var primitive = field.getType().isPrimitive();
        var name = field.getName();
        if (primitive) {
            if (field.getType().equals(float.class)) {
                segment.write(convert.apply("Float.compare(that." + name + ", " + name + ") == 0"));
            } else if (field.getType().equals(double.class)) {
                segment.write(convert.apply("Double.compare(that." + name + ", " + name + ") == 0"));
            } else {
                segment.write(convert.apply(name + " == that." + name));
            }
        } else {
            if (params.is("useObjects")) {
                segment.write(convert.apply("Objects.equals(" + name + ", that." + name + ")"));
            } else {
                if (params.is("notNull")) {
                    segment.write(convert.apply(name + ".equals(that." + name + ")"));
                } else {
                    segment.write(convert.apply(name + " != null ? !!" + name + ".equals(that." + name + ") : that." + name + " == null"));
                }
            }
        }
    }

    /**
     * Return the string that is the code for testing the last field in the list of the fields. When we are testing
     * the fields for equality we return if a field does not match the same field from the other object, otherwise
     * we go on testing the rest of the fields.
     * <p>
     * In case of the last field, however, there are no more fields, this is the last one. Therefore the code is simply
     * return the result of the comparison of the two fields. If they match then the whoe two objects are equal sine all
     * fields were some way equal. If the last fields do not match then the two objects are not equal.
     *
     * @param condition to check the equality. Since the same string is converted to test the non-equality when this
     *                  is not the last field this string also optionally contains {@code !!} characters, which are
     *                  deleted in this case. When the field is not the last one and {@link #ret(String)} is invoked
     *                  these will become {@code !} negations.
     * @return the code that handles the condition
     */
    private String retLast(String condition) {
        final var modCondition = condition.replace("!!", "");
        return "return " + modCondition + ";";
    }

    /**
     * Return the string that is the code for testing the field in the list of the fields in case this is NOT the last
     * field. See also {@link #retLast(String)}.
     * <p>
     * Note that the {@code condition} is the string that tests for equality. In this case, however, we need to test
     * inequality, thus the condition has to be reversed. The format of the conditions is very limited, there are only
     * six different strings that are used as condition, and therefore some very simple rule can be used to reverse
     * the condition:
     * <ul>
     * <li>If there is a '{@code ==}' sign in the condition then it has to be converted to '{@code !=}' and then</li>
     * <li>if there is a '{@code !!}' in the condition it has to become '{@code !}'</li>
     * <li>If there is NO '{@code ==}' in the condition string then the condition has to be preceeded
     * with '{@code !}'</li>
     * </ul>
     * <p>
     * Note that this is not a general algorithm to convert an arbitrary boolean Java expression. It just works for
     * these six cases.
     *
     * @param condition that tests the equality and which is negated using the special algorithm.
     * @return the code that handles the condition, simply {@code if( not condition) return false;}
     */
    private String ret(final String condition) {
        final String notCondition;
        if (condition.contains("==")) {
            notCondition = condition.replace("==", "!=").replace("!!", "!");
        } else {
            notCondition = "!" + condition;
        }
        return "if (" + notCondition + ") return false;";
    }


    private Method getMethodOrNull(Class<?> klass, String name, Class<?>... args) {
        try {
            return klass.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global, Field[] fields) throws Exception {
        final var gid = global.get("id");
        var segment = source.open(gid);
        var hashCodeMethod = getMethodOrNull(klass, "hashCode");
        var generateHashCode = hashCodeMethod == null || GeciAnnotationTools.isGenerated(hashCodeMethod);
        if (generateHashCode) {
            segment.write("@javax0.geci.annotations.Generated(\"equals\")");
            segment.write("@Override");
            segment.write_r("public int hashCode() {");

            if (global.is("useObjects")) {
                generateHashCodeBodyUsingObjects(segment);
            } else {
                generateHashCodeBody(segment, global);
            }
            segment.write_l("}");
        }
    }

    private void generateHashCodeBody(Segment segment, CompoundParams global) {
        segment.write("int result = 0;");
        if (thereIsAtLeastOneDoubleField()) {
            segment.write("long temp;");
        }
        segment.newline();
        for (final var field : fields) {
            var local = GeciReflectionTools.getParameters(field, mnemonic());
            var params = new CompoundParams(local, global);
            var primitive = field.getType().isPrimitive();
            final var name = field.getName();
            if (primitive) {
                if (field.getType().equals(boolean.class)) {
                    segment.write("result = 31 * result + (%s ? 1 : 0);", name);
                } else if (field.getType().equals(long.class)) {
                    segment.write("result = 31 * result + (int) (%s ^ (%s >>> 32));", name, name);
                } else if (field.getType().equals(float.class)) {
                    segment.write("result = 31 * result + (%s != +0.0f ? Float.floatToIntBits(%s) : 0);",
                        name, name);
                } else if (field.getType().equals(double.class)) {
                    segment.write("temp = Double.doubleToLongBits(%s);", name);
                    segment.write("result = 31 * result + (int) (temp ^ (temp >>> 32));");
                } else {
                    segment.write("result = 31 * result + (int) %s;", name);
                }
            } else {
                if (params.is("notNull")) {
                    segment.write("result = 31 * result + %s.hashCode();", name);
                } else {
                    segment.write("result = 31 * result + (%s != null ? %s.hashCode() : 0);",
                        name, name);
                }
            }
        }
        segment.write("return result;");
    }

    private boolean thereIsAtLeastOneDoubleField() {
        return fields.stream().map(Field::getType).anyMatch(c -> c.equals(double.class));
    }

    private void generateHashCodeBodyUsingObjects(Segment segment) {
        segment.write("return Objects.hash(%s);",
            fields.stream().map(Field::getName).collect(Collectors.joining(", ")));
    }

    @Override
    protected String defaultFilterExpression() {
        return "!static";
    }
}
