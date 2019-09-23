package javax0.geci.engine;

import javax0.geci.api.Context;
import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.api.*;
import javax0.geci.javacomparator.Comparator;
import javax0.geci.log.Logger;
import javax0.geci.log.LoggerFactory;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.Tracer;
import javax0.geci.util.DirectoryLocator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
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
    private final List<Generator> generators = new ArrayList<>();
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
    private int phaseCounter = 0;

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
     * a predicate and the same time the {@code toString()} of the
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
    public boolean generate() throws IOException {
        try {
            final var exceptions = new ArrayList<String>();
            injectContextIntoGenerators();
            final var phases = generators.stream()
                .mapToInt(Generator::phases)
                .max()
                .orElse(1);
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

            for (int phase = 0; phase < phases; phase++) {
                try (final var posPhase = Tracer.push("Phase", "Starting phase " + phase)) {
                    for (final var source : collector.getSources()) {
                        try (final var posSource = Tracer.push("Source", source.getAbsoluteFile())) {
                            if (!source.isBinary) {
                                try (final var posGenerators = Tracer.push("Generators", null)) {
                                    for (var generator : generators) {
                                        try (final var posGenerator = Tracer.push("Generator." + generator.getClass().getSimpleName(), generator.getClass().getName())) {
                                            if (generator.activeIn(phase)) {
                                                Tracer.log("ACTIVE");
                                                source.allowDefaultSegment = false;
                                                source.currentGenerator = generator;
                                                try {
                                                    generator.process(source);
                                                } catch (javax0.geci.engine.Source.SourceIsBinary e) {
                                                    Tracer.log("source processing failed, it is a binary file");
                                                    exceptions.add(e.getAbsoluteFile());
                                                }
                                            } else {
                                                Tracer.log("INACTIVE");
                                            }
                                        }
                                    }
                                }
                            } else {
                                Tracer.log(source.getAbsoluteFile() + " seems to be binary, skipped");
                            }
                        }
                    }
                }
            }
            try (final var pos1 = Tracer.push("GlobalGenerators", null)) {
                for (var generator : generators) {
                    if (generator instanceof GlobalGenerator) {
                        try (final var pos2 = Tracer.push("GlobalGenerator." + generator.getClass().getSimpleName(), generator.getClass().getName())) {
                            ((GlobalGenerator) generator).process();
                        }
                    }
                }
            }
            if (exceptions.size() > 0 && !ignoreBinary) {
                try (final var pos = Tracer.push("Exceptions")) {
                    exceptions.forEach(Tracer::log);
                }
                throw new GeciException("Cannot read the files\n" + String.join("\n", exceptions) + "\nThey are probably binary file. Use '.ignore()' to filter binary files out");
            }
            if (!sourcesConsolidate(collector)) {
                if (generators.stream().anyMatch(g -> !(g instanceof Distant))) {
                    throw new GeciException("The generators did not touch any source");
                }
            }
            return sourcesModifiedAndSave(collector);
        } finally {
            if (traceFileName != null) {
                try {
                    Tracer.dumpXML(traceFileName);
                } catch (IOException e) {
                    LoggerFactory.getLogger().error("Trace cannot be written into '" + traceFileName + "'", e);
                }
            }
        }
    }

    /**
     * Save the sources that were modified and return true if there was
     * any source that was modified and thus saved.
     *
     * @param collector that provides the sources
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
                if (source.isTouched() && source.isModified(getSourceComparator(source))) {
                    Tracer.log("SaveSource", source.getAbsoluteFile());
                    source.save();
                    modifiedSources.add(source);
                    generated = true;
                } else {
                    Tracer.log("SourceUnchanged", source.getAbsoluteFile());
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

    private void logSourceMessages(javax0.geci.engine.Source source) {
        for (var entry : source.logEntries) {
            final String generatorId;
            if (entry.generator instanceof AbstractJavaGenerator) {
                generatorId = ((AbstractJavaGenerator) entry.generator).mnemonic();
            } else {
                generatorId = entry.generator.getClass().getSimpleName();
            }
            LOG.log(entry.level, generatorId + ":" + entry.message);
        }
    }

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

    private void injectContextIntoGenerators() {
        if (context == null) {
            context = new javax0.geci.engine.Context();
        }
        generators.forEach(g -> g.context(context));
    }
}
