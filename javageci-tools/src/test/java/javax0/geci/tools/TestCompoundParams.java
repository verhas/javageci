package javax0.geci.tools;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class TestCompoundParams {

    @Test
    @DisplayName("Throws IllegalArgumentException when the constructor argument is illegal")
    void testBadConstructorUse(){
        Assertions.assertThrows(IllegalArgumentException.class , () -> new CompoundParams("theId", Map.of("a", Arrays.asList(1,2,3))));
        Assertions.assertThrows(IllegalArgumentException.class , () -> new CompoundParams("theId", Map.of("a", 3)));
    }

    @Test
    @DisplayName("Get returns with value if key exists or default value or supplied value")
    void testGet() {
        final var sut = new CompoundParams("theId");
        Assertions.assertEquals("theId", sut.get("id"));
        Assertions.assertEquals("", sut.get("nonexistent"));
        Assertions.assertEquals("foo", sut.get("nonexistent", "foo"));
        Assertions.assertEquals("foobar", sut.get("nonexistent", () -> "foo" + "bar"));
    }

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
                String.join(",", sut.keySet()));
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
                String.join(",", sut.keySet()));
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
                String.join(",", sut.keySet()));
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
                String.join(",", sut.keySet()));
    }

    @Test
    @DisplayName("KeySet can be constrained")
    void testConstrainingKeys() {
        final var source = new TestSource();
        final var mnemonic = "TestGen";
        final var sut = new CompoundParams("theId", Map.of(
            "a", "1",
            "b", "2",
            "c", "3")
        );

        Assertions.assertThrows(GeciException.class, () -> sut.setConstraints(source, mnemonic, new HashSet<>(Arrays.asList("a", "c", "e"))));
        Assertions.assertDoesNotThrow(() -> sut.setConstraints(source, mnemonic, new HashSet<>(Arrays.asList("a", "b", "c", "d", "e"))));
    }

    private static class TestSource extends AbstractTestSource {
        @Override
        public String getAbsoluteFile() {
            return "ABSOLUTE_TEST_FILE";
        }
    }
}
