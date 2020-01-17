package javax0.geci.javacomparator.lex;

import javax0.geci.tools.JVM8Tools;

public class Escape {
    static char octal(StringBuilder sb, int maxLen) {
        int i = maxLen;
        int occ = 0;
        while (i > 0 && sb.length() > 0 && sb.charAt(0) >= '0' && sb.charAt(0) <= '7') {
            occ = 8 * occ + sb.charAt(0) - '0';
            sb.deleteCharAt(0);
            i--;
        }
        return (char) occ;
    }

    private static final String escapes = "btnfr\"'\\";
    private static final String escaped = "\b\t\n\f\r\"'\\";


    static void handleEscape(StringBuilder sb, StringBuilder output) {
        sb.deleteCharAt(0);
        if (sb.length() == 0) {
            throw new IllegalArgumentException("Source ended inside a string.");
        }
        final var nextCh = sb.charAt(0);
        final int esindex = escapes.indexOf(nextCh);
        if (esindex == -1) {
            if (nextCh >= '0' && nextCh <= '3') {
                output.append(octal(sb, 3));
            } else if (nextCh >= '4' && nextCh <= '7') {
                output.append(octal(sb, 2));
            } else {
                throw new IllegalArgumentException("Invalid escape sequence in string: \\" + nextCh);
            }
        } else {
            output.append(escaped.charAt(esindex));
            sb.deleteCharAt(0);
        }
    }

    static void handleNormalCharacter(StringBuilder sb, StringBuilder output) {
        final char ch = sb.charAt(0);
        if (ch == '\n' || ch == '\r') {
            throw new IllegalArgumentException("String not terminated before eol.");
        }
        output.append(ch);
        sb.deleteCharAt(0);
    }

    static void handleNormalMultiLineStringCharacter(StringBuilder sb, StringBuilder output) {
        char ch = sb.charAt(0);
        if (ch == '\n' || ch == '\r') {
            normalizedNewLines(sb, output);
        } else {
            output.append(ch);
            sb.deleteCharAt(0);
        }
    }

    private static void normalizedNewLines(StringBuilder sb, StringBuilder output) {
        char ch = sb.charAt(0);
        int countNewLines = 0;
        while (sb.length() > 0 && ch == '\n' || ch == '\r') {
            if (ch == '\n') {
                countNewLines++;
            }
            sb.deleteCharAt(0);
            if (sb.length() > 0) {
                ch = sb.charAt(0);
            }
        }
        if (countNewLines == 0) {
            countNewLines++;
        }
        output.append(JVM8Tools.string('\n', countNewLines));
    }

    static StringBuilder createOutput(StringBuilder sb, String string) {
        if (sb.length() < 2) {
            throw new IllegalArgumentException(string + " has to be at least two characters long.");
        }
        return new StringBuilder();
    }
}
