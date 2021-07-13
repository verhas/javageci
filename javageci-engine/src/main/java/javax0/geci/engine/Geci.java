package javax0.geci.engine;

import javax0.geci.api.Context;
import javax0.geci.api.Distant;
import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.GlobalGenerator;
import javax0.geci.api.Segment;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.api.Source;
import javax0.geci.engine.Source.SourceIsBinary;
import javax0.geci.javacomparator.Comparator;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.Tracer;
import javax0.geci.util.DirectoryLocator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax0.geci.api.Source.Predicates.exists;
import static javax0.geci.api.Source.Set.set;

public class Geci implements javax0.geci.api.Geci {
    /**
     * A simple constant text that can be used in the unit tests to
     * report that the code was changed.
     */
    public static final String FAILED = "Geci modified source code. Please compile and test again.";
    public static final int ALL = 0x00;
    public static final int MODIFIED = ~0x01;
    public static final int TOUCHED = ~0x02;
    public static final int UNTOUCHED = ~0x04;
    public static final int NONE = 0xFF;
    private static final Logger LOG = LoggerFactory.getLogger();
    private final Map<Source.Set, javax0.geci.api.DirectoryLocator> directories = new HashMap<>();
    private final Set<Generator> generators = new HashSet<>();
    private final Set<Source> modifiedSources = new HashSet<>();
    private final Map<String, SegmentSplitHelper> splitHelpers = new HashMap<>();
    private final Set<Predicate<Path>> onlys = new HashSet<>();
    private final Set<Predicate<Path>> ignores = new HashSet<>();
    private int whatToLog = MODIFIED & TOUCHED;
    private BiPredicate<List<String>, List<String>> sourceComparator = null;
    public static final BiPredicate<List<String>, List<String>> EQUALS_COMPARATOR = (orig, gen) -> !orig.equals(gen);
    public static final BiPredicate<List<String>, List<String>> JAVA_COMPARATOR = new Comparator();
    public static final BiPredicate<List<String>, List<String>> JAVA_COMPARATOR_COMMENT = new Comparator().commentSensitive();
    private final Set<Source.Set> outputSet = new HashSet<>();
    private Source.Set lastSet = null;
    private boolean ignoreBinary = false;
    private String traceFileName = null;
    private String diffDirectory = null;

// Jdocify constants to be used in Javadoc
//DEFINE EXCEPTIONS=the list of exceptions that are caught and suppressed
//DEFINE PHASES=the number of the phases to execute the generators in
//DEFINE PHASE=the serial number of the current phase between {@code 0} and {@code phases-1}
//DEFINE COLLECTOR=the object where the sources are collected and kept
//DEFINE SOURCE=the source object that the generator works on
//DEFINE GENERATOR=to be invoked

    @Override
    public Geci source(String... directory) {
        return source(exists(), directory);
    }

    @Override
    public Geci source(Predicate<String> predicate, String... directory) {
        source(set(), new DirectoryLocator(predicate, directory));
        lastSet = null;
        return this;
    }

    @Override
    public Geci source(javax0.geci.api.DirectoryLocator locator) {
        source(set(), locator);
        lastSet = null;
        return this;
    }

    @Override
    public Geci source(Source.Set set, javax0.geci.api.DirectoryLocator locator) {
        lastSet = set;
        directories.put(set, locator);
        return this;
    }

    /**
     * Java::Geci logs the absolute file names of the files it processes
     * along with the result of the processing. There are three results
     * in case of a source file:
     *
     * <ul>
     *
     * <li>The file was scanned, but there was nothing in  the file that
     * indicated the use of a code generator. The file is an ordinary
     * source file, that needs no code generation. These sources are
     * {@code UNTOUCHED}.</li>
     *
     * <li>The file was scanned and the framework realized that there
     * are one or more registered code generators that need to process
     * the source code, but none of these code generators modified the
     * source code. These sources are {@code TOUCHED}.</li>
     *
     * <li>The file was modified by one or more code generators. These
     * files are {@code MODIFIED}.</li>
     *
     * </ul>
     * <p>
     * Note that when none of the scanned files are {@code TOUCHED} it
     * is considered as an error and Geci will throw an exception. This
     * is because there is no reason to execute a code generator set on
     * a source set that does not need code generation and in case none
     * of the sources are touched it is likely a configuration error in
     * the unit test starting the generators.
     *
     * <p>
     * Using this method you can specify what you want to see in the log
     * of Geci information output.
     *
     * @param whatToLog is a bitwise combination of the constants {@code
     *                  Geci.MODIFIED}, {@code Geci.TOUCHED} and {@code
     *                  Geci.UNTOUCHED}. The default value is {@code
     *                  MODIFIED&TOUCHED} that will instruct the logger
     *                  to log the source files that were either
     *                  modified or touched (considered for modification
     *                  but the code to be generated was already there.)
     *                  If you want to switch off this information
     *                  logging then you should use {@code
     *                  log(Geci.NONE)}.
     * @return {@code this}
     */
    public Geci log(int whatToLog) {
        this.whatToLog = whatToLog;
        return this;
    }

