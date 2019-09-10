package javax0.geci.javacomparator.lex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCommentLiteral {

    private static StringBuilder comment(String s) {
        return new StringBuilder(s);
    }

    private static void testAndAssert(String comment, String expected, String ...rest){
        final var sut = new CommentLiteral();
        final var sb = comment(comment);
        final var result = sut.apply(sb);
        Assertions.assertEquals(result.type, LexicalElement.Type.COMMENT);
        Assertions.assertEquals(expected,result.lexeme);
        if( rest.length == 1) {
            Assertions.assertEquals(rest[0], sb.toString());
        }else {
            Assertions.assertEquals("", sb.toString());
        }

    }

    @Test
    void testGoodComments() {

        testAndAssert("// vouw\n"," vouw","\n");
        testAndAssert("/* vouw */"," vouw ");
        testAndAssert("/* vo\nuw */"," vo\nuw ");
        testAndAssert("/** vouw */","* vouw ");
        testAndAssert("// vouw */"," vouw */");
    }
}
