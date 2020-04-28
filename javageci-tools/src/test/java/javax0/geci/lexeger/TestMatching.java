package javax0.geci.lexeger;

import javax0.geci.lexeger.matchers.Lexpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Collections;
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
import static javax0.geci.lexeger.LexpressionBuilder.modifier;
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
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed.match(match("private final int")).fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithSpaces() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed.sensitivity(Lexpression.SPACE_SENSITIVE).find(match("public var h")).fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithComments() {
        final var source = new TestSource("private final int z = 13;\npublic var //comment\nh = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed.sensitivity(Lexpression.COMMENT_SENSITIVE).find(list(match("public var "), comment(), match("h"))).fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithPatternedComments() {
        final var source = new TestSource("private final int z = 13;\npublic var //comment\nh = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result =
                javaLexed.sensitivity(Lexpression.COMMENT_SENSITIVE)
                    .find(list(match("public var "), comment(Pattern.compile("//c.*t")), match("h")))
                    .fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
        }
    }

    @Test
    void testSimpleListFindingWithNamedPatternedComments() {
        final var source = new TestSource("private final int z = 13;\npublic var //comment\nh = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result =
                javaLexed.sensitivity(Lexpression.COMMENT_SENSITIVE)
                    .find(list(match("public var "), comment("comment", Pattern.compile("//(c.*t)")), match("h")))
                    .fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
            Assertions.assertTrue(javaLexed.regexGroups("comment").isPresent());
            Assertions.assertEquals("comment", javaLexed.regexGroups("comment").get().group(1));
        }
    }

    @Test
    void testSimpleListFindingWithTextComments() {
        final var source = new TestSource("private final int z = 13;\npublic var //comment\nh = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result =
                javaLexed.sensitivity(Lexpression.COMMENT_SENSITIVE)
                    .find(list(match("public var "), comment("//comment"), match("h")))
                    .fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(20, result.end);
        }
    }

    //snippet testSimpleListFinding
    @Test
    void testSimpleListFinding() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final MatchResult result = javaLexed.find(match("public var h")).fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }
    //end snippet

    //snippet testSimpleGroupCollection
    @Test
    void testSimpleGroupCollection() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed.find(list(oneOf(group("protection"), "public", "private"), match("var h"))).fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
            Assertions.assertEquals(1, javaLexed.group("protection").size());
            Assertions.assertEquals("public", javaLexed.group("protection").get(0).getLexeme());
        }
    }
    //end snippet

    @Test
    void testSimpleUnmatchedGroup() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            //snippet testSimpleUnmatchedGroup
            javaLexed.find(list(group("protection", oneOf(match("public"), group("private", match("private")))), match("var h")));
            // skip 4 lines
            final var result = javaLexed.fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
            Assertions.assertEquals(0, javaLexed.group("private").size());
            Assertions.assertEquals(1, javaLexed.group("protection").size());
            // end snippet
        }
    }

    @Test
    void testSimpleSetFinding() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.find(unordered("h public var"));
            final var result = javaLexed.fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testComplexSetFinding() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.find(unordered(identifier("h"), keyword("public"), match("var")));
            final var result = javaLexed.fromStart().result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(13, result.start);
            Assertions.assertEquals(18, result.end);
        }
    }

    @Test
    void testSimpleSetNotFinding() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {

            javaLexed.find(unordered("k public var"));
            final var result = javaLexed.fromStart().result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testIdentifiertMatching() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(keyword("private"), identifier("final"), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOrMoretMatching() {
        final var source = new TestSource("private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(oneOrMore(keyword("private")), zeroOrMore(identifier("final"))
                , zeroOrMore(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOrMoretMatchingMany() {
        final var source = new TestSource("private private private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(oneOrMore(keyword("private")), optional(identifier("final"))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testrepeatMinNumber() {
        final var source = new TestSource("private private private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(repeat(keyword("private"), 3), optional(identifier("final"))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testRepeatMinMaxNumber() {
        final var source = new TestSource("private private private final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(repeat(keyword("private"), 2, 3), optional(identifier("final"))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(9, result.end);
        }
    }

    @Test
    void testAnyIdentifier() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(keyword("private"), optional(identifier())
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testOneOf() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(oneOf("final int", "int", "private final int"));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testPatternedIdentifier() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(keyword("private"), optional(identifier(Pattern.compile("f.*")))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
        }
    }

    @Test
    void testNamedPatternedIdentifier() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(keyword("private"), optional(identifier("final", Pattern.compile("f(.*)")))
                , optional(identifier("abraka dabra")), unordered("int")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(5, result.end);
            Assertions.assertTrue(javaLexed.regexGroups("final").isPresent());
            Assertions.assertFalse(javaLexed.regexGroups("nonexistent").isPresent());
            Assertions.assertEquals(1, javaLexed.regexGroups("final").get().groupCount());
            Assertions.assertEquals("inal", javaLexed.regexGroups("final").get().group(1));
        }
    }

    @Test
    void testFloatNumber() {
        final var source = new TestSource("private  final int z = 13.5;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), floatNumber()));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testFloatNumberWithPredicate() {
        final var source = new TestSource("private  final int z = 13.5;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), floatNumber(f -> f > 13 && f < 14)));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testFloatNumberWithFailingPredicate() {
        final var source = new TestSource("private  final int z = 13.5;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), floatNumber(f -> false)));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }


    @Test
    void testIntegerNumber() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), integerNumber()));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testIntegerNumberWithPredicate() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {

            javaLexed.match(list(match("private final int z = "), integerNumber(f -> f > 12 && f < 14)));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testIntegerNumberWithFailingPredicate() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {

            javaLexed.match(list(match("private final int z = "), integerNumber(f -> false)));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testNumber() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), number()));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testNumberWithPredicate() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), number(f -> true)));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testNumberWithFailingPredicate() {
        final var source = new TestSource("private  final int z = 13;\npublic var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("private final int z = "), number(f -> false)));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testType() {
        final var source = new TestSource("List");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testTypeFollowedByEllipsis() {
        final var source = new TestSource("List...");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testTypeWithNonType() {
        final var source = new TestSource("123");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testTypeWithIncompleteGenerics() {
        final var source = new TestSource("List<String");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testTypeWithNestedGenerics() {
        final var source = new TestSource("List<List<>>");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testTypeWithNestedNestedGenerics() {
        final var source = new TestSource("List<List<List<List<List<List<>>>>> >");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testTypeWithMisplacedDot() {
        final var source = new TestSource("List.[]");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testTypeWithGenerics() {
        final var source = new TestSource("List<Object,?>");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(6, result.end);
        }
    }

    @Test
    void testArrayTypeWithGenerics() {
        final var source = new TestSource("List<String, List[]>[]");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testArrayType() {
        final var source = new TestSource("List[]");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(3, result.end);
        }
    }

    @Test
    void testArrayArrayType() {
        final var source = new TestSource("List[][][][][]");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(11, result.end);
        }
    }

    @Test
    void testUnbalancedArrayType() {
        final var source = new TestSource("List[][][][][");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type());
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testTypeWithGenericsAndPackageAndSpace() {
        final var source = new TestSource("java.util.List< Object , ? >");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(type(group("xx")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertEquals(0, result.start);
            Assertions.assertEquals(14, result.end);
            Assertions.assertEquals("[IDENTIFIER[java], "
                + "SYMBOL[.], "
                + "IDENTIFIER[util], "
                + "SYMBOL[.], "
                + "IDENTIFIER[List], "
                + "SYMBOL[<], "
                + "SPACING[ ], "
                + "IDENTIFIER[Object], "
                + "SPACING[ ], "
                + "SYMBOL[,], "
                + "SPACING[ ], "
                + "SYMBOL[?], "
                + "SPACING[ ], "
                + "SYMBOL[>]]", javaLexed.group("xx").toString());
        }
    }

    @Test
    void testString() {
        final var source = new TestSource("public var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), string()));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithValue() {
        final var source = new TestSource("public var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), string("kkk")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithPattern() {
        final var source = new TestSource("public var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), string(Pattern.compile("k{3}"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testStringWithNamedPattern() {
        final var source = new TestSource("public var h = \"kkk\"");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), string("Zkk", Pattern.compile("(k{3})"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(javaLexed.regexGroups("Zkk").isPresent());
            Assertions.assertEquals(1, javaLexed.regexGroups("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", javaLexed.regexGroups("Zkk").get().group(1));
        }
    }


    @Test
    void testCharacter() {
        final var source = new TestSource("public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), character()));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithValue() {
        final var source = new TestSource("public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), character("kkk")));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithPattern() {
        final var source = new TestSource("public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), character(Pattern.compile("k{3}"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
        }
    }

    @Test
    void testCharacterWithNamedPattern() {
        final var source = new TestSource("public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(match("public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(javaLexed.regexGroups("Zkk").isPresent());
            Assertions.assertEquals(1, javaLexed.regexGroups("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", javaLexed.regexGroups("Zkk").get().group(1));
        }
    }

    @Test
    void testModifiers() {
        final var source = new TestSource("public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed.match(list(modifier(Modifier.PRIVATE | Modifier.PUBLIC), match(" var h = "), character("Zkk", Pattern.compile("(k{3})")))).fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(javaLexed.regexGroups("Zkk").isPresent());
            Assertions.assertEquals(1, javaLexed.regexGroups("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", javaLexed.regexGroups("Zkk").get().group(1));
        }
    }

    @Test
    void testModifiersNoMatch() {
        final var source = new TestSource("final var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.find(list(modifier(Modifier.PRIVATE | Modifier.PUBLIC), match(" var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testBackTrackOneStep() {
        final var source = new TestSource("public public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(oneOrMore(match("public")), match("public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(javaLexed.regexGroups("Zkk").isPresent());
            Assertions.assertEquals(1, javaLexed.regexGroups("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", javaLexed.regexGroups("Zkk").get().group(1));
        }
    }

    @Test
    void testBackTrackTwoSteps() {
        final var source = new TestSource("public public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(oneOrMore("public"), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertFalse(result.matches);
        }
    }

    @Test
    void testBackTrackTwoStepsSuccessful() {
        final var source = new TestSource("public public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            javaLexed.match(list(zeroOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))));
            final var result = javaLexed.fromIndex(0).result();
            Assertions.assertTrue(result.matches);
            Assertions.assertTrue(javaLexed.regexGroups("Zkk").isPresent());
            Assertions.assertEquals(1, javaLexed.regexGroups("Zkk").get().groupCount());
            Assertions.assertEquals("kkk", javaLexed.regexGroups("Zkk").get().group(1));
        }
    }

    @Test
    void testSimpleReplacement() {
        final var source = new TestSource("/** this is a comment */ public public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed
                                   .find(list(zeroOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))))
                                   .fromStart().result();
            javaLexed.replace(result.start, result.end, Lex.of("public var h = "));
        }
        Assertions.assertEquals("/** this is a comment */ public var h = ", source.toString());
    }

    @Test
    void testSimpleReplacement2() {
        final var source = new TestSource("/** this is a comment */ public public var h = 'kkk'");
        try (final var javaLexed = new JavaLexed(source)) {
            final var result = javaLexed
                                   .find(list(zeroOrMore(match("public")), match("public public var h = "), character("Zkk", Pattern.compile("(k{3})"))))
                                   .fromStart().result();
            javaLexed.replaceWith(Lex.of("public var h = "));
        }
        Assertions.assertEquals("/** this is a comment */ public var h = ", source.toString());
    }
}
