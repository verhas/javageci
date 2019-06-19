package javax0.geci.javacomparator.lex;

public class SymbolLiteral implements LexEater {

    private static final String[] symbols = {
            ">>>=",
            "...",
            ">>>",
            "<<=",
            ">>=",
            "->",
            "==",
            ">=",
            "<=",
            "!=",
            "&&",
            "||",
            "++",
            "--",
            "<<",
            ">>",
            "::",
            "+=",
            "-=",
            "*=",
            "/=",
            "&=",
            "|=",
            "^=",
            "%=",
            "+",
            "-",
            "*",
            "=",
            ">",
            "<",
            "!",
            "~",
            "?",
            ":",
            "/",
            "&",
            "|",
            "^",
            "%",
            "(",
            ")",
            "[",
            "]",
            "{",
            "}",
            "@",
            ";",
            ",",
            ".",
    };

    @Override
    public LexicalElement consume(StringBuilder sb) {
        for( final var s : symbols ){
            if( sb.length() >= s.length() && sb.subSequence(0,s.length()).equals(s)){
                sb.delete(0,s.length());
                return new LexicalElement.Symbol(s);
            }
        }
        return null;
    }
}
