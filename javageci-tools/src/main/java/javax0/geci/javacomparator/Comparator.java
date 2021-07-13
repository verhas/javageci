package javax0.geci.javacomparator;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * <p>Compare two Java source code, both given as list of strings. If
 * the strings are equal then it is okay (return {@code
 * false}).</p>
 *
 * <p>If the strings in the lists are not the same then the two source
 * code can still be the same if they only differ in formatting. To
 * check that the comparator performs a lexical analysis and if the
 * resulting lists of lexical elements are the same then the two source
 * codes, the original and the generated are the same.</p>
 *
 * <p> Note that the lexical analysis is limited in the sense that the
 * analyzer cannot be used as a Java lexical analyzer. It was developed
 * for the sole purpose to support the comparator.</p>
 *
 * <p>The two files are the same if there is difference only in spacing
 * and/or difference is only in content of comments, or comments are
 * missing or new comments are added, and/or numbers are expressed
 * differently, but they still have the same value.</p>
 */
public class Comparator implements BiPredicate<List<String>, List<String>> {
    private boolean checkComments = false;


    /**
     * This comparator can also be used in a way that also compares the
     * difference in the comments. This is used in cases when comments
     * are generated and that generated code is significant. Typically
     * when the snippet handlers write JavaDoc. In that case the normal
     * Java compare function would report that the files are identical.
     *
     * @return {@code this}
     */
    public Comparator commentSensitive() {
        checkComments = true;
        return this;
    }

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param strings1 the lines of the original code
     * @param strings2 the lines of the generated code
     * @return {@code true} if the inputs are not the same, otherwise
     * {@code false}
     */
    @Override
    public boolean test(List<String> strings1, List<String> strings2) {
        if (strings1.equals(strings2)) return false;
        final Lexer lexer;
        if (checkComments) {
            lexer = new Lexer().commentSensitive();
        } else {
            lexer = new Lexer();
        }
        LexicalElement[] elements1 = lexer.apply(strings1);
        LexicalElement[] elements2 = lexer.apply(strings2);
        if (elements1.length != elements2.length) {
            return true;
        }
        for (int i = 0; i < elements1.length; i++) {
            if (!elements1[i].equals(elements2[i])) {
                return true;
            }
        }
        return false;
    }
}
