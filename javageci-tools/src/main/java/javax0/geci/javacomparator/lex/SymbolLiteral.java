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
    };

    @Override
    public LexicalElement apply(StringBuilder sb) {
        for( final var s : symbols ){
            if( sb.length() >= s.length() && sb.subSequence(0,s.length()).equals(s)){
                sb.delete(0,s.length());
                return new LexicalElement.Symbol(s);
            }
        }
        if( sb.length() > 0 ) {
            var s = sb.substring(0, 1);
            sb.delete(0, 1);
            return new LexicalElement.Symbol(s);
        }else{
            return null;
        }
    }
}
