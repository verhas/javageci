package javax0.geci.javacomparator;

public interface LexicalElement {

    String getLexeme();
    String getFullLexeme();
    Type getType();

    enum Type {
        COMMENT, STRING, CHARACTER, IDENTIFIER, INTEGER, FLOAT, SYMBOL, SPACING;

        public boolean is(Type... types) {
            for (final var t : types) {
                if (t == this) return true;
            }
            return false;
        }
    }
}
