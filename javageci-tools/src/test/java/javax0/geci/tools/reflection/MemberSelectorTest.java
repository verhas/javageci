package javax0.geci.tools.reflection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class MemberSelectorTest {

    private final int i = 0;
    private int j = 0;

    @Test
    void testFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(new MemberSelector().compile("final").match(f));
    }

    @Test
    void testNotFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("j");
        Assertions.assertFalse(new MemberSelector().compile("final").match(f));
    }

    @Test
    void testNegFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(new MemberSelector().compile("!final").match(f));
    }

    @Test
    void testNegNotFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(new MemberSelector().compile("!final").match(f));
    }

    @Test
    void testPrivateAndFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(new MemberSelector().compile("final & private").match(f));
    }
    @Test
    void testNegPrivateAndFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(new MemberSelector().compile("!final | private").match(f));
    }

    @Test
    void testNegPrivateAndFinal2() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(new MemberSelector().compile("(!final) | private").match(f));
    }

    @Test
    void testPrivateOrFinal() throws NoSuchFieldException {
        Field f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(new MemberSelector().compile("final | private").match(f));
    }
}
