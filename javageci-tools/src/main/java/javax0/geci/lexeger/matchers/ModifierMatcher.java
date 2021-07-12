package javax0.geci.lexeger.matchers;

import javax0.geci.javacomparator.LexicalElement;
import javax0.geci.lexeger.JavaLexed;
import javax0.geci.lexeger.MatchResult;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ModifierMatcher extends LexMatcher {

    private static final Map<String, Integer> modifierMap = new HashMap<>();
    private final int modifiers;

    static {
        modifierMap.putAll(Map.of(
            "public", Modifier.PUBLIC,
            "private", Modifier.PRIVATE,
            "protected", Modifier.PROTECTED,
            "static", Modifier.STATIC,
            "final", Modifier.FINAL,
            "synchronized", Modifier.SYNCHRONIZED));
        modifierMap.putAll(Map.of(
            "volatile", Modifier.VOLATILE,
            "transient", Modifier.TRANSIENT,
            "native", Modifier.NATIVE,
            "interface", Modifier.INTERFACE,
            "abstract", Modifier.ABSTRACT,
            "strict", Modifier.STRICT));
    }


    public ModifierMatcher(Lexpression factory, JavaLexed javaLexed) {
        super(factory, javaLexed);
        this.modifiers = 0xFFFFFFFF;
    }

    public ModifierMatcher(Lexpression factory, JavaLexed javaLexed, int modifiers) {
        super(factory, javaLexed);
        this.modifiers = modifiers;
    }

    @Override
    public MatchResult matchesAt(int i) {
        final var lex = javaLexed.get(i);
        if (lex.getType() == LexicalElement.Type.IDENTIFIER &&
            modifierMap.containsKey(lex.getLexeme()) &&
            (modifierMap.get(lex.getLexeme()) & modifiers) > 0) {
            return new MatchResult(true, i, i + 1);
        } else {
            return MatchResult.NO_MATCH;
        }
    }
}
