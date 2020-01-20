package javax0.geci.accessor;

import javax0.geci.api.Segment;
import javax0.geci.core.annotations.AnnotationBuilder;

import java.lang.reflect.Field;

@AnnotationBuilder()
public class Accessor extends AbstractAccessor {
    public Accessor(){
        config.mnemonic = "accessor";
    }

    @Override
    protected void writeSetter(Field field, String name, String setterName,
                               String type, String access, Segment segment) {
        segment._r("%s%svoid %s(%s %s) {",
                access, conditionalSpace(access), setterName, type, name)
                .write("this.%s = %s;", name, name)
                ._l("}")
                .newline();
    }


    @Override
    public String mnemonic() {
        return config.mnemonic;
    }


    public static AbstractAccessor.Builder builder() {
        return new Accessor().new Builder();
    }
}
