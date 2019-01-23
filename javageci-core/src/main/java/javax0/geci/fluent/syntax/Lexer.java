package javax0.geci.fluent.syntax;

public class Lexer {
    private final StringBuilder input;

    public Lexer(String input) {
        this.input = new StringBuilder(input);
    }

    private static final Lexeme EOF = new Lexeme("", Lexeme.Type.EOF);

    private Lexeme lookAhead = null;

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
        if (input.length() == 0) {
            return EOF;
        }
        if (Character.isJavaIdentifierStart(input.charAt(0))) {
            final var word = new StringBuilder();
            while (input.length() > 0 &&
                    (Character.isJavaIdentifierPart(input.charAt(0))
                            || '.' == input.charAt(0)
                            || '/' == input.charAt(0)
                            || ',' == input.charAt(0)
                            || '(' == input.charAt(0)
                            || ')' == input.charAt(0))
            ) {
                word.append(input.charAt(0));
                input.delete(0, 1);
            }
            return new Lexeme(word.toString(), Lexeme.Type.WORD);
        }
        if (Character.isWhitespace(input.charAt(0))) {
            while (input.length() > 0 && Character.isWhitespace(input.charAt(0))) {
                input.delete(0, 1);
            }
            return new Lexeme(" ", Lexeme.Type.SPACE);
        }
        var symbol = input.substring(0, 1);
        input.delete(0, 1);
        return new Lexeme(symbol, Lexeme.Type.SYMBOL);
    }
}
