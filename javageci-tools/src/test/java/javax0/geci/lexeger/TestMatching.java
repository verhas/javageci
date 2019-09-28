package javax0.geci.lexeger;

import javax0.geci.lexeger.matchers.Lexpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static javax0.geci.lexeger.LexpressionBuilder.*;

public class TestMatching {

    @Test
    void testSimpleListMatching() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.match("private final int");
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithSpaces() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed, Lexpression.SPACE_SENSITIVE);
            final var matcher = expr.match("public var h");
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithComments() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = expr.list(expr.match("public var "), expr.comment(), expr.match("h"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithPatternedComments() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = expr.list(expr.match("public var "), expr.comment(
                Pattern.compile("//c.*t")
            ), expr.match("h"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithNamedPatternedComments() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = expr.list(expr.match("public var "), expr.comment(
                "comment", Pattern.compile("//(c.*t)")
            ), expr.match("h"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
            Assertions.assertTrue(expr.matchResult("comment").isPresent());
            Assertions.assertEquals("comment", expr.matchResult("comment").get().group(1));
        }
    }

    @Test
    void testSimpleListFindingWithTextComments() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = expr.list(expr.match("public var "), expr.comment(
                "//comment"
            ), expr.match("h"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
        }
    }

    @Test
    void testSimpleListFinding() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.match("public var h");
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testSimpleGroupCollection() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.list(expr.group("protection", expr.oneOf("public", "private")), expr.match("var h"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
            Assertions.assertEquals(1, expr.group("protection").size());
            Assertions.assertEquals(1, expr.group("protection").size());
            Assertions.assertEquals("public", expr.group("protection").get(0).lexeme);
        }
    }

    @Test
    void testSimpleUnmatchedGroup() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.list(expr.group("protection", expr.oneOf(expr.match("public"), expr.group("private", expr.match("private")))), expr.match("var h"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
            Assertions.assertEquals(0, expr.group("private").size());
            Assertions.assertEquals(1, expr.group("protection").size());
        }
    }

    @Test
    void testSimpleSetFinding() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.unordered("h public var");
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testComplexSetFinding() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.unordered(expr.identifier("h"), expr.keyword("public"), expr.match("var"));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testSimpleSetNotFinding() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.unordered("k public var");
            final var result = matcher.find();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testIdentifiertMatching() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var expr = Lexpression.of(javaLexed);
            final var matcher = expr.list(expr.keyword("private"), expr.identifier("final"), expr.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOrMoretMatching() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.oneOrMore(e.keyword("private")), e.zeroOrMore(e.identifier("final"))
                , e.zeroOrMore(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOrMoretMatchingMany() {
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.oneOrMore(e.keyword("private")), e.optional(e.identifier("final"))
                , e.optional(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testrepeatMinNumber() {
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.repeat(e.keyword("private"), 3), e.optional(e.identifier("final"))
                , e.optional(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testRepeatMinMaxNumber() {
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.repeat(e.keyword("private"), 2, 3), e.optional(e.identifier("final"))
                , e.optional(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testAnyIdentifier() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.keyword("private"), e.optional(e.identifier())
                , e.optional(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOneOf() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.oneOf("final int", "int", "private final int");
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testPatternedIdentifier() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.keyword("private"), e.optional(e.identifier(Pattern.compile("f.*")))
                , e.optional(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testNamedPatternedIdentifier() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.keyword("private"), e.optional(e.identifier("final", Pattern.compile("f(.*)")))
                , e.optional(e.identifier("abraka dabra")), e.unordered("int"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
            Assertions.assertTrue(e.matchResult("final").isPresent());
            Assertions.assertTrue(e.matchResult("nonexistent").isEmpty());
            Assertions.assertEquals(1, e.matchResult("final").get().groupCount());
            Assertions.assertEquals("inal", e.matchResult("final").get().group(1));
        }
    }

    @Test
    void testFloatNumber() {
        final var source = new TestSource(List.of("private  final int z = 13.5;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.floatNumber());
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testFloatNumberWithPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13.5;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.floatNumber(f -> f > 13 && f < 14));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testFloatNumberWithFailingPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13.5;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.floatNumber(f -> false));
            final var result = matcher.match(0);
            Assertions.assertFalse(result.matches);
        }
    }


    @Test
    void testIntegerNumber() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.integerNumber());
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testIntegerNumberWithPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.integerNumber(f -> f > 12 && f < 14));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testIntegerNumberWithFailingPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.integerNumber(f -> false));
            final var result = matcher.match(0);
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testNumber() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.number());
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testNumberWithPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.number(f -> true));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testNumberWithFailingPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("private final int z = "), e.number(f -> false));
            final var result = matcher.match(0);
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testType() {
        final var source = new TestSource(List.of("List"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.type();
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testTypeWithGenerics() {
        final var source = new TestSource(List.of("List<Object,?>>"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.type();
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testString() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.string());
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithValue() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.string("kkk"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithPattern() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.string(Pattern.compile("k{3}")));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithNamedPattern() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.string("Zkk", Pattern.compile("(k{3})")));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(e.matchResult("Zkk").isPresent());
            Assertions.assertEquals(1, e.matchResult("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", e.matchResult("Zkk").get().group(1));
        }
    }


    @Test
    void testCharacter() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = list(match("public var h = "), character()).apply(javaLexed,e);
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithValue() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.character("kkk"));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithPattern() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.character(Pattern.compile("k{3}")));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithNamedPattern() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.match("public var h = "), e.character("Zkk", Pattern.compile("(k{3})")));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(e.matchResult("Zkk").isPresent());
            Assertions.assertEquals(1, e.matchResult("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", e.matchResult("Zkk").get().group(1));
        }
    }


    @Test
    void testBackTrackOneStep() {
        final var source = new TestSource(List.of("public public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.oneOrMore(e.match("public")), e.match("public var h = "), e.character("Zkk", Pattern.compile("(k{3})")));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(e.matchResult("Zkk").isPresent());
            Assertions.assertEquals(1, e.matchResult("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", e.matchResult("Zkk").get().group(1));
        }
    }

    @Test
    void testBackTrackTwoSteps() {
        final var source = new TestSource(List.of("public public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.oneOrMore(e.match("public")), e.match("public public var h = "), e.character("Zkk", Pattern.compile("(k{3})")));
            final var result = matcher.match(0);
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testBackTrackTwoStepsSuccessful() {
        final var source = new TestSource(List.of("public public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = Lexpression.of(javaLexed);
            final var matcher = e.list(e.zeroOrMore(e.match("public")), e.match("public public var h = "), e.character("Zkk", Pattern.compile("(k{3})")));
            final var result = matcher.match(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(e.matchResult("Zkk").isPresent());
            Assertions.assertEquals(1, e.matchResult("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", e.matchResult("Zkk").get().group(1));
        }
    }

}
