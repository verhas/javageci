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
import java.util.stream.Collectors;

public class Equals extends AbstractFilteredFieldsGenerator {

    private final Class<? extends Annotation> generatedAnnotation;
    private final List<Field> fields = new ArrayList<>();
    private Segment equalsSegment;
    private Segment hashCodeSegment;
    private Field lastField;
    private CompoundParams lastParams;
    private boolean generateEquals;
    private boolean generateHashCode;

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
        hashCodeSegment = source.temporary();
        generateHashCodeHeader(hashCodeSegment, klass, global);
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
            generateEqualsForField(equalsSegment, lastParams, lastField, true);
        }
        generateEqualsTail(equalsSegment);
        generateHashCodeTail(hashCodeSegment, global);
        final var gid = global.get("id");
        var segment = source.open(gid);
        if (generateEquals) {
            segment.write(equalsSegment);
        }
        if (generateHashCode) {
            segment.write(hashCodeSegment);
        }
    }

    private void generateEqualsHeader(Segment segment, Class<?> klass, CompoundParams global) {
        var equalsMethod = getEqualsMethod(klass);
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
            generateEqualsForField(segment, lastParams, lastField, false);
        }
        lastParams = params;
        lastField = field;
    }

    private void generateEqualsForField(Segment segment, CompoundParams params, Field field, boolean isLast) {
        var primitive = field.getType().isPrimitive();
        var name = field.getName();
        if (primitive) {
            if (field.getType().equals(float.class)) {
                segment.write(retZ(isLast, "Float", name));
            } else if (field.getType().equals(double.class)) {
                segment.write(retZ(isLast, "Double", name));
            } else {
                segment.write(retId(isLast, name));
            }
        } else {
            if (params.is("useObjects")) {
                segment.write(retObjEq(isLast, name));
            } else {
                if (params.is("notNull")) {
                    segment.write(retEq(isLast, name));
                } else {
                    segment.write(retNNEq(isLast, name));
                }
            }
        }
    }

    private String retLast(String condition) {
        return "return " + condition + ";";
    }

    private String ret(String condition) {
        return "if (" + condition + ") return false;";
    }

    private String retZ(boolean isLast, String type, String name) {
        if (isLast) {
            return retLast(type + ".compare(that." + name + ", " + name + ") == 0");
        } else {
            return ret(type + ".compare(that." + name + ", " + name + ") != 0");
        }
    }

    private String retId(boolean isLast, String name) {
        if (isLast) {
            return retLast(name + " == that." + name);
        } else {
            return ret(name + " != that." + name);
        }
    }

    private String retObjEq(boolean isLast, String name) {
        if (isLast) {
            return retLast("Objects.equals(" + name + ", that." + name + ")");
        } else {
            return ret("!Objects.equals(" + name + ", that." + name + ")");
        }
    }

    private String retEq(boolean isLast, String name) {
        if (isLast) {
            return retLast(name + ".equals(that." + name + ")");
        } else {
            return ret("!" + name + ".equals(that." + name + ")");
        }
    }

    private String retNNEq(boolean isLast, String name) {
        if (isLast) {
            return retLast(name + " != null ? " + name + ".equals(that." + name + ") : that." + name + " == null");
        } else {
            return ret(name + "!= null ? !" + name + ".equals(that." + name + ") : that." + name + " != null");
        }
    }

    /**
     * Get the equals() method from the class or return null, if the class does not declare its equals() method.
     *
     * @param klass from which we need the equals() method
     * @return the method or null
     */
    private Method getEqualsMethod(Class<?> klass) {
        try {
            return klass.getDeclaredMethod("equals", Object.class);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private Method getHashCodeMethod(Class<?> klass) {
        try {
            return klass.getDeclaredMethod("hashCode");
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private void generateHashCodeHeader(Segment segment, Class<?> klass, CompoundParams global) {
        var hashCodeMethod = getHashCodeMethod(klass);
        generateHashCode = hashCodeMethod == null || GeciAnnotationTools.isGenerated(hashCodeMethod);
        segment.write("@javax0.geci.annotations.Generated(\"equals\")");
        segment.write("@Override");
        segment.write_r("public int hashCode() {");
    }

    private void generateHashCodeTail(Segment segment, CompoundParams global) {
        if (global.is("useObjects")) {
            generateHashCodeBodyUsingObjects(segment);
        } else {
            generateHashCodeBody(segment, global);
        }
        segment.write_l("}");
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

    protected String defaultFilterExpression() {
        return "!static";
    }
}
