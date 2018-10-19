package javax0.geci.tests.fluent;

import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestFluent {


    public static FluentBuilder definition() {
        var t = FluentBuilder.from(SimpleSample.class).start("sample").cloner("copy");
        return t.oneOrMore(t.oneOf(t.one("a").one("b"),t.one("c").one("d"))).oneOf("get","got");
    }

    @Test
    public void testFluent() throws Exception {
        if (new Geci().source("./src/main/java", "./tests/src/main/java").register(new Fluent()).generate()) {
            Assertions.fail("Fluent modified source code. Please compile again.");
        }
        //Assertions.assertEquals("C(1)D(2)A(3)B(4)",SimpleSample.sample().c("1").d("2").a("3").b("4").got());
    }

}
