package javax0.geci.tools;

import javax0.geci.api.Source;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Field;

/**
 * Generators that generate code using the fields are encouraged to extend this class. This abstract class will invoke
 * once {@link #preprocess(Source, Class, CompoundParams)}, then {@link #processField(Source, Class, CompoundParams, Field)}
 * for each field that has to be included by the expression configured in the {@code filter} parameter and finally
 * once {@link #postprocess(Source, Class, CompoundParams)}.
 */
public abstract class AbstractDeclaredFieldsGenerator extends AbstractGenerator {

    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        preprocess(source, klass, global);
        final var fields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
        for (final var field : fields) {
            var params = new CompoundParams(GeciReflectionTools.getParameters(field, mnemonic()), global);
            var filter = params.get("filter", "true");
            var selector = Selector.compile(filter);
            if (selector.match(field)) {
                processField(source, klass, params, field);
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
