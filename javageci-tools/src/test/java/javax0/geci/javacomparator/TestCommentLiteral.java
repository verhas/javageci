package javax0.geci.javacomparator;

import javax0.geci.javacomparator.lex.CommentLiteral;
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
        Assertions.assertNull(sut.consume(sb));
        sb = comment("/* vo\nuw */");
        Assertions.assertNull(sut.consume(sb));
        Assertions.assertEquals("", sb.toString());
        sb = comment("/** vouw */");
        Assertions.assertNull(sut.consume(sb));
        Assertions.assertEquals("", sb.toString());
        sb = comment("// vouw */");
        Assertions.assertNull(sut.consume(sb));
        Assertions.assertEquals("", sb.toString());
        sb = comment("// vouw\n");
        Assertions.assertNull(sut.consume(sb));
        Assertions.assertEquals("\n", sb.toString());
    }
}
