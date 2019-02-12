package javax0.geci.tests.mapper;

import javax0.geci.engine.Geci;
import javax0.geci.mapper.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

public class TestMapper {

    @Test
    public void testMapper() throws Exception {
        if (new Geci().source(maven().module("javageci-examples").mainSource()).register(new Mapper()).generate()) {
            Assertions.fail(Geci.FAILED);
        }
    }
}
