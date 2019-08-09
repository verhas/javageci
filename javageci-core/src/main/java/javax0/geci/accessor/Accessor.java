package javax0.geci.accessor;

import java.lang.reflect.Field;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.core.annotations.AnnotationBuilder;

@AnnotationBuilder(absolute = "yes")
public class Accessor extends AbstractAccessor {
    public Accessor(){
        config.mnemonic = "accessor";
    }

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
        return config.mnemonic;
    }


    public static AbstractAccessor.Builder builder() {
        return new Accessor().new Builder();
    }
}
