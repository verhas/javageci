package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Source;

/**
 * An abtract generator. Generators that do not want to implement the interface {@link Generator} directly
 * can extend this class and define the method {@link #processEx(Source)} which is abstract in this class. The
 * method {@link #processEx(Source)} is almost the same as the {@link Generator#process(Source)} method but
 * it can throw an exception.
 */
public abstract class AbstractGeneratorEx implements Generator {
    @Override
    public void process(Source source) {
        try {
            processEx(source);
        } catch (Exception e) {
            throw new GeciException(e);
        }
    }

    /**
     * Concrete class extending this abstract class {@link AbstractGeneratorEx} have to define this method.
     * This method is essentially the same as the method {@link Generator#process(Source)} but this method
     * can throw an exception.
     * @param source the source code the generator works on
     * @throws Exception any exception thrown by the generator
     */
    public abstract void processEx(Source source) throws Exception;
}
