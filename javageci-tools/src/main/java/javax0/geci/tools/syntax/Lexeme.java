package javax0.geci.tools.syntax;

public class Lexeme {
    public Lexeme(String string, Type type) {
        this.string = string;
        this.type = type;
    }

    public enum Type{
        WORD, SYMBOL, SPACE, REGEX, EOF
    }

    public final String string;
    public final Type type;

}
