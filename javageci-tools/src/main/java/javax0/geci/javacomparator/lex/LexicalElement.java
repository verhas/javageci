package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;

import java.util.Objects;

public class LexicalElement implements javax0.geci.javacomparator.LexicalElement {

    LexicalElement(String lexeme, Type type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(final String lexeme) {
        this.lexeme = lexeme;
    }

    public void setOriginal(final String original) {
        if( type != Type.CHARACTER && type != Type.STRING ){
            throw new GeciException("Setting the original is possible only in case the lexeme is character or string");
        }
        this.original = original;
        this.lexeme = Escape.escape(original);

    }

    public String getOriginalLexeme() {
        if (type == javax0.geci.javacomparator.LexicalElement.Type.STRING) {
            final String enclosing = ((StringLiteral) this).enclosing;
            return enclosing + this.original + enclosing;
        }
        if (type == javax0.geci.javacomparator.LexicalElement.Type.CHARACTER) {
            return "'" + this.original + "'";
        }
        return this.original;
    }

    public String getFullLexeme() {
        if (type == javax0.geci.javacomparator.LexicalElement.Type.STRING) {
            final String enclosing = ((StringLiteral) this).enclosing;
            return enclosing + this.lexeme + enclosing;
        }
        if (type == javax0.geci.javacomparator.LexicalElement.Type.CHARACTER) {
            return "'" + this.lexeme + "'";
        }
        return this.lexeme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexicalElement that = (LexicalElement) o;
        return lexeme.equals(that.lexeme);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexeme);
    }

    @Override
    public String toString() {
        return type.toString() + "[" + lexeme + "]";
    }

    public String lexeme;
    public String original;
    public final Type type;

    public static class IntegerLiteral extends LexicalElement {
        public final long value;

        public IntegerLiteral(String lexeme) {
            super(lexeme, Type.INTEGER);
            final int radix;
            if (lexeme.startsWith("0x") || lexeme.startsWith("0X")) {
                radix = 16;
                lexeme = lexeme.substring(2);
            } else {
                radix = 10;
            }
            if (lexeme.toUpperCase().endsWith("L")) {
                lexeme = lexeme.substring(0, lexeme.length() - 1);
            }
            value = Long.parseLong(lexeme, radix);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntegerLiteral literal = (IntegerLiteral) o;
            return value == literal.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class FloatLiteral extends LexicalElement {
        public final double value;

        public FloatLiteral(String lexeme) {
            super(lexeme, Type.FLOAT);
            value = Double.parseDouble(lexeme);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FloatLiteral literal = (FloatLiteral) o;
            return Double.compare(literal.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class Symbol extends LexicalElement {
        public Symbol(String lexeme) {
            super(lexeme, Type.SYMBOL);
        }
    }

    public static class StringLiteral extends LexicalElement {
        public final String enclosing;

        StringLiteral(String lexeme, String original, String enclosing) {
            super(lexeme, Type.STRING);
            this.original = original;
            this.enclosing = enclosing;
        }
    }

    public static class CharacterLiteral extends LexicalElement {
        public CharacterLiteral(String lexeme, String original) {
            super(lexeme, Type.CHARACTER);
            this.original = original;
        }
    }

    public static class Identifier extends LexicalElement {
        public Identifier(String lexeme) {
            super(lexeme, Type.IDENTIFIER);
        }
    }

    public static class Spacing extends LexicalElement {
        public Spacing(String lexeme) {
            super(lexeme, Type.SPACING);
        }
    }

    public static class Comment extends LexicalElement {
        public Comment(String lexeme) {
            super(lexeme, Type.COMMENT);
        }
    }
}
