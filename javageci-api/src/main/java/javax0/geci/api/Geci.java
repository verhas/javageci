package javax0.geci.api;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public interface Geci {

    String MAIN_SOURCE = "mainSource";
    String MAIN_RESOURCES = "mainResources";
    String TEST_SOURCE = "testSource";
    String TEST_RESOURCES = "testResources";

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
     * Set the source set with the given name specified in the {@code nameAndSet} parameter that has the name as
     * well as the array of directory names.
     *
     * @param nameAndSet the name of the set and the directories array
     * @return {@code this}
     */
    default Geci source(Source.NamedSourceSet nameAndSet) {
        return source(nameAndSet.set, nameAndSet.directories);
    }


    /**
     * Register the four standard maven directories as source sets. The source sets are also named with the
     * names
     *
     * <ul>
     * <li>mainSource</li>
     * <li>mainResource</li>
     * <li>testSource</li>
     * <li>testResource</li>
     * </ul>
     * <p>
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
        source(maven.mainSource());
        source(maven.mainResources());
        source(maven.testSource());
        source(maven.testResources());
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
     * Set filters to filter the sources. The absolute file names of the
     * files are matched against the patterns and a file is only
     * processed by the generators if at least one regular expression
     * pattern matches the absolute file name.
     * <p>
     * The matching is done calling the regular expression matcher
     * method {@link Matcher#find()}. It means that the regular
     * expression pattern needs to match a substring in the file name
     * and does not need to match the whole file. This is because
     * usually the caller specifies a specific directory or package name
     * or the name of the source file. Using {@link Matcher#matches()}
     * would require adding {@code .*} to the start and to the end of
     * the patterns. On the other hand if the caller needs in a rare
     * case a matching aganst the whole absolute file name, then the
     * pattern can be started with the {@code ^} character and ended
     * with the {@code $} character. In this case the pattern is
     * probably already complex (at least presumably if we assume that
     * the pattern is operating system independent.)
     * <p>
     * It is recommended not to use this method, and restrict the
     * operation of a generator using annotations. The gain provided by
     * this filtering in terms of test execution speedup may not be
     * significant and on the other hand it makes the generator
     * configuration more complex, relying on file names and it is
     * fairly easy to induce operating system dependent errors.
     *
     * @param patterns regular expression patterns used to filter the
     *                 source files
     * @return {@code this}
     */
    Geci only(String... patterns);

    /**
     * Set filters to filter the sources. The paths are tested using the
     * predicates provided as argument. This method is more difficult to
     * use than the one that tests the absolute file names against
     * regular expression patterns, and thus will be used rarer. On the
     * other hand it gives more control into the hand of the caller to
     * filter out files.
     *
     * @param predicates regular expression patterns used to filter the
     *                   source files
     * @return {@code this}
     */
    Geci only(Predicate<Path>... predicates);


    /**
     * Run the code generation.
     *
     * @return {@code false} if the code generation did not produce any
     * output. It means that the code was already up to date.
     * {@code true} when code was generated. When the code
     * generation is executed as a unit test this return value
     * can be asserted and compilation may fail in case code was
     * changed.
     * @throws Exception in case a generator could not finish its
     *                   operation and throws exception. Generators can
     *                   only throw non-checked {@code RuntimeException}
     *                   and it is recommended that they be {@link
     *                   GeciException}. The only checked exception that
     *                   this method can in the current implementation
     *                   throw is {@code java.io.IOException} when some
     *                   of the input files cannot be read or some of
     *                   the output files cannot be written.
     */
    boolean generate() throws Exception;

}
