package javax0.geci.api;

/**
 * Generators get source code information from the framework and generate code. File writing and inserting the
 * code to the existing source code and mixing the manual and generated code is up to the framework.
 */
@FunctionalInterface
public interface Generator {

    /**
     * Process the {@link Source} and presumably generate some code.
     * @param source the object representing the source code that can also be altered by the generation during the
     *               processing. The {@code source} object is not the raw source code, rather it represents it and
     *               the generator should use the API provided by the class {@link Source} to access the text of the
     *               code as a series of lines as {@link String} objects as well as to write generated code back to
     *               the source code. The actual file manipulation will be handled by the framework.
     */
    void process(Source source);
}
