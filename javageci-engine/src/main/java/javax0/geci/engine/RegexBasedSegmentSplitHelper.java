package javax0.geci.engine;

import javax0.geci.api.SegmentSplitHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexBasedSegmentSplitHelper implements SegmentSplitHelper {

    private static final Pattern attributePattern = Pattern.compile("([\\w\\d_$]+)\\s*=\\s*\"(.*?)\"");
    final Pattern startPattern;
    final Pattern endPattern;
    private boolean segmentStart;
    private boolean segmentEnd;
    private Map<String, String> attributes;
    private int tabs;

    public RegexBasedSegmentSplitHelper(Pattern startPattern, Pattern endPattern) {
        this.startPattern = startPattern;
        this.endPattern = endPattern;
    }

    @Override
    public void match(String line) {
        final var matcher = startPattern.matcher(line);
        segmentStart = matcher.matches();
        if (segmentStart) {
            attributes = parseParametersString(matcher.group(2));
            tabs = matcher.group(1).length();
        }
        segmentEnd = endPattern.matcher(line).matches();
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

    @Override
    public boolean isSegmentStart() {
        return segmentStart;
    }

    @Override
    public boolean isSegmentEnd() {
        return segmentEnd;
    }

    @Override
    public int tabs() {
        return 0;
    }

    @Override
    public String attribute(String key) {
        return attributes.get(key);
    }
}
