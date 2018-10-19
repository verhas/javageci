package javax0.geci.tests.fluent;

import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFluent {


    public static FluentBuilder definition() {
        var t = FluentBuilder.from(SimpleSample.class).start("sample");
        return t.oneOrMore(t.oneOf(t.one("a").one("b"),t.one("c").one("d"))).oneOf("get","got");
    }

    @Test
    public void testDelegator() throws Exception {
        if (new Geci().source("./src/main/java", "./tests/src/main/java").register(new Fluent()).generate()) {
            Assertions.fail("Fluent modified source code. Please compile again.");
        }
        Assertions.assertEquals("",SimpleSample.sample().a("ss").b("bb").c("").get());
    }

}
