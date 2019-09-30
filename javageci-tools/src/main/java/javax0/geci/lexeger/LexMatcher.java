package javax0.geci.lexeger;

/**
 * A LexMatcher can match a part of a list of lexical elements. The
 * actual implementations work on the list of lexical elements contained
 * in a {@link JavaLexed} object. The matching expression is also
 * created and compiled and contained by the matcher.
 */
public interface LexMatcher {

    /**
     * Checks if the matching expression matches the lexical elements
     * that start at the index {@code i}.
     *
     * @param i index, where the matching starts
     * @return a match result with the match start and end index and a
     * boolean flag signalling success. If the matching was successful
     * then the start index will be the same value as {@code i}.
     */
    MatchResult matchesAt(int i);

    /**
     * Checks if the matching expression matches the lexical elements
     * that start at the index {@code i} or at some later index {@code j
     * > i}.
     *
     * @param i index, where the search for a matching part starts
     * @return a match result with the match start and end index and a
     * boolean flag signalling success
     */
    MatchResult find(int i);

    /**
     * Convenience method calling {@code find(0)}. See {@link
     * #find(int)}.
     *
     * @return See {@link #find(int)}.
     */
    MatchResult find();

}
