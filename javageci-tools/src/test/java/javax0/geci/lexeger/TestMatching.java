package javax0.geci.lexeger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestMatching {

    @Test
    void testSimpleListMatching(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var factory = LexMatcher.factory(javaLexed);
        final var matcher = factory.match("private final int");
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
    }

    @Test
    void testSimpleListFinding(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var factory = LexMatcher.factory(javaLexed);
        final var matcher = factory.match("public var h").spaceSensitive();
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(18,result.end);
    }
}
