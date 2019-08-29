package javax0.geci.docugen;

import javax0.geci.api.SegmentSplitHelper;

import java.util.List;
import java.util.regex.Pattern;

public class JavaDocSegmentSplitHelper extends MarkdownSegmentSplitHelper {
    public JavaDocSegmentSplitHelper() {
        super(
            Pattern.compile(
                    "^(\\s*)\\*?\\s*<!--\\s*snip\\s+(.*)-->\\s*$"
            ),
            Pattern.compile(
                "^(\\s*)```(\\s*)$" +
                    "|" +
                    "^(\\s*)\\[//]:\\s*#\\s*\\(\\s*end\\s+snip\\s*(.*)\\)\\s*$" +
                    "|" +
                    "^(\\s*)<!--\\s*end\\s+snip\\s*(.*)-->\\s*$"
            ),
            Pattern.compile("DEFAULT SEGMENT") // probably will not match ever, if yes, c'mon!

        );
    }

    @Override
    public SegmentSplitHelper.Matcher match(List<String> lines, int i) {
        final var line = lines.get(i);
        if (line.stripLeading().startsWith("<!--")) {
            final var sb = new StringBuilder();
            int j;
            for (j = i; j < lines.size(); j++) {
                sb.append(lines.get(j));
                if (lines.get(j).stripTrailing().endsWith("-->")) {
                    break;
                }
            }
            return new Matcher(match(sb.toString()), j - i + 1);
        } else {
            return match(line);
        }
    }
}
