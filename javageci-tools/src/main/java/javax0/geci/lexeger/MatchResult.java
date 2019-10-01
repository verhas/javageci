package javax0.geci.lexeger;

/**
 * A record that holds
 * <ol>
 *     <li>a {@code boolean} flag named {@code matches}, which is {@code
 *     true} if the result represents a successful match</li>
 *     <li>the {@code start} index of the matched area. This value is
 *     the index of the first element of the lexical elements that were
 *     matched.</li>
 *     <li>the {@code end} index of the matched area. This value is the
 *     index of the first element of the lexical elements AFTER the
 *     matched area.</li>
 * </ol>
 *
 * When the flag {@code matches} is {@code false} (a.k.a. the matching
 * was not successful then the {@code start} and {@code stop} values are
 * indefinite.
 *
 */
public class MatchResult {
    public final boolean matches;
    public final int start;
    public final int end;
    public static final MatchResult NO_MATCH = new MatchResult(false, 0, 0);

    public MatchResult(boolean matches, int start, int end) {
        this.matches = matches;
        this.start = start;
        this.end = end;
    }
}
