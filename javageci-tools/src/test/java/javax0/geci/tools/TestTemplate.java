package javax0.geci.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

public class TestTemplate {

    @Test
    @DisplayName("When there are no params every string just returns as is")
    void emptyTest() {
        final var sut = new Template(Map.of());
        final var samples = Arrays.asList("alma", "{{kirte}}", "just {a{{nithing");
        for (final var sample : samples) {
            Assertions.assertEquals(sample, sut.resolve(sample));
        }
    }

    @Test
    @DisplayName("When there are params they are replaced")
    void goodTest() {
        // snippet Template_test_01
        final var sut = new Template(Map.of("a", "b", "huhh", "spooky"));
        Assertions.assertEquals("this is a spooky baboon", sut.resolve("this is a {{huhh}} {{a}}a{{a}}oon"));
        // end snippet
    }

    @Test
    @DisplayName("Parameters replaces also at the start")
    void startTest() {
        // snippet Template_test_02
        final var sut = new Template(Map.of("a", "b", "huhh", "spooky"));
        Assertions.assertEquals("bthis is {{a...}} spooky bab{{oon", sut.resolve("{{a}}this is {{a...}} {{huhh}} {{a}}a{{a}}{{oon"));
        // end snippet
    }

    @Test
    @DisplayName("When there are params they are replaced but not the undefined")
    void goodTestStill() {
        // snippet Template_test_03
        final var sut = new Template(Map.of("a", "b", "huhh", "spooky"));
        Assertions.assertEquals("this is {{a...}} spooky baboon", sut.resolve("this is {{a...}} {{huhh}} {{a}}a{{a}}oon"));
        // end snippet
    }

    @Test
    @DisplayName("Unterminated placeholders are handled gracefully")
    void unterminatedTest() {
        // snippet Template_test_04
        final var sut = new Template(Map.of("a", "b", "huhh", "spooky"));
        Assertions.assertEquals("this is {{a...}} spooky bab{{oon", sut.resolve("this is {{a...}} {{huhh}} {{a}}a{{a}}{{oon"));
        // end snippet
    }

}
