package javax0.geci.lexeger;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.javacomparator.lex.Lexer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>A simple utility class that can be used to create list of lexical
 * elements from strings. This is useful when using the {@link
 * JavaLexed#replace(int, int, List[])} or {@link
 * JavaLexed#replace(MatchResult, List[])} methods.</p>
 */
public class Lex {
    public static List<LexicalElement> of(String s) {
        return Arrays.stream(new Lexer().spaceSensitive().commentSensitive().apply(Collections.singletonList(s))).collect(Collectors.toList());
    }
}
