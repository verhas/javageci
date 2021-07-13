package javax0.geci.engine;


import javax0.geci.api.CompoundParams;
import javax0.geci.api.Distant;
import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Logger;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.tools.GeciReflectionTools;
import javax0.geci.util.JavaSegmentSplitHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public class Source implements javax0.geci.api.Source {
    final List<String> lines = new ArrayList<>();
    private final String className;
    final String relativeFile;
    final String absoluteFile;
    private final Map<String, Segment> segments = new HashMap<>();
    final List<String> originals = new ArrayList<>();
    private final FileCollector collector;
    private final SegmentSplitHelper splitHelper;
    boolean inMemory = false;
    private Segment globalSegment = null;
    private boolean touched = false;
    private long touchBits = 0;
    boolean allowDefaultSegment = false;
    boolean isBinary = false;
    private boolean isBorrowed = false;

    public MockSourceStore getSourceStore() {
        if( store instanceof MockSourceStore) {
            return (MockSourceStore) store;
        }
        throw new GeciException("Source.getSourceStore() must be invoked only from test code");
    }

    private final SourceStore store;

    Generator currentGenerator = null;

    static class SourceIsBinary extends GeciException {
        String getAbsoluteFile() {
            return absoluteFile;
        }

        final String absoluteFile;

        SourceIsBinary(String absoluteFile) {
            super();
            this.absoluteFile = absoluteFile;
        }
    }

    private void assertTouching() {
        if (currentGenerator != null && currentGenerator instanceof Distant) {
            throw new GeciException("The distant generator " + currentGenerator.getClass().getName() +
                " tried to touch the source " + getAbsoluteFile());
        }
    }

    public static class MockBuilder {
        private final Generator sut;
        private String className;
        private String relativeFile = "DIRECTORY\\MOCKED_FILE.MOCKED";
        private String absoluteFile = "C:\\MOCKED\\DIRECTORY\\MOCKED_FILE.MOCKED";
        private SegmentSplitHelper splitHelper = new JavaSegmentSplitHelper();
        final List<String> lines = new ArrayList<>();
        private Source mockedSource;

        public MockBuilder(Generator sut) {
            this.sut = sut;
        }

        public MockBuilder absoluteFile(String absoluteFile) {
            this.absoluteFile = absoluteFile;
            return this;
        }

        public MockBuilder className(String className) {
            this.className = className;
            return this;
        }

        public MockBuilder relativeFile(String relativeFile) {
            this.relativeFile = relativeFile;
            return this;
        }

        public MockBuilder splitHelper(String fileName) {
            this.splitHelper = splitHelper;
            return this;
        }

        public MockBuilder splitHelper(SegmentSplitHelper splitHelper) {
            this.splitHelper = splitHelper;
            return this;
        }

        public MockBuilder lines(String... lines) {
            for (final var line : lines) {
                this.lines.addAll(Arrays.asList(line.split("\n")));
            }
            return this;
        }

        public boolean isTouched() {
            return mockedSource.isTouched();
        }

        public boolean isModified(BiPredicate<List<String>, List<String>> sourceModified) {
            return mockedSource.isModified(sourceModified);
        }

        public Source getSource() {
            return mockedSource = new Source(sut, className, relativeFile, absoluteFile, splitHelper, lines);
        }
    }

    public static MockBuilder mock(Generator sut) {
        return new MockBuilder(sut);
    }

    /**
     * Constructor used only when the object is created as a mock.
     *
     * @param currentGenerator the generator that is currently tested
     * @param className the mock class name, usually the real class name
     * @param relativeFile the relavite file name of the source
     * @param absoluteFile the absolute file name of the source
     * @param splitHelper the split helper, which is Java in case nothing else was set in the mock factory
     * @param lines the lines of the source code.
     */
    private Source(Generator currentGenerator,
                   String className,
                   String relativeFile,
                   String absoluteFile,
                   SegmentSplitHelper splitHelper,
                   List<String> lines
    ) {
        this.currentGenerator = currentGenerator;
        this.className = className;
        this.relativeFile = relativeFile;
        this.absoluteFile = absoluteFile;
        this.splitHelper = splitHelper;
        this.lines.addAll(lines);
        this.originals.addAll(lines);
        this.collector = null;
        this.inMemory = true;
        this.store = new MockSourceStore();
    }

    /**
     * The constructor is not supposed to be used from outside, only through the {@link FileCollector} which
     * is invoked only from {@link Geci#generate()}.
     *
     * @param collector the file collector that this source belongs to. Note that the type {@link FileCollector}
     *                  is not exported by the module and this prevents the users of the module to use this constructor.
     * @param dir       the directory of the source
     * @param path      the path of the source
     */
    Source(FileCollector collector, String dir, Path path) {
        this.collector = collector;
        className = FileCollector.calculateClassName(dir, path);
        relativeFile = FileCollector.calculateRelativeName(dir, path);
        absoluteFile = FileCollector.toAbsolute(path);
        splitHelper = collector.getSegmentSplitHelper(this);
        store = new FileSystemSourceStore(this.collector, relativeFile, dir);
    }

    /**
     * A source is touched if the generator was writing to it. It is
     * even touched if the generator was writing the same content to it
     * what there was originally. This flag is used to identify the
     * situation when a generator does not touch any source When a
     * generator is executed and does not touch any source it throws an
     * exception because it certainly means that there is a
     * configuration error. Either it is supposed to touch something or
     * it should not be executed, the test should just be disabled.
     *
     * @return true when the source was touched
     */
    boolean isTouched() {
        return touched;
    }

    long getTouchBits() {
        return touchBits;
    }

    @Override
    public String getAbsoluteFile() {
        return absoluteFile;
    }

    @Override
    public void init(String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return;
        }
        open(id);
    }

    @Override
    public javax0.geci.api.Source newSource(Source.Set sourceSet, String fileName) {
        assertTouching();
        return store.get(sourceSet, fileName);
    }

    @Override
    public javax0.geci.api.Source newSource(String fileName) {
        assertTouching();
        return store.get(fileName);
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

    @Override
    public void returns(final List<String> lines) {
        if (!isBorrowed) {
            throw new GeciException("Source " + getAbsoluteFile() + " cannot be returned before it was borrowed.");
        }
        if (!inMemory) {
            throw new GeciException("Source " + getAbsoluteFile() + " cannot be returned before it is read from file.");
        }
        inMemory = true;
        if (lines != null) {
            this.lines.clear();
            this.lines.addAll(lines);
        }
        isBorrowed = false;
        touched = true;
    }

    @Override
    public List<String> borrows() {
        if (isBorrowed) {
            throw new GeciException("Source " + getAbsoluteFile() + " cannot be borrowed more than once. Has to be returned before.");
        }
        final var lines = getLines();
        isBorrowed = true;
        return lines;
    }

    @Override
    public List<String> getLines() {
        assertNotBorrowed();
        readToMemorySafely();
        return lines;
    }

    /**
     * Read the content of the source into the object from the file. In case there is an
     * {@code IOException} then we treat it like the file does not exist yet. No lines, but
     * was read into memory.
     */
    private void readToMemorySafely() {
        if (!inMemory) {
            try {
                readToMemory();
            } catch (IOException e) {
                inMemory = true;
                originals.clear();
                lines.clear();
            }
        }
    }

    @Override
    public Segment open() {
        assertNotBorrowed();
        assertTouching();
        if (!segments.isEmpty()) {
            throw new GeciException("Global segment was opened when the there were already opened segments");
        }
        if (globalSegment == null) {
            globalSegment = new Segment(0);
        }
        readToMemorySafely();
        return globalSegment;
    }

    @Override
    public java.util.Set<String> segmentNames() {
        assertNotBorrowed();
        loadSegments();
        return segments.keySet();
    }

    @Override
    public Segment temporary() {
        assertTouching();
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

    /**
     * Replace every {@code {{menmonic}}} with {@code id} in the strings
     * {@code s}
     *
     * @param id the identifier to replace the mnemonic placeholders
     * @param s  the strings to replace the mnemonics in
     * @return the modified string array
     */
    private String[] mnemonize(String id, String... s) {
        return Arrays.stream(s)
            .map(item -> item.replaceAll("\\{\\{mnemonic}}", id))
            .toArray(String[]::new);
    }

    @Override
    public void allowDefaultSegment() {
        this.allowDefaultSegment = true;
    }

    @Override
    public Segment open(String id) throws IOException {
        assertNotBorrowed();
        assertTouching();
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
            try (final var segment = new Segment(segDesc.tab, segDesc.attr, segDesc.originals)) {
                if (defaultSegment) {
                    segment.setPreface(mnemonize(id, splitHelper.getSegmentPreface()));
                    segment.setPostface(mnemonize(id, splitHelper.getSegmentPostface()));
                }
                segments.put(id, segment);
            }
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

    private void assertNotBorrowed() {
        if (isBorrowed) {
            throw new GeciException("Source " + getAbsoluteFile() + " was borrowed and not returned.");
        }
    }

    /**
     * Replace the original content of the segments with the generated lines.
     */
    public void consolidate() {
        assertNotBorrowed();
        if (!inMemory && !segments.isEmpty()) {
            throw new GeciException(
                "This is an internal error: source was not read into memory but segments were generated");
        }
        if (globalSegment == null) {
            segments.forEach(
                (id, segment) -> {
                    touched = true;
                    touchBits |= segment.touch(0);
                    var segmentLocation = findSegment(id);
                    if (segmentLocation == null) {
                        segmentLocation = findDefaultSegment();
                        if (segmentLocation == null) {
                            throw new GeciException("Segment " + id + " disappeared from source" + absoluteFile);
                        }
                    }
                    mergeSegment(segment, segmentLocation);
                }
            );
        } else {
            touched = true;
            lines.clear();
            lines.addAll(globalSegment.lines);
        }
    }

    private void mergeSegment(Segment segment, SegmentDescriptor segmentLocation) {
        if (segmentLocation.startLine < segmentLocation.endLine
            || segment.lines.size() > 0) {
            if (segmentLocation.startLine < segmentLocation.endLine) {
                lines.subList(segmentLocation.startLine, segmentLocation.endLine).clear();
            }
            lines.addAll(segmentLocation.startLine, segment.postface);
            lines.addAll(segmentLocation.startLine, segment.lines);
            lines.addAll(segmentLocation.startLine, segment.preface);
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
        final Path path = Paths.get(absoluteFile);
        final Path parent = path.getParent();
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (Exception ignored) {
            }
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
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
        try (final var stream = Files.lines(Paths.get(absoluteFile))) {
            stream.forEach(line -> {
                lines.add(line);
                originals.add(line);
            });
            inMemory = true;
        } catch (IOException e) {
            throw e;
        } catch (UncheckedIOException e) {
            isBinary = true;
            throw new SourceIsBinary(absoluteFile);
        } catch (Exception e) {
            throw new GeciException("Cannot read the file " + absoluteFile + "\nIt is probably binary file. Use '.ignore()' to filter binary files out", e);
        }
    }

    private SegmentDescriptor findDefaultSegment() {
        if (allowDefaultSegment) {
            for (int i = lines.size() - 1; 0 < i; i--) {
                final var matcher = splitHelper.match(lines, i);
                if (matcher.isDefaultSegmentEnd()) {
                    var seg = new SegmentDescriptor();
                    seg.attr = null;
                    seg.startLine = i + matcher.headerLength();
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
            var line = lines.get(i);
            final var matcher = splitHelper.match(lines, i);
            if (matcher.isSegmentStart()) {
                var attr = matcher.attributes();
                if (id.equals(attr.id())) {
                    var seg = new SegmentDescriptor();
                    seg.id = id;
                    seg.originals = new ArrayList<>();
                    seg.attr = attr;
                    seg.tab = matcher.tabbing();
                    seg.startLine = i + matcher.headerLength();
                    for (int j = seg.startLine; j < lines.size(); j++) {
                        line = lines.get(j);
                        final var endMatcher = splitHelper.match(line);
                        if (endMatcher.isSegmentEnd()) {
                            seg.endLine = j;
                            return seg;
                        }
                        seg.originals.add(line);
                    }
                    throw new GeciException("Segment '" + seg.attr.id() + "'does not end in file " + getAbsoluteFile());
                }
            }
        }
        return null;
    }

    private boolean segmentsLoaded = false;

    private void loadSegments() {
        if (segmentsLoaded) {
            return;
        }
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            final var matcher = splitHelper.match(lines, i);
            if (matcher.isSegmentStart()) {
                var attr = matcher.attributes();
                var seg = new SegmentDescriptor();
                seg.id = attr.id();
                seg.originals = new ArrayList<>();
                seg.attr = attr;
                seg.tab = matcher.tabbing();
                seg.startLine = i + matcher.headerLength();
                for (i = seg.startLine; i < lines.size(); i++) {
                    line = lines.get(i);
                    final var endMatcher = splitHelper.match(lines, i);
                    if (endMatcher.isSegmentEnd()) {
                        seg.endLine = i;
                        if (!segments.containsKey(seg.id)) {
                            segments.put(seg.id, new Segment(seg.tab, seg.attr, seg.originals));
                        } else {
                            throw new GeciException("Segment " + seg.id + " is defined multiple times in source " + getAbsoluteFile());
                        }
                        break;
                    }
                    seg.originals.add(line);
                }
                if (i >= lines.size()) {
                    throw new GeciException("Segment '" + seg.attr.id() + "' does not end in file " + getAbsoluteFile());
                }
            }
        }
        segmentsLoaded = true;
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
    private static class SegmentDescriptor {
        String id;
        List<String> originals;
        int startLine;
        int endLine;
        CompoundParams attr;
        int tab;
    }

    /**
     * Equals and hashCode are needed when we collect the sources to avoid that a single file gets into the source set
     * more than once.
     *
     * @param other source to compare
     * @return as per contract
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Source source = (Source) other;
        return absoluteFile.equals(source.absoluteFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(absoluteFile);
    }
}
