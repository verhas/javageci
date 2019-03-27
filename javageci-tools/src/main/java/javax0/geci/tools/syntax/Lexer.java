package javax0.geci.tools.syntax;

public class Lexer {
    private static final Lexeme EOF = new Lexeme("", Lexeme.Type.EOF);
    private final StringBuilder input;
    private final boolean skipSpace;
    private Lexeme lookAhead = null;

    public Lexer(final String input) {
        this(input, false);
    }

    public Lexer(final String input, boolean skipSpace) {
        final String preprocessed;

        if (skipSpace) {
            preprocessed = input.replaceAll("\\s+", " ");
        } else {
            preprocessed = input.replaceAll("\\s+", " ")
                    .replaceAll("\\s\\|\\s", "|")
                    .replaceAll("\\s,\\s", ",")
                    .replaceAll("\\(\\s", "(")
                    .replaceAll("\\s\\)", ")")
                    .replaceAll("\\s\\?", "?")
                    .replaceAll("\\s\\*", "*")
                    .replaceAll("\\s\\+", "+");
        }
        this.input = new StringBuilder(preprocessed);
        this.skipSpace = skipSpace;
    }

    @Override
    public String toString() {
        if (lookAhead == null) {
            return input.toString();
        } else {
            return lookAhead + input.toString();
        }
    }

    public String rest() {
        return lookAhead.string + input.toString();
    }

    public Lexeme get() {
        if (lookAhead != null) {
            var ret = lookAhead;
            lookAhead = next();
            return ret;
        } else {
            var ret = next();
            lookAhead = next();
            return ret;
        }
    }

    public Lexeme peek() {
        if (lookAhead == null) {
            lookAhead = next();
        }
        return lookAhead;
    }

    private Lexeme next() {

        if (input.length() > 0 && Character.isWhitespace(input.charAt(0))) {
            while (input.length() > 0 && Character.isWhitespace(input.charAt(0))) {
                input.delete(0, 1);
            }
            if (!skipSpace) {
                return new Lexeme(" ", Lexeme.Type.SPACE);
            }
        }

        if (input.length() == 0) {
            return EOF;
        }

        if (Character.isJavaIdentifierStart(input.charAt(0))) {
            final var word = new StringBuilder();
            boolean inArgs = false;
            while (input.length() > 0 &&
                    (Character.isJavaIdentifierPart(input.charAt(0))
                            || '.' == input.charAt(0)
                            || (',' == input.charAt(0) && inArgs)
                            || ('(' == input.charAt(0) && !inArgs)
                            || (')' == input.charAt(0) && inArgs))) {
                char c = input.charAt(0);
                word.append(input.charAt(0));
                input.delete(0, 1);
                if ('(' == c) {
                    inArgs = true;
                }
                if (')' == c) {
                    break;
                }
            }
            return new Lexeme(word.toString(), Lexeme.Type.WORD);
        }

        if (input.charAt(0) == '/') {
            final var regex = new StringBuilder();
            input.delete(0, 1);
            while (input.length() > 0 && input.charAt(0) != '/') {
                if (input.charAt(0) == '\\' && input.length() > 1 && input.charAt(1) == '/') {
                    input.delete(0, 1);
                }
                regex.append(input.charAt(0));
                input.delete(0, 1);
            }
            if (input.length() > 0) {
                input.delete(0, 1);
            }
            return new Lexeme(regex.toString(), Lexeme.Type.REGEX);
        }
        var symbol = input.substring(0, 1);
        input.delete(0, 1);
        return new Lexeme(symbol, Lexeme.Type.SYMBOL);
    }
}
