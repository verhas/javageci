package javax0.geci.engine;

import javax0.geci.api.GeciException;
import javax0.geci.api.Source;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>Implements the {@link SourceStore} interface in a way that uses the underlying {@link FileCollector}. The get
 * methods look in the set of the new sources and in case there is a source for the requested file name then it is
 * returned. In case the new source object is not there then a new object is created and it gets stores in the new
 * sources set. This ensures that for the same (calculated absolute) file name the same object will be returned.</p>
 *
 * <p>This implementation creates a separate store for each source. This way when a generator is calling {@link
 * Source#newSource(Source.Set, String)} or {@link Source#newSource(String)} on a {@link Source} object (those are
 * implemented calling the {@link #get(String)} and {@link #get(Source.Set, String)} methods of the store object that
 * belongs to that source) then the {@code fileName} argument is relative to the file of the source. This is fairly
 * straightforward in case of {@link Source#newSource(String)} because in that case the physical file will have the
 * relative file name specified as {@code fileName}.</p>
 *
 * <p>In case the generator calls {@link Source#newSource(Source.Set, String)} this is a bit more difficult. In that case
 * the new file will be created in a directory "relative" to the original source, but in a different set and hence under
 * a different root directory. For example the source set "a" has the root directory {@code "a-root/"} and the source
 * set "b" has the root directory {@code "b-root/"}. If the generator is working on the {@link Source} object from the
 * source set "a" with the file name {@code "main/java/javax0/sample.txt"} and the file actually is {@code
 * "/Users/me/myproject/a-root/main/java/javax0/sample.txt"} then the call to {@link Source#newSource(Source.Set,
 * String) source.newSource(set("b"),"boo/newSample.txt"} will be created in the directory {@code
 * "/Users/me/myproject/b-root/main/java/javax0/boo/newSample.txt"}. The root directory is taken from the named source
 * set, the directory below from the actual source.</p>
 */
class FileSystemSourceStore implements SourceStore {
    final private FileCollector collector;
    final private String relativeFile;
    private final String dir;

    /**
     * Create a new store facade.
     *
     * @param collector    the underlying file collector that stores and manages the files.
     * @param relativeFile the relative file name of the source that creates this store
     * @param dir          the directory of the source
     */
    FileSystemSourceStore(FileCollector collector, String relativeFile, String dir) {
        this.collector = collector;
        this.relativeFile = relativeFile;
        this.dir = dir;
    }

    /**
     * <p>Calculate the {@link Path} of the {@code fileName}, if it has to be relative to the directory {@code dir}.</p>
     *
     * @param dir      the directory to which the {@code fileName} is relative to
     * @param fileName the name of the file we need the {@link Path} to
     * @return the {@link Path} to the existing or non-existing file
     */
    private Path inDir(String dir, String fileName) {
        var parent = Paths
                .get(relativeFile)
                .getParent();
        return Paths.get(
                FileCollector.normalize(
                        dir + (parent == null ? fileName : parent.resolve(fileName).toString())
                )
        );
    }

    /**
     * <p>Get an existing source object from the new sources set or create a new one and store it there and then return
     * the newly created source object.</p>
     *
     * @param dir      the directory to which the {@code fileName} is relative
     * @param fileName the file name of the new source
     * @return the {@link Source} object for thew new source
     */
    private Source getExistingOrNew(String dir, String fileName) {
        var path = inDir(dir, fileName);
        var absoluteFile = FileCollector.toAbsolute(path);
        for (final var source : collector.getNewSources()) {
            if (absoluteFile.equals(source.absoluteFile)) {
                return source;
            }
        }
        var source = new javax0.geci.engine.Source(collector, dir, path);
        collector.addNewSource(source);
        return source;
    }

    /**
     * <p>Get the source object that has the relative file name {@code fileName}, which is relative to the {@link
     * Source} object this {@link SourceStore} belongs to.</p>
     *
     * @param fileName the name of the file from which the source is read
     * @return the source for the file name
     */
    @Override
    public Source get(String fileName) {
        return getExistingOrNew(dir, fileName);
    }

    /**
     * <p>Get the source object that has the relative file name {@code fileName}, which is relative to the {@link
     * Source} object this {@link SourceStore} belongs to, but in the {@code set} source set.</p>
     *
     * @param set      the source set in which the source object should be
     * @param fileName the name of the file from which the source is read
     * @return the source for the file name
     */
    @Override
    public Source get(Source.Set set, String fileName) {
        var dir = collector.getDirectory(set);
        if (dir == null) {
            throw new GeciException("SourceSet '" + set + "' does not exist");
        }
        return getExistingOrNew(dir, fileName);
    }
}
