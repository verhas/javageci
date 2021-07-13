package javax0.geci.api;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public interface Geci {

    String MAIN_SOURCE = "mainSource";
    String MAIN_RESOURCES = "mainResources";
    String TEST_SOURCE = "testSource";
    String TEST_RESOURCES = "testResources";

    /**
     * Add a new directory to the list of source directories that Geci
     * should process.
     *
     * @param directory list of directories that can be used as
     *                  alternatives. When looking for files the first
     *                  is used at start, if that fails then the second
     *                  and so on.
     * @return {@code this}
     */
    Geci source(String... directory);

    /**
     * <p>Instruct the framework to create a trace file for this instance and
     * save the trace information into the file named by the argument.</p>
     *
     * <p>If the file already exists it will be overwritten.</p>
     *
     * <p>It is a good practice to save the trace file into the {@code target}
     * folder when a maven structure is used.</p>
     *
     * @param fileName the name of the file where the trace will be written.
     * @return {@code this}
     */
    Geci trace(final String fileName);

    /**
     * Instruct the framework to save a backup of the original source as well as the mdified source into a temporary
     * file so that some external tool can easily compare them.
     *
     * @param directoryName the name of the directory where to save the files
     * @return {@code this}
     */
    Geci diffOutput(final String directoryName);

    /**
     * Add a new directory to the list of source directories that Geci
     * should process. See {@link #source(String...)} and also {@link
     * #source(Source.Set, Predicate, String...)} on the use of the
     * predicate.
     *
     * @param predicate the predicate used to test the directory names
     * @param directory list of directories that can be used as
     *                  alternatives. When looking for files the first
     *                  is used at start, if that fails then the second
     *                  and so on.
     * @return {@code this}
     */
    Geci source(Predicate<String> predicate, String... directory);

    /**
     * Define the sources using an implementation of the interface
     * {@link DirectoryLocator}. Use this method if you want to have
     * tight and special control on how the directory for a given source
     * set is found. If you want to use the available implementation you
     * should use the other {@code source()} methods that implicitly use
     * the Geci implemented {@code javax0.geci.util.DirectoryLocator}.
     *
     * @param set     identifies the source set with a name
     * @param locator the directory locator instance
     * @return {@code this}
     */
    Geci source(Source.Set set, DirectoryLocator locator);

    /**
     * Same as {@link #source(Source.Set, DirectoryLocator)} but without
     * naming the souzrce set.
     *
     * @param locator the directory locator instance
     * @return {@code this}
     */
    Geci source(DirectoryLocator locator);

    /**
     * Add a new directory to the list of source directories that Geci
     * should process. The set of source files will belong to the set
     * represented by the first argument and can later be referenced.
     * This is needed when a generator wants to open a new source but in
     * a different source set than where the source worked up is.
     *
     * @param directory list of directories that can be used as
     *                  alternatives. When looking for files the first
     *                  is used at start, if that fails then the second
     *                  and so on.
     * @param set       identifies the source set with a name
     * @return {@code this}
     */
    Geci source(Source.Set set, String... directory);

    /**
     * Add a new directory to the list of source directories that Geci
     * should process. See {@link #source(Source.Set, String...)}.
     *
     * <p>When identifying the actual directory instead of checking that
     * the directory exists use the provided predicate. Note that it is
     * an error if the predicate tests {@code true} but the directory
     * does not exist or is not a directory.
     *
     * @param directory list of directories that can be used as
     *                  alternatives. When looking for files the first
     *                  is used at start, if that fails then the second
     *                  and so on.
     * @param predicate the predicate used to test the directory names
     * @param set       identifies the source set with a name
     * @return {@code this}
     */
    Geci source(Source.Set set, Predicate<String> predicate, String... directory);

    /**
     * Register a new {@link SegmentSplitHelper} associated with the
     * file name extension. When a source file is split up into segments
     * it is done by the class {@link javax0.geci.api.Source} using
     * segment split helpers. Different files may have different
     * helpers. The file collector keeps track of the different helpers
     * and provide it to the source to help the splitting process.
     *
     * <p> It is an error to register a helper for an extension that is
     * already registered.
     *
     * <p>Note that the {@code java} extension has a default segment
     * split helper. It is not an error to register a different helper
     * for the {@code java} extension, but it is still an error to
     * register another one after that.
     *
     * @param fileNameExtension the file name extension to which the
     *                          helper will be registered.
     * @param helper            the helper associated to the
     *                          fileNameExtension
     * @return {@code this}
     */
    Geci splitHelper(String fileNameExtension, SegmentSplitHelper helper);

    /**
     * Set the source set with the given name specified in the {@code
     * nameAndSet} parameter that has the name as well as the array of
     * directory names. Using this method you can simply specify the
     * source set as, for example {@code geci.source(maven.mainSource());}.
     *
     * @param nameAndSet the name of the set and the directories array
     * @return {@code this}
     */
    default Geci source(Source.NamedSourceSet nameAndSet) {
        return source(nameAndSet.set, nameAndSet.directories);
    }

    /**
     * Set the source set with the given name specified in the
     * {@code set} parameter but use the array of directory
     * names from the parameter {@code nameAndSet}. Using this method
     * you can simply specify the source set as, for
     * example {@code geci.source(set("otherName"), maven.mainSource());}.
     *
     * @param set        the set that contains the name of the set
     * @param nameAndSet the name of the set and the directories array
     * @return {@code this}
     */
    default Geci source(Source.Set set, Source.NamedSourceSet nameAndSet) {
        return source(set, nameAndSet.directories);
    }

    /**
     * Set the source set with the given name specified in the
     * {@code set} parameter but use the array of directory
     * names from the parameter {@code nameAndSet}. Using this method
     * you can simply specify the source set as, for
     * example {@code geci.source("otherName", maven.mainSource());}.
     *
     * @param set        the set name as string
     * @param nameAndSet the name of the set and the directories array
     * @return {@code this}
     */
    default Geci source(String set, Source.NamedSourceSet nameAndSet) {
        return source(Source.Set.set(set), nameAndSet.directories);
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
    Geci source(Source.Maven maven);


    /**
     * Register one or more generator instances. (Probably instances of different generators.)
     *
     * @param generatorArr the generators to register
     * @return {@code this}
     */
    Geci register(Generator... generatorArr);

    /**
     * <p>This is a convenience version of the register method that simply
     * calls the {@code build()} on the arguments and then registers the
     * generators. This is simply to make the code less repetitive
     * not requiring to write {@code .build()} at the end of the builder
     * chain when registering a generator created using a builder.</p>
     *
     * <p>This method can also be used by tools that generate a list of
     * configured generators to be registered. For example it can be used
     * to accept the array of generator builders created by
     * {@code javax0.geci.docugen.Register.allSnippetGenerators()}</p>
     *
     * @param generatorBuilders the builders that contain a built
     *                          generator waiting only to call the
     *                          {@code build()} method on them
     * @return {@code this}
     */
    default Geci register(GeneratorBuilder... generatorBuilders) {
        final var generators = Arrays.stream(generatorBuilders)
            .map(GeneratorBuilder::build)
            .toArray(Generator[]::new);
        return register(generators);
    }

    /**
     * Add filters to filter the sources. The absolute file names of the
     * files are matched against the patterns, and a file is only
     * processed by the generators if at least one regular expression
     * pattern matches the absolute file name.
     * <p>
     * The matching is done calling the regular expression matcher
     * method {@link Matcher#find()}. It means that the regular
     * expression pattern needs to match a substring in the file name,
     * and does not need to match the whole file name. This is because
     * usually the caller specifies a specific directory or package name,
     * or the name of the source file. Using {@link Matcher#matches()}
     * would require adding {@code .*} to the start and to the end of
     * the patterns. On the other hand if the caller needs in a rare
     * case a matching against the whole absolute file name, then the
     * pattern can be started with the {@code ^} character and ended
     * with the {@code $} character. In this case the pattern is
     * probably already complex, at least presumably if we assume that
     * the pattern is operating system independent.
     * <p>
     * It is recommended not to use this method, and restrict the
     * operation of a generator using annotations. The gain provided by
     * this filtering in terms of test execution speedup may not be
     * significant and on the other hand it makes the generator
     * configuration more complex, relying on file names, and it is
     * fairly easy to induce operating system dependent errors.
     * <p>
     * This method can be called more than one time. Every time the
     * patterns will be added to the already configured patterns.
     *
     * @param patterns regular expression patterns used to filter the
     *                 source files
     * @return {@code this}
     */
    Geci only(String... patterns);

    /**
     * This method is the opposite of the method {@link #only(String...)}.
     * Using this method you can specify patterns to be ignored by the
     * source collections.
     *
     * @param patterns regular expression patterns used to filter the
     *                 source files
     * @return {@code this}
     */
    Geci ignore(String... patterns);

    /**
     * Ignore the files that cannot be read as text files.
     *
     * <p>The project directory may contain files that are not text
     * files, like png, pdf and so on. These files cannot be processed
     * by the framework, because the files are read in text mode. By
     * default in cases like that the source that cannot be read will be
     * excluded from the processing and at the end of the processing an
     * exception will be thrown listing all the files that were binary.
     * </p>
     *
     * <p>Using this method the exception can be suppressed. In that
     * case having a non-processable binary file in the source directory
     * will silently be ignored. </p>
     *
     * <p>Note that there is an inherent risk ignoring all files that
     * seem to be binary for the framework. It may happen that a file
     * that was not supposed to be ignored will silently be ignored. A
     * safer approach is to use the {@link #ignore(String...)} method
     * and explicitly exclude patterns like {@code "\.png$"}.</p>
     *
     * @return {@code this}
     */
    Geci ignoreBinary();

    /**
     * This method declares that certain sets are output sets. It means
     * that they are available for the generators to create new sources
     * in the set, but the sources are not collected from these sets and
     * no generator will be executed for any source file in these source
     * sets.
     *
     * @param sets the sets that are for output only and will not be
     *             used to collect source files.
     * @return {@code this}
     */
    Geci output(Source.Set... sets);

    /**
     * This method declares that the last source set defined is an
     * output set. It means that it is available for the generators to
     * create new sources in the set, but the sources are not collected
     * from the set and no generator will be executed for any source
     * file in the source set.
     *
     * @return {@code this}
     */
    Geci output();

    /**
     * Set filters to filter the sources. The paths are tested using the
     * predicates provided as argument. This method is more difficult to
     * use than the one that tests the absolute file names against
     * regular expression patterns, and thus will be used rarer. On the
     * other hand it gives more control into the hand of the caller to
     * filter out files.
     *
     * @param predicates used to filter the source files
     * @return {@code this}
     */
    Geci only(Predicate<Path>... predicates);

    /**
     * Set filters to filter the sources. The paths are tested using the
     * predicates provided as argument. This method is more difficult to
     * use than the one that tests the absolute file names against
     * regular expression patterns, and thus will be used rarer. On the
     * other hand it gives more control into the hand of the caller to
     * filter out files.
     *
     * @param predicates used to filter the source files. If a predicate matches
     *                   a source file it will not be collected to the source set
     *                   the generators work on.
     * @return {@code this}
     */
    Geci ignore(Predicate<Path>... predicates);

    /**
     * Set the source code comparator. This comparator returns {@code true}
     * if the source code was changed. The arguments to the {@code isModified}
     * bi-predicate are the lists of strings that contain the source code
     * as it was read from the disk (first argument) and as it is after the
     * code generation (second argument).
     *
     * <p>If this method is not invoked then the implementation of the interface uses
     * a bi-comparator that simply checks that the number of the lines are the
     * same and all the lines are equal.
     *
     * <p>There can be more relaxed implementations for certain application. If it is
     * known that the source code is Java code then formatting may be neglected and
     * the predicate may return {@code false} even if the codes are not the same but
     * the difference is only formatting and white space.
     *
     * @param isModified the bi predicate that compares the source code before and after code
     *                   generation
     * @return {@code this}
     */
    Geci comparator(BiPredicate<List<String>, List<String>> isModified);

    /**
     * Get the context of the Geci object that is injected into the generators. This method can be used in the special
     * case of different generators should share the same context. In that case one Geci object can be used to execute
     * one generator and another can be used to execute other generators using the same context.
     *
     * <p>For example the application wants to run the {@code SnippetCollector} code generator only on Java source files
     * and thus the {@code source()} method calls define the source sets for the Java files only.
     *
     * <p>The same application want to insert the snippets into asciidoc files and using a separate Geci object is
     * configured for the source sets that contain only the asciidoc files to run the generator {@code
     * AsciidocCodeInserter} (it does not exist yet).
     *
     * <p>The collected snippets are stored in objects referenced by the context of the Geci object. This method can be
     * used to get access to the context object after the first Geci object {@code generate()} returned and the {@link
     * #context(Context)} method can be used on the next Geci object (configured for different source sets) to inject the
     * contex.
     *
     * @return the context object
     */
    Context context();

    /**
     * Inject a context into the Geci object. In case a contex object is not injected into the Geci object before the
     * method {@link #generate()} is invoked then a context will automatically be created. If there was a non-null
     * context object injected then the one injected will be used.
     *
     * @param context the object to be injected into the Geci object.
     * @return this
     */
    Geci context(Context context);


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
