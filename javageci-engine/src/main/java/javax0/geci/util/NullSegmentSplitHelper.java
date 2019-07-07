package javax0.geci.util;

import javax0.geci.api.CompoundParams;
import javax0.geci.api.SegmentSplitHelper;

/**
 * A segment split helper that will not find any segment in any source.
 */
public class NullSegmentSplitHelper implements SegmentSplitHelper {
    @Override
    public Matcher match(String line) {
        return new SegmentSplitHelper.Matcher() {
            @Override
            public boolean isSegmentStart() {
                return false;
            }

            @Override
            public boolean isSegmentEnd() {
                return false;
            }

            @Override
            public boolean isDefaultSegmentEnd() {
                return false;
            }

            @Override
            public int tabbing() {
                return 0;
            }

            @Override
            public CompoundParams attributes() {
                return new javax0.geci.tools.CompoundParams();
            }
        };
    }

    @Override
    public String[] getSegmentPreface() {
        return new String[0];
    }

    @Override
    public String[] getSegmentPostface() {
        return new String[0];
    }
}
