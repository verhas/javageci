package javax0.geci.docugen;

import javax0.geci.api.SegmentSplitHelper;

import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>A segment split helper that helps to split Asciidoc documents into
 * segments. In case of asciidoc documents the comment characters are
 * {@code //} at the start of the line. There is also a block comment
 * format of the asciidoc, but that is ignored, not used by this
 * segment split helper.</p>
 *
 * <p>Asciidoc documents serve as the target for snippet handling
 * therefore the segment starts and ends are snippet targets.</p>
 *
 * <p>A segment starts with a line that is </p>
 * <pre>{@code
 *   // snip snip_name
 * }</pre>
 *
 * and it ends with a line that is either
 *
 * <pre>{@code
 * // end snip
 * }</pre>
 *
 * or
 *
 * <pre>{@code
 * ```
 * }</pre>
 *
 * <p>three back ticks.</p>
 *
 * <p>The latter can be used to insert verbaring code fragments into
 * the documentation exactly the same way as it is used to insert code
 * fragments into markdown files. As a matter of fact the snippet code
 * inserter to be used with asciidoc *is* the
 * {@link MarkdownCodeInserter}.</p>
 *
 * <p>The inserter will keep the first line of the original content in
 * case that line starts with {@code ```}. It should, however also
 * contain the format of the code, e.g.: {@code ```java}, or else the
 * split helper will recognize the three backticks as the end of the
 * segment.</p>
 *
 * <p>The segment split helper also lets you split the segment start to
 * multiple lines. The starting line is the first {@code // snip} line
 * and the consecutive lines starting with {@code //} characters are
 * also the part of the segment start unless one is the {@code // end
 * snip} line.</p>
 *
 */
public class AdocSegmentSplitHelper extends AbstractXMLSegmentSplitHelper {

    public AdocSegmentSplitHelper() {
        super(
            Pattern.compile("^(\\s*)//\\s*snip\\s+(.*)\\s*$"),
            Pattern.compile("^(\\s*)```(\\s*)$" +
                "|" +
                "^(\\s*)//\\s*end\\s+snip(.*)$"),
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!
        );
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }

    private static final Pattern SNIP = Pattern.compile("\\s*//\\s*snip\\s+.*");
    private static final Pattern SNIPEND = Pattern.compile("\\s*//\\s*end\\s+snip.*");

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
            if (SNIP.matcher(line).matches()) {
                int j;
                for (j = i; j < lines.size(); j++) {
                    if (!lines.get(j).stripLeading().startsWith("//") ||
                        lines.get(j).matches("^\\s*//\\s*end\\s+snip")) {
                        break;
                    }
                    sb.append(stripContinuationLine(lines.get(j)));
                }
                sb.insert(0, "//");
                return new Matcher(match(sb.toString()), j - i);
            } else if (SNIPEND.matcher(lines.get(i)).matches()) {
                return new Matcher(match(lines.get(i)), 1);
            }
        }
        return match(line);
    }

    protected String stripContinuationLine(String line) {
        return line.replaceAll("^\\s*//", "");
    }
}
