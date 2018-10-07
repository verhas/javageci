package javax0.geci.engine;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Source implements javax0.geci.api.Source {
    private final String className;
    private final String absoluteFile;
    private final Map<String, Segment> segments = new HashMap<>();
    final List<String> lines = new ArrayList<>();
    private final List<String> originals = new ArrayList<>();
    boolean inMemory = false;

    public Source(String className, String absoluteFile) {
        this.className = className;
        this.absoluteFile = absoluteFile;
    }

    @Override
    public Segment open(String id) throws IOException {
        if (!inMemory) {
            readToMemory();
        }
        if (!segments.containsKey(id)) {
            var seg = findSegmentStart(id);
            if (seg == null) {
                return null;
            }
            var segment = new Segment(seg.tab);
            segments.put(id, segment);

        }
        return segments.get(id);
    }

    @Override
    public String getKlassName() {
        return className;
    }

    @Override
    public Class<?> getKlass() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
    }

    /**
     * Replace the original content of the segments with the generated lines.
     */
    void consolidate() throws IOException {
        if (!inMemory) {
            readToMemory();
        }
        for (var entry : segments.entrySet()) {
            var id = entry.getKey();
            var segment = entry.getValue();
            var seg = findSegmentStart(id);
            var endIndex = findSegmentEnd(seg.startLine);
            for (int i = endIndex - 1; i > seg.startLine; i--) {
                lines.remove(i);
            }
            var i = 1;
            for (var line : segment.lines) {
                lines.add(seg.startLine + i, line);
                i++;
            }
        }
    }

    /**
     * Saves the modified lines to the file and return {@code true}. If the lines were not modified then
     * do not touch the file and return {@code false}
     *
     * @return {@code true} if the file was modified and successfully saved
     */
    boolean save() throws IOException {
        boolean modified = false;
        if (originals.size() == lines.size()) {
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).equals(originals.get(i))) {
                    modified = true;
                    break;
                }
            }
        } else {
            modified = true;
        }
        if (modified) {
            Files.write(Paths.get(absoluteFile), lines, Charset.forName("utf-8"));
        }
        return modified;
    }

    private void readToMemory() throws IOException {
        Files.lines(Paths.get(absoluteFile)).forEach(line -> {
            lines.add(line);
            originals.add(line);
        });
        inMemory = true;
    }

    private static final Pattern startPattern = Pattern.compile("^(\\s*)//\\s*<\\s*editor-fold\\s+(.*)>\\s*$");
    private static final Pattern endPattern = Pattern.compile("^\\s*//\\s*<\\s*/\\s*editor-fold\\s*>\\s*$");
    private static final Pattern attributePattern = Pattern.compile("(\\w+)\\s*=\\s*\"(.*?)\"");

    private class SegmentStart {
        int startLine;
        Map<String, String> attr;
        int tab;
    }

    private SegmentStart findSegmentStart(String id) {
        var seg = new SegmentStart();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            var lineMatcher = startPattern.matcher(line);
            if (lineMatcher.matches()) {
                var attributes = lineMatcher.group(2);
                var attributeMatcher = attributePattern.matcher(attributes);
                var attr = new HashMap<String, String>();
                while (attributeMatcher.find()) {
                    var key = attributeMatcher.group(1);
                    var value = attributeMatcher.group(2);
                    attr.put(key, value);
                }
                if (attr.containsKey("id") && attr.get("id").equals(id)) {
                    seg.attr = attr;
                    seg.tab = lineMatcher.group(1).length();
                    seg.startLine = i;
                    return seg;
                }
            }
        }
        return null;
    }

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

    private boolean isStart(String line) {
        return startPattern.matcher(line).matches();
    }

    @Override
    public String toString() {
        return className;
    }

}
