package javax0.geci.engine;


import javax0.geci.api.GeciException;
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
import java.util.regex.Pattern;

public class Source implements javax0.geci.api.Source {
    private static final Pattern startPattern = Pattern.compile("^(\\s*)//\\s*<\\s*editor-fold\\s+(.*)>\\s*$");
    private static final Pattern endPattern = Pattern.compile("^\\s*//\\s*<\\s*/\\s*editor-fold\\s*>\\s*$");
    private static final Pattern attributePattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*\"(.*?)\"");
    private static final String NOT_COMPILED_YET = null;
    final List<String> lines = new ArrayList<>();
    private final String dir;
    private final String className;
    private final String relativeFile;
    private final String absoluteFile;
    private final Map<String, Segment> segments = new HashMap<>();
    private final List<String> originals = new ArrayList<>();
    private final FileCollector collector;
    boolean inMemory = false;
    private Segment globalSegment = null;

    /**
     * The constructor is not supposed to be used from outside, only through the {@link FileCollector} which indeed
     * is invoked only from {@link Geci#generate()}.
     *
     * @param collector the file collector that this source belongs to. Note that the type {@link FileCollector}
     *                  is not expoted by the module and this prevents the usres of the module to use this constructor.
     * @param dir       the directory of the source
     * @param path      the path of the source
     */
    public Source(FileCollector collector, String dir, Path path) {
        this.collector = collector;
        this.dir = dir;
        this.className = FileCollector.calculateClassName(dir, path);
        this.relativeFile = FileCollector.calculateRelativeName(dir, path);
        this.absoluteFile = FileCollector.toAbsolute(path);
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
    public Source newSource(Source.Set sourceSet, String fileName) {
        if (!collector.directories.containsKey(sourceSet)) {
            throw new GeciException("SourceSet '" + sourceSet + "' does not exist");
        }
        var directory = collector.directories.get(sourceSet)[0];
        var source = new Source(collector, directory, inDir(directory, fileName));
        collector.addNewSource(source);
        return source;
    }

    @Override
    public Source newSource(String fileName) {
        for (final var source : collector.newSources) {
            if (this.absoluteFile.equals(source.absoluteFile)) {
                return source;
            }
        }
        var source = new Source(collector, dir, inDir(dir, fileName));
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

    @Override
    public Segment open(String id) throws IOException {
        if (globalSegment != null) {
            throw new GeciException("Segment was opened after the global segment was already created.");
        }
        if (!inMemory) {
            readToMemory();
        }
        if (!segments.containsKey(id)) {
            var segDesc = findSegment(id);
            if (segDesc == null) {
                return null;
            }
            var segment = new Segment(segDesc.tab);
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
            return Class.forName(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
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
                var id = entry.getKey();
                var segment = entry.getValue();
                var segDesc = findSegment(id);
                if (segDesc == null) {
                    throw new GeciException("Segment " + id + " disappeared from source" + absoluteFile);
                }
                lines.subList(segDesc.startLine + 1, segDesc.endLine).clear();
                lines.addAll(segDesc.startLine + 1, segment.lines);
            }
        } else {
            lines.clear();
            lines.addAll(globalSegment.lines);
        }
    }

    /**
     * @return {@code true} if the file was modified
     */
    boolean isModified() {
        if (originals.size() == lines.size()) {
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).equals(originals.get(i))) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Saves the modified lines to the file and return {@code true}. If the lines were not modified then
     * do not touch the file and return {@code false}
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

    /**
     * Search the segment of the given 'id'.
     *
     * @param id is the identifier of the segment
     * @return the {@link SegmentDescriptor} structure describing the segment start or {@code null} if there is no segment
     * with the given 'id'.
     */
    private SegmentDescriptor findSegment(String id) {
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            var lineMatcher = startPattern.matcher(line);
            if (lineMatcher.matches()) {
                var attr = parseParametersString(lineMatcher.group(2));
                if (id.equals(attr.get("id"))) {
                    var seg = new SegmentDescriptor();
                    seg.attr = attr;
                    seg.tab = lineMatcher.group(1).length();
                    seg.startLine = i;
                    seg.endLine = findSegmentEnd(i);
                    return seg;
                }
            }
        }
        return null;
    }

    /**
     * Parses the parameters on the line that contains the {@code // <editor-fold...>} line. For example
     * if the line is
     * <pre>
     *     // <editor-fold id="aa" desc="sample description" other_param="other">
     * </pre>
     * <p>
     * then the map will contain the values:
     * <pre>
     *     Map.of("id","aa","desc","sample description","other_param","other")
     * </pre>
     *
     * @param attributes the string containing the part of the line that is after the {@code editor-fold} and
     *                   before the closing {@code >}
     * @return the attributes map
     */
    private Map<String, String> parseParametersString(String attributes) {
        var attributeMatcher = attributePattern.matcher(attributes);
        var attr = new HashMap<String, String>();
        while (attributeMatcher.find()) {
            var key = attributeMatcher.group(1);
            var value = attributeMatcher.group(2);
            attr.put(key, value);
        }
        return attr;
    }

    /**
     * Find the end of the segment that starts at line {@code start}.
     *
     * @param start the start of the segment of which we seek the end
     * @return the index of the line that contains the segment ending
     */
    private int findSegmentEnd(int start) {
        for (int i = start + 1; i < lines.size(); i++) {
            var line = lines.get(i);
            var lineMatcher = endPattern.matcher(line);
            if (lineMatcher.matches()) {
                return i;
            }
        }
        return lines.size();
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
