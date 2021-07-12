package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

public class Escape {
    static char octal(StringBuilder sb, int maxLen, StringBuilder original) {
        int i = maxLen;
        int occ = 0;
        while (i > 0 && sb.length() > 0 && sb.charAt(0) >= '0' && sb.charAt(0) <= '7') {
            occ = 8 * occ + sb.charAt(0) - '0';
            original.append(sb.charAt(0));
            sb.deleteCharAt(0);
            i--;
        }
        return (char) occ;
    }

    private static final String escapes = "btnfr\"'\\";
    private static final String escaped = "\b\t\n\f\r\"'\\";

    static String escape(String original) {
        final var sb = new StringBuilder(original);
        final var output = new StringBuilder();
        final var ignored = new StringBuilder();
        while (sb.length() > 0) {
            final char ch = sb.charAt(0);
            if (ch == '\\') {
                handleEscape(sb, output, ignored);
            } else {
                output.append(ch);
                sb.deleteCharAt(0);
            }
        }
        return output.toString();
    }

    static void handleEscape(StringBuilder sb, StringBuilder output, StringBuilder original) {
        original.append(sb.charAt(0));
        sb.deleteCharAt(0);
        if (sb.length() == 0) {
            throw new IllegalArgumentException("Source ended inside a string.");
        }
        final var nextCh = sb.charAt(0);
        final int esindex = escapes.indexOf(nextCh);
        if (esindex == -1) {
            if (nextCh >= '0' && nextCh <= '3') {
                output.append(octal(sb, 3, original));
            } else if (nextCh >= '4' && nextCh <= '7') {
                output.append(octal(sb, 2, original));
            } else {
                throw new IllegalArgumentException("Invalid escape sequence in string: \\" + nextCh);
            }
        } else {
            original.append(nextCh);
            output.append(escaped.charAt(esindex));
            sb.deleteCharAt(0);
        }
    }

    static void handleNormalCharacter(StringBuilder sb, StringBuilder output, StringBuilder original) {
        final char ch = sb.charAt(0);
        if (ch == '\n' || ch == '\r') {
            throw new GeciException("String not terminated before eol:\n" + sb.substring(1, Math.min(sb.length(), 60)) + "...");
        }
        output.append(ch);
        original.append(ch);
        sb.deleteCharAt(0);
    }

    static void handleNormalMultiLineStringCharacter(StringBuilder sb, StringBuilder output, StringBuilder original) {
        char ch = sb.charAt(0);
        if (ch == '\n' || ch == '\r') {
            normalizedNewLines(sb, output, original);
        } else {
            output.append(ch);
            original.append(ch);
            sb.deleteCharAt(0);
        }
    }

    /**
     * <p>Convert many subsequent {@code \n} and {@code \r} characters to {@code \n} only. There will be as many {@code
     * \n} characters in the output as many there were in the input and the {@code \r} characters are simply ignored.
     * The only exception is, when there are no {@code \n} characters. In this case there will be one {@code \n} in the
     * output.</p>
     *
     * <p>The method deletes the characters from the start of the input StringBuilder {@code sb} and append the output
     * to the {@code output}. The original characters will be appended to the end of {@code original} without any
     * conversion.</p>
     *
     * @param sb       the string builder input, from which the characters are consumed.
     * @param output   where the converted newline are appended to
     * @param original where the original characters removed from {@code sb} are appended
     */
    private static void normalizedNewLines(StringBuilder sb, StringBuilder output, StringBuilder original) {
        char ch = sb.charAt(0);
        int countNewLines = 0;
        while (sb.length() > 0 && (ch == '\n' || ch == '\r')) {
            if (ch == '\n') {
                countNewLines++;
            }
            sb.deleteCharAt(0);
            original.append(ch);
            if (sb.length() > 0) {
                ch = sb.charAt(0);
            }
        }
        // if there was a single, or multiple \r without any \n
        if (countNewLines == 0) {
            countNewLines++;
        }
        output.append("\n".repeat(countNewLines));
    }

    static StringBuilder createOutput(StringBuilder sb, String string) {
        if (sb.length() < 2) {
            throw new IllegalArgumentException(string + " has to be at least two characters long.");
        }
        return new StringBuilder();
    }
}
