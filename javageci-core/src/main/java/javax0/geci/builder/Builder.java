package javax0.geci.builder;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.tools.AbstractFilteredFieldsGenerator;
import javax0.geci.tools.CompoundParams;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Geci("builder")
public class Builder extends AbstractFilteredFieldsGenerator {
    private Class<? extends Annotation> generatedAnnotation = javax0.geci.annotations.Generated.class;
    private String filter = "private & !static";

    @Override
    public String mnemonic() {
        return "builder";
    }

    @Override
    public void preprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        segment.write_r("public static %s.Builder builder() {", klass.getName())
                .write("return new %s().new Builder();", klass.getName())
                .write_l("}")
                .write_r("public class Builder {");
    }

    @Override
    public void process(Source source, Class<?> klass, CompoundParams params, Field field, Segment segment) throws Exception {
        final var name = field.getName();
        final var type = field.getType().getName();
        segment.write_r("public Builder %s(%s %s){", name, type, name)
                .write("%s.this.%s = %s;", name, name)
                .write("return this;")
                .write_l("}");
    }

    @Override
    public void postprocess(Source source, Class<?> klass, CompoundParams global, Segment segment) {
        segment.write_l("}"); // end of builder class
    }

    @Override
    protected String defaultFilterExpression() {
        return filter;
    }

    //<editor-fold id="builder">
    //</editor-fold>
}
