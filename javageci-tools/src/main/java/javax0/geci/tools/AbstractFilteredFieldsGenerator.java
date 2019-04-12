package javax0.geci.tools;

import javax0.geci.api.Source;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Field;


/**
 * This abstract generator does the same as {@link AbstractDeclaredFieldsGenerator} with the additional functionality
 * that it looks at the {@code Geci} annotation parameter named {@code filter}, which supposed to contain a
 * {@link Selector} expression and invokes the {@link #process(Source, Class, CompoundParams, Field)} method only
 * for the fields, which match the filter criterion.
 * <p>
 * Note that the filter criteria is taken from the {@code Geci} annotation from the class level but also form the
 * field level. Since the field level configuration overwrites the class level configuration and since it is controlling
 * the filtering of the single field, which is annotation the only reasonable selector expression on a field
 * {@code Geci} annotation is either {@code true} and {@code false}.
 */
public abstract class AbstractFilteredFieldsGenerator extends AbstractDeclaredFieldsGenerator {

    @Override
    protected final void processFieldHook(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        var filter = params.get("filter", "true");
        var selector = Selector.compile(filter);
        if (selector.match(field)) {
            processSelectedFieldHook(source, klass, params, field);
        }
    }

    /**
     * Extending this interface can override this method adding extra functionality and keeping the signature and the
     * name of the abstract method {@link AbstractDeclaredFieldsGenerator#process(Source, Class, CompoundParams, Field)}.
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param params the parameters collected from the class and also from the actual field. The parameters defined on
     *               the field annotation have precedence over the annotations on the class.
     * @param field  the field that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator     *
     */
    protected void processSelectedFieldHook(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        process(source, klass, params, field);
    }
}
