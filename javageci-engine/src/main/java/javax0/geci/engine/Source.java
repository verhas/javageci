package javax0.geci.engine;


import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Logger;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.util.FileCollector;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class Source implements javax0.geci.api.Source {
    final List<String> lines = new ArrayList<>();
    private final String dir;
    private final String className;
    private final String relativeFile;
    private final String absoluteFile;
    private final Map<String, Segment> segments = new HashMap<>();
    private final List<String> originals = new ArrayList<>();
    private final FileCollector collector;
    private final SegmentSplitHelper splitHelper;
    boolean inMemory = false;
    private Segment globalSegment = null;
    private boolean touched = false;
    boolean allowDefaultSegment = false;

    Generator currentGenerator = null;

    /**
     * The constructor is not supposed to be used from outside, only through the {@link FileCollector} which
     * is invoked only from {@link Geci#generate()}.
     *
     * @param collector the file collector that this source belongs to. Note that the type {@link FileCollector}
     *                  is not exported by the module and this prevents the users of the module to use this constructor.
     * @param dir       the directory of the source
     * @param path      the path of the source
     */
    @SuppressWarnings("ClassEscapesDefinedScope")
    public Source(FileCollector collector, String dir, Path path) {
        this.collector = collector;
        this.dir = dir;
        className = FileCollector.calculateClassName(dir, path);
        relativeFile = FileCollector.calculateRelativeName(dir, path);
        absoluteFile = FileCollector.toAbsolute(path);
        splitHelper = collector.getSegmentSplitHelper(this);
    }

    /**
     * A source is touched if the generator was writing to it. It is even touched if the generator was writing the same
     * content to it what there was originally. This flag is used to identify the situation when a generator does not
     * touch any source When a generator is executed and does not touch any source it throws an exception because it
     * certainly means that there is a configuration error. Either it is supposed to touch something or it should
     * not be executed, the test should just be disabled.
     *
     * @return true when the source was touched
     */
    public boolean isTouched() {
        return touched;
    }

    @Override
    public String getAbsoluteFile() {
        return absoluteFile;
    }

    @Override
    public void init(String id) throws IOException {
        if (id == null || id.equals("")) {
            return;
        }
        open(id);
    }

    @Override
    public Source newSource(String directory, String fileName) {
        if(directory == null || directory.equals("")) {
            return newSource(fileName);
        }
        return createNewSource(FileCollector.normalized(directory), fileName);
    }

    @Override
    public Source newSource(Source.Set sourceSet, String fileName) {
        if (!collector.directories.containsKey(sourceSet)) {
            throw new GeciException("SourceSet '" + sourceSet + "' does not exist");
        }
        var directory = collector.directories.get(sourceSet)[0];
        return createNewSource(directory, fileName);
    }

    private Source createNewSource(String directory, String fileName) {
        var source = new Source(collector, directory, inDir(directory, fileName));
        collector.addNewSource(source);
        return source;
    }

    @Override
    public Source newSource(String fileName) {
        var source = collector.newSources.stream()
            .filter(other -> this.absoluteFile.equals(other.absoluteFile)).findFirst()
            .orElse(new Source(collector, dir, inDir(dir, fileName)));
        collector.addNewSource(source);
        return source;
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
    public String toString() {
        if (!inMemory) {
            try {
                readToMemory();
            } catch (IOException e) {
                throw new GeciException(e);
            }
        }
        return String.join("\n", lines);
    }

    public List<String> getLines() {
        if (!inMemory) {
            try {
                readToMemory();
            } catch (IOException e) {
                inMemory = true;
                originals.clear();
                lines.clear();
            }
        }
        return lines;
    }

    @Override
    public Segment open() {
        if (!segments.isEmpty()) {
            throw new GeciException("Global segment was opened when the there were already opened segments");
        }
        if (globalSegment == null) {
            globalSegment = new Segment(0);
        }
        if (!inMemory) {
            try {
                readToMemory();
            } catch (IOException e) {
                inMemory = true;
                originals.clear();
                lines.clear();
            }
        }
        return globalSegment;
    }

    public Segment temporary() {
        return new Segment(0);
    }

    @Override
    public Segment safeOpen(String id) throws IOException {
        final var segment = open(id);
        if (segment == null) {
            throw new GeciException(getAbsoluteFile() + " does not have segment named '" + id + "'");
        }
        return segment;
    }

    private String[] mnemonize(String id, String... s) {
        final String[] res = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            res[i] = s[i].replaceAll("\\{\\{mnemonic}}", id);
        }
        return res;
    }

    @Override
    public void allowDefaultSegment() {
        this.allowDefaultSegment = true;
    }

    @Override
    public Segment open(String id) throws IOException {
        if (globalSegment != null) {
            throw new GeciException("Segment was opened after the global segment was already created.");
        }
        if (!inMemory) {
            readToMemory();
        }
        if (!segments.containsKey(id)) {
            boolean defaultSegment = false;
            var segDesc = findSegment(id);
            if (segDesc == null) {
                segDesc = findDefaultSegment();
                if (segDesc == null) {
                    return null;
                }
                defaultSegment = true;
            }
            var segment = new Segment(segDesc.tab);
            if (defaultSegment) {
                segment.setPreface(mnemonize(id, splitHelper.getSegmentPreface()));
                segment.setPostface(mnemonize(id, splitHelper.getSegmentPostface()));
            }
            segments.put(id, segment);

        }
        return segments.get(id);
    }

    @Override
    public String getKlassName() {
        return className;
    }

    @Override
    public String getKlassSimpleName() {
        return className.replaceAll("^.*\\.", "");
    }

    @Override
    public Class<?> getKlass() {
        try {
            return GeciReflectionTools.classForName(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
    }

    final List<SourceLogger.LogEntry> logEntries = new ArrayList<>();

    private final Logger logger = new SourceLogger(this);

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getPackageName() {
        return getKlassName().replaceAll("\\.\\w+$", "");
    }

    /**
     * Replace the original content of the segments with the generated lines.
     */
    void consolidate() {
        if (!inMemory && !segments.isEmpty()) {
            throw new GeciException(
                    "This is an internal error: source was not read into memory but segments were generated");
        }
        if (globalSegment == null) {
            for (var entry : segments.entrySet()) {
                touched = true;
                var id = entry.getKey();
                var segment = entry.getValue();
                var segmentLocation = findSegment(id);
                if (segmentLocation == null) {
                    segmentLocation = findDefaultSegment();
                    if (segmentLocation == null) {
                        throw new GeciException("Segment " + id + " disappeared from source" + absoluteFile);
                    }
                }
                mergeSegment(segment, segmentLocation);
            }
        } else {
            touched = true;
            lines.clear();
            lines.addAll(globalSegment.lines);
        }
    }

    private void mergeSegment(Segment segment, SegmentDescriptor segmentLocation) {
        if (segmentLocation.startLine + 1 < segmentLocation.endLine
                || segment.lines.size() > 0) {
            lines.subList(segmentLocation.startLine + 1, segmentLocation.endLine).clear();
            lines.addAll(segmentLocation.startLine + 1, segment.postface);
            lines.addAll(segmentLocation.startLine + 1, segment.lines);
            lines.addAll(segmentLocation.startLine + 1, segment.preface);
        }
    }

    /**
     * @return {@code true} if the file was modified
     */
    boolean isModified(BiPredicate<List<String>, List<String>> sourceModified) {
        return sourceModified.test(originals, lines);
    }

    /**
     * Saves the modified lines to the file.
     */
    void save() throws IOException {
        Path path = Paths.get(absoluteFile);
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (Exception ignored) {
            }
        }
        Files.write(Paths.get(absoluteFile), lines, Charset.forName("utf-8"));
    }

    /**
     * Reads the content of the file into the fields {@code lines} and {@code originals}.
     * Each element of the list will contain one line of the file. The list in {@code lines}
     * is updated by code generation, while {@code originals} is kept as a reference to decide
     * during the save process if the lines have to be written back to the file or not.
     *
     * @throws IOException if the file cannot be read
     */
    private void readToMemory() throws IOException {
        Files.lines(Paths.get(absoluteFile)).forEach(line -> {
            lines.add(line);
            originals.add(line);
        });
        inMemory = true;
    }

    private SegmentDescriptor findDefaultSegment() {
        if (allowDefaultSegment) {
            for (int i = lines.size() - 1; 0 < i; i--) {
                final var line = lines.get(i);
                final var matcher = splitHelper.match(line);
                if (matcher.isDefaultSegmentEnd()) {
                    var seg = new SegmentDescriptor();
                    seg.attr = null;
                    seg.startLine = i - 1;
                    seg.endLine = i;
                    seg.tab = matcher.tabbing();
                    return seg;
                }
            }
        }
        return null;
    }

    /**
     * Search the segment of the given 'id'.
     *
     * @param id is the identifier of the segment
     * @return the {@link SegmentDescriptor} structure describing the segment start or {@code null} if there is no segment
     * with the given 'id'.
     */
    private SegmentDescriptor findSegment(String id) {
        for (int i = 0; i < lines.size(); i++) {
            final var line = lines.get(i);
            final var matcher = splitHelper.match(line);
            if (matcher.isSegmentStart()) {
                var attr = matcher.attributes();
                if (id.equals(attr.get("id"))) {
                    var seg = new SegmentDescriptor();
                    seg.attr = attr;
                    seg.tab = matcher.tabbing();
                    seg.startLine = i;
                    seg.endLine = findSegmentEnd(i, id);
                    return seg;
                }
            }
        }
        return null;
    }

    /**
     * Find the end of the segment that starts at line {@code start}.
     *
     * @param start the start of the segment of which we seek the end
     * @return the index of the line that contains the segment ending
     */
    private int findSegmentEnd(int start, String id) {
        for (int i = start + 1; i < lines.size(); i++) {
            final var line = lines.get(i);
            final var matcher = splitHelper.match(line);
            if (matcher.isSegmentEnd()) {
                return i;
            }
        }
        throw new GeciException("Segment '" + id + "'does not end in file " + getAbsoluteFile());
    }

    /**
     * Structure that contains the index in the {@code lines} list where the segment starts,
     * the attributes specified in the {@code <editor-fold ...>} line and the number of spaces
     * at the start of the segment starting line. This is used as the initial indentation of the segment.
     * <p>
     * Note that this structure has to be short living and it is mainly to return these values from the function
     * {@link #findSegment(String)}. When the segments are consolidated the index values in {@code lines}
     * will change.
     */
    private class SegmentDescriptor {
        int startLine;
        int endLine;
        Map<String, String> attr;
        int tab;
    }
}
