package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Source;
import javax0.geci.tools.reflection.Selector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * This abstract generator does the same as {@link AbstractFieldsGenerator} with the additional functionality
 * that it looks at the {@code Geci} annotation parameter named {@code filter}, which supposed to contain a
 * {@link Selector} expression and invokes the {@link #process(Source, Class, CompoundParams, Field)} method only
 * for the fields, which match the filter criterion.
 * <p>
 * Note that the filter criteria is taken from the {@code Geci} annotation from the class level but also form the
 * field level. Since the field level configuration overwrites the class level configuration and since it is controlling
 * the filtering of the single field, which is annotation the only reasonable selector expression on a field
 * {@code Geci} annotation is either {@code true} and {@code false}.
 */
@Geci("copyClass copyTo='AbstractFilteredMethodsGenerator.java'")
public abstract class AbstractFilteredFieldsGenerator extends AbstractFieldsGenerator {
    private final List<Field> fields = new ArrayList<>();

    @Override
    protected final void processFieldHook(Source source, Class<?> klass, CompoundParams params, Field field)
        throws Exception {
        var filter = params.get("filter", defaultFilterExpression());
        var selector = Selector.compile(filter);
        if (selector.match(field)) {
            processSelectedFieldHook(source, klass, params, field);
            fields.add(field);
        }
    }

    /**
     * This implementation clears the private {@code fields} field ArrayList and then passes the control to the
     * method {@link #preprocess(Source, Class, CompoundParams)}. This way the original functionality is kept and
     * the method {@link #processSelectedFieldHook(Source, Class, CompoundParams, Field)} can collect the filtered
     * fields after a fresh start into the object variable {@code fields} even if the generator object was called to
     * generate some code for a different class beforehand.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the {@code Geci} annotation on the class.
     * @throws Exception any exception that the is thrown by the generator
     */
    @Override
    protected final void preprocessHook(Source source, Class<?> klass, CompoundParams global) throws Exception {
        fields.clear();
        preprocess(source, klass, global);
    }

    @Override
    protected final void processFieldHook(Source source, Class<?> klass, CompoundParams global, Field[] fields)
            throws Exception {
        processSelectedFieldHook(source, klass, global, this.fields.toArray(new Field[this.fields.size()]));
    }

    /**
     * Implementations should override this method if they need a different default filter expression for the fields.
     *
     * @return the filter expression that is used when none is specified in the configuration in the class needing
     * the generated code.
     */
    protected String defaultFilterExpression() {
        return "true";
    }

    /**
     * Extending this interface can override this method adding extra functionality and keeping the signature and the
     * name of the abstract method {@link AbstractFieldsGenerator#process(Source, Class, CompoundParams, Field)}.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the class and also from the actual field.
     * @param fields the field that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator
     */
    protected void processSelectedFieldHook(Source source, Class<?> klass, CompoundParams global, Field[] fields)
            throws Exception {
        process(source, klass, global, fields);
    }

    /**
     * Extending this interface can override this method adding extra functionality and keeping the signature and the
     * name of the abstract method {@link AbstractFieldsGenerator#process(Source, Class, CompoundParams, Field)}.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param params the parameters collected from the class and also from the actual field. The parameters defined on
     *               the field annotation have precedence over the annotations on the class.
     * @param field  the field that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator     *
     */
    protected void processSelectedFieldHook(Source source, Class<?> klass, CompoundParams params, Field field)
            throws Exception {
        process(source, klass, params, field);
    }
}
