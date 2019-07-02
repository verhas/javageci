package javax0.geci.docugen;

import javax0.geci.api.GeciException;
import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;
import javax0.geci.tools.CompoundParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SnippetBuilder extends CompoundParams {
    private Map<String, String> params = new HashMap<>();
    private String name;
    private List<String> lines = new ArrayList<>();

    public String snippetName(){
        return name;
    }

    public Snippet build(){
        return new Snippet(params, lines);
    }

    SnippetBuilder startLine(String s) {
        final var lexer = new Lexer();
        final var elements = lexer.apply(List.of(s));
        if (elements.length < 1 || elements[0].type != LexicalElement.Type.IDENTIFIER ){
            throwMalformed(s);
        }
        name = elements[0].lexeme;
        for( int i = 1 ; i < elements.length ; i ++ ){
            if( elements[i].type != LexicalElement.Type.IDENTIFIER ){
                throwMalformed(s);
            }
            final var key = elements[i].lexeme;
            i++;
            if( i >= elements.length || elements[i].type != LexicalElement.Type.SYMBOL || !elements[i].lexeme.equals("=")){
                throwMalformed(s);
            }
            i++;
            if( i >= elements.length || (elements[i].type != LexicalElement.Type.STRING && elements[i].type != LexicalElement.Type.CHARACTER)){
                throwMalformed(s);
            }
            final var value = elements[i].lexeme;
            params.put(key,value);
        }
        return this;
    }

    SnippetBuilder add(String line){
        lines.add(line);
        return this;
    }

    private void throwMalformed(String s) {
        throw new GeciException("snippet pattern is malformed '" + s + "'");
    }

}
