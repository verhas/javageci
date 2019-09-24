package javax0.geci.javacomparator.lex;

import java.util.regex.Pattern;

/**
 * Number consuming lexical analyser. It will recognize a number at the
 * start of the input and return either an integer or a float lexical
 * element.
 */
public class NumberLiteral implements LexEater {

    private static final Pattern[] patterns = new Pattern[]{
        Pattern.compile("^([+-]?(?:0[Bb])?\\d[\\d_]*[lL]?)")
        ,
        Pattern.compile("^([+-]?0[Xx][\\da-fA-F][\\d_a-fA-F]*[lL]?)")
        ,
        Pattern.compile("^([+-]?\\d+(?:\\.\\d*)?(?:[eE][+-]?\\d+)?[fFdD]?)")
        ,
        Pattern.compile("^([+-]?0[Xx][\\da-fA-F]*(?:\\.[\\da-fA-F]*)?[pP][+-]?\\d+[fFdD]?)")
        ,
    };

    @Override
    public LexicalElement apply(StringBuilder sb) {
        final var literals = new String[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            final var matcher = patterns[i].matcher(sb.toString());
            if (matcher.find()) {
                literals[i] = matcher.group(1);
            } else {
                literals[i] = "";
            }
        }

        int maxLength = literals[0].length();
        int index = 0;
        for (int i = 1; i < patterns.length; i++) {
            if (maxLength < literals[i].length()) {
                index = i;
                maxLength = literals[i].length();
            }
        }
        if (maxLength == 0) {
            return null;
        }
        sb.delete(0, maxLength);
        return index > 1 ? new LexicalElement.FloatLiteral(literals[index])
                : new LexicalElement.IntegerLiteral(literals[index]);
    }
}
