package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

public class StringLiteral {
    private static final String escapes = "btnfr\"'\\";
    private static final String escaped = "\b\t\n\f\r\"'\\";

    public LexicalElement consume(StringBuilder sb) {
        if (sb.length() < 2) {
            throw new IllegalArgumentException("String has to be at least two characters long.");
        }
        sb.deleteCharAt(0);
        final var output = new StringBuilder();
        while (sb.length() > 0 && sb.charAt(0) != '"') {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                sb.deleteCharAt(0);
                if (sb.length() == 0) {
                    throw new IllegalArgumentException("Source ended inside a string.");
                }
                final var nextCh = sb.charAt(0);
                int esindex = escapes.indexOf(nextCh);
                if (esindex == -1) {
                    if (nextCh >= '0' && nextCh <= '3') {
                        output.append(octal(sb, 3));
                    } else if (nextCh >= '4' && nextCh <= '7') {
                        output.append(octal(sb, 2));
                    } else {
                        throw new GeciException("Invalid escape sequence in string");
                    }
                } else {
                    output.append(escaped.charAt(esindex));
                    sb.deleteCharAt(0);
                }
            } else {
                if (ch == '\n' || ch == '\r') {
                    throw new GeciException("String not terminated before eol.");
                }
                output.append(ch);
                sb.deleteCharAt(0);
            }
        }
        if( sb.length() == 0 ){
            throw new IllegalArgumentException("String is not terminated before eol");
        }
        sb.deleteCharAt(0);
        return new LexicalElement(output.toString(), LexicalElement.Type.STRING);
    }


    private static char octal(StringBuilder sb, int maxLen) {
        int i = maxLen;
        int occ = 0;
        while (i > 0 && sb.length() > 0 && sb.charAt(0) >= '0' && sb.charAt(0) <= '7' ) {
            occ = 8 * occ + sb.charAt(0) - '0';
            sb.deleteCharAt(0);
            i--;
        }
        return (char)occ;
    }
}