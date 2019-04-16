package javax0.geci.api;

public interface SegmentSplitHelper {

    void match(String line);
    boolean isSegmentStart();
    boolean isSegmentEnd();
    int tabs();
    String attribute(String key);
}
