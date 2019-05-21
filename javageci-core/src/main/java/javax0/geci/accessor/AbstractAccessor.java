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

public abstract class AbstractAccessor extends AbstractFilteredFieldsGenerator {

    private static final Set<String> accessModifiers =
            Set.of("public", "private", "protected", "package");

    protected String cap(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    protected void writeGetter(Field field, String name, String getterName,
                               String type, String access, Segment segment) {
        segment._r("%s %s %s(){", access, type, getterName)
                .write("return %s;", name)
                ._l("}")
                .newline();
    }

    protected abstract void writeSetter(Field field, String name, String setterName,
                                        String type, String access, Segment segment);

    protected String getterName(String name) {
        return "get" + cap(name);
    }

    protected String setterName(String name) {
        return "set" + cap(name);
    }

    private String check(final String access) {
        if (!access.endsWith("!") && !accessModifiers.contains(access)) {
            throw new GeciException("'" + access + "' is not a valid access modifier");
        }
        final String modifiedAccess;
        if (access.endsWith("!")) {
            modifiedAccess = access.substring(0, access.length() - 1);
        } else {
            modifiedAccess = access;
        }
        if (modifiedAccess.equals("package")) {
            return "";
        }
        return modifiedAccess;
    }

    @Override
    public void process(Source source, Class<?> klass,
                        CompoundParams params,
                        Field field,
                        Segment segment) throws Exception {
        final var isFinal = Modifier.isFinal(field.getModifiers());
        final var name = field.getName();
        final var fieldType = GeciReflectionTools.typeAsString(field);
        final var access = check(params.get("access", "public"));
        final var setter = params.get("setter", () -> setterName(name));
        final var getter = params.get("getter", () -> getterName(name));
        final var only = params.get("only");
        if (!isFinal && !"getter".equals(only)) {
            writeSetter(field, name, setter, fieldType, access, segment);
        }
        if (!"setter".equals(only)) {
            writeGetter(field, name, getter, fieldType, access, segment);
        }
    }
}
