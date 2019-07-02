package javax0.geci.javacomparator.lex;

import java.util.Objects;

public class LexicalElement {
    public static final LexicalElement IGNORED = new LexicalElement("",Type.COMMENT);

    LexicalElement(String lexeme, Type type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexicalElement that = (LexicalElement) o;
        return lexeme.equals(that.lexeme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexeme);
    }

    public enum Type {
        COMMENT, STRING, CHARACTER, IDENTIFIER, INTEGER, FLOAT, SYMBOL
    }

    ;
    public final String lexeme;
    public final Type type;

    public static class INteger extends LexicalElement {
        public final int value;

        INteger(String lexeme) {
            super(lexeme, Type.INTEGER);
            if (lexeme.startsWith("0x") || lexeme.startsWith("0X")) {
                value = Integer.parseInt(lexeme.substring(2), 16);
            } else {
                value = Integer.parseInt(lexeme);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            INteger iNteger = (INteger) o;
            return value == iNteger.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class FLoat extends LexicalElement {
        public final double value;

        FLoat(String lexeme) {
            super(lexeme, Type.FLOAT);
            value = Double.parseDouble(lexeme);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FLoat fLoat = (FLoat) o;
            return Double.compare(fLoat.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class Symbol extends LexicalElement {
        Symbol(String lexeme) {
            super(lexeme, Type.SYMBOL);
        }
    }

    public static class STring extends LexicalElement {
        STring(String lexeme) {
            super(lexeme, Type.STRING);
        }
    }

    public static class CHaracter extends LexicalElement {
        CHaracter(String lexeme) {
            super(lexeme, Type.CHARACTER);
        }
    }

    public static class Identifier extends LexicalElement {
        Identifier(String lexeme) {
            super(lexeme, Type.IDENTIFIER);
        }
    }
}
