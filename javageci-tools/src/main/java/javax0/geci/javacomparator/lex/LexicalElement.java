package javax0.geci.javacomparator.lex;

public class LexicalElement {
    public LexicalElement(String lexeme, Type type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    public enum Type {
        COMMENT, STRING, CHARACTER
    };
    public final String lexeme;
    public final Type type;

    public static class Comment extends LexicalElement {


        public Comment(String lexeme, Type type) {
            super(lexeme, type);
        }
    }

}
