package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;

import java.nio.file.Path;
import java.nio.file.Paths;

class FileSystemSourceStore implements SourceStore {
    final private FileCollector collector;
    final private String relativeFile;
    final private String absoluteFile;
    private final String dir;

    FileSystemSourceStore(FileCollector collector, String relativeFile, String absoluteFile, String dir) {
        this.collector = collector;
        this.relativeFile = relativeFile;
        this.absoluteFile = absoluteFile;
        this.dir = dir;
    }

    private Path inDir(String dir, String fileName) {
        return Paths.get(FileCollector.normalize(
            dir +
                Paths
                    .get(relativeFile)
                    .getParent()
                    .resolve(fileName)
                    .toString()));
    }

    @Override
    public Source get(String fileName) {
        for (final var source : collector.getNewSources()) {
            if (this.absoluteFile.equals(source.absoluteFile)) {
                return source;
            }
        }
        var source = new javax0.geci.engine.Source(collector, dir, inDir(dir, fileName));
        collector.addNewSource(source);
        return source;
    }

    @Override
    public Source get(Source.Set set, String fileName) {
        var directory = collector.getDirectory(set);
        if (directory == null) {
            throw new GeciException("SourceSet '" + set + "' does not exist");
        }
        var source = new javax0.geci.engine.Source(collector, directory, inDir(directory, fileName));
        collector.addNewSource(source);
        return source;
    }
}
