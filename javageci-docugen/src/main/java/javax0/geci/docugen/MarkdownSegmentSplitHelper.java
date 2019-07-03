package javax0.geci.docugen;

import javax0.geci.api.SegmentSplitHelper;
import javax0.geci.engine.RegexBasedSegmentSplitHelper;

import java.util.Map;
import java.util.regex.Pattern;

public class MarkdownSegmentSplitHelper  extends RegexBasedSegmentSplitHelper {
    public MarkdownSegmentSplitHelper() {
        super(Pattern.compile(
                "^(\\s*)\\[//\\]:s*\\(\\s*snipp?et\\s+(.*)\\)\\s*$"),
                Pattern.compile("^\\s*```\\s*$"),
                Pattern.compile("it will not be used ever"));
        setSegmentPreface("");
        setSegmentPostface("");
        defaultOffset = 4;
    }

    @Override
    public SegmentSplitHelper.Matcher match(String line) {
        return new Matcher(super.match(line));
    }

    class Matcher implements SegmentSplitHelper.Matcher {

        private final SegmentSplitHelper.Matcher delegate;
        protected Matcher(SegmentSplitHelper.Matcher delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean isDefaultSegmentEnd() {
            return false;
        }

        @Override
        public boolean isSegmentStart() {
            return delegate.isSegmentStart();
        }

        @Override
        public boolean isSegmentEnd() {
            return delegate.isSegmentEnd();
        }

        @Override
        public int tabbing() {
            return delegate.tabbing();
        }

        @Override
        public Map<String, String> attributes() {
            return delegate.attributes();
        }
    }

}
