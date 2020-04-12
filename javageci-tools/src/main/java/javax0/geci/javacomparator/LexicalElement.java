package javax0.geci.javacomparator;

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
     * @param lexeme the new lexeme string.
     */
    void setLexeme(String lexeme);

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
