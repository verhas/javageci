package javax0.geci.util;


import javax0.geci.api.GeciException;
import javax0.geci.engine.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class FileCollector {

    public final Map<Source.Set, String[]> directories;
    public final Set<Source> newSources = new HashSet<>();
    public final Set<Source> sources = new HashSet<>();

    public FileCollector(Map<Source.Set, String[]> directories) {
        this.directories = new HashMap<>(directories);
    }

    /**
     * Collect the names of the files that are in the directories given in the sources. Also modify the
     * global {@code directories} map so that for each {@link Source.Set} key in the map there will be only
     * a one element array containing the name of the directory that was used to collect the files.
     *
     * @param patterns limits the collected files to a subset that matches at least one of the patterns in the set. If
     *                 the set is empty or the parameter is {@code null} then there is no filtering, all files
     *                 are collected that are otherwise collected from the directory
     */
    public void collect(Set<Pattern> patterns) {
        for (var entry : directories.entrySet()) {
            var processed = false;
            for (final var directory : entry.getValue()) {
                var dir = normalized(directory);
                try {
                    Files.find(Paths.get(dir), Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile())
                        .filter(path -> (patterns == null || patterns.isEmpty())
                            || patterns.stream().anyMatch(pattern -> pattern.matcher(toAbsolute(path)).find()))
                        .forEach(path -> sources.add(
                            new Source(this,
                                dir,
                                path)));
                    processed = true;
                    entry.setValue(new String[]{dir});
                    break;
                } catch (IOException ignore) {
                }
            }
            if (!processed) {
                throw new GeciException("Source directory [" + String.join(",", entry.getValue()) + "] is not found");
            }
        }
    }

    public void addNewSource(Source source) {
        newSources.add(source);
    }

    /**
     * Normalize a file name. Convert all \ separator to / and remove all '/./' path parts.
     *
     * @param s the not yet normalized file name
     * @return the file directory name
     */
    public static String normalize(String s) {
        return s.replace("\\", "/")
            .replace("/./", "/");
    }

    /**
     * Normalize a directory name. The same as normalizing a file, but also adding a trailing / if that is missing.
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
     * Calculate the relative file name, relative to the start point of the source set.
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
     * Convert the path to absolute path and also normalize off some weird stuff that may remain after
     * applying the JDK methods (e.g.: {@code /./} inside the path)
     *
     * @param path to convert to absolute path string
     * @return the absolute path as a string
     */
    public static String toAbsolute(Path path) {
        return normalize(path.toAbsolutePath().toString());
    }


}
