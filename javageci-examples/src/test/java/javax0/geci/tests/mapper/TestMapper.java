package javax0.geci.tests.mapper;

import javax0.geci.engine.Geci;
import javax0.geci.equals.Equals;
import javax0.geci.mapper.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

class TestMapper {

    @Test
    void testMapper() throws Exception {
        Assertions.assertFalse(
                new Geci().source(maven().module("javageci-examples").mainSource()).register(new Mapper()).generate(),
                Geci.FAILED);
    }

    @Test
    void testEquals() throws Exception {
        Assertions.assertFalse(
                new Geci().source(maven().module("javageci-examples").mainSource()).register(new Equals()).generate(),
                Geci.FAILED);
    }
}
