package javax0.geci.tools;

import javax0.geci.api.Source;

import java.lang.reflect.Field;

public abstract class AbstractDeclaredFieldsGenerator extends AbstractGenerator {

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        preprocess(source, klass, global);
        final var fields = Tools.getDeclaredFieldsSorted(klass);
        for (final var field : fields) {
            var params = Tools.getParameters(field, mnemonic());
            if (params != null) {
                processField(source, klass, new CompoundParams(params, global), field);
            }
        }
        postprocess(source, klass, global);
    }

    public void preprocess(Source source, Class<?> klass, CompoundParams global) throws Exception {
    }

    public void postprocess(Source source, Class<?> klass, CompoundParams global) throws Exception {
    }

    public abstract void processField(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception;
}
