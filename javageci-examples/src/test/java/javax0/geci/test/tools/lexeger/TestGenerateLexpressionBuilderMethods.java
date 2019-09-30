package javax0.geci.test.tools.lexeger;

import javax0.geci.api.Segment;
import javax0.geci.api.Source;
import javax0.geci.engine.Geci;
import javax0.geci.tools.AbstractJavaGenerator;
import javax0.geci.tools.CompoundParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class TestGenerateLexpressionBuilderMethods extends AbstractJavaGenerator {

    @Test
    void createMethodsInLexpressionBuilder() throws IOException {
        final var geci = new Geci();
        Assertions.assertFalse(geci.source("../javageci-tools/src/main/java", "./javageci-tools/src/main/java")
                                   .register(new TestGenerateLexpressionBuilderMethods())
                                   .only("LexpressionBuilder.java")
                                   .generate(),
            geci.failed());
    }


    @Override
    public void process(Source source, Class<?> klass, CompoundParams global) throws Exception {
        Source lexpression = source.newSource("matchers/Lexpression.java");
        Segment segment = source.open("testGenerateLexpressionBuilderMethods");
        final var pattern = Pattern.compile("\\s*public\\s+LexMatcher\\s+(\\w+)\\((.*?)\\)\\s+\\{\\s*");
        for (final var line : lexpression.getLines()) {
            final var match = pattern.matcher(line);
            if (match.matches()) {
                createMethod(segment, match);
            }
        }
    }

    private void createMethod(Segment segment, Matcher match) {
        final var methodName = match.group(1);
        var arguments = match.group(2);
        final var parameters = Arrays.stream(arguments.split(",", -1))
                                   .map(s -> s.trim().replaceAll("^.*?\\s+", ""))
                                   .map(s -> s.startsWith("matcher") ? "X(" + s + ", jLex, e)" : s)
                                   .collect(Collectors.joining(", "));
        arguments = arguments.replaceAll("LexMatcher\\s+matcher",
            "BiFunction<JavaLexed, Lexpression, LexMatcher> matcher")
                        .replaceAll("LexMatcher\\.\\.\\.\\s+matchers", "BiFunction<JavaLexed, Lexpression, LexMatcher>... matchers");
        segment.write_r("public static BiFunction<JavaLexed, Lexpression, LexMatcher> " + methodName + "(" + arguments + ") {");
        segment.write("return (jLex, e) -> e." + methodName + "(" + parameters + ");");
        segment.write_l("}");
        segment.newline();
    }
}
