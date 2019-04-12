package javax0.geci.accessor;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Accessor extends AbstractJavaGenerator {
    private static final int NOT_PACKAGE = Modifier.PROTECTED | Modifier.PRIVATE | Modifier.PUBLIC;

    @Override
    public String mnemonic() {
        return "accessor";
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        final var accessMask = GeciReflectionTools.mask(global.get("include"), Modifier.PRIVATE);
        final var gid = global.get("id");
        source.init(gid);
        final var fields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
        for (final var field : fields) {
            var local = GeciReflectionTools.getParameters(field, mnemonic());
            var isFinal = Modifier.isFinal(field.getModifiers());
            var params = new CompoundParams(local, global);
            if (params.isNot("exclude")) {
                var name = nameAsString(field);
                var capName = cap(name);
                var fieldType = GeciReflectionTools.typeAsString(field);
                var access = params.get("access", "public");
                var only = params.get("only");
                if (matchMask(field, accessMask)) {
                    var id = getId(field, params);
                    source.init(id);
                    try (var segment = source.open(id)) {
                        if (!isFinal && !"getter".equals(only)) {
                            writeSetter(name, capName, fieldType, access, segment);
                        }
                        if (!"setter".equals(only)) {
                            writeGetter(name, capName, fieldType, access, segment);
                        }
                    }
                }
            }
        }
    }

    private static void writeGetter(String name, String ucName, String type, String access, Segment segment) {
        segment.write_r(access + " " + type + " get" + ucName + "(){");
        segment.write("return " + name + ";");
        segment.write_l("}");
        segment.newline();
    }

    private static void writeSetter(String name, String ucName, String type, String access, Segment segment) {
        segment.write_r(access + " void set" + ucName + "(" +
                type + " " + name + "){");
        segment.write("this." + name + " = " + name + ";");
        segment.write_l("}");
        segment.newline();
    }

    private static boolean matchMask(Field field, int mask) {
        int modifiers = field.getModifiers();
        if ((mask & GeciReflectionTools.PACKAGE) != 0 && (modifiers & NOT_PACKAGE) == 0) {
            return true;
        }
        return (modifiers & mask) != 0;
    }

    private static String cap(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String nameAsString(Field field) {
        return field.getName();
    }

    private static String getId(Field field, CompoundParams fieldParams) {
        var id = fieldParams.get("id");
        if (id == null) {
            throw new GeciException("accessor field " + field + " has no segment id");
        }
        return id;
    }
}
