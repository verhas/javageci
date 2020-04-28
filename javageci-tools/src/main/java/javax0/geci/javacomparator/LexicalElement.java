package javax0.geci.javacomparator;

import javax0.geci.api.GeciException;

public interface LexicalElement {

    /**
     * @return the lexeme string of the lexical element. Note that this
     * string does not contain the delimiting characters in case there
     * are any. For example if the lexical element is a String then the
     * return value is the string itself without the delimiting {@code
     * "} characters.
     */
    String getLexeme();

    /**
     * Replace the existing lexeme with a new string.
     *
     * @param lexeme the new lexeme string.
     */
    void setLexeme(String lexeme);

    /**
     * <p>Replace the existing original with a new original string.</p>
     *
     * <p>The original contains the original text of a string or character literal as it is in the source code without
     * the delimiter characters. The {@code lexeme} contains the actual character or string. The difference is that
     * the {@code lexeme} does not contain escape sequences. When the value for the {@code lexeme} field is created
     * the escape sequences (octal, \n, \r etc) are replaced with the actual character. The {@code original} field
     * contains these in their original sequence.</p>
     *
     * <p>When the original is set then this setter calculates the escaped string converting all escape sequences
     * replacing them with the character they mean and it is stored in the field {@code lexeme}. That way there is no
     * need to call {@link #setLexeme(String)} after calling this method.</p>
     *
     * <p>This method should be called only in case the lexical element is a CHARACTER or STRING. Calling this method on
     * any other type of lexical element will throw a {@link GeciException}.</p>
     *
     * @param original the new "original" string
     */
    void setOriginal(final String original);

    /**
     *
     * @return the full lexeme of the lexical element. This string
     * contains the delimiting characters as they are in the source
     * code. That way, for example a String will include the opening and
     * closing {@code "} character, a character will contain the opening
     * and closing {@code '} character and later, when Java 13 will be
     * handled by the lexical analyzer then multi line strings will
     * include the opening {@code """} with the spaces and the new line
     * characters and the closing {@code """} delimiter.
     */
    String getFullLexeme();

    String getOriginalLexeme();

    /**
     *
     * @return the type of the lexeme, which is an embedded enum type of
     * this interface (see {@link Type}).
     */
    Type getType();

    /**
     * The type of a lexeme.
     */
    enum Type {
        COMMENT, STRING, CHARACTER, IDENTIFIER, INTEGER, FLOAT, SYMBOL, SPACING, INVALID;

        /**
         * Decides if a certain lexeme type is one of the types provided
         * in the argument. Thus for example the code
         *
         * <pre>
         *     {@code
         *     element.type.is(STRING, CHARACTER, FLOAT)
         *     }
         * </pre>
         *
         * is {@code true} if the element type is STRING or CHARACTER or
         * FLOAT.
         *
         * @param types the possible types we check against
         * @return {@code true} if the type is one of those listed,
         * {@code false} otherwise.
         */
        public boolean is(Type... types) {
            for (final var t : types) {
                if (t == this) return true;
            }
            return false;
        }
    }
}
