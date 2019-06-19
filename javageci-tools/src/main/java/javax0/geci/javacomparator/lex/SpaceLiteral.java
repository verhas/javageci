package javax0.geci.javacomparator.lex;

public class SpaceLiteral implements LexEater {
    @Override
    public LexicalElement consume(StringBuilder sb) {
        while( sb.length() > 0 && Character.isWhitespace(sb.charAt(0))){
            sb.deleteCharAt(0);
        }
        return null;
    }
}
