package javax0.geci.accessor;

import javax0.geci.api.Segment;

import java.lang.reflect.Field;

public class Accessor extends AbstractAccessor {


    @Override
    protected void writeSetter(Field field, String name, String setterName,
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


    public static AbstractAccessor.Builder builder() {
        return new Accessor().new Builder();
    }
}
