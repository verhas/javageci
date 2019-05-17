package javax0.geci.accessor;

import javax0.geci.api.Segment;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Field;
import java.util.Set;

public class ChainedAccessor extends AbstractAccessor {

    private static final Set<String> accessModifiers =
            Set.of("public", "private", "protected", "package");

    protected String setterName(String name) {
        return "with" + cap(name);
    }

    @Override
    protected void writeSetter(Field field, String name, String setterName,
                               String type, String access, Segment segment) {
        final var klass = field.getDeclaringClass();
        final var fullyQualified = GeciReflectionTools.getSimpleGenericClassName(klass);
        segment._r("%s %s %s(%s %s){",
                access, fullyQualified, setterName, type, name)
                .write("this.%s = %s;", name, name)
                .write("return this;")
                ._l("}")
                .newline();
    }


    @Override
    public String mnemonic() {
        return "caccessor";
    }
}
