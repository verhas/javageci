package javax0.geci.javacomparator;

import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public class Comparator implements BiPredicate<List<String>, List<String>> {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param strings1 the first input argument
     * @param strings2 the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(List<String> strings1, List<String> strings2) {
        if (fastCheck(strings1, strings2)) return false;
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

    private boolean fastCheck(List<String> strings1, List<String> strings2) {
        return strings1.size() == strings2.size() &&
                !IntStream.range(0, strings2.size())
                        .anyMatch(i -> !strings2.get(i).equals(strings1.get(i)));
    }
}
