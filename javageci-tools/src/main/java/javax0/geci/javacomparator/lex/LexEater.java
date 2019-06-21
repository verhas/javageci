package javax0.geci.javacomparator.lex;

import java.util.function.Function;

/**
 * A lex eater recognizes when the string in the input {@code
 * StringBuilder} starts with the lexical element that the specific lex
 * eater is responsible for and it removes the lexical element from the
 * start of the input. It also returns a new lexical element
 * object that was created from the string of the lexical element.
 *
 * <p> If the lex eater could not find any lexical element that it could
 * remove from the input then it returns {@code null}
 *
 * <p>Some lewx eaters, namely the {@link CommentLiteral} and {@link
 * SpaceLiteral} return a special instance, {@code
 * LexicalElement.IGNORED}, which is not stored in the list of lexical
 * elements that are later used for comparision. That way the comparison
 * of the lexical elements will ignore difference in formatting and
 * comment content.
 */
public interface LexEater extends Function<StringBuilder, LexicalElement> {
}
