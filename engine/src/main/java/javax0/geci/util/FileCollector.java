package javax0.geci.util;


import javax0.geci.engine.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileCollector {

    private final Set<String[]> directories;
    final public Set<Source> newSources = new HashSet<>();

    public FileCollector(Set<String[]> directories) {
        this.directories = directories;
    }

    /**
     * Collect the names of the files that are in the directories given  in the sources.
     *
     * @return the list of the file names containing the full path absolute file names.
     */
    public Set<Source> collect() {
        var sources = new HashSet<Source>();
        for (var dirAlternatives : directories) {
            var processed = false;
            for (int i = 0; i < dirAlternatives.length; i++) {
                var directory = dirAlternatives[i];
                if (!directory.endsWith("/") && !directory.endsWith("\\")) {
                    directory = directory + "/";
                }
                var dir = normalize(directory);
                try {
                    Files.find(Paths.get(directory), Integer.MAX_VALUE,
                            (filePath, fileAttr) -> fileAttr.isRegularFile()
                    ).forEach(path -> sources.add(
                            new javax0.geci.engine.Source(newSources, calculateClassName(dir, path), toAbsolute(path)))
                    );
                    processed = true;
                } catch (IOException ignore) {
                }
            }
            if (!processed) {
                throw new RuntimeException("Source directory [" + String.join(",", dirAlternatives) + "] is not found");
            }
        }
        return sources;
    }

    /**
     * Normalize a directory name. Convert all \ separator to / and remove all '/./' path parts.
     *
     * @param s the not yet normalized directory name
     * @return the normalized directory name
     */
    private String normalize(String s) {
        return s.replace("\\", "/")
                .replace("/./", "/");
    }

    /**
     * Calulate the class name.
     *
     * @param directory as a reference
     * @param path
     * @return
     */
    private String calculateClassName(String directory, Path path) {
        var s = normalize(path.toString())
                .substring(directory.length())
                .replaceAll("/", ".")
                .replaceAll("\\.java$", "");
        return normalize(s);
    }

    private String toAbsolute(Path path) {
        var s = path.toAbsolutePath().toString();
        return normalize(s);
    }
}
