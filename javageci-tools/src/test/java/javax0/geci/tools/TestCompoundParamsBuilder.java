package javax0.geci.tools;

import javax0.geci.api.GeciException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestCompoundParamsBuilder {

    @Test
    @DisplayName("Test that single and double quotes strings are working")
    void testStringValues() {
        final var sut = new CompoundParamsBuilder("abrakadabra alma=\"Hungarian\" apple='English' manzana='Espa\\'nol'");
        final var cp = sut.build();
        Assertions.assertEquals("abrakadabra", cp.id());
        Assertions.assertEquals(3, cp.keySet().size());
        Assertions.assertEquals("Hungarian", cp.get("alma"));
        Assertions.assertEquals("English", cp.get("apple"));
        Assertions.assertEquals("Espa'nol", cp.get("manzana"));
    }

    @Test
    @DisplayName("Test that single and double quotes strings are working")
    void testMultipleValues() {
        final var sut = new CompoundParamsBuilder("abrakadabra alma=\"Hungarian\" alma='German' apple='English' manzana='Espa\\'nol'");
        final var cp = sut.build();
        Assertions.assertEquals("abrakadabra", cp.id());
        Assertions.assertEquals(3, cp.keySet().size());
        Assertions.assertEquals("Hungarian", cp.get("alma"));
        Assertions.assertEquals("English", cp.get("apple"));
        Assertions.assertEquals("Espa'nol", cp.get("manzana"));

        Assertions.assertEquals(2, cp.getValueList("alma").size());
        Assertions.assertEquals("Hungarian", cp.getValueList("alma").get(0));
        Assertions.assertEquals("German", cp.getValueList("alma").get(1));
    }

    @Test
    @DisplayName("Test that numeric values are working")
    void testNumericValues() {
        final var sut = new CompoundParamsBuilder("abrakadabra long=12L int=5 float=7.54e13 hex=0x55 hexFloat=0x55P3");
        final var cp = sut.build();
        Assertions.assertEquals("abrakadabra", cp.id());
        Assertions.assertEquals(5, cp.keySet().size());
        Assertions.assertEquals("12L", cp.get("long"));
        Assertions.assertEquals("5", cp.get("int"));
        Assertions.assertEquals("7.54e13", cp.get("float"));
        Assertions.assertEquals("0x55", cp.get("hex"));
        Assertions.assertEquals("0x55P3", cp.get("hexFloat"));
    }

    @Test
    @DisplayName("Test that id values do not need parentheses")
    void testIdValues() {
        final var sut = new CompoundParamsBuilder("abrakadabra me=thisIs_id");
        final var cp = sut.build();
        Assertions.assertEquals("abrakadabra", cp.id());
        Assertions.assertEquals(1, cp.keySet().size());
        Assertions.assertEquals("thisIs_id", cp.get("me"));
    }

    @Test
    @DisplayName("Test that some keys can be excluded")
    void testExclude() {
        final var sut = new CompoundParamsBuilder("abrakadabra long=12L int=5 float=7.54e13 hex=0x55 hexFloat=0x55P3").exclude("long");
        final var cp = sut.build();
        Assertions.assertEquals("abrakadabra", cp.id());
        Assertions.assertEquals(4, cp.keySet().size());
        Assertions.assertEquals("", cp.get("long"));
        Assertions.assertEquals("5", cp.get("int"));
        Assertions.assertEquals("7.54e13", cp.get("float"));
        Assertions.assertEquals("0x55", cp.get("hex"));
        Assertions.assertEquals("0x55P3", cp.get("hexFloat"));
    }

    @Test
    @DisplayName("Test that 'id' as key throws exception when not redefinable")
    void testIdThrows() {
        final var sut = new CompoundParamsBuilder("abrakadabra id=12L int=5 float=7.54e13 hex=0x55 hexFloat=0x55P3");
        Assertions.assertThrows(GeciException.class, sut::build);
    }

    @Test
    @DisplayName("Test that 'id' as key is ignored when not redefinable can be excluded")
    void testIdRedefined() {
        final var sut = new CompoundParamsBuilder("abrakadabra id=12L int=5 float=7.54e13 hex=0x55 hexFloat=0x55P3").redefineId();
        final var cp = sut.build();
        Assertions.assertEquals("12L", cp.id());
        Assertions.assertEquals(4, cp.keySet().size());
        Assertions.assertEquals("5", cp.get("int"));
        Assertions.assertEquals("7.54e13", cp.get("float"));
        Assertions.assertEquals("0x55", cp.get("hex"));
        Assertions.assertEquals("0x55P3", cp.get("hexFloat"));
    }

    @Test
    @DisplayName("Test that it can build params without id")
    void testNoId() {
        final var sut = new CompoundParamsBuilder("abrakadabra=13");
        final var cp = sut.build();
        Assertions.assertEquals("", cp.id());
        Assertions.assertEquals(1, cp.keySet().size());
        Assertions.assertEquals("13", cp.get("abrakadabra"));
    }

    @Test
    @DisplayName("Test that it can build an id only param set")
    void testNoParams() {
        final var sut = new CompoundParamsBuilder("abrakadabra");
        final var cp = sut.build();
        Assertions.assertEquals("abrakadabra", cp.id());
        Assertions.assertEquals(0, cp.keySet().size());
    }
}
