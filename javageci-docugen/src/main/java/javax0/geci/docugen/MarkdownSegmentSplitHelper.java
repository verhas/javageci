package javax0.geci.docugen;

import java.util.regex.Pattern;
/**
 * <p>A segment split helper that helps to split markdown documents into
 * segments. In case of Markdown documents the comment characters are
 * {@code <!--} at the start of the line and they finish with the next
 * {@code -->}.</p>
 *
 * <p>Markdown documents serve as the target for snippet handling
 * therefore the segment starts and ends are snippet targets.</p>
 *
 * <p>A segment starts with a line that is </p>
 * <pre>
 *   &lt;!-- snip snip_name parameters --&gt;
 * </pre>
 *
 * and it ends with a line that is either
 *
 * <pre>
 * &lt;!-- end snip --&gt;
 * </pre>
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
 * the documentation.</p>
 *
 * <p>The inserter will keep the first line of the original content in
 * case that line starts with {@code ```}. It should, however also
 * contain the format of the code, e.g.: {@code ```java}, or else the
 * split helper will recognize the three backticks as the end of the
 * segment.</p>
 *
 * <p>The segment split helper also lets you split the segment start to
 * multiple lines. The starting line is the first {@code <!-- snip} line
 * and the consecutive lines will also belong to the segment start
 * ending with the line that ends with {@code -->}.</p>
 */
public class MarkdownSegmentSplitHelper extends AbstractXMLSegmentSplitHelper {

    public MarkdownSegmentSplitHelper() {
        super(
// snippet MarkdownSegmentSplitHelper_patterns
                //startPattern
                Pattern.compile(
                        "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*snip\\s+(.*)\\)\\s*$" +
                                "|" +
                                "^(\\s*)<!--\\s*snip\\s+(.*)-->\\s*$"
// skip 1 line
                ),
                // e_ndPattern
                Pattern.compile(
                        "^(\\s*)```(\\s*)$" +
                                "|" +
                                "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*end\\s+snip\\s*(.*)\\)\\s*$" +
                                "|" +
                                "^(\\s*)<!--\\s*end\\s+snip\\s*(.*)-->\\s*$"
// end snippet
                ),
                //default pattern skip 1 line
                Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!

        );
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }
}
