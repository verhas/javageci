package javax0.geci.tools.reflection;

import javax0.geci.tools.syntax.Lexeme;
import javax0.geci.tools.syntax.Lexer;

import java.util.Arrays;

/**
 * <ul>
 * <li>EXPRESSION ::= EXPRESSION1 ['|' EXPRESSION1 ]+ </li>
 * <li>EXPRESSION1 ::= EXPRESSION2 ['&amp;' EXPRESSION2] +</li>
 * <li>EXPRESSION2 :== TERMINAL | '!' EXPRESSION2 | '(' EXPRESSION ')' </li>
 * <li>TERMINAL ::= MODIFIER | PSEUDO_MODIFIER | name '~' REGEX | signature '~' REGEX |
 * annotation ~ REGEX | returns ~ REGEX | CALLER_DEFINED_SELECTOR</li>
 * <li>MODIFIER ::= private | protected | package | public | final | transient | volatile | static |
 * synthetic | synchronized | native | abstract | strict </li>
 * <li>PSEUDO_MODIFIER ::= default | implements | inherited | overrides | vararg | true | false </li>
 * </ul>
 */
class SelectorCompiler {

    private Lexer lexer;

    static SelectorNode compile(String expression) {
        final var it = new SelectorCompiler();
        it.lexer = new Lexer(expression, true);
        final var topNode = it.expression();
        if (it.lexer.rest().length() > 0) {
            throw new IllegalArgumentException("There are extra characters " +
                "at the end of the selector expresison" + it.atRest());
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

    private boolean isWord(String... s) {
        return lexer.peek().type == Lexeme.Type.WORD && Arrays.stream(s).anyMatch(k -> lexer.peek().string.equals(k));
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
            if (isSymbol(")")) {
                lexer.get();
                return sub;
            } else {
                throw new IllegalArgumentException("Closing ')' is missing" + atRest());
            }
        }
        if (lexer.peek().type == Lexeme.Type.WORD) {
            final var name = lexer.get().string;
            if (isSymbol("~")) {
                lexer.get();
                if (lexer.peek().type != Lexeme.Type.REGEX) {
                    throw new IllegalArgumentException("Regex is missing after '~'" + atRest());
                }
                final var regex = lexer.get();
                return new SelectorNode.Regex(regex.string, name);
            } else {
                return new SelectorNode.Terminal(name);
            }
        }
        throw new IllegalArgumentException("Invalid syntax" + atRest());
    }
}
