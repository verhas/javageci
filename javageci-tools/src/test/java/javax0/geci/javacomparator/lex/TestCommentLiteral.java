package javax0.geci.javacomparator.lex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCommentLiteral {

    private static StringBuilder comment(String s) {
        return new StringBuilder(s);
    }

    @Test
    void testGoodComments() {
        final var sut = new CommentLiteral();
        StringBuilder sb;
        sb = comment("/* vouw */");
        Assertions.assertEquals(LexicalElement.IGNORED,sut.apply(sb));
        sb = comment("/* vo\nuw */");
        Assertions.assertEquals(LexicalElement.IGNORED,sut.apply(sb));
        Assertions.assertEquals("", sb.toString());
        sb = comment("/** vouw */");
        Assertions.assertEquals(LexicalElement.IGNORED,sut.apply(sb));
        Assertions.assertEquals("", sb.toString());
        sb = comment("// vouw */");
        Assertions.assertEquals(LexicalElement.IGNORED,sut.apply(sb));
        Assertions.assertEquals("", sb.toString());
        sb = comment("// vouw\n");
        Assertions.assertEquals(LexicalElement.IGNORED,sut.apply(sb));
        Assertions.assertEquals("\n", sb.toString());
    }
}
