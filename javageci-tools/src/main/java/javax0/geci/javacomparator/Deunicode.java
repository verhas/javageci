package javax0.geci.javacomparator;

import javax0.geci.api.GeciException;

import java.util.function.Function;

/**
 * Convert a string that contains \u000d format unicode escape sequences
 * to contain the characters without the escape containing the raw
 * references.
 *
 * <p> This class is used when the original and generated Java source
 * code is compared and they are not character-by-character equal. In
 * that case the comparator resolves the unicode sequences and then
 * makes a limited lexical analysis and compares the lexical elements
 * instead of the characters.
 *
 */
class Deunicode implements Function<String, String> {
    /**
     * Convert the string removing the unicode segments
     *
     * @param s the string that may optionally contain unicode segments
     * @return the string with the unicode escape sequences resolved
     */
    @Override
    public String apply(String s) {
        final var output = new StringBuilder();
        for (int index = 0; index < s.length(); index++) {
            final var currentChar = s.charAt(index);
            if (currentChar == '\\' && index + 1 < s.length() && s.charAt(index + 1) == 'u') {
                index++;
                while (index < s.length() && s.charAt(index) == 'u') {
                    index++;
                }
                if (index + 3 < s.length()) {
                    try {
                        final var hex = Integer.parseInt(s.substring(index, index + 4), 16);
                        index += 3;
                        output.append((char) hex);
                    } catch (NumberFormatException e) {
                        throw new GeciException(
                                "The Java source starts a unicode escape that is malformed at position "
                                        + index
                                        + " ... "
                                        + s.substring(index, index + 4));
                    }
                } else {
                    throw new GeciException(
                            "The Java source input ended premature at position "
                                    + index
                                    + " ... "
                                    + s.substring(index));
                }
            } else {
                output.append(currentChar);
                if (currentChar == '\\' && index + 1 < s.length()) {
                    output.append(s.charAt(index + 1));
                    index++;
                }
            }
        }
        return output.toString();
    }
}
