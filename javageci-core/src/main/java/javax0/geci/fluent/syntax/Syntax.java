package javax0.geci.fluent.syntax;

import javax0.geci.api.GeciException;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.tools.syntax.Lexer;

import java.util.ArrayList;
import java.util.List;

import static javax0.geci.tools.syntax.Lexeme.Type.*;

/**
 * This class implements the syntax analyzer that can process fluent api grammar definition.
 * <p>
 * The definition of the syntax of an expression is the following in lazy BNF:
 * <pre>
 *     expression ::= alternate ... alternate
 *     alternate ::= simple '|' ... '|' simple
 *     simple ::= terminal | terminal '*' | terminal '+' | terminal '?'
 *     terminal := method | '(' expression ')
 * </pre>
 *
 * An {@code expression} is one or more '{@code alternate}'s separated by spaces.
 * An {@code alternate} is one or more {@code simple} separated by the character {@code |}. These are alternatives,
 * and alternatives have higher precendence so {@code a b|c} means either {@code a().b()} or {@code a().c()} in the
 * final fluent API and NOT {@code a().b()} or {@code c()}.
 * A {@code simple} is a {@code terminal} followed by one of the modifier characters {@code *+?} denoting zero or more,
 * one or more and optional occurence of the {@code terminal}. A {@code terminal} can be a method name or signature
 * or an expression enclosed in parentheses.
 *
 */
public class Syntax {
    private final Lexer lexer;
    private final FluentBuilder topBuilder;

    public Syntax(Lexer lexer, FluentBuilder builder) {
        this.lexer = lexer;
        this.topBuilder = builder;
    }
    /*
    expression ::= one ( SPACE one )* ;
    one ::= WORD |
            group |
            WORD '*' |
            group '*' |
            WORD '+' |
            group '+' |
            WORD '?' |
            group '?' |
            ;
     */

    private FluentBuilder one() {
        var lexeme = lexer.peek();
        if (lexeme.type == WORD) {
            var method = lexer.get();
            var next = lexer.peek();
            if (next.type == SYMBOL) {
                switch (next.string) {
                    case "*":
                        lexer.get();
                        return topBuilder.zeroOrMore(method.string);
                    case "?":
                        lexer.get();
                        return topBuilder.optional(method.string);
                    case "+":
                        lexer.get();
                        return topBuilder.oneOrMore(method.string);
                    default:
                        return topBuilder.one(method.string);
                }
            } else {
                return topBuilder.one(method.string);
            }
        }
        if (lexeme.string.equals("(")) {
            var localBuilder = group();
            if (lexer.peek().type == SYMBOL) {
                switch (lexer.peek().string) {
                    case "*":
                        lexer.get();
                        return topBuilder.zeroOrMore(localBuilder);
                    case "?":
                        lexer.get();
                        return topBuilder.optional(localBuilder);
                    case "+":
                        lexer.get();
                        return topBuilder.oneOrMore(localBuilder);
                    default:
                        return localBuilder;
                }
            }
            return localBuilder;
        }
        return null;
    }

    private FluentBuilder group() {
        lexer.get(); // step over the '('
        var localBuilder = one();
        var next = lexer.peek();
        if (next.string.equals(")")) {
            lexer.get();
            return localBuilder;
        }
        if (next.type == SPACE) {
            while (next.type == SPACE) {
                topBuilder.one(localBuilder);
                lexer.get();// step over the SPACE
                localBuilder = one();
                next = lexer.peek();
            }
            if (next.string.equals(")")) {
                lexer.get();//step over the ')'
                return topBuilder.one(localBuilder);
            }
            throw new GeciException("Fluent expression syntax error after ( .... ) missing closing parenthesis at '"
                    + lexer.rest() + "'");
        }
        if (next.string.equals("|")) {
            List<FluentBuilder> arglist = new ArrayList<>();
            arglist.add(localBuilder);
            while (next.string.equals("|")) {
                lexer.get();
                arglist.add(one());
                next = lexer.peek();
            }
            if (next.string.equals(")")) {
                lexer.get();//step over the ')'
                return topBuilder.oneOf(arglist.toArray(new FluentBuilder[0]));
            }
            throw new GeciException("Fluent expression syntax error after ( .... ) missing closing parenthesis at '"
                    + lexer.rest() + "'");
        }
        throw new GeciException("Fluent expression syntax error at '" + lexer.rest() + "'");
    }

    public FluentBuilder expression() {
        var builder = topBuilder.one(one());
        while (lexer.peek().type == SPACE) {
            lexer.get();
            builder = builder.one(one());
        }
        if (lexer.peek().type != EOF) {
            throw new GeciException("Extra characters at the end: '" + lexer.rest() + "'");
        }
        return builder;
    }

}
