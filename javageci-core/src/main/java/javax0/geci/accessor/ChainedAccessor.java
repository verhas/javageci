package javax0.geci.accessor;

import javax0.geci.api.Segment;
import javax0.geci.tools.GeciReflectionTools;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static javax0.geci.tools.CaseTools.ucase;

public class ChainedAccessor extends AbstractAccessor {

    public ChainedAccessor(){
        config.setterNameGenerator = name -> "with" + ucase(name);
        config.mnemonic = "caccessor";
    }

    @Override
    protected void writeSetter(Field field, String name, String setterName,
                               String type, String access, Segment segment) {
        final var klass = field.getDeclaringClass();
        final var fullyQualified = GeciReflectionTools.getSimpleGenericClassName(klass);
        segment._r("%s%s%s %s(%s %s) {",
                access, conditionalSpace(access), fullyQualified, setterName, type, name)
                .write("this.%s = %s;", name, name)
                .write("return this;")
                ._l("}")
                .newline();
    }

    public static AbstractAccessor.Builder builder() {
        return new ChainedAccessor().new Builder();
    }
    @Override
    public String mnemonic() {
        return config.mnemonic;
    }
}
