package javax0.geci.tests.mapper;

import javax0.geci.engine.Geci;
import javax0.geci.equals.Equals;
import javax0.geci.mapper.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

class TestMapper {

    // snippet TestMapper_testMapper
    @Test
    void testMapper() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-examples")
                        .mainSource())
                        .register(Mapper.builder().build()).generate(),
                geci.failed());
    }
    // end snippet

    @Test
    void testEquals() throws Exception {
        final var geci = new Geci();
        Assertions.assertFalse(
                geci.source(maven().module("javageci-examples").mainSource()).register(Equals.builder().build()).generate(),
                geci.failed());
    }
}
