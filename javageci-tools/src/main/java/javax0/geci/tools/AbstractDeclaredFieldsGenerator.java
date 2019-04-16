package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import javax0.geci.api.Source;

import java.lang.reflect.Field;

/**
 * Generators that generate code using the fields are encouraged to extend this class. This abstract class will invoke
 * <ul>
 * <li> once {@link #preprocess(Source, Class, CompoundParams)}, then
 * </li>
 * <li> {@link #process(Source, Class, CompoundParams, Field)} for each declared field, and finally
 * </li>
 * <li> once {@link #postprocess(Source, Class, CompoundParams)}.
 * </li>
 * </ul>
 */
@Geci("copyClass copyTo='AbstractDeclaredMethodsGenerator.java'")
public abstract class AbstractDeclaredFieldsGenerator extends AbstractJavaGenerator {

    @Override
    public final void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        preprocessHook(source, klass, global);
        final var fields = GeciReflectionTools.getDeclaredFieldsSorted(klass);
        for (final var field : fields) {
            var params = GeciReflectionTools.getParameters(field, mnemonic());
            processFieldHook(source, klass, new CompoundParams(params, global), field);
        }
        processFieldHook(source, klass, global, fields);
        postprocessHook(source, klass, global);
    }

    /**
     * This method is invoked when the generator starts. The actual implementation has to perform initialization and
     * code generation that is to be done before processing the first field.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the {@code Geci} annotation on the class.
     * @throws Exception any exception that the is thrown by the generator
     */
    @SuppressWarnings("unused")
    public void preprocess(Source source, Class<?> klass, CompoundParams global) throws Exception {
    }

    /**
     * Hook method that should be overridden by extending abstract classes that intend to leave the override-ability
     * of the method {@link #preprocess(Source, Class, CompoundParams)} intact.
     * This way the extending abstract class can keep the signature of the method
     * {@link #preprocess(Source, Class, CompoundParams)}.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the {@code Geci} annotation on the class.
     * @throws Exception any exception that the is thrown by the generator
     */
    protected void preprocessHook(Source source, Class<?> klass, CompoundParams global) throws Exception {
        preprocess(source, klass, global);
    }

    /**
     * This method is invoked after the last field was processed. The actual implementation has to perform the last
     * code generation actions on the source object.
     *
     * @param source see {@link #preprocess(Source, Class, CompoundParams)}
     * @param klass  see {@link #preprocess(Source, Class, CompoundParams)}
     * @param global see {@link #preprocess(Source, Class, CompoundParams)}
     * @throws Exception any exception that the is thrown by the generator
     */
    @SuppressWarnings("unused")
    public void postprocess(Source source, Class<?> klass, CompoundParams global) throws Exception {
    }

    /**
     * Hook method that should be overridden by extending abstract classes that intend to leave the override-ability
     * of the method {@link #postprocess(Source, Class, CompoundParams)} intact.
     * This way the extending abstract class can keep the signature of the method
     * {@link #postprocess(Source, Class, CompoundParams)}.
     *
     * @param source see {@link #preprocess(Source, Class, CompoundParams)}
     * @param klass  see {@link #preprocess(Source, Class, CompoundParams)}
     * @param global see {@link #preprocess(Source, Class, CompoundParams)}
     * @throws Exception any exception that the is thrown by the generator
     */
    public void postprocessHook(Source source, Class<?> klass, CompoundParams global) throws Exception {
        postprocess(source, klass, global);
    }

    /**
     * Hook method that should be overridden by extending abstract classes that intend to leave the override-ability
     * of the method {@link #process(Source, Class, CompoundParams, Field)} intact.
     * This way the extending abstract class can keep the signature of the method
     * {@link #process(Source, Class, CompoundParams, Field)}.
     * <p>
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param params the parameters collected from the class and also from the actual field. The parameters defined on
     *               the field annotation have precedence over the annotations on the class.
     * @param field  the field that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator
     */
    protected void processFieldHook(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
        process(source, klass, params, field);
    }

    /**
     * Hook method that should be overridden by extending abstract classes that intend to leave the override-ability
     * of the method {@link #process(Source, Class, CompoundParams, Field)} intact.
     * This way the extending abstract class can keep the signature of the method
     * {@link #process(Source, Class, CompoundParams, Field)}.
     * <p>
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the class.
     * @param fields the fields that the process has to work on in a deterministic order
     * @throws Exception any exception that the is thrown by the generator
     */
    protected void processFieldHook(Source source, Class<?> klass, CompoundParams global, Field[] fields) throws Exception {
        process(source, klass, global, fields);
    }

    /**
     * This method is invoked for each {@code field}, which is declared in the class. The actual implementation has
     * to generate the code in this method that handles the very specific field, or it may just build up it's own
     * data structure that is to be used when the method {@link #postprocess(Source, Class, CompoundParams)} is invoked.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param params the parameters collected from the class and also from the actual field. The parameters defined on
     *               the field annotation have precedence over the annotations on the class.
     * @param field  the field that the process has to work on.
     * @throws Exception any exception that the is thrown by the generator
     */
    public void process(Source source, Class<?> klass, CompoundParams params, Field field) throws Exception {
    }

    ;

    /**
     * This method is invoked after all the invocation of {@link #process(Source, Class, CompoundParams, Field)} and
     * before the invocation of {@link #postprocess(Source, Class, CompoundParams)}.
     *
     * @param source see the documentation of the same name argument in
     *               {@link javax0.geci.api.Generator#process(Source)}
     * @param klass  see the documentation of the same name argument in
     *               {@link AbstractJavaGenerator#process(Source, Class, CompoundParams)}
     * @param global the parameters collected from the class.
     * @param fields the fields that the process has to work on in a deterministic order
     * @throws Exception any exception that the is thrown by the generator
     */
    public void process(Source source, Class<?> klass, CompoundParams global, Field[] fields) throws Exception {
    }
}