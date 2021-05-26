package javax0.geci.tools.reflection;

/**
 * Lexical analyzer used to analyze strings in API grammar expressions and also for the selector expressions (see {@link
 * Selector} and {@code javax0.geci.fluent.syntax.Syntax}.
 * <p>
 * The selector expression does not need spaces. If there is a space it is simply terminating the previous lexical
 * element. For example {@code private | public} is just the same as {@code private|public}. When the selector
 * expression syntax analyzer initializes the lexical analyzer it instructs it to ignore spaces. On the other hand
 * fluent grammar API uses method names one after the other as a list separated by spaces. In this case a space is
 * important and the syntax analyzer of the fluent API grammar initializes the lexilca analyzer to care about the spaces
 * and return a space as lexical element for each group of space (multiple spaces count as one).
 * <p>
 * Fluent grammar API uses not only method names, but also method signatures as terminal symbols. The lexical analyzer
 * works up anything that looks like {@code methodName(type,type...,type)} as a single lexical element. This does not
 * make any harm when analysing selector expression because in the syntax of a selector expression a {@code (} character
 * never follows directly a word: there has to be {@code &} or {@code |} character before any {@code ( )} subexpression
 * grouping.
 * <p>
 * The lexical analyzer works from a String that is specified for the constructor and the individual lexemes can be
 * fetched calling {@link #get()}. It is also possibkle to peek ahead calling {@link #peek()} and to get the rest of the
 * string that was not consumed by the analysis calling {@link #rest()}. This method is usually used by error reporting
 * and is not needed for the analysis.
 * <p>
 * Lexemes are returned as instances of {@link Lexeme}.
 */
public class Lexer {
    private static final Lexeme EOF = new Lexeme("", Lexeme.Type.EOF);
    private final StringBuilder input;
    private Lexeme lookAhead = null;

    /**
     * Create a new lexical analyzer.
     *
     * @param input              the string containing the expression to be analyzed
     */
    public Lexer(final String input) {
        final String preprocessed;

        preprocessed = input.trim().replaceAll("\\s+", " ");
        this.input = new StringBuilder(preprocessed);
    }

    /**
     * A simple string conversion that shows the characters that are not yet processed. This is mainly used for
     * debugging.
     *
     * @return the characters that were not processed yet enclosed between " characters
     */
    @Override
    public String toString() {
        return "\"" + rest() + "\"";
    }

    /**
     * The characters that were not processed yet. This method is used to ease error reporting. When there is a syntax
     * error the error reporting code may invoke this method to give a hint to the user where the syntax analysis got
     * stopped. This, of course, also assumes that the syntax analysis does not read many lexemes ahead.
     *
     * @return the characters that were not processed yet.
     */
    public String rest() {
        if (lookAhead == null) {
            return input.toString();
        } else {
            return lookAhead + input.toString();
        }
    }

    /**
     * Get the next lexeme from the input and consume it. Consecutive calls to {@code get()} will get the lexemes one
     * after the other.
     *
     * @return the next lexeme.
     */
    public Lexeme get() {
        final var ret = lookAhead != null ? lookAhead : next();
        lookAhead = next();
        return ret;
    }

    /**
     * Get the next lexeme from the input but as opposed to {@link #get()} this method does not consume the lexeme. A
     * consecutive call to {@code  #peek()} or to {@link #get()} will return the same lexeme.
     *
     * @return the next lexeme
     */
    public Lexeme peek() {
        if (lookAhead == null) {
            lookAhead = next();
        }
        return lookAhead;
    }

    /**
     * Get the next lexeme from the input. This method does not look into the look-ahead buffer. This method works
     * directly on the input. This method is called by {@link #get()} and {@link #peek()}, which manage the look-ahead
     * buffer.
     * <p>
     *
     * @return the next lexeme from the input
     */
    private Lexeme next() {

        if (inputStartsWithSpace()) {
            deleteSpaceFromTheStartOfInput();
        }

        if (input.length() == 0) {
            return EOF;
        }

        if (inputStartsWithAnIdentifier()) {
            final var word = new StringBuilder();
            boolean inArgs = false;
            while (input1stCharIsStillPartOfMethodPrototype(inArgs)) {
                final char c = input.charAt(0);
                word.append(input.charAt(0));
                deleteOneCharacter();
                if ('(' == c) {
                    inArgs = true;
                } else if (')' == c) {
                    break;
                }
            }
            return new Lexeme(word.toString(), Lexeme.Type.WORD);
        }

        if (inputStartsWithRegex()) {
            final var regex = new StringBuilder();
            deleteOneCharacter();
            while (regexIsNotFinished()) {
                if (inputStartsWithEscapedRegexDelimiter()) {
                    deleteOneCharacter();
                }
                regex.append(input.charAt(0));
                deleteOneCharacter();
            }
            if (input.length() > 0) {
                deleteOneCharacter();
            }
            return new Lexeme(regex.toString(), Lexeme.Type.REGEX);
        }
        var symbol = input.substring(0, 1);
        deleteOneCharacter();
        return new Lexeme(symbol, Lexeme.Type.SYMBOL);
    }

    private void deleteOneCharacter() {
        input.delete(0, 1);
    }

    private boolean inputStartsWithEscapedRegexDelimiter() {
        return input.charAt(0) == '\\' && input.length() > 1 && input.charAt(1) == '/';
    }

    private boolean regexIsNotFinished() {
        return input.length() > 0 && input.charAt(0) != '/';
    }

    private boolean inputStartsWithRegex() {
        return input.length() > 0 && input.charAt(0) == '/';
    }

    private boolean input1stCharIsStillPartOfMethodPrototype(boolean inArgs) {
        return input.length() > 0 &&
            (Character.isJavaIdentifierPart(input.charAt(0))
                || '.' == input.charAt(0)
                || (',' == input.charAt(0) && inArgs)
                || ('(' == input.charAt(0) && !inArgs)
                || (')' == input.charAt(0) && inArgs));
    }

    private boolean inputStartsWithAnIdentifier() {
        return Character.isJavaIdentifierStart(input.charAt(0));
    }

    private void deleteSpaceFromTheStartOfInput() {
        while (inputStartsWithSpace()) {
            deleteOneCharacter();
        }
    }

    private boolean inputStartsWithSpace() {
        return input.length() > 0 && Character.isWhitespace(input.charAt(0));
    }
}
