package javax0.geci.tests.record;

import javax0.geci.engine.Geci;
import javax0.geci.record.Record;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax0.geci.api.Source.maven;

class TestRecord {

    @Test
    void testRecord() throws Exception {
        Geci geci;
        Assertions.assertFalse(
            (geci = new Geci()).source(maven().module("javageci-examples").mainSource())
                .register(Record.builder().build())
                .generate(),
            geci.failed());
    }
}
