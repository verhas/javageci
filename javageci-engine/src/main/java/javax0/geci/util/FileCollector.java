package javax0.geci.util;


import javax0.geci.api.DirectoryLocator;
import javax0.geci.api.GeciException;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.engine.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileCollector {

    private final Map<Source.Set, DirectoryLocator> directories;
    private final Map<Source.Set, DirectoryLocated> located = new HashMap<>();
    private final Set<Source> newSources = new HashSet<>();
    private final Set<Source> sources = new HashSet<>();
    private boolean lenient = false;

    public FileCollector(Map<Source.Set, javax0.geci.api.DirectoryLocator> directories) {
        this.directories = new HashMap<>(directories);
    }

    public void registerSplitHelpers(Map<String, SegmentSplitHelper> splitHelpers) {
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
                    pathElements.remove(i+1);
                    pathElements.remove(i);
                    changed = true;
                    break;
                }
            }
        } while (changed);
        return String.join("/",pathElements);
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

    private final static SegmentSplitHelper nullSegmentSplitHelper = new NullSegmentSplitHelper();
    private final Map<String, SegmentSplitHelper> splitHelpers = new HashMap<>();
    private static final SegmentSplitHelper javaSegmentSplitHelper = new JavaSegmentSplitHelper();

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

    private static final int MAX_DEPTH_UNLIMITED = Integer.MAX_VALUE;

    /**
     * Collect the names of the files that are in the directories given
     * in the sources. Also modify the global {@code directories} map so
     * that for each {@link Source.Set} key in the map there will be
     * only a one element array containing the name of the directory
     * that was used to collect the files.
     *
     * @param onlys   limits the collected files to a subset that
     *                matches at least one of the predicates. If the
     *                set is empty or the parameter is {@code null}
     *                then there is no filtering, all files are
     *                collected that are otherwise collected from the
     *                directory.
     * @param ignores limits the collected files to a subset that
     *                does not match any of the predicates. If the
     *                set is empty or the parameter is {@code null}
     *                then there is no filtering, all files are
     *                collected that are otherwise collected from the
     *                directory.
     */
    public void collect(Set<Predicate<Path>> onlys, Set<Predicate<Path>> ignores, Set<Source.Set> outputSets) {
        var processedSome = new AtomicBoolean(false);
        for (var entry : directories.entrySet()) {
            var processed = new AtomicBoolean(false);
            final var locator = entry.getValue();
            locator.alternatives().takeWhile(x -> !processed.get())
                    .forEach(directory -> {
                        var dir = normalized(directory);
                        try {
                            if (locator.test(dir)) {
                                if (!outputSets.contains(entry.getKey())) {
                                    Files.find(Paths.get(dir), MAX_DEPTH_UNLIMITED,
                                            (filePath, fileAttr) -> fileAttr.isRegularFile())
                                            .filter(path -> (onlys == null || onlys.isEmpty())
                                                    || onlys.stream().anyMatch(predicate -> predicate.test(path)))
                                            .filter(path -> (ignores == null || ignores.isEmpty())
                                                    || ignores.stream().noneMatch(negicate -> negicate.test(path)))
                                            .forEach(path -> sources.add(
                                                    new Source(this,
                                                            dir,
                                                            path)));
                                    processed.set(true);
                                    processedSome.set(true);
                                    located.put(entry.getKey(), new DirectoryLocated(dir));
                                }
                            }
                        } catch (IOException ioex) {
                            throw new GeciException("The directory '"
                                    + dir
                                    + "' was selected but no files can be collected from it.",
                                    ioex);
                        }
                    });

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
