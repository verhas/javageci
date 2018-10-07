package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTools {
    @Geci("aaa a='b' b='c' c='d'")
    @Geci("xxx x='x' y='y' z='z'")
    private static Object something;

    @Test
    public void testParameterParser() {
        var map = Tools.getParameters("a='b' b='c' c='d'");
        assertEquals(map.size(), 3);
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");

    }

    @Test
    public void testParameterFetcher() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("something");
        var map = Tools.getParameters(f, "aaa");
        assertEquals(map.get("a"), "b");
        assertEquals(map.get("b"), "c");
        assertEquals(map.get("c"), "d");
    }
}
