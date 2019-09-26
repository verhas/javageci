package javax0.geci.lexeger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class TestMatching {

    @Test
    void testSimpleListMatching(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.match("private final int");
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
    }

    @Test
    void testSimpleListFindingWithSpaces(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed, LexExpression.SPACE_SENSITIVE);
        final var matcher = expr.match("public var h");
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(18,result.end);
    }

    @Test
    void testSimpleListFindingWithComments(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed, LexExpression.COMMENT_SENSITIVE);
        final var matcher = expr.list(expr.match("public var "),expr.comment(),expr.match("h"));
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(20,result.end);
    }

    @Test
    void testSimpleListFindingWithPatternedComments(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed, LexExpression.COMMENT_SENSITIVE);
        final var matcher = expr.list(expr.match("public var "),expr.comment(
            Pattern.compile("//c.*t")
        ),expr.match("h"));
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(20,result.end);
    }

    @Test
    void testSimpleListFindingWithNamedPatternedComments(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed, LexExpression.COMMENT_SENSITIVE);
        final var matcher = expr.list(expr.match("public var "),expr.comment(
            "comment",Pattern.compile("//(c.*t)")
        ),expr.match("h"));
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(20,result.end);
        Assertions.assertEquals(1,expr.get("comment").size());
        Assertions.assertEquals("comment",expr.get("comment").get(0).group(1));
    }

    @Test
    void testSimpleListFindingWithTextComments(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed, LexExpression.COMMENT_SENSITIVE);
        final var matcher = expr.list(expr.match("public var "),expr.comment(
            "//comment"
        ),expr.match("h"));
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(20,result.end);
    }

    @Test
    void testSimpleListFinding(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.match("public var h");
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(18,result.end);
    }

    @Test
    void testSimpleGroupCollection(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.list(expr.group("protection",expr.oneOf("public","private")),expr.match("var h"));
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(18,result.end);
        Assertions.assertEquals(1,expr.group("protection").size());
        Assertions.assertEquals(1,expr.group("protection").get(0).size());
        Assertions.assertEquals("public",expr.group("protection").get(0).get(0).lexeme);
    }

    @Test
    void testSimpleUnmatchedGroup(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.list(expr.group("protection",expr.oneOf(expr.match("public"),expr.group("private",expr.match("private")))),expr.match("var h"));
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(18,result.end);
        Assertions.assertEquals(0,expr.group("private").size());
        Assertions.assertEquals(1,expr.group("protection").size());
    }

    @Test
    void testSimpleSetFinding(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.unordered("h public var");
        final var result = matcher.find();
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(13,result.start);
        Assertions.assertEquals(18,result.end);
    }
    @Test
    void testSimpleSetNotFinding(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.unordered("k public var");
        final var result = matcher.find();
        Assertions.assertFalse(result.matches);
    }

    @Test
    void testIdentifiertMatching(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var expr = LexMatcher.of(javaLexed);
        final var matcher = expr.list(expr.keyword("private"),expr.identifier("final"),expr.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
    }

    @Test
    void testOrMoretMatching(){
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.oneOrMore(e.keyword("private")),e.zeroOrMore(e.identifier("final"))
            ,e.zeroOrMore(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
    }

    @Test
    void testOrMoretMatchingMany(){
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.oneOrMore(e.keyword("private")),e.optional(e.identifier("final"))
            ,e.optional(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(9,result.end);
    }

    @Test
    void testrepeatMinNumber(){
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.repeat(e.keyword("private"),3),e.optional(e.identifier("final"))
            ,e.optional(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(9,result.end);
    }
    @Test
    void testRepeatMinMaxNumber(){
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.repeat(e.keyword("private"),2,3),e.optional(e.identifier("final"))
            ,e.optional(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(9,result.end);
    }

    @Test
    void testAnyIdentifier(){
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.keyword("private"),e.optional(e.identifier())
            ,e.optional(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
    }

    @Test
    void testPatternedIdentifier(){
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.keyword("private"),e.optional(e.identifier(Pattern.compile("f.*")))
            ,e.optional(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
    }

    @Test
    void testNamedPatternedIdentifier(){
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        final var javaLexed = new JavaLexed(source);
        final var e = LexMatcher.of(javaLexed);
        final var matcher = e.list(e.keyword("private"),e.optional(e.identifier("final",Pattern.compile("f(.*)")))
            ,e.optional(e.identifier("abraka dabra")),e.unordered("int"));
        final var result = matcher.match(0);
        Assertions.assertTrue(result.matches);
        Assertions.assertEquals(0,result.start);
        Assertions.assertEquals(5,result.end);
        Assertions.assertEquals(1, e.get("final").size());
        Assertions.assertEquals(0, e.get("nonexistent").size());
        Assertions.assertEquals(1, e.get("final").get(0).groupCount());
        Assertions.assertEquals("inal", e.get("final").get(0).group(1));

    }
}
