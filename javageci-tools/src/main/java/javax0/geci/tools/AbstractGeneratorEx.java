package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

/**
 * An abstract generator. Generators that do not want to implement the
 * interface {@link Generator} directly can extend this class and define
 * the method {@link #processEx(Source)} which is abstract in this
 * class. The method {@link #processEx(Source)} is almost the same as
 * the {@link Generator#process(Source)} method but it can throw an
 * exception.
 */
public abstract class AbstractGeneratorEx implements Generator {


    /**
     * Call the abstract method {@link #processEx(Source)} and catch any
     * exception and rethrow it embedded into a {@link GeciException},
     * which is a {@link RuntimeException}. That way the concrete
     * implementation of {@link #processEx(Source)} may just throw any
     * {@link Exception}. Note that {@link Exception} is caught and not
     * {@link Throwable}.
     *
     * @param source See the documentation {@link Generator#process(Source)}
     */
    @Override
    public final void process(Source source) {
        try {
            processEx(source);
        } catch (Exception e) {
            if (e instanceof GeciException) {
                throw (GeciException) e;
            } else {
                throw new GeciException("There was an "
                    + e.getClass().getSimpleName(), e);
            }
        }
    }

    /**
     * Concrete class extending this abstract class {@link
     * AbstractGeneratorEx} have to define this method. This method is
     * essentially the same as the method {@link
     * Generator#process(Source)} but this method can throw an
     * exception. (Hence the name has an {@code Ex} at the end.)
     *
     * @param source the source code the generator works on
     * @throws Exception any exception thrown by the generator
     */
    public abstract void processEx(Source source) throws Exception;
}
