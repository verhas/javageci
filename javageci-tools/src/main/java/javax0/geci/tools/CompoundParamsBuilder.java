package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.javacomparator.lex.Lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax0.geci.javacomparator.lex.LexicalElement.Type.*;

/**
 * Create a new CompoundParams object out of a string. The string shouéd
 * start with the {@code id} and it is optionally followed by {@code
 * key=value} pairs, where {@code key} is an identifier and {@code
 * value} is a single or double quoted string.
 *
 * <p> Such lines appear in editor-folds or in the {@code value}
 * argument of a {@code Geci} annotation and also in other places where
 * generators may use such syntax. (E.g.: Snippets)
 *
 * <p> The analysis of the string is performed using the Java lexical
 * analyzer, therefore the strings or characters may contain escape
 * sequences.
 */
public class CompoundParamsBuilder {

    public CompoundParamsBuilder(final String line) {
        this.line = line;
    }

    private final String line;

    private final List<String> excludedKeys = new ArrayList<>();
    private boolean redefine = false;

    /**
     * @return
     */
    public CompoundParamsBuilder redefineId() {
        redefine = true;
        return this;
    }

    public CompoundParamsBuilder exclude(String... keys) {
        excludedKeys.addAll(List.of(keys));
        return this;
    }

    public CompoundParams build() {
        final Map<String, String> params = new HashMap<>();
        final var lexer = new Lexer();
        final var elements = lexer.apply(List.of(line));
        if (elements.length < 1 || elements[0].type != IDENTIFIER) {
            throwMalformed(line);
        }
        String name = elements[0].lexeme;
        for (int i = 1; i < elements.length; i++) {
            if (elements[i].type != IDENTIFIER) {
                throwMalformed(line);
            }
            final var key = elements[i].lexeme;
            i++;
            if (i >= elements.length || !elements[i].type.is(SYMBOL) || !elements[i].lexeme.equals("=")) {
                throwMalformed(line);
            }
            i++;
            if (i >= elements.length || !elements[i].type.is(STRING, CHARACTER, FLOAT, INTEGER, IDENTIFIER)) {
                throwMalformed(line);
            }
            final var value = elements[i].lexeme;
            if (redefine && "id".equals(key)) {
                name = value;
            } else if ("id".equals(key)) {
                throw new GeciException("id is not allowed as parameter name in '" + line + "'");
            } else if (!excludedKeys.contains(key)) {
                params.put(key, value);
            }
        }
        return new CompoundParams(name, params);
    }

    private void throwMalformed(String s) {
        throw new GeciException("snippet pattern is malformed '" + s + "'");
    }

}
