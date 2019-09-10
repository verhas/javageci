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
     * Normalize a file name. Convert all {@code \} separator to {@code
     * /} and remove all '{@code /./}' path parts.
     *
     * @param s the not yet normalized file name
     * @return the file directory name
     */
    public static String normalize(String s) {
        final var unixStyle = s.replace("\\", "/")
            .replace("/./", "/");
        final var pathElements = new ArrayList<>(Arrays.asList(unixStyle.split("/", -1)));
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < pathElements.size() - 1; i++) {
                if (!pathElements.get(i).equals("..") && pathElements.get(i + 1).equals("..")) {
                    pathElements.remove(i + 1);
                    pathElements.remove(i);
                    changed = true;
                    break;
                }
            }
        } while (changed);
        return String.join("/", pathElements);
    }

    /**
     * Normalize a directory name. The same as normalizing a file, but
     * also adding a trailing / if that is missing.
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
        splitHelpers.entrySet().forEach(e -> Tracer.log("Helper " + e.getKey() + " = " + e.getValue().getClass().getName()));
        this.splitHelpers.putAll(splitHelpers);
    }

    public String getDirectory(Source.Set sourceSet) {
        return located.get(sourceSet).getDirectory();
    }

    public Set<Source> getNewSources() {
        return newSources;
    }

    public Set<Source> getSources() {
        return sources;
    }

    /**
     * When the sources are configured by default, simply not specifying
     * any source then Geci will automatically configure all the four
     * default Maven directories for main and test / sources and
     * resources.
     *
     * <p>
     * In this case these source sets are not configured explicitly and
     * therefore the user should not be notified throwing an exception
     * and aborting the code generation if some of the source sets are
     * not available. When the source sets are configured explicit then
     * it is a hard error when a source set is not found and the user
     * has to be notified. Not in case of setting just the default
     * directories.
     *
     * <p>
     * Calling this method the file collection will throw an exception
     * only in case there is no any defined source sets available. If
     * some of the source sets are not available this is not a problem.
     * Geci is calling this method when the source sets are defined as
     * default. Without this call the default source set configuration
     * could only be used if all {@code src/main/java}, {@code
     * src/test/java}, {@code src/main/resources}, {@code
     * src/test/resources} exist.
     */
    public void lenient() {
        lenient = true;
    }

    /**
     * Get the segment split helper that is to be used for this source.
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
     * Collect the names of the files that are in the directories given
     * in the sources. Also modify the global {@code directories} map so
     * that for each {@link Source.Set} key in the map there will be
     * only a one element array containing the name of the directory
     * that was used to collect the files.
     *
     * @param onlys      limits the collected files to a subset that
     *                   matches at least one of the predicates. If the
     *                   set is empty or the parameter is {@code null}
     *                   then there is no filtering, all files are
     *                   collected that are otherwise collected from the
     *                   directory.
     * @param ignores    limits the collected files to a subset that
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
    public void collect(Set<Predicate<Path>> onlys, Set<Predicate<Path>> ignores, Set<Source.Set> outputSets) {
        Tracer.log("Current Working Directory is '" + getCwd() + "'");
        var processedSome = new AtomicBoolean(false);
        for (var entry : directories.entrySet()) {
            Tracer.push("Entry","File collecting started for entry [" + entry.getValue().alternatives().collect(Collectors.joining(",")) + "]");
            var processed = new AtomicBoolean(false);
            final var locator = entry.getValue();
            locator.alternatives().takeWhile(x -> !processed.get())
                .forEach(directory -> {
                    var dir = normalized(directory);
                    try (final var tracePosition = Tracer.push("Alternative","File collecting started for alternative '" + directory + "'")) {
                        if (locator.test(dir)) {
                            Tracer.log("'" + directory + "' seems to be the right alternative");
                            if (!outputSets.contains(entry.getKey())) {
                                Tracer.log("'" + directory + "' is input, collecting files...");
                                Files.find(Paths.get(dir), MAX_DEPTH_UNLIMITED,
                                        (filePath, fileAttr) -> fileAttr.isRegularFile())
                                    .peek(s -> Tracer.push("File", "'" + s + "' was found"))
                                        .peek(s -> Tracer.push("Only","Checking predicates"))
                                        .filter(path -> {
                                            if (onlys == null || onlys.isEmpty()) {
                                                Tracer.log("There are no 'only' predicates");
                                                return true;
                                            }
                                            if (onlys.stream()
                                                    .peek(p -> Tracer.log("Checking 'only' predicate " + p))
                                                    .anyMatch(predicate -> predicate.test(path))) {
                                                Tracer.append(", predicate matched");
                                                return true;
                                            }
                                            Tracer.log("No 'only' predicate match, file is skipped.");
                                            Tracer.pop();
                                            Tracer.pop();
                                            return false;
                                        })
                                        .peek(s -> Tracer.pop())
                                        .peek(s -> Tracer.push("Ignore","Checking predicates"))
                                        .filter(path -> {
                                                    if (ignores == null || ignores.isEmpty()) {
                                                        Tracer.log("There are no 'ignore' predicates");
                                                        return true;
                                                    }
                                                    if (ignores.stream()
                                                            .peek(p -> Tracer.log("Checking 'ignore' predicate " + p))
                                                            .noneMatch(negicate -> negicate.test(path))) {
                                                        Tracer.log("None of the  predicates matched");
                                                        return true;
                                                    }
                                                    Tracer.append(", predicate matched, file is skipped");
                                                    Tracer.pop();
                                            Tracer.pop();
                                                    return false;
                                                }
                                        )
                                    .peek(s -> {
                                        Tracer.pop();
                                        Tracer.pop();
                                    })
                                        .forEach(path -> sources.add(
                                                new Source(this,
                                                        dir,
                                                        path)));
                                processed.set(true);
                                processedSome.set(true);
                                located.put(entry.getKey(), new DirectoryLocated(dir));
                            } else {
                                Tracer.log("'" + directory + "' is an output location, files are not collected");
                            }
                        } else {
                            Tracer.log("'" + directory + "' is not the right alternative");
                        }
                    } catch (IOException ioex) {
                        throw new GeciException("The directory '"
                            + dir
                            + "' was selected but no files can be collected from it.",
                            ioex);
                    }
                });
            Tracer.pop();
            if (!processed.get() && !lenient) {
                throw new GeciException("Source directory [" +
                    locator.alternatives().collect(Collectors.joining(","))
                    + "] is not found");
            }
        }
        if (!processedSome.get()) {
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
     * Add a new source to the set of the new sources. The new sources.
     * The collection of the new sources contains those sources that are
     * not read from the disk, but are created during the code
     * generation by generators who do not (only) modify existing source
     * files but generate new sources as well.
     *
     * @param source to add to the collection of new sources
     */
    public void addNewSource(Source source) {
        newSources.add(source);
    }
}
