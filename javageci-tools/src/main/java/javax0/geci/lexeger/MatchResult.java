package javax0.geci.lexeger;

public class MatchResult {
    public final boolean matches;
    public final int start;
    public final int end;
    public static MatchResult NO_MATCH = new MatchResult(false, 0, 0);

    public MatchResult(boolean matches, int start, int end) {
        this.matches = matches;
        this.start = start;
        this.end = end;
    }
}
