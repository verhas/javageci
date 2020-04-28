package javax0.geci.tools;

import java.util.function.Function;

public class CaseTools {

    /**
     * Lower case the first character of the input string.
     *
     * @param s the input string
     * @return the string with the first character lower cased
     */
    public static String lcase(String s) {
        return xcase(s, Character::toLowerCase);
    }

    /**
     * Upper case the first character of the input string.
     *
     * @param s the input string
     * @return the string with the first character upper cased
     */
    public static String ucase(String s) {
        return xcase(s, Character::toUpperCase);
    }

    /**
     * Modify the string altering the first character
     *
     * @param s the input string
     * @param f the function applied to the first character
     * @return null for null, empty string for empty string and first character converted string for all other cases.
     */
    private static String xcase(String s, Function<Character, Character> f) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return f.apply(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
    }


    /**
     * Convert a snake cased identifier to camel case. The first character is going to be upper cased and all others
     * that are preceded in the source with an underline character. The underline characters are not copied to the
     * result String.
     *
     * @param s the string to be camel cased.
     * @return the camel cased string
     */
    public static String camel(String s) {
        if (s == null) {
            return null;
        }
        final var result = new char[s.length()];
        int dst = 0;
        var capIt = true;
        for (int src = 0; src < s.length(); src++) {
            if (s.charAt(src) == '_') {
                capIt = true;
            } else {
                if (capIt) {
                    capIt = false;
                    result[dst++] = Character.toUpperCase(s.charAt(src));
                } else {
                    result[dst++] = Character.toLowerCase(s.charAt(src));
                }
            }
        }
        return new String(result, 0, dst);
    }

    /**
     * Convert a camel cased identifier to snake cased. Every character will be upper cased and there will be an
     * underline character inserted before all the characters that are upper cased already in the source string.
     *
     * @param s the camel cased identifier that is to be converted
     * @return the snake cased string
     */
    public static String snake(String s) {
        if (s == null) {
            return null;
        }
        int dst = 0;
        final var result = new char[2 * s.length()];
        for (int src = 0; src < s.length(); src++) {
            if (Character.isUpperCase(s.charAt(src)) && src > 0) {
                result[dst++] = '_';
            }
            result[dst++] = Character.toUpperCase(s.charAt(src));
        }
        return new String(result, 0, dst);
    }

}
