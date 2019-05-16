package javax0.geci.accessor;

import javax0.geci.api.GeciException;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public class Accessor extends AbstractFilteredFieldsGenerator {

    private static final Set<String> accessModifiers = Set.of("public", "private", "protected", "package");

    private static void writeGetter(String name, String getterName, String type, String access, Segment segment) {
        segment.write_r(access + " " + type + " " + getterName + "(){")
                .write("return " + name + ";")
                .write_l("}")
                .newline();
    }

    private static void writeSetter(String name, String setterName, String type, String access, Segment segment) {
        segment.write_r(access + " void " + setterName + "(" +
                type + " " + name + "){")
                .write("this." + name + " = " + name + ";")
                .write_l("}")
                .newline();
    }

    private static String cap(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public String mnemonic() {
        return "accessor";
    }

    private String check(final String access) {
        if (!access.endsWith("!") && !accessModifiers.contains(access)) {
            throw new GeciException("'"+access+"' is not a valid access modifier");
        }
        final String modifiedAccess;
        if( access.endsWith("!")){
            modifiedAccess = access.substring(0,access.length()-1);
        }else {
            modifiedAccess = access;
        }
        if( modifiedAccess.equals("package")){
            return "";
        }
        return modifiedAccess;
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        final var id = params.get("id");
        source.init(id);
        var isFinal = Modifier.isFinal(field.getModifiers());
        var name = field.getName();
        var fieldType = GeciReflectionTools.typeAsString(field);
        var access = check(params.get("access", "public"));
        var ucName = cap(name);
        var setter = params.get("setter", "set" + ucName);
        var getter = params.get("getter", "get" + ucName);
        var only = params.get("only");
        source.init(id);
        try (var segment = source.open(id)) {
            if (!isFinal && !"getter".equals(only)) {
                writeSetter(name, setter, fieldType, access, segment);
            }
            if (!"setter".equals(only)) {
                writeGetter(name, getter, fieldType, access, segment);
            }
        }
    }
}
