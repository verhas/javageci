package javax0.geci.tools.reflection;

import javax0.geci.annotations.Generated;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberSelectorTest {

    private final int i = 0;
    @Generated
    private final int j = 0;

    @Test
    @DisplayName("true and !false return true")
    void testTrue() {
        Assertions.assertTrue(new MemberSelector().compile("true").match(null));
        Assertions.assertTrue(new MemberSelector().compile("!false").match(null));
    }

    @Test
    @DisplayName("false and !true return false")
    void testFalse() {
        Assertions.assertFalse(new MemberSelector().compile("false").match(null));
        Assertions.assertFalse(new MemberSelector().compile("!true").match(null));
    }


    @Test
    @DisplayName("& has higher precedence than | and there can be parentheses")
    void testPrecedence() {
        Assertions.assertTrue(new MemberSelector().compile("true | false & false").match(null));
        Assertions.assertFalse(new MemberSelector().compile("(true | false) & false").match(null));
    }

    @Test
    @DisplayName("field with final is recognized")
    void testFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(new MemberSelector().compile("final").match(f));
    }

    @Test
    @DisplayName("field with annotation is recognized")
    void testFieldHasAnnotation() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(new MemberSelector().compile("annotation ~ /Generated/").match(f));
        Assertions.assertTrue(new MemberSelector().compile("annotation ~ /Generated$/").match(f));
        Assertions.assertFalse(new MemberSelector().compile("annotation ~ /^Generated/").match(f));
        Assertions.assertTrue(new MemberSelector().compile("annotation ~ /^javax0\\.geci\\.annotations\\.Generated$/").match(f));
    }

    @SuppressWarnings("SameReturnValue")
    private int z() {
        return 1;
    }

    @Test
    void testReturns() throws NoSuchMethodException {
        final var f1 = this.getClass().getDeclaredMethod("z");
        Assertions.assertTrue(new MemberSelector().compile("returns ~ /int/").match(f1));
        final var f2 = this.getClass().getDeclaredMethod("testReturns");
        Assertions.assertTrue(new MemberSelector().compile("returns ~ /void/").match(f2));
    }

    @Test
    @DisplayName("method with annotation is recognized")
    void testMethodHasAnnotation() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("testMethodHasAnnotation", (Class<?>[]) null);
        Assertions.assertTrue(new MemberSelector().compile("annotation ~ /Test/").match(f));
        Assertions.assertTrue(new MemberSelector().compile("annotation ~ /Test$/").match(f));
        Assertions.assertFalse(new MemberSelector().compile("annotation ~ /^Test/").match(f));
        Assertions.assertTrue(new MemberSelector().compile("annotation ~ /^org\\.junit\\.jupiter\\.api\\.Test$/").match(f));
    }


    @Test
    @DisplayName("non final field is recognized")
    void testNotFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(new MemberSelector().compile("final").match(f));
    }

    @Test
    void testNegFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(new MemberSelector().compile("!final").match(f));
    }

    @Test
    void testNegNotFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertFalse(new MemberSelector().compile("!final").match(f));
    }

    @Test
    void testPrivateAndFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(new MemberSelector().compile("final & private").match(f));
    }

    @Test
    void testNegPrivateAndFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(new MemberSelector().compile("!(final | private)").match(f));
        Assertions.assertFalse(new MemberSelector().compile("!final & !private").match(f));
    }

    @Test
    void testNegPrivateAndFinal2() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(new MemberSelector().compile("!final | private").match(f));
    }

    @Test
    void testPrivateOrFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(new MemberSelector().compile("final | private").match(f));
    }
}
