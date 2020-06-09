package javax0.geci.api;

/**
 * <p>A global generator is a generator that is executed to do some specific code generation, which is for the whole
 * application and not specific to some source. This interface defines a method {@code process()} (without arguments),
 * which is invoked after all the generators were executed for all the sources in all the phases.</p>
 *
 * <p>Global generators usually do nothing during the generator phases. Some of them may. This interface defines the
 * method {@link Generator#process(Source)} method as a do-nothing default method.</p>
 */
public interface GlobalGenerator extends Generator {
    /**
     * The default implementation of this method does nothing. Since this interface is the extension of the {@link
     * Generator} interface implementations may implement this method in case they need to do something specific for the
     * specific source in the specific phase. It may be some special code generation, or it may be just collecting some
     * information, which may later be used when the method {@link #process()} (note: no arguments) is invoked.
     *
     * @param source See the documentation of {@link Generator#process(Source)}
     */
    default void process(Source source){}

    /**
     * <p>GlobalGenerators have to implement this method. This method is invoked after the other generators were all
     * executed. This method can generate some global code.</p>
     *
     * <p>Note that this method has no argument, which also means that there is no {@link Source} object available for
     * this method. If a global generator needs a {@link Source} object (to call for example {@link
     * Source#newSource(String)} to generate a new source file) then the class has to implement the method {@link
     * #process(Source)} and store some of the source objects for later use.</p>
     */
    void process();
}
