package javax0.geci.lexeger;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.javacomparator.lex.Lexer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lex {
    public static List<LexicalElement> of(String s) {
        return Arrays.stream(new Lexer().spaceSensitive().commentSensitive().apply(List.of(s))).collect(Collectors.toList());
    }
}
