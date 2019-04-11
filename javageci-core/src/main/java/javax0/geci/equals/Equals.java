package javax0.geci.equals;

import javax0.geci.api.Source;
import javax0.geci.tools.AbstractGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.tools.syntax.GeciAnnotationTools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

//TODO: support field filter expression
//TODO: refactor to extend field generator
public class Equals extends AbstractGenerator {

    private final Class<? extends Annotation> generatedAnnotation;

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
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var gid = global.get("id");
        source.init(gid);
        generateEquals(source, klass, global);
        generateHashCode(source, klass, global);
    }

    private void generateEquals(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var fields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
        final var gid = global.get("id");
        var segment = source.open(gid);
        var equalsMethod = getEqualsMethod(klass);
        var subclassingAllowed = global.is("subclass");
        if (equalsMethod == null || GeciAnnotationTools.isGenerated(equalsMethod)) {
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
            var index = fields.length;
            for (final var field : fields) {
                index--;
                var isLast = index == 0;
                var local = GeciReflectionTools.getParameters(field, mnemonic());
                var params = new CompoundParams(local, global);
                var primitive = field.getType().isPrimitive();
                if (isNeeded(field, params)) {
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
            }
            segment.write_l("}");
            segment.newline();
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
            return retLast(name + ".equals(that." + name + ");");
        } else {
            return ret("!" + name + ".equals(that." + name + ");");
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

    private void generateHashCode(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var fields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
        final var gid = global.get("id");
        var segment = source.open(gid);
        var hashCodeMethod = getHashCodeMethod(klass);
        var useObjects = global.is("useObjects");
        if (hashCodeMethod == null || GeciAnnotationTools.isGenerated(hashCodeMethod)) {
            segment.write("@javax0.geci.annotations.Generated(\"equals\")");
            segment.write("@Override");
            segment.write_r("public int hashCode() {");
            if (useObjects) {
                var fieldNamesCSV = new StringBuilder();
                var separator = "";
                for (final var field : fields) {
                    var local = GeciReflectionTools.getParameters(field, mnemonic());
                    var params = new CompoundParams(local, global);
                    if (isNeeded(field, params)) {
                        fieldNamesCSV.append(separator).append(field.getName());
                        separator = ", ";
                    }
                }
                segment.write("return Objects.hash(%s);", fieldNamesCSV);
            } else {
                segment.write("int result = 0;");
                if (Arrays.stream(fields)
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .map(Field::getType)
                        .anyMatch(c -> c.equals(double.class))) {
                    segment.write("long temp;");
                }
                segment.newline();
                for (final var field : fields) {
                    var local = GeciReflectionTools.getParameters(field, mnemonic());
                    var params = new CompoundParams(local, global);
                    var primitive = field.getType().isPrimitive();
                    if (isNeeded(field, params)) {
                        var name = field.getName();
                        if (primitive) {
                            var type = field.getType();
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
                }
                segment.write("return result;");
            }
            segment.write_l("}");
        }
    }

    private boolean isNeeded(Field field, CompoundParams params) {
        return params.isNot("exclude") && !Modifier.isStatic(field.getModifiers());
    }
}
