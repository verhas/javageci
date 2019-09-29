package javax0.geci.lexeger;

import javax0.geci.lexeger.matchers.LexMatcher;
import javax0.geci.lexeger.matchers.Lexpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static javax0.geci.lexeger.LexpressionBuilder.character;
import static javax0.geci.lexeger.LexpressionBuilder.comment;
import static javax0.geci.lexeger.LexpressionBuilder.floatNumber;
import static javax0.geci.lexeger.LexpressionBuilder.group;
import static javax0.geci.lexeger.LexpressionBuilder.identifier;
import static javax0.geci.lexeger.LexpressionBuilder.integerNumber;
import static javax0.geci.lexeger.LexpressionBuilder.keyword;
import static javax0.geci.lexeger.LexpressionBuilder.list;
import static javax0.geci.lexeger.LexpressionBuilder.match;
import static javax0.geci.lexeger.LexpressionBuilder.number;
import static javax0.geci.lexeger.LexpressionBuilder.oneOf;
import static javax0.geci.lexeger.LexpressionBuilder.oneOrMore;
import static javax0.geci.lexeger.LexpressionBuilder.optional;
import static javax0.geci.lexeger.LexpressionBuilder.repeat;
import static javax0.geci.lexeger.LexpressionBuilder.string;
import static javax0.geci.lexeger.LexpressionBuilder.type;
import static javax0.geci.lexeger.LexpressionBuilder.unordered;
import static javax0.geci.lexeger.LexpressionBuilder.zeroOrMore;

class TestMatching {

    @Test
    void testSimpleListMatching() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = LexMatcher.when(javaLexed).usingExpression(match("private final int")).matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithSpaces() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = LexMatcher.when(javaLexed, Lexpression.SPACE_SENSITIVE).usingExpression(match("public var h")).find(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithComments() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = e.usingExpression(list(match("public var "), comment(), match("h")));
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
            final var e = LexMatcher.when(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = e.usingExpression(list(match("public var "), comment(
                Pattern.compile("//c.*t")
            ), match("h")));
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
            final var e = LexMatcher.when(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = e.usingExpression(list(match("public var "), comment(
                "comment", Pattern.compile("//(c.*t)")
            ), match("h")));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
            Assertions.assertTrue(e.matchResult("comment").isPresent());
            Assertions.assertEquals("comment", e.matchResult("comment").get().group(1));
        }
    }

    @Test
    void testSimpleListFindingWithTextComments() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var //comment\nh = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed, Lexpression.COMMENT_SENSITIVE);
            final var matcher = e.usingExpression(list(match("public var "), comment(
                "//comment"
            ), match("h")));
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(match("public var h"));
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(group("protection", oneOf("public", "private")), match("var h")));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
            Assertions.assertEquals(1, e.group("protection").size());
            Assertions.assertEquals(1, e.group("protection").size());
            Assertions.assertEquals("public", e.group("protection").get(0).getLexeme());
        }
    }

    @Test
    void testSimpleUnmatchedGroup() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(group("protection", oneOf(match("public"), group("private", match("private")))), match("var h")));
            final var result = matcher.find();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
            Assertions.assertEquals(0, e.group("private").size());
            Assertions.assertEquals(1, e.group("protection").size());
        }
    }

    @Test
    void testSimpleSetFinding() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(unordered("h public var"));
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(unordered(identifier("h"), keyword("public"), match("var")));
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(unordered("k public var"));
            final var result = matcher.find();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testIdentifiertMatching() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(keyword("private"), identifier("final"), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOrMoretMatching() {
        final var source = new TestSource(List.of("private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(oneOrMore(keyword("private")), zeroOrMore(identifier("final"))
                , zeroOrMore(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOrMoretMatchingMany() {
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(oneOrMore(keyword("private")), optional(identifier("final"))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testrepeatMinNumber() {
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(repeat(keyword("private"), 3), optional(identifier("final"))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testRepeatMinMaxNumber() {
        final var source = new TestSource(List.of("private private private final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(repeat(keyword("private"), 2, 3), optional(identifier("final"))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testAnyIdentifier() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(keyword("private"), optional(identifier())
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOneOf() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(oneOf("final int", "int", "private final int"));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testPatternedIdentifier() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(keyword("private"), optional(identifier(Pattern.compile("f.*")))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testNamedPatternedIdentifier() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(keyword("private"), optional(identifier("final", Pattern.compile("f(.*)")))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = matcher.matchesAt(0);
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), floatNumber()));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testFloatNumberWithPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13.5;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), floatNumber(f -> f > 13 && f < 14)));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testFloatNumberWithFailingPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13.5;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), floatNumber(f -> false)));
            final var result = matcher.matchesAt(0);
            Assertions.assertFalse(result.matches);
        }
    }


    @Test
    void testIntegerNumber() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), integerNumber()));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testIntegerNumberWithPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), integerNumber(f -> f > 12 && f < 14)));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testIntegerNumberWithFailingPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), integerNumber(f -> false)));
            final var result = matcher.matchesAt(0);
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testNumber() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), number()));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testNumberWithPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), number(f -> true)));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testNumberWithFailingPredicate() {
        final var source = new TestSource(List.of("private  final int z = 13;\npublic var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("private final int z = "), number(f -> false)));
            final var result = matcher.matchesAt(0);
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testType() {
        final var source = new TestSource(List.of("List"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(type());
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testTypeWithGenerics() {
        final var source = new TestSource(List.of("List<Object,?>>"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(type());
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testString() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), string()));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithValue() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), string("kkk")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithPattern() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), string(Pattern.compile("k{3}"))));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithNamedPattern() {
        final var source = new TestSource(List.of("public var h = \"kkk\""));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), string("Zkk", Pattern.compile("(k{3})"))));
            final var result = matcher.matchesAt(0);
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), character()));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithValue() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), character("kkk")));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithPattern() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), character(Pattern.compile("k{3}"))));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithNamedPattern() {
        final var source = new TestSource(List.of("public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(match("public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = matcher.matchesAt(0);
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(oneOrMore(match("public")), match("public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = matcher.matchesAt(0);
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
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(oneOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = matcher.matchesAt(0);
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testBackTrackTwoStepsSuccessful() {
        final var source = new TestSource(List.of("public public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var e = LexMatcher.when(javaLexed);
            final var matcher = e.usingExpression(list(zeroOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = matcher.matchesAt(0);
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(e.matchResult("Zkk").isPresent());
            Assertions.assertEquals(1, e.matchResult("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", e.matchResult("Zkk").get().group(1));
        }
    }

    @Test
    void testSimpleReplacement() {
        final var source = new TestSource(List.of("/** this is a comment */ public public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = LexMatcher.when(javaLexed)
                .usingExpression(list(zeroOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))))
                .find();
            javaLexed.replace(result.start, result.end, Lex.of("public var h = "));
        }
        Assertions.assertEquals("/** this is a comment */ public var h = ", source.toString());
    }

    @Test
    void testSimpleReplacement2() {
        final var source = new TestSource(List.of("/** this is a comment */ public public var h = 'kkk'"));
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = LexMatcher.when(javaLexed)
                .usingExpression(list(zeroOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))))
                .find();
            javaLexed.replace(result, Lex.of("public var h = "));
        }
        Assertions.assertEquals("/** this is a comment */ public var h = ", source.toString());
    }
}
