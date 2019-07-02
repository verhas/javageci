package javax0.geci.docugen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSnippetBuilder {



    @Test
    void test(){
        final var sut = new SnippetBuilder();
        sut.startLine("abrakadabra alma=\"Hungarian\" apple='English' manzana='Espa\\'nol'");
        Assertions.assertEquals("abrakadabra",sut.snippetName());
        final var snippet = sut.build();
        Assertions.assertEquals(3,snippet.keys().size());
        Assertions.assertEquals("Hungarian",snippet.param("alma"));
        Assertions.assertEquals("English",snippet.param("apple"));
        Assertions.assertEquals("Espa'nol",snippet.param("manzana"));
    }
}
