package javax0.geci.docugen;

import javax0.geci.api.SegmentSplitHelper;

import java.util.List;
import java.util.regex.Pattern;

public class AdocSegmentSplitHelper extends AbstractXMLSegmentSplitHelper {

    public AdocSegmentSplitHelper() {
        super(
            Pattern.compile("^(\\s*)//\\s*snip\\s+(.*)\\s*$"),
            Pattern.compile("^(\\s*)//\\s*end\\s+snip\\s+(.*)\\s*$"),
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!
        );
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }


    /**
     * <p>If the line starts with {@code // } then the lines following
     * it are appended to the line until one line</p>
     *
     * <ul>
     * <li>does not start with {@code //} or</li>
     * <li>starts with {@code //} but it is {@code // end snip}.</li>
     * </ul>
     * <p>
     * In any other case the call is just falling over to the {@code
     * super.match()}
     *
     * @param lines the list of lines
     * @param i     the index of the current line
     * @return a new matcher just like {@link #match(String)}.
     */
    @Override
    public SegmentSplitHelper.Matcher match(List<String> lines, int i) {
        final var line = lines.get(i);
        if (stripLine(line).startsWith("//")) {
            final var sb = new StringBuilder();
            int j;
            for (j = i; j < lines.size(); j++) {
                if (!lines.get(j).stripLeading().startsWith("//") ||
                    lines.get(j).matches("^\\s*//\\s*end\\s+snip")) {
                    j--;
                    break;
                }
                sb.append(stripContinuationLine(lines.get(j)));
            }
            return new Matcher(match(sb.toString()), j - i + 1);
        } else {
            return match(line);
        }
    }

    protected String stripContinuationLine(String line) {
        return line.replaceAll("^\\s*//", "");
    }
}
