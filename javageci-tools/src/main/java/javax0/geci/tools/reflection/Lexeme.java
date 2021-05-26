package javax0.geci.tools.reflection;

public class Lexeme {
    public final String string;
    public final Type type;

    public Lexeme(String string, Type type) {
        this.string = string;
        this.type = type;
    }

    @Override
    public String toString() {
        return string;
    }
    public enum Type {
        WORD, SYMBOL, SPACE, REGEX, EOF
    }

}
