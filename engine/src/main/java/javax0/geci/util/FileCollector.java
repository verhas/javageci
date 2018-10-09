package javax0.geci.util;


import javax0.geci.api.GeciException;
import javax0.geci.engine.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileCollector {

    public final Map<Source.Set, String[]> directories;
    public final Set<Source> newSources = new HashSet<>();
    public final Set<Source> sources = new HashSet<>();

    public FileCollector(Map<Source.Set, String[]> directories) {
        this.directories = directories;
    }

    /**
     * Collect the names of the files that are in the directories given  in the sources.
     */
    public void collect() {
        for (var dirAlternatives : directories.values()) {
            var processed = false;
            for (final var directory : dirAlternatives) {
                var dir = normalized(directory);
                try {
                    Files.find(Paths.get(dir), Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile()
                    ).forEach(path -> sources.add(
                        new Source(this,
                            dir,
                            path)));
                    processed = true;
                    dirAlternatives[0] = dir;
                } catch (IOException ignore) {
                }
            }
            if (!processed) {
                throw new GeciException("Source directory [" + String.join(",", dirAlternatives) + "] is not found");
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
            .replaceAll("\\.java$", "");
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
     * @param path
     * @return the absolute path as a string
     */
    public static String toAbsolute(Path path) {
        return normalize(path.toAbsolutePath().toString());
    }


}
