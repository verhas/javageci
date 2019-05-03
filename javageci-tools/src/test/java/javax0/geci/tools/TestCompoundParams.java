package javax0.geci.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

public class TestCompoundParams {

    @Test
    @DisplayName("It proxies a normal map")
    void testFromMap() {
        final var sut = new CompoundParams("theId", Map.of("a", "1", "b", "2"));
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from a normal map")
    void testKeySetFromMap() {
        final var sut = new CompoundParams("theId", Map.of("a", "1", "b", "2"));
        Assertions.assertEquals("a,b",
                sut.keySet().stream().collect(Collectors.joining(",")));
    }

    @Test
    @DisplayName("It proxies multiple maps and earlier hides the later")
    void testFromMapMultiple() {
        final var sut = new CompoundParams("theId", Map.of("a", "1", "b", "2"), Map.of("a", "4", "b", "4", "c", "5"));
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("5", sut.get("c"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from multiple maps")
    void testKeySetFromMapMultiple() {
        final var sut = new CompoundParams("theId", Map.of("a", "1", "b", "2"), Map.of("a", "4", "b", "4", "c", "5"));
        Assertions.assertEquals("a,b,c",
                sut.keySet().stream().collect(Collectors.joining(",")));
    }

    @Test
    @DisplayName("It proxies multiple CompoundParameters and earlier hides the later")
    void testFromMapMultipleCompound() {
        final var sut = new CompoundParams(
                new CompoundParams("theId",
                        Map.of("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        Map.of("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("5", sut.get("c"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from multiple CompoundParameters")
    void testKeySetFromMapMultipleCompound() {
        final var sut = new CompoundParams(
                new CompoundParams("theId",
                        Map.of("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        Map.of("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("a,b,c",
                sut.keySet().stream().collect(Collectors.joining(",")));
    }

    @Test
    @DisplayName("It proxies multiple CompoundParameters with possible null Co..Pa..ms")
    void testFromMapMultipleCompoundWithNull() {
        final var sut = new CompoundParams(
                null,
                new CompoundParams("theId",
                        Map.of("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        Map.of("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("1", sut.get("a"));
        Assertions.assertEquals("2", sut.get("b"));
        Assertions.assertEquals("5", sut.get("c"));
        Assertions.assertEquals("", sut.get("nonexsistent"));
    }

    @Test
    @DisplayName("KeySet from multiple CompoundParameters some null")
    void testKeySetFromMapMultipleCompoundWithNull() {
        final var sut = new CompoundParams(
                new CompoundParams("theId",
                        Map.of("a", "1", "b", "2")),
                new CompoundParams("otherID-no one cares",
                        Map.of("a", "4", "b", "4", "c", "5"))
        );
        Assertions.assertEquals("a,b,c",
                sut.keySet().stream().collect(Collectors.joining(",")));
    }
}
