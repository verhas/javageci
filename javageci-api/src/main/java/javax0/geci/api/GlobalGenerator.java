package javax0.geci.api;

/**
 * <p>A global generator is a generator that is to execute a specific code generation, which is for the whole application.
 * It defines a method {@code process()} (without arguments), which is invoked after all the generators were executed
 * for all the sources in all the phases.</p>
 *
 * <p>Global generators usually do nothing, although they may, during the phases therefore this interface defines the
 * method {@link Generator#process(Source)} method as a do-nothing default method.</p>
 */
public interface GlobalGenerator extends Generator {
    default void process(Source source){}
    void process();
}
