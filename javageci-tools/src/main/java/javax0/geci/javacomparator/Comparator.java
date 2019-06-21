package javax0.geci.javacomparator;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.List;
import java.util.function.BiPredicate;

public class Comparator implements BiPredicate<List<String>, List<String>> {
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
        Lexer lexer = new Lexer();
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
