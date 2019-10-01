package javax0.geci.tests.accessor;

import javax0.geci.accessor.Accessor;
import javax0.geci.engine.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

class TestAccessor {

    //snippet TestAccessor
    @Test
    void testAccessor() throws Exception {
        Geci geci;
        Assertions.assertFalse(
                (geci = new Geci()).source(maven().module("javageci-examples").mainSource())
                        .register(Accessor.builder().build())
                        .generate(),
                geci.failed());
    }
//end snippet


    @Test
    void testAllSourcesAccessor() throws Exception {
        Geci geci;
        Assertions.assertFalse(
                (geci = new Geci()).source(
                        maven().module("javageci-examples").mainSource()
                ).register(Accessor.builder().mnemonic("SettersGetters").filter("annotation ~ /Getter|Setter/").processAllClasses(true).build()).generate(),
                geci.failed());
    }

}
