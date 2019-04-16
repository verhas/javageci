package javax0.geci.api;

import java.util.Map;

public interface SegmentSplitHelper {

    void match(String line);
    boolean isSegmentStart();
    boolean isSegmentEnd();
    int tabbing();
    Map<String,String> attributes();
}
