package javax0.geci.engine;


import javax0.geci.api.DirectoryLocator;
import javax0.geci.api.GeciException;
import javax0.geci.api.Logger;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.tools.Tracer;
import javax0.geci.util.DirectoryLocated;
import javax0.geci.util.JavaSegmentSplitHelper;
import javax0.geci.util.NullSegmentSplitHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FileCollector {
    private static final Logger log = new javax0.geci.log.Logger(FileCollector.class);
    private final static SegmentSplitHelper nullSegmentSplitHelper = new NullSegmentSplitHelper();
    private static final SegmentSplitHelper javaSegmentSplitHelper = new JavaSegmentSplitHelper();
    private static final int MAX_DEPTH_UNLIMITED = Integer.MAX_VALUE;
    private final Map<Source.Set, DirectoryLocator> directories;
    private final Map<Source.Set, DirectoryLocated> located = new HashMap<>();
    private final Set<Source> newSources = new HashSet<>();
    private final Set<Source> sources = new HashSet<>();
    private final Map<String, SegmentSplitHelper> splitHelpers = new HashMap<>();
    private boolean lenient = false;

    public FileCollector(Map<Source.Set, javax0.geci.api.DirectoryLocator> directories) {
        this.directories = new HashMap<>(directories);
    }

    /**
     * <p>Normalize a file name.</p>
     *
     * <p>Remove all {@code /./} part from the file name, and eliminate all {@code something/..} subsequence from the
     * path. (Essentially what {@link Path#normalize()} does.) Also convert all {@code \} separator to {@code /}.</p>
     *
     * <p>The conversion of the {@code \} character to {@code /} makes file reporting more concise under Windows. Using
     * {@code /} as file separator is absolutely legit under Windows. However, when the used {@link Path#normalize()}
     * method changes the actual path string then it uses {@code \\} characters when reconstructing the path from the
     * individual parts, because that is the default file separator. If the path is already normalized then it does not
     * change anything. That way the separator will depend on the normalization process and when the file name is
     * printed into the log it would be sometimes with {@code \}, other times {@code /}. Changing {@code \} to {@code /}
     * will result a smooth and coherent file name representation in the log files.</p>
     *
     * <p>The change from {@code \} to {@code /} in the file name also helps the developers when they specify {@link
     * Geci#only(String...) Geci.only()} and {@link Geci#ignore(String...) Geci.ignore()} file patterns. The patterns
     * that may contain directory names may be much more complex if the regular expression is to be prepared to match
     * {@code \} as well as {@code /} as path separator.</p>
     *
     * @param s the not yet normalized file name
     * @return the file directory name
     */
    public static String normalize(String s) {
        return Paths.get(s).normalize().toString().replace("\\","/");
    }

    /**
     * <p>Normalize a directory name. The same as normalizing a file, but
     * also adding a trailing / if that is missing.</p>
     *
     * @param s the not yet normalized directory name
     * @return the normalized directory name
     */
    public static String normalized(String s) {
        s = normalize(s);
        if (!s.endsWith("/")) {
            s += "/";
        }
        return s;
    }

    /**
     * Calculate the class name.
     *
     * @param directory as a reference
     * @param path      points to the source file
     * @return the name of the class calculated from the file name
     */
    public static String calculateClassName(String directory, Path path) {
        return calculateRelativeName(directory, path)
            .replaceAll("/", ".")
            .replaceAll("\\.\\w+$", "");
    }

    /**
     * Calculate the relative file name, relative to the start point of
     * the source set.
     *
     * @param directory the starting, top level directory of the source set
     * @param path      the path in the source set
     * @return the relative file name
     */
    public static String calculateRelativeName(String directory, Path path) {
        return normalize(path.toString())
            .substring(directory.length());
    }

    /**
     * Convert the path to absolute path and also normalize off some
     * weird stuff that may remain after applying the JDK methods (e.g.:
     * {@code /./} inside the path)
     *
     * @param path to convert to absolute path string
     * @return the absolute path as a string
     */
    public static String toAbsolute(Path path) {
        return normalize(path.toAbsolutePath().toString());
    }

    private static String getCwd() {
        try {
            return new java.io.File(".").getCanonicalPath();
        } catch (IOException ignored) {
            return null;
        }
    }

    public void registerSplitHelpers(Map<String, SegmentSplitHelper> splitHelpers) {
        splitHelpers.forEach((key, value) -> Tracer.log("Helper " + key + " = " + value.getClass().getName()));
        this.splitHelpers.putAll(splitHelpers);
    }

    public String getDirectory(Source.Set sourceSet) {
        return located.get(sourceSet).getDirectory();
    }

    /**
     * Get the set of the new sources. These are the sources TODO
     *
     * @return
     */
    public Set<Source> getNewSources() {
        return newSources;
    }

    public Set<Source> getSources() {
        return sources;
    }

    /**
     * <p>When the sources are configured by default, simply not specifying
     * any source then Geci will automatically configure all the four
     * default Maven directories for main and test / sources and
     * resources.</p>
     *
     * <p>In this case these source sets are not configured explicitly and
     * therefore the user should not be notified throwing an exception
     * and aborting the code generation if some of the source sets are
     * not available. When the source sets are configured explicit then
     * it is a hard error when a source set is not found and the user
     * has to be notified. That is because it is likely a mistake that
     * the user is configuring a source set that does not exist. In case
     * of the default setting it happens all the time and this is not an
     * error.</p>
     *
     * <p>Calling this method the file collection will throw an exception
     * only in case there is no any defined source sets available. If
     * some of the source sets are not available this is not a problem.
     * Geci is calling this method when the source sets are defined as
     * default. Without this call the default source set configuration
     * could only be used if all {@code src/main/java}, {@code
     * src/test/java}, {@code src/main/resources}, {@code
     * src/test/resources} exist.</p>
     */
    public void lenient() {
        lenient = true;
    }

    /**
     * <p>Get the segment split helper that is to be used for this source.</p>
     *
     * @param source for which we need the helper
     * @return the helper object
     */
    public SegmentSplitHelper getSegmentSplitHelper(Source source) {
        final var absFn = source.getAbsoluteFile();
        final var extStartPos = absFn.lastIndexOf('.');
        if (extStartPos == -1) {
            return nullSegmentSplitHelper;
        }
        final var ext = absFn.substring(extStartPos + 1);
        if (splitHelpers.containsKey(ext)) {
            return splitHelpers.get(ext);
        } else if ("java".equals(ext)) {
            return javaSegmentSplitHelper;
        } else {
            return nullSegmentSplitHelper;
        }
    }

    /**
     * <p>Collect the names of the files that are in the directories given in the sources. Also modify the global {@code
     * directories} map so that for each {@link Source.Set} key in the map there will be only a one element array
     * containing the name of the directory that was used to collect the files.</p>
     *
     * @param onlySet    limits the collected files to a subset that
     *                   matches at least one of the predicates. If the
     *                   set is empty or the parameter is {@code null}
     *                   then there is no filtering, all files are
     *                   collected that are otherwise collected from the
     *                   directory.
     * @param ignoreSet  limits the collected files to a subset that
     *                   does not match any of the predicates. If the
     *                   set is empty or the parameter is {@code null}
     *                   then there is no filtering, all files are
     *                   collected that are otherwise collected from the
     *                   directory.
     * @param outputSets contains the output sets. It means that they
     *                   are available for the generators to create new
     *                   sources in the set, but the sources are not
     *                   collected from these sets and no generator will
     *                   be executed for any source file in these source
     *                   sets.
     */
    public void collect(Set<Predicate<Path>> onlySet, Set<Predicate<Path>> ignoreSet, Set<Source.Set> outputSets) {
        Tracer.log("Current Working Directory is '" + getCwd() + "'");
        boolean processedSomeOfTheEntries = false;
        for (var entry : directories.entrySet()) {
            processedSomeOfTheEntries |= collectEntry(entry, onlySet, ignoreSet, outputSets);
        }
        if (!processedSomeOfTheEntries) {
            throw new GeciException("None of the configured directories {" +
                    directories.entrySet().stream()
                            .map(entry -> "\"" +
                                    entry.getKey() +
                                    " : " + "[" +
                                    entry.getValue().alternatives().collect(Collectors.joining(","))
                                    + "]")
                            .collect(Collectors.joining(",\n"))
                    + "} are found.");
        }
    }

    /**
     * Collect the sources for the entry.
     *
     * @param entry      the entry for which the sources are collected
     * @param onlySet    same as in {@link #collect(Set, Set, Set) collect()}
     * @param ignoreSet  same as in {@link #collect(Set, Set, Set) collect()}
     * @param outputSets same as in {@link #collect(Set, Set, Set) collect()}
     * @return {@code true} if some
     */
    private boolean collectEntry(final Map.Entry<javax0.geci.api.Source.Set, DirectoryLocator> entry,
                                 final Set<Predicate<Path>> onlySet,
                                 final Set<Predicate<Path>> ignoreSet,
                                 final Set<javax0.geci.api.Source.Set> outputSets
    ) {
        Tracer.push("Entry", "File collecting started for entry [" + entry.getValue().alternatives().collect(Collectors.joining(",")) + "]");
        var processedSome = new AtomicBoolean(false);
        var processed = new AtomicBoolean(false);
        final var locator = entry.getValue();
        locator.alternatives().filter(x -> !processed.get())
                .forEach(directory -> {
                    collectDirectoryTraced(directory, entry, onlySet, ignoreSet, outputSets, processedSome, processed, locator);
                });
        Tracer.pop();
        if (!processed.get() && !lenient) {
            throw new GeciException("Source directory [" +
                    locator.alternatives().collect(Collectors.joining(","))
                    + "] is not found");
        }
        return processedSome.get();
    }

    private void collectDirectoryTraced(final String directory,
                                        final Map.Entry<javax0.geci.api.Source.Set, DirectoryLocator> entry,
                                        final Set<Predicate<Path>> onlySet,
                                        final Set<Predicate<Path>> ignoreSet,
                                        final Set<javax0.geci.api.Source.Set> outputSets,
                                        final AtomicBoolean processedSome,
                                        final AtomicBoolean processed,
                                        final DirectoryLocator locator
    ) {
        try (final var __ = Tracer.push("Alternative", "File collecting started for alternative '" + directory + "'")) {
            collectDirectory(directory, entry, onlySet, ignoreSet, outputSets, processedSome, processed, locator);
        } catch (IOException ioException) {
            throw new GeciException("The directory '"
                    + normalized(directory)
                    + "' was selected but no files can be collected from it.",
                    ioException);
        }
    }

    private void collectDirectory(final String directory,
                                  final Map.Entry<javax0.geci.api.Source.Set, DirectoryLocator> entry,
                                  final Set<Predicate<Path>> onlySet,
                                  final Set<Predicate<Path>> ignoreSet,
                                  final Set<javax0.geci.api.Source.Set> outputSets,
                                  final AtomicBoolean processedSome,
                                  final AtomicBoolean processed,
                                  final DirectoryLocator locator
    ) throws IOException {
        var dir = normalized(directory);
        if (locator.test(dir)) {
            collectTestedDirectory(directory, entry, onlySet, ignoreSet, outputSets, processedSome, processed, dir);
        } else {
            Tracer.log("'" + directory + "' is not the right alternative");
        }
    }

    /**
     * <ul>
     * <li>Collect the source files from the input directory {@code directory},
     * <li>trace the act of collection,</li>
     * <li>store the directory name in the {@code located} {@code Map}, and</li>
     * <li>set {@code processed} and {@code processedSome} to {@code true}</li>
     * </ul>
     *
     * <p>If the entry is listed in the set {@code outputSets} then this directory is not collected.</p>
     *
     * @param directory     where the source files are
     * @param entry
     * @param onlySet
     * @param ignoreSet
     * @param outputSets
     * @param processedSome
     * @param processed
     * @param dir
     * @throws IOException
     */
    private void collectTestedDirectory(final String directory,
                                        final Map.Entry<javax0.geci.api.Source.Set, DirectoryLocator> entry,
                                        final Set<Predicate<Path>> onlySet,
                                        final Set<Predicate<Path>> ignoreSet,
                                        final Set<javax0.geci.api.Source.Set> outputSets,
                                        final AtomicBoolean processedSome,
                                        final AtomicBoolean processed,
                                        final String dir
    ) throws IOException {
        Tracer.log("'" + directory + "' seems to be the right alternative");
        if (outputSets.contains(entry.getKey())) {
            Tracer.log("'" + directory + "' is an output location, files are not collected");
        } else {
            Tracer.log("'" + directory + "' is input, collecting files...");
            collectInputDirectory(onlySet, ignoreSet, dir);
            located.put(entry.getKey(), new DirectoryLocated(dir));
            processed.set(true);
            processedSome.set(true);
        }
    }

    /**
     * <p>Collect the source files from the {@code dir} directory, create {@link javax0.geci.api.Source Source} objects
     * that encapsulate the file and add the new object to the set of sources.</p>
     *
     * @param onlySet   only the files are processed that match some element of this set unless this set is empty. In
     *                  this case the set is not consulted.
     * @param ignoreSet
     * @param dir
     * @throws IOException
     */
    private void collectInputDirectory(final Set<Predicate<Path>> onlySet,
                                       final Set<Predicate<Path>> ignoreSet,
                                       final String dir
    ) throws IOException {
        getAllRegularFiles(dir)
                .peek(s -> Tracer.push("File", "'" + s + "' was found"))
//
                .peek(s -> Tracer.push("Only", "Checking predicates"))
                .filter(path -> pathIsMatchingOnlySet(onlySet, path))
                .peek(s -> Tracer.pop())
//
                .peek(s -> Tracer.push("Ignore", "Checking predicates"))
                .filter(path -> pathIsNotIgnored(ignoreSet, path))
                .peek(s -> Tracer.pop())
//
                .peek(s -> Tracer.pop())
                .forEach(path -> sources.add(new Source(this, dir, path)));
    }

    /**
     * <p>Get all files in a directory recursively visiting subdirectories.</p>
     *
     * @param dir the root directory where the collection of the files starts
     * @return get the stream of regular files in and under the directory with no directory depth limitation
     * @throws IOException in case there is some problem with the file system
     */
    private Stream<Path> getAllRegularFiles(final String dir) throws IOException {
        return Files.find(Paths.get(dir),
                MAX_DEPTH_UNLIMITED,
                (filePath, fileAttr) -> fileAttr.isRegularFile()
        );
    }

    /**
     * <p>Check that the path is to be selected based on the predicate set 'ignoreSet'.</p>
     *
     * <p>The path is to be selected if the {@code ignoreSet} set is empty, or none of the predicates in the set matches
     * the path. In other word if there is no ignore set and in case there is an ignore set then the path is not
     * matching any of the elements.</p>
     *
     * @param ignoreSet a predicate set that says which paths are to be ignored. If a path matches any of the elements
     *                  of the set then the path is to be ignored
     * @param path      the path to be ignored or included
     * @return {@code true} iff the path is not to be ignored
     */
    private boolean pathIsNotIgnored(Set<Predicate<Path>> ignoreSet, Path path) {
        if (ignoreSet == null || ignoreSet.isEmpty()) {
            Tracer.log("There are no 'ignore' predicates");
            return true;
        }
        if (ignoreSet.stream()
                .peek(p -> Tracer.log("Checking 'ignore' predicate " + p))
                .noneMatch(predicate -> predicate.test(path))) {
            Tracer.log("None of the  predicates matched");
            return true;
        }
        Tracer.append(", predicate matched, file is skipped");
        Tracer.pop();
        Tracer.pop();
        return false;
    }

    /**
     * <p>Check that the path is to be selected based on the predicate set 'onlySet'.</p>
     *
     * <p>The path is to be selected if the {@code onlySet} set is empty, or any of the predicates in the set matches
     * the path. In other words if there is no explicit 'only' set then we are OK with all the paths. If there
     * is a set then we are OK with a path if any of the set element matches the path.</p>
     *
     * <p>The method also calls trace to leave trace of the operation in case debugging is needed.</p>
     *
     * @param onlySet a predicate set that says that only the paths should be used that are in the predicate set.
     * @param path    the path to be selected or not selected
     * @return {@code true} if the path is to be selected
     */
    private boolean pathIsMatchingOnlySet(final Set<Predicate<Path>> onlySet, final Path path) {
        if (onlySet == null || onlySet.isEmpty()) {
            Tracer.log("There are no 'only' predicates");
            return true;
        }
        if (onlySet.stream()
                .peek(p -> Tracer.log("Checking 'only' predicate " + p))
                .anyMatch(predicate -> predicate.test(path))) {
            Tracer.append(", predicate matched");
            return true;
        }
        Tracer.log("No 'only' predicate match, file is skipped.");
        Tracer.pop();
        Tracer.pop();
        return false;
    }

    /**
     * <p>Add a new source to the set of the new sources. The collection of the new sources contains those sources that
     * are not read from the disk, but are created during the code generation by generators who do not (only) modify
     * existing source files but generate new sources as well.</p>
     *
     * @param source to add to the collection of new sources
     */
    public void addNewSource(Source source) {
        newSources.add(source);
    }
}
