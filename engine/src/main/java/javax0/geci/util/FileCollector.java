package javax0.geci.util;


import javax0.geci.engine.Source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class FileCollector {

    final Set<String> directories;

    public FileCollector(Set<String> directories) {
        this.directories = directories;
    }

    /**
     * Collect the names of the files that are in the directories given  in the sources.
     *
     * @return the list of the file names containing the full path absolute file names.
     */
    public Set<Source> collect() throws IOException {
        var sources = new HashSet<Source>();
        for (var directory : directories) {
            Files.find(Paths.get(directory),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile()
            ).forEach(path -> sources.add(new javax0.geci.engine.Source(path.toString(),toAbsolute(path))));
        }
        return sources;
    }

    private String toAbsolute(Path path){
        var s = path.toAbsolutePath().toString();
        return s.replace("\\.\\","\\")
                .replace("/./","/");
    }
}
