package javax0.geci.buildfluent;

import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.tools.JavaSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class BuildFluentForSourceBuilder {

    @Test
    public void testSourceBuilderGeneratedApiIsGood() throws Exception{
        if (new Geci().source("../tools/src/main/java", "./tools/src/main/java").register(new Fluent()).generate()) {
            Assertions.fail("Fluent modified source code. Please compile again.");
        }
    }

    public static FluentBuilder sourceBuilderGrammar() {
        var source = FluentBuilder.from(JavaSource.class).start("builder").fluentType("Builder").implement("AutoCloseable").exclude("close");
        var statement = source.oneOf("comment", "statement", "write", "write_r", "write_l", "newline", "open");
        var methodStatement = source.oneOf(statement, source.oneOf("returnStatement()", "returnStatement(String,Object[])"));
        var ifStatement = source.one("ifStatement").zeroOrMore(statement).optional(source.one("elseStatement").zeroOrMore(statement));
        var whileStatement = source.one("whileStatement").zeroOrMore(statement);
        var forStatement = source.one("forStatement").zeroOrMore(statement);
        var methodDeclaration = source.one("method").optional("modifiers").optional("returnType").optional("exceptions").oneOf("noArgs", "args");
        var method = source.one(methodDeclaration).zeroOrMore(methodStatement);
        var grammar = source.zeroOrMore(source.oneOf(statement, ifStatement, whileStatement, forStatement, method)).one("toString");
        return grammar;
    }

}
