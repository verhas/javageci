package javax0.geci.javacomparator.lex;

import javax0.geci.api.GeciException;
import javax0.geci.javacomparator.LexicalElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestCommentLiteral {

    private static void testAndAssert(String comment, String ...rest){
        final var sut = new CommentLiteral();
        final var sb = new StringBuilder(comment);
        final var result = sut.apply(sb);
        Assertions.assertEquals(result.type, LexicalElement.Type.COMMENT);
        final var expected = comment.substring(0,comment.length()-sb.length());
        Assertions.assertEquals(expected,result.lexeme);
        if( rest.length == 1) {
            Assertions.assertEquals(rest[0], sb.toString());
        }else {
            Assertions.assertEquals("", sb.toString());
        }

    }

    @Test
    void testGoodComments() {

        testAndAssert("// vouw\n","\n");
        testAndAssert("/* vouw */");
        testAndAssert("/* vo\nuw */");
        testAndAssert("/** vouw */");
        testAndAssert("// vouw */");
    }

    @Test
    void testBadComment() {
        assertThrows(GeciException.class, () -> testAndAssert("/* notclosed"));
    }
}
