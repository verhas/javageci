package javax0.geci.fluent.syntax;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;

import java.util.ArrayList;
import java.util.List;

import static javax0.geci.fluent.syntax.Lexeme.Type.*;

public class Syntax {
    private final Lexer lexer;
    private final FluentBuilder builder;

    public Syntax(Lexer lexer, FluentBuilder builder) {
        this.lexer = lexer;
        this.builder = builder;
    }
    /*
    expression ::= one ( SPACE one )* ;
    one ::= WORD |
            '(' one ( '|' one)* ')' |
            '(' one ( SPACE one)* ')' |
            one '*' |
            one '+' |
            one '?'
            ;
     */

    public FluentBuilder one() {
        var lexeme = lexer.peek();
        if (lexeme.type == WORD) {
            var method = lexer.get();
            final String methodSignature;
            final String name;
                var i = method.string.indexOf('/');
            if( i == -1){
                methodSignature = method.string;
                name = null;
            }else{
                methodSignature = method.string.substring(0,i);
                name = method.string.substring(i+1);
            }
            var next = lexer.peek();
            if (next.type == SYMBOL) {
                switch (next.string) {
                    case "*":
                        lexer.get();
                        return builder.zeroOrMore(methodSignature).name(name);
                    case "?":
                        lexer.get();
                        return builder.optional(methodSignature).name(name);
                    case "+":
                        lexer.get();
                        return builder.oneOrMore(methodSignature).name(name);
                    default:
                        return builder.one(methodSignature).name(name);
                }
            } else {
                return builder.one(method.string).name(name);
            }
        }
        if (lexeme.string.equals("(")) {
            lexer.get();
            var lastBuilder = builder.one(one());
            var next = lexer.peek();
            if (next.string.equals(")")) {
                lexer.get();
                return lastBuilder;
            }
            if (next.type == SPACE) {
                while (lexeme.type == SPACE) {
                    lexer.get();
                    lastBuilder = builder.one(one());
                    lexeme = lexer.peek();
                }
                if (lexeme.string.equals(")")) {
                    lexer.get();
                    return lastBuilder;
                }
                throw new GeciException("Fluent expression syntax error after ( .... ) missing closing parenthesis.");
            }
            if (next.string.equals("|")) {
                List<FluentBuilder> arglist = new ArrayList<>();
                arglist.add(lastBuilder);
                while (next.string.equals("|")) {
                    lexer.get();
                    arglist.add(builder.one(one()));
                    next = lexer.peek();
                }
                if (next.string.equals(")")) {
                    lexer.get();
                    return builder.oneOf(arglist.toArray(new FluentBuilder[arglist.size()]));
                }
                throw new GeciException("Fluent expression syntax error after ( .... ) missing closing parenthesis.");
            }
        }
        return null;
    }

    public FluentBuilder expression() {
        var lastBuilder = builder.one(one());
        for (; ; ) {
            if (lexer.peek().type != SPACE) {
                break;
            }
            lexer.get();
            lastBuilder = builder.one(one());
        }
        return lastBuilder;
    }

}
