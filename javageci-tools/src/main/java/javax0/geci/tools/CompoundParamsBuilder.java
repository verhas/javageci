package javax0.geci.tools;

import javax0.geci.api.GeciException;
import javax0.geci.javacomparator.lex.Lexer;
import javax0.geci.javacomparator.lex.LexicalElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax0.geci.javacomparator.LexicalElement.Type.CHARACTER;
import static javax0.geci.javacomparator.LexicalElement.Type.FLOAT;
import static javax0.geci.javacomparator.LexicalElement.Type.IDENTIFIER;
import static javax0.geci.javacomparator.LexicalElement.Type.INTEGER;
import static javax0.geci.javacomparator.LexicalElement.Type.STRING;
import static javax0.geci.javacomparator.LexicalElement.Type.SYMBOL;

/**
 * Create a new CompoundParams object out of a string. The string should
 * start with the {@code id} and it is optionally followed by {@code
 * key=value} pairs, where {@code key} is an identifier and {@code
 * value} is a single or double-quoted string.
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
     * @return this
     */
    public CompoundParamsBuilder redefineId() {
        redefine = true;
        return this;
    }

    public CompoundParamsBuilder exclude(String... keys) {
        excludedKeys.addAll(Arrays.asList(keys));
        return this;
    }

    public CompoundParams build() {
        final Map<String, List<String>> params = new HashMap<>();
        final var lexer = new Lexer();
        final LexicalElement[] elements;
        try {
            elements = lexer.apply(Collections.singletonList(line));
        } catch (IllegalArgumentException iae) {
            throw new GeciException("Cannot parse the line for parameters: " + line, iae);
        }
        if (elements.length < 1) {
            throwMalformed(line);
        }
        String name = "";
        final int startAt;
        if (elements[0].type == IDENTIFIER && elements.length < 2 || elements.length > 1 && elements[1].type != SYMBOL) {
            name = elements[0].lexeme;
            startAt = 1;
        } else {
            startAt = 0;
        }
        for (int i = startAt; i < elements.length; i++) {
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
                if (!params.containsKey(key)) {
                    params.put(key, new ArrayList<>());
                }
                params.get(key).add(value);
            }
        }
        return new CompoundParams(name, params);
    }

    private void throwMalformed(String s) {
        throw new GeciException("snippet pattern is malformed '" + s + "'");
    }

}