    /**
     * You can use this method to get a longer error message that
     * includes the names of the source files that were modified. A
     * simple constant text is {@link #FAILED}.
     *
     * @return the detailed error message
     */
    public String failed() {
        final var sb = new StringBuilder();
        sb.append('\n');
        sb.append(FAILED).append('\n');
        sb.append('\n');
        sb.append(String.format("The file%s that %s modified:",
            modifiedSources.size() > 1 ? "s" : "",
            modifiedSources.size() > 1 ? "were" : "was")).append('\n');
        for (final var source : modifiedSources) {
            sb.append(source.getAbsoluteFile()).append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    @Override
    public Geci source(Source.Set set, String... directory) {
        return source(set, exists(), directory);
    }

    @Override
    public Geci source(Source.Set set, Predicate<String> predicate, String... directory) {
        lastSet = set;
        if (directories.containsKey(set)) {
            set.tryRename();
            if (directories.containsKey(set)) {
                throw new GeciException("The set '" + set + "' is defined more than once.");
            }
        }
        directories.put(set, new DirectoryLocator(predicate, directory));
        return this;
    }

    @Override
    public Geci splitHelper(String fileNameExtension, SegmentSplitHelper helper) {
        if (fileNameExtension.startsWith(".")) {
            fileNameExtension = fileNameExtension.substring(1);
        }
        if (splitHelpers.containsKey(fileNameExtension)) {
            throw new GeciException(fileNameExtension + " already has an associated SegmentSplitHelper.");
        }
        splitHelpers.put(fileNameExtension, helper);
        return this;
    }

    /**
     * See {@link FileCollector#lenient() FileCollector.lenient}
     */
    private boolean lenient = false;

    @Override
    public Geci source(Source.Maven maven) {
        source(maven.mainSource());
        source(maven.mainResources());
        source(maven.testSource());
        source(maven.testResources());
        lenient = true;
        lastSet = null;
        return this;
    }

    @Override
    public Geci register(Generator... generatorArr) {
        Collections.addAll(generators, generatorArr);
        return this;
    }

    @Override
    public Geci only(String... patterns) {
        Collections.addAll(this.onlys,
            Arrays.stream(patterns)
                .map(PatternPredicate::new)
                .toArray((IntFunction<Predicate<Path>[]>) Predicate[]::new));
        return this;
    }

    @Override
    public Geci output(javax0.geci.engine.Source.Set... sets) {
        outputSet.addAll(Arrays.asList(sets));
        return this;
    }

    @Override
    public Geci output() {
        if (lastSet == null) {
            throw new GeciException("Source set not defined but declared as output calling Geci.source()");
        }
        outputSet.add(lastSet);
        return this;
    }

    public Geci ignoreBinary() {
        ignoreBinary = true;
        return this;
    }

    public Geci ignore(String... patterns) {
        Collections.addAll(this.ignores,
            Arrays.stream(patterns)
                .map(PatternPredicate::new)
                .toArray((IntFunction<Predicate<Path>[]>) Predicate[]::new));
        return this;
    }

    /**
     * A simple utility class that converts the regular expression into
     * a predicate, and the same time the {@code toString()} of the
     * object returns the regular expression itself to aid debugging.
     */
    private static class PatternPredicate implements Predicate<Path> {
        private final Predicate<Path> predicate;
        private final String regex;
        private final Pattern pattern;

        private PatternPredicate(String regex) {
            this.pattern = Pattern.compile(regex);
            this.predicate = s -> pattern.matcher(FileCollector.toAbsolute(s)).find();
            this.regex = regex;
        }

        @Override
        public boolean test(Path path) {
            return predicate.test(path);
        }

        @Override
        public String toString() {
            return regex;
        }
    }

    @Override
    @SafeVarargs
    public final javax0.geci.api.Geci only(Predicate<Path>... onlys) {
        Collections.addAll(this.onlys, onlys);
        return this;
    }

    @Override
    @SafeVarargs
    public final javax0.geci.api.Geci ignore(Predicate<Path>... ignores) {
        Collections.addAll(this.ignores, ignores);
        return this;
    }

    private BiPredicate<List<String>, List<String>> getSourceComparator(Source source) {
        if (sourceComparator == null) {
            if (source.getAbsoluteFile().endsWith(".java")) {
                if (isCommentTouched(source)) {
                    return JAVA_COMPARATOR_COMMENT;
                } else {
                    return JAVA_COMPARATOR;
                }
            } else {
                return EQUALS_COMPARATOR;
            }
        } else {
            return sourceComparator;
        }
    }

    private static boolean isCommentTouched(final Source source) {
        if (source instanceof javax0.geci.engine.Source) {
            final var src = (javax0.geci.engine.Source) source;
            return (src.getTouchBits() & Segment.COMMENT_TOUCH) > 0;
        }
        return false;
    }

    @Override
    public Geci comparator(BiPredicate<List<String>, List<String>> sourceComparator) {
        this.sourceComparator = sourceComparator;
        return this;
    }

    private void setDefaultDirectories() {
        source(Source.maven());
    }

    private void traceDirectories() {
        Tracer.push("SourceSets to be collected");
        for (final var directory : directories.entrySet()) {
            final var set = directory.getKey();
            final var locator = directory.getValue();
            Tracer.log(set.toString() + " set with alternative directory locations ["
                + locator.alternatives().collect(Collectors.joining(",")) + "]");
        }
        Tracer.pop();
    }

    @Override
    public Geci trace(final String fileName) {
        Tracer.on();
        traceFileName = fileName;
        return this;
    }

    @Override
    public javax0.geci.api.Geci diffOutput(String directoryName) {
        diffDirectory = directoryName;
        return this;
    }

    @Override
    public boolean generate() throws IOException {
        try {
            final var exceptions = new ArrayList<SourceIsBinary>();
            injectContextIntoGenerators();
            final var phases = getPhases();
            Tracer.log("There will be " + phases + " phases.");
            final FileCollector collector;
            if (directories.isEmpty()) {
                Tracer.log("There are no configured directories, using the default");
                setDefaultDirectories();
                collector = new FileCollector(directories);
                collector.lenient();
            } else {
                traceDirectories();
                collector = new FileCollector(directories);
                if (lenient) {
                    collector.lenient();
                }
            }
            Tracer.push("Registering split helpers");
            collector.registerSplitHelpers(splitHelpers);
            Tracer.pop();
            Tracer.push("SourceCollect", "Collecting sources");
            collector.collect(onlys, ignores, outputSet);
            Tracer.pop();

            invokeGeneratorsOnAllSourcesForAllPhases(exceptions, phases, collector);
            invokeGlobalGenerators();
            logAndThrowDeferredExceptionsIfAny(exceptions);
            consolidateSources(collector);

            return sourcesModifiedAndSave(collector);
        } finally {
            dumpCollectedTracesAsXML();
        }
    }

    /**
     * <p>Get the maximum number that the different generators report as the number of phases they need.</p>
     *
     * <p>The generators report the number of phases they need as the return value of their {@code n=}{@link
     * Generator#phases() phases()} method. This means that they have to be invoked several times from phase zero till
     * phase {@code n-1}.</p>
     *
     * @return the largest number of phases all the generators need
     */
    private int getPhases() {
        return generators.stream()
            .mapToInt(Generator::phases)
            .max()
            .orElse(1);
    }

    /**
     * <p>Create the hierarchical trace log information in XML format into the trace file.</p>
     */
    private void dumpCollectedTracesAsXML() {
        if (traceFileName != null) {
            try {
                Tracer.dumpXML(traceFileName);
            } catch (IOException e) {
                LoggerFactory.getLogger().error("Trace cannot be written into '" + traceFileName + "'", e);
            }
        }
    }

    /**
     * <p>Consolidate all the sources and throw an exception if the generators did not touch any source though they
     * should have.</p>
     *
     * @param collector <!--COLLECTOR-->the object where the sources are collected and kept<!--/-->
     */
    private void consolidateSources(FileCollector collector) {
        if (!sourcesConsolidate(collector)) {
            if (generators.stream().anyMatch(g -> !(g instanceof Distant))) {
                throw new GeciException("The generators did not touch any source");
            }
        }
    }

    /**
     * <p>Log the exceptions that the different generators were throwing and then create a compound exception
     * containing the messages of all the exceptions and throw this aggregate.</p>
     *
     * <p>Return only if there was no any exceptions.</p>
     *
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     */
    private void logAndThrowDeferredExceptionsIfAny(List<SourceIsBinary> exceptions) {
        if (exceptions.size() > 0 && !ignoreBinary) {
            try (final var pos = Tracer.push("Exceptions")) {
                exceptions.forEach(Tracer::log);
            }
            final var geciE = new GeciException("Cannot read the files\n" +
                exceptions.stream().map(SourceIsBinary::getAbsoluteFile).collect(Collectors.joining("\n")) +
                "\nThey are probably binary file. Use '.ignore()' to filter binary files out");
            exceptions.forEach(geciE::addSuppressed);
            throw geciE;
        }
    }

    private void invokeGlobalGenerators() {
        try (final var pos1 = Tracer.push("GlobalGenerators", null)) {
            for (var generator : generators) {
                if (generator instanceof GlobalGenerator) {
                    try (final var pos2 = Tracer.push("GlobalGenerator." + generator.getClass().getSimpleName(), generator.getClass().getName())) {
                        ((GlobalGenerator) generator).process();
                    }
                }
            }
        }
    }

    /**
     * Invoke all the generators for all the sources that were collected by the collector for all the phases from {@code
     * 0} to {@code phases-1}.
     *
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     * @param phases     <!--PHASES-->the number of the phases to execute the generators in<!--/-->
     * @param collector  <!--COLLECTOR-->the object where the sources are collected and kept<!--/-->
     */
    private void invokeGeneratorsOnAllSourcesForAllPhases(ArrayList<SourceIsBinary> exceptions, int phases, FileCollector collector) {
        for (int phase = 0; phase < phases; phase++) {
            try (final var posPhase = Tracer.push("Phase", "Starting phase " + phase)) {
                invokeGeneratorsOnAllSources(collector, exceptions, phase);
            }
        }
    }

    /**
     * Invoke the generators on all the sources that the collector collected.
     *
     * @param collector  <!--COLLECTOR-->the object where the sources are collected and kept<!--/-->
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     * @param phase      <!--PHASE-->the serial number of the current phase between {@code 0} and {@code phases-1}<!--/-->
     */
    private void invokeGeneratorsOnAllSources(FileCollector collector, List<SourceIsBinary> exceptions, int phase) {
        for (final var source : collector.getSources()) {
            try (final var posSource = Tracer.push("Source", source.getAbsoluteFile())) {
                invokeGeneratorsOnNonBinary(source, exceptions, phase);
            }
        }
    }

    /**
     * Invoke all generators on sources that are not binary. In case the source seems to be binary then only trace the
     * fact that no generators are invoked.
     *
     * @param source     <!--SOURCE-->the source object that the generator works on<!--/-->
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     * @param phase      <!--PHASES-->the number of the phases to execute the generators in<!--/-->
     */
    private void invokeGeneratorsOnNonBinary(javax0.geci.engine.Source source, List<SourceIsBinary> exceptions, int phase) {
        if (!source.isBinary) {
            try (final var posGenerators = Tracer.push("Generators", null)) {
                invokeGenerators(source, exceptions, phase);
            }
        } else {
            Tracer.log(source.getAbsoluteFile() + " seems to be binary, skipped");
        }
    }

    /**
     * Invoke all configured generators in a loop.
     *
     * @param source     <!--SOURCE-->the source object that the generator works on<!--/-->
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     * @param phase      <!--PHASES-->the number of the phases to execute the generators in<!--/-->
     */
    private void invokeGenerators(javax0.geci.engine.Source source, List<SourceIsBinary> exceptions, int phase) {
        for (var generator : generators) {
            try (final var posGenerator = Tracer.push("Generator." + generator.getClass().getSimpleName(), generator.getClass().getName())) {
                invokeGeneratorIfActiveInPhase(generator, source, exceptions, phase);
            }
        }
    }

    /**
     * Invoke the generator if it is active in this phase. If the generator is not active in this phase then just trace
     * that the generator was not invoked.
     *
     * @param generator  <!--GENERATOR><!--/-->
     * @param source     <!--SOURCE-->the source object that the generator works on<!--/-->
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     * @param phase      <!--PHASE-->the serial number of the current phase between {@code 0} and {@code phases-1}<!--/-->
     */
    private void invokeGeneratorIfActiveInPhase(Generator generator, javax0.geci.engine.Source source, List<SourceIsBinary> exceptions, int phase) {
        if (generator.activeIn(phase)) {
            Tracer.log("ACTIVE");
            source.allowDefaultSegment = false;
            source.currentGenerator = generator;
            invokeGenerator(generator, source, exceptions);
        } else {
            Tracer.log("INACTIVE");
        }
    }

    /**
     * Invoke the generator and handle the exceptions. In case the generator throws an {@link
     * javax0.geci.engine.Source.SourceIsBinary} exception then trace it and add it to the list of exceptions.
     * <p>
     * In case the generator throws a different {@link GeciException} then add the source definition to it and rethrow
     * it. (Technically it creates a new {@link SourcedGeciException} from the original exception.)
     *
     * @param generator  <!--GENERATOR><!--/-->
     * @param source     <!--SOURCE-->the source object that the generator works on<!--/-->
     * @param exceptions <!--EXCEPTIONS-->the list of exceptions that are caught and suppressed<!--/-->
     */
    private void invokeGenerator(Generator generator, javax0.geci.engine.Source source, List<SourceIsBinary> exceptions) {
        try {
            generator.process(source);
        } catch (javax0.geci.engine.Source.SourceIsBinary e) {
            Tracer.log("source processing failed, it is a binary file");
            exceptions.add(e);
        } catch (GeciException e) {
            throw new SourcedGeciException(source, e);
        }
    }

    /**
     * Save the sources that were modified and return true if there was any source that was modified and thus saved.
     *
     * @param collector <!--COLLECTOR-->the object where the sources are collected and kept<!--/-->
     * @return {@code true} if there was something saved
     * @throws IOException when some file cannot be written
     */
    private boolean sourcesModifiedAndSave(FileCollector collector) throws IOException {
        try (final var pos = Tracer.push("Save", null)) {
            var generated = false;
            var allSources = Stream.concat(
                collector.getSources().stream(),
                collector.getNewSources().stream()
            ).collect(Collectors.toSet());
            for (var source : allSources) {
                try {
                    final var comparator = getSourceComparator(source);
                    final var modified = source.isModified(comparator);
                    if (source.isTouched() && modified) {
                        Tracer.log("SaveSource", source.getAbsoluteFile());
                        source.save();
                        modifiedSources.add(source);
                        generated = true;
                    } else {
                        Tracer.log("SourceUnchanged", source.getAbsoluteFile());
                    }
                    // if we create diff, and they are different even if not different enough to fail
                    if (diffDirectory != null && ((comparator == EQUALS_COMPARATOR && modified) || source.isModified(EQUALS_COMPARATOR))) {
                        createDiffFiles(source);
                    }
                } catch (GeciException e) {
                    throw new SourcedGeciException(source, e);
                }
            }
            for (var source : Stream.concat(collector.getSources().stream(), collector.getNewSources().stream()).collect(Collectors.toSet())) {
                if (modifiedSources.contains(source)) {
                    if ((whatToLog & ~MODIFIED) == 0) {
                        LOG.info("MODIFIED  '%s'", source.getAbsoluteFile());
                        logSourceMessages(source);
                    }
                } else {
                    if (source.isTouched()) {
                        if ((whatToLog & ~TOUCHED) == 0) {
                            LOG.info("TOUCHED   '%s'", source.getAbsoluteFile());
                            logSourceMessages(source);
                        }
                    } else {
                        if ((whatToLog & ~UNTOUCHED) == 0) {
                            LOG.info("UNTOUCHED '%s'", source.getAbsoluteFile());
                            logSourceMessages(source);
                        }
                    }
                }
            }
            return generated;
        }
    }

    private void createDiffFiles(javax0.geci.engine.Source source) throws IOException {
        Path pathOriginal = Paths.get(diffDirectory + "/geci/original", source.relativeFile);
        assertDirectoryExistsForPath(pathOriginal);
        Path pathNew = Paths.get(diffDirectory + "/geci/generated", source.relativeFile);
        assertDirectoryExistsForPath(pathNew);
        Files.write(pathOriginal, source.originals, StandardCharsets.UTF_8);
        Files.write(pathNew, source.getLines(), StandardCharsets.UTF_8);
    }

    private void assertDirectoryExistsForPath(Path path) {
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (Exception ignored) {
            }
        }
    }

    private void logSourceMessages(javax0.geci.engine.Source source) {
        for (var entry : source.logEntries) {
            final String generatorId;
            if (entry.generator instanceof AbstractJavaGenerator) {
                generatorId = ((AbstractJavaGenerator) entry.generator).mnemonic();
            } else {
                generatorId = entry.generator.getClass().getSimpleName();
            }
            switch (entry.level) {
                case SourceLogger.TRACE:
                    LOG.trace(generatorId + ":" + entry.message);
                    break;
                case SourceLogger.DEBUG:
                    LOG.debug(generatorId + ":" + entry.message);
                    break;
                case SourceLogger.INFO:
                    LOG.info(generatorId + ":" + entry.message);
                    break;
                case SourceLogger.WARNING:
                    LOG.warning(generatorId + ":" + entry.message);
                    break;
                case SourceLogger.ERROR:
                    LOG.error(generatorId + ":" + entry.message);
                    break;
            }
        }
    }

    /**
     * <p>Go through all the old (already existing) sources and also all the new sources (those that are generated from
     * other sources and did not necessarily existed before) and consolidate them (create the final source code from the
     * segments).</p>
     *
     * @param collector <!--COLLECTOR-->the object where the sources are collected and kept<!--/-->
     * @return {@code true} if at least one source was touched.
     */
    private boolean sourcesConsolidate(FileCollector collector) {
        try (final var pos1 = Tracer.push("SourceConsolidation", null)) {
            var touched = false;
            try (final var pos2 = Tracer.push("OldSources", null)) {
                for (var source : collector.getSources()) {
                    source.consolidate();
                    touched = touched || source.isTouched();
                    Tracer.log("Source", (source.isTouched() ? "[TOUCHED]" : "") + source.getAbsoluteFile());
                }
            }
            try (final var pos2 = Tracer.push("NewSources", null)) {
                for (var source : collector.getNewSources()) {
                    source.consolidate();
                    touched = touched || source.isTouched();
                    Tracer.log("Source", (source.isTouched() ? "[TOUCHED]" : "") + source.getAbsoluteFile());
                }
            }
            Tracer.log("Result", "some sources are " + (touched ? "touched" : "virgin"));
            return touched;
        }
    }


    private Context context = null;

    public Context context() {
        return context;
    }

    public Geci context(Context context) {
        Tracer.log("Setting context for Geci engine");
        this.context = context;
        return this;
    }

    /**
     * <p>Every generator has access to a context object. (For context, what is a context see {@link
     * Context}.</p>
     *
     * <p>This method checks if there is any context already injected into the Geci object and then injects the context
     * object into each and every generator. If there was no context injected during the configuration of the Geci
     * object then a {@link javax0.geci.engine.Context} context implementation is injected. If there was a context
     * injected during the configuration of the Geci object then that object is injected into the generators.</p>
     */
    private void injectContextIntoGenerators() {
        if (context == null) {
            context = javax0.geci.engine.Context.singletonInstance;
        }
        generators.forEach(g -> g.context(context));
    }

    /**
     * A new exception, which is a {@link GeciException} but the message contains the absolute file name of the source
     * in a new line and the stack trace of the new exception is the same as the old one. This exception is thrown in
     * the method {@link #generate()} after catching the original {@link GeciException}.
     */
    private static final class SourcedGeciException extends GeciException {

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }

        private SourcedGeciException(Source source, GeciException e) {
            super(e.getMessage() + (source == null ? "" : "\nSource: " + source.getAbsoluteFile()), e.getCause());
            this.setStackTrace(e.getStackTrace());
        }
    }
}
