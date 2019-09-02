package javax0.geci.tests.fluent;

import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import javax0.geci.tutorials.xmlbuilder.XmlBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static javax0.geci.api.Source.maven;

public class TestFluent {


    public static FluentBuilder definition() {
        var t = FluentBuilder.from(SimpleSample.class).start("sample").cloner("copy");
        return t.oneOrMore(t.oneOf(t.one("a").one("b"), t.one("c").one("d"))).oneOf("get", "got");
    }

    public static FluentBuilder regex() {
        var t = FluentBuilder.from(Regex.class).start("pattern").cloner("copy");
        return t.syntax("(terminal | set | optional | zeroOrMore | oneOrMore | more)+ get");
    }

    public static FluentBuilder xml() {
        var t = FluentBuilder.from(XmlBuilder.class).cloner("copy").autoCloseable();
        var tag = t.one("tag").zeroOrMore("attribute").optional("text");
        return t.oneOrMore(tag).one("toString");
    }

    @Test
    public void testXmlBuilder() throws Exception {
        var xml = XmlBuilder.start().tag("alma").attribute("a", "b").text("kakukk");
        try (xml) {
            xml.tag("hamar").close();
        }
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<alma a=\"b\">\n" +
            "kakukk\n" +
            "  <hamar>\n" +
            "\n" +
            "  </hamar>\n" +
            "</alma>", xml.toString());
    }

    @Test
    public void testRegex() {
        var rx = Regex.pattern();
        Pattern pt = Regex.pattern().oneOrMore(Regex.pattern().terminal("a").oneOrMore(rx.terminal("b")).terminal("a")).get();
        Assertions.assertEquals("(?:a(?:b)+a)+", pt.toString());
    }

    @Test
    public void testFluent() throws Exception {
        if (new Geci().source(maven().module("javageci-examples").mainSource()).register(new Fluent()).generate()) {
            Assertions.fail("Fluent modified source code. Please compile again.");
        }
    }

}
