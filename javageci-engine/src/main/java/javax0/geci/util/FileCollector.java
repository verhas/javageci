package javax0.geci.util;


import javax0.geci.api.GeciException;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.engine.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileCollector {

    public final Map<Source.Set, String[]> directories;
    public final Set<Source> newSources = new HashSet<>();
    public final Set<Source> sources = new HashSet<>();
    private boolean lenient = false;

    public FileCollector(Map<Source.Set, String[]> directories) {
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
        return s.replace("\\", "/")
                .replace("/./", "/");
    }

    /**
     * Normalize a directory name. The same as normalizing a file, but
     * also adding a trailing / if that is missing.
     *
     * @param s the not yet normalized directory name
     * @return the normalized directory name
     */
    private static String normalized(String s) {
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
     *
     * In this case these source sets are not configured explicitly and
     * therefore the user should not be notified throwing an exception
     * and aborting the code generation if some of the source sets are
     * not available. When the source sets are configured explicit then
     * it is a hard error when a source set is not found and the user
     * has to be notified. Not in case of setting just the default
     * directories.
     *
     * <p>
     *
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

    private static final SegmentSplitHelper javaSegmentSplitHelper = new JavaSegmentSplitHelper();

    public SegmentSplitHelper getSegmentSplitHelper(Source source){
        return javaSegmentSplitHelper;
    }

    /**
     * Collect the names of the files that are in the directories given
     * in the sources. Also modify the global {@code directories} map so
     * that for each {@link Source.Set} key in the map there will be
     * only a one element array containing the name of the directory
     * that was used to collect the files.
     *
     * @param predicates limits the collected files to a subset that
     *                   matches at least one of the predicates. If the
     *                   set is empty or the parameter is {@code null}
     *                   then there is no filtering, all files are
     *                   collected that are otherwise collected from the
     *                   directory.
     */
    public void collect(Set<Predicate<Path>> predicates) {
        var processedSome = false;
        for (var entry : directories.entrySet()) {
            var processed = false;
            for (final var directory : entry.getValue()) {
                var dir = normalized(directory);
                try {
                    Files.find(Paths.get(dir), Integer.MAX_VALUE,
                            (filePath, fileAttr) -> fileAttr.isRegularFile())
                            .filter(path -> (predicates == null || predicates.isEmpty())
                                    || predicates.stream().anyMatch(predicate -> predicate.test(path)))
                            .forEach(path -> sources.add(
                                    new Source(this,
                                            dir,
                                            path)));
                    processed = true;
                    processedSome = true;
                    entry.setValue(new String[]{dir});
                    break;
                } catch (IOException ignore) {
                }
            }
            if (!processed && !lenient) {
                throw new GeciException("Source directory [" +
                        String.join(",", entry.getValue()) + "] is not found");
            }
        }
        if (!processedSome) {
            throw new GeciException("None of the configured directories {" +
                    directories.entrySet().stream()
                            .map(entry -> "\"" +
                                    entry.getKey() +
                                    " : " + "[" +
                                    String.join(",", entry.getValue())
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
