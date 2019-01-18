package javax0.geci.api;

import static javax0.geci.api.Source.Set.set;

public interface Geci {

    /**
     * Add a new directory to the list of source directories that Geci should process.
     *
     * @param directory list of directories that can be used as alternatives. When looking for files the first is used
     *                  at start, if that fails then the second and so on.
     * @return {@code this}
     */
    Geci source(String... directory);

    /**
     * Add a new directory to the list of source directories that Geci should process. The set of source files will
     * belong to the set represented by the first argument and can later be referenced. This is needed when a
     * generator wants to open a new source but in a different source set than where the source worked up is.
     *
     * @param directory list of directories that can be used as alternatives. When looking for files the first is used
     *                  at start, if that fails then the second and so on.
     * @param set       identifies the source set with a name
     * @return {@code this}
     */
    Geci source(Source.Set set, String... directory);


    /**
     * Register the four standard maven directories as source sets. The source sets are also named with the
     * names
     *
     * <ul>
     *     <li>mainSource</li>
     *     <li>mainResource</li>
     *     <li>testSource</li>
     *     <li>testResource</li>
     * </ul>
     *
     * Note, that this single call to {@code source()} defines four source sets. Other calls to the different
     * overloaded versions of {@code source()} always define a single source set even though they may
     * have several {@code String} arguments that define directories. Those are alternative directories for the
     * one single source set. In this case, however, the call automatically defines four source sets that
     * correspond to the standard directories of a Maven project.
     *
     * @param maven and "empty" maven object or one that suffered already the call to {@code module()}
     *              method in case the code generator runs on a module of a multi-module maven
     *              project.
     * @return {@code this}
     */
    default Geci source(Source.Maven maven) {
        source(set("mainSource"), maven.mainSource());
        source(set("mainResources"), maven.mainResources());
        source(set("testSource"), maven.testSource());
        source(set("testResources"), maven.testResources());
        return this;
    }

    /**
     * Register one or more generator instances. (Probably instances of different generators.)
     *
     * @param generatorArr the generators to register
     * @return {@code this}
     */
    Geci register(Generator... generatorArr);

    /**
     * Run the code generation.
     *
     * @return {@code false} if the code generation did not produce any output. It means that the code was
     * already up to date.<br>
     * {@code true} when code was generated. When the code generation is executed
     * as a unit test this return value can be asserted and compilation may fail in case code was changed.
     * @throws Exception in case a generator could not finish its operation and throws exception. Generators
     * can only throw non-checked {@code RuntimeException} and it is recommended that they be
     * {@link GeciException}. The only checked exception that this method can in the current implementation
     * throw is {@code java.io.IOException} when some of the input files cannot be read or some of the output
     * files cannotbe written.
     */
    boolean generate() throws Exception;

}
