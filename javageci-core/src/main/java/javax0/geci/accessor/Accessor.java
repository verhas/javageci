package javax0.geci.accessor;

import javax0.geci.api.Segment;

import java.lang.reflect.Field;
import java.util.Set;

public class Accessor extends AbstractAccessor {

    private static final Set<String> accessModifiers =
            Set.of("public", "private", "protected", "package");

    @Override
    protected void writeSetter(Field field,String name, String setterName,
                                    String type, String access, Segment segment) {
        segment._r("%s void %s(%s %s){",
                access, setterName, type, name)
                .write("this.%s = %s;", name, name)
                ._l("}")
                .newline();
    }


    @Override
    public String mnemonic() {
        return "accessor";
    }
}
