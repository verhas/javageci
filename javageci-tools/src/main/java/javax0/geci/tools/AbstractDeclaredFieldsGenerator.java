package javax0.geci.tools;

import javax0.geci.api.Source;

import java.lang.reflect.Field;

public abstract class AbstractDeclaredFieldsGenerator extends AbstractGenerator {

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        preprocess(source, klass, global);
        final var fields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
        for (final var field : fields) {
            var params = GeciReflectionTools.getParameters(field, mnemonic());
            if (params != null) {
                processField(source, klass, new CompoundParams(params, global), field);
            }
        }
        postprocess(source, klass, global);
    }

    @SuppressWarnings("unused")
    public void preprocess(Source source, Class<?> klass, CompoundParams global) {
    }

    @SuppressWarnings("unused")
    public void postprocess(Source source, Class<?> klass, CompoundParams global) {
    }

    public abstract void processField(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception;
}
