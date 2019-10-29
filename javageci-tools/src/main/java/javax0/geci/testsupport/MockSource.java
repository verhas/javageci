package javax0.geci.testsupport;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.Distant;
import javax0.geci.api.GeciException;
import javax0.geci.api.Generator;
import javax0.geci.api.Logger;
import javax0.geci.api.Segment;
import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.api.Source;
import javax0.geci.tools.GeciReflectionTools;

import java.io.IOException;
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

public class MockSource implements Source {

    final List<String> lines = new ArrayList<>();

    public void mockSetDir(String dir) {
        this.dir = dir;
    }

    public void mockSetClassName(String className) {
        this.className = className;
    }

    public void mockSetRelativeFile(String relativeFile) {
        this.relativeFile = relativeFile;
    }

    public void mockSetAbsoluteFile(String absoluteFile) {
        this.absoluteFile = absoluteFile;
    }

    public void mockSetSplitHelper(SegmentSplitHelper splitHelper) {
        this.splitHelper = splitHelper;
    }

    private String dir;
    private String className;
    private String relativeFile;
    private String absoluteFile;
    private SegmentSplitHelper splitHelper;

    private final Map<String, MockSegment> segments = new HashMap<>();
    private final List<String> originals = new ArrayList<>();

    private MockSegment globalSegment = null;
    private boolean touched = false;
    private long touchBits = 0;
    boolean allowDefaultSegment = false;
    boolean isBinary = false;
    private boolean isBorrowed = false;

    Generator currentGenerator = null;

    private void assertTouching() {
        if (currentGenerator != null && currentGenerator instanceof Distant) {
            throw new GeciException("The distant generator " + currentGenerator.getClass().getName() +
                                        " tried to touch the source " + getAbsoluteFile());
        }
    }

    public MockSource(String... lines) {
        for (final var line : lines) {
            this.lines.addAll(Arrays.asList(line.split("\n")));
        }
    }

    public boolean isTouched() {
        return touched;
    }

    public long getTouchBits() {
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

    public void setStore(MockSourceStore store) {
        this.store = store;
    }

    private MockSourceStore store;

    @Override
    public Source newSource(Source.Set sourceSet, String fileName) {
        return store.get(sourceSet, fileName);
    }

    @Override
    public Source newSource(String fileName) {
        return store.get(fileName);
    }

    @Override
    public String toString() {
        return String.join("\n", lines);
    }

    @Override
    public void returns(final List<String> lines) {
        if (!isBorrowed) {
            throw new GeciException("Source " + getAbsoluteFile() + " cannot be returned before it was borrowed.");
        }
        if (lines != null) {
            this.lines.clear();
            this.lines.addAll(lines);
        }
        isBorrowed = false;
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
        return lines;
    }

    @Override
    public Segment open() {
        assertNotBorrowed();
        assertTouching();
        if (!segments.isEmpty()) {
            throw new GeciException("Global segment was opened when the there were already opened segments");
        }
        if (globalSegment == null) {
            globalSegment = new MockSegment(0);
        }
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
        return new MockSegment(0);
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
        assertNotBorrowed();
        assertTouching();
        if (globalSegment != null) {
            throw new GeciException("Segment was opened after the global segment was already created.");
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
            try (final var segment = new MockSegment(segDesc.tab, segDesc.attr, segDesc.originals)) {
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

    private final Logger logger = new MockLogger();

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
    public void mockConsolidate() {
        assertNotBorrowed();
        if (globalSegment == null) {
            for (var entry : segments.entrySet()) {
                touched = true;
                var id = entry.getKey();
                MockSegment segment = entry.getValue();
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
        } else {
            touched = true;
            lines.clear();
            lines.addAll(globalSegment.lines);
        }
    }

    private void mergeSegment(MockSegment segment, SegmentDescriptor segmentLocation) {
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
        Path path = Paths.get(absoluteFile);
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (Exception ignored) {
            }
        }
        Files.write(Paths.get(absoluteFile), lines, StandardCharsets.UTF_8);
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
                            segments.put(seg.id, new MockSegment(seg.tab, seg.attr, seg.originals));
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
        MockSource source = (MockSource) other;
        return absoluteFile.equals(source.absoluteFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(absoluteFile);
    }
}
