package javax0.geci.tools.reflection;

import javax0.geci.tools.syntax.Lexeme;
import javax0.geci.tools.syntax.Lexer;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/**
 * <ul>
 * <li>EXPRESSION ::= EXPRESSION1 ['|' EXPRESSION1 ]+ </li>
 * <li>EXPRESSION1 ::= EXPRESSION2 ['&mp;' EXPRESSION2] +</li>
 * <li>EXPRESSION2 :== TERMINAL | '!' EXPRESSION2 | '(' EXPRESSION ')' </li>
 * <li>TERMINAL ::= MODIFIER | PSEUDO_MODIFIER | name '~' REGEX | signature '~' REGEX |
 * annotation ~ REGEX | returns ~ REGEX | CALLER_DEFINED_SELECTOR</li>
 * <li>MODIFIER ::= private | protected | package | public | final | transient | volatile | static |
 * synthetic | synchronized | native | abstract | strict </li>
 * <li>PSEUDO_MODIFIER ::= default | implements | inherited | overrides | vararg | true | false </li>
 * </ul>
 */
class SelectorCompiler {

    private final Map<String, Function<Member, Boolean>> selectors;

    private Lexer lexer;

    SelectorCompiler(Map<String, Function<Member, Boolean>> selectors) {
        this.selectors = selectors;
    }

    SelectorNode compile(String expression) {
        lexer = new Lexer(expression, true);
        final var topNode = expression();
        if (lexer.rest().length() > 0) {
            throw new IllegalArgumentException("There are extra characters at the end of the selector expresison" + atRest());
        }
        return topNode;
    }

    private String atRest() {
        var rest = lexer.rest();
        if (rest.length() > 8) {
            rest = rest.substring(0, 8) + "...";
        }
        return " at '" + rest + "'";
    }

    private boolean isSymbol(String s) {
        return lexer.peek().type == Lexeme.Type.SYMBOL && lexer.peek().string.equals(s);
    }

    private boolean isWord(String ... s) {
        return lexer.peek().type == Lexeme.Type.WORD && Arrays.stream(s).anyMatch( k -> lexer.peek().string.equals(k));
    }

    private SelectorNode expression() {
        final var topNode = expression1();
        if (isSymbol("|")) {
            final var orNode = new SelectorNode.Or();
            orNode.subNodes.add(topNode);
            while (isSymbol("|")) {
                lexer.get();
                orNode.subNodes.add(expression1());
            }
            return orNode;
        } else {
            return topNode;
        }
    }

    private SelectorNode expression1() {
        final var topNode = expression2();
        if (isSymbol("&")) {
            final var andNode = new SelectorNode.And();
            andNode.subNodes.add(topNode);
            while (isSymbol("&")) {
                lexer.get();
                andNode.subNodes.add(expression2());
            }
            return andNode;
        } else {
            return topNode;
        }
    }


    private SelectorNode expression2() {
        if (isSymbol("!")) {
            lexer.get();
            return new SelectorNode.Not(expression2());
        }
        if (isSymbol("(")) {
            lexer.get();
            final var sub = expression();
            if (!isSymbol(")")) {
                throw new IllegalArgumentException("Closing ')' is missing" + atRest());
            }
            lexer.get();
            return sub;
        }
        if (isWord("name","signature","annotation","returns")) {
            final var name = lexer.get().string;
            if (!isSymbol("~")) {
                throw new IllegalArgumentException("'name' or 'signature' has to be followed by ~" + atRest());
            }
            lexer.get();
            if (lexer.peek().type != Lexeme.Type.REGEX) {
                throw new IllegalArgumentException("Regex is missing after 'name ~' or 'signature ~'" + atRest());
            }
            final var regex = lexer.get();
            switch (name) {
                case "name":
                    return new SelectorNode.Name(regex.string);
                case "signature":
                    return new SelectorNode.Signature(regex.string);
                case "annotation":
                    return new SelectorNode.Annotation(regex.string);
                case "returns":
                    return new SelectorNode.Returns(regex.string);
                default:
                    throw new IllegalArgumentException("Internal error");
            }
        }
        if (lexer.peek().type == Lexeme.Type.WORD) {
            final var name = lexer.peek().string;
            if (!selectors.containsKey(name)) {
                throw new IllegalArgumentException("The selector '" + name + "' is not known" + atRest());
            }
            lexer.get();
            return new SelectorNode.Terminal(selectors.get(name));
        }

        throw new IllegalArgumentException("Invalid syntax" + atRest());
    }
}
