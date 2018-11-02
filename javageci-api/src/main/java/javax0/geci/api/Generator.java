package javax0.geci.api;

/**
 * Generators get source code information from the framework and generate code. File writing and inserting the
 * code to the existing source code and mixing the manual and generated code is up to the framework.
 */
public interface Generator {

    void process(Source source);
}
