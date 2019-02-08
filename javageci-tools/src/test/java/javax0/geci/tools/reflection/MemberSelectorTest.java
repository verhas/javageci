package javax0.geci.tools.reflection;

import javax0.geci.annotations.Generated;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

@SuppressWarnings("ALL")
class MemberSelectorTest {

    static int var_static;
    final int var_final = 1;
    private final int i = 0;
    public int var_public;
    protected int var_protected;
    int var_package;
    transient int var_transient;
    volatile int var_volatile;
    @Generated
    private int j = 0;
    private int var_private;

    static void method_static() {
    }

    synchronized void method_synchronized() {
    }

    strictfp void method_strict() {
    }

    void method_vararg(Object... x) {
    }

    void method_notVararg(Object[] x) {
    }


    @Test
    @DisplayName("throws IllegalArgumentException when we test something for 'blabla'")
    void testInvalidTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("blabla").match(null));
    }

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
    @DisplayName("return type can be checked for int and void")
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
        Assertions.assertFalse(new MemberSelector().compile("final").match(f));
    }

    @Test
    @DisplayName("Testing a final field for !final is false")
    void testNegFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(new MemberSelector().compile("!final").match(f));
    }

    @Test
    @DisplayName("Non final field tested with !final is true")
    void testNegNotFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(new MemberSelector().compile("!final").match(f));
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

    @Test
    void testPrivateField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_private");
        Assertions.assertTrue(new MemberSelector().compile("private").match(f));
    }

    @Test
    void testProtectedField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_protected");
        Assertions.assertTrue(new MemberSelector().compile("protected").match(f));
    }

    @Test
    void testPackageField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_package");
        Assertions.assertTrue(new MemberSelector().compile("package").match(f));
    }

    @Test
    void testPublicField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_public");
        Assertions.assertTrue(new MemberSelector().compile("public").match(f));
    }

    @Test
    void testFinalField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_final");
        Assertions.assertTrue(new MemberSelector().compile("final").match(f));
    }

    @Test
    void testTransientField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_transient");
        Assertions.assertTrue(new MemberSelector().compile("transient").match(f));
    }

    @Test
    void testVolatileField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_volatile");
        Assertions.assertTrue(new MemberSelector().compile("volatile").match(f));
    }

    @Test
    void testStaticField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_static");
        Assertions.assertTrue(new MemberSelector().compile("static").match(f));
    }

    @Test
    void testSynchronizedMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_synchronized");
        Assertions.assertTrue(new MemberSelector().compile("synchronized").match(f));
    }

    @Test
    void testAbstractMethod() throws NoSuchMethodException {
        final var f = X.class.getDeclaredMethod("method_abstract");
        Assertions.assertTrue(new MemberSelector().compile("abstract").match(f));
    }

    @Test
    void testStrictMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_strict");
        Assertions.assertTrue(new MemberSelector().compile("strict").match(f));
    }

    @Test
    void testVarargMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_vararg", Object[].class);
        Assertions.assertTrue(new MemberSelector().compile("vararg").match(f));
    }

    @Test
    void testNotVarargMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_notVararg", Object[].class);
        Assertions.assertFalse(new MemberSelector().compile("vararg").match(f));
    }

    @Test
    void testNativeMethod() throws NoSuchMethodException {
        final var f = System.class.getDeclaredMethod("initProperties", Properties.class);
        Assertions.assertTrue(new MemberSelector().compile("native").match(f));
    }

    private void method_private() {
    }

    @Test
    void testPrivateMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_private");
        Assertions.assertTrue(new MemberSelector().compile("private").match(f));
    }

    protected void method_protected() {
    }

    @Test
    void testProtectedMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_protected");
        Assertions.assertTrue(new MemberSelector().compile("protected").match(f));
    }

    @Test
    void testPackageMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertTrue(new MemberSelector().compile("package").match(f));
    }

    public void method_public() {
    }

    @Test
    void testPublicMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_public");
        Assertions.assertTrue(new MemberSelector().compile("public").match(f));
    }

    final void method_final() {
    }

    @Test
    void testFinalMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_final");
        Assertions.assertTrue(new MemberSelector().compile("final").match(f));
    }

    @Test
    void testTransientMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("transient").match(f));
    }

    @Test
    void testVolatileMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("volatile").match(f));
    }

    @Test
    void testStaticMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertTrue(new MemberSelector().compile("static").match(f));
    }

    @Test
    void testSynchronizedField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("synchronized").match(f));
    }

    @Test
    void testAbstractField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("abstract").match(f));
    }

    @Test
    void testStrictField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("strict").match(f));
    }

    @Test
    void testVarargField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("vararg").match(f));
    }

    @Test
    void testNativeField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("native").match(f));
    }
    @Test
    void testThrowingField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new MemberSelector().compile("throws ~ /IllegalArgumentException/").match(f));
    }

    private void method_throws() throws IllegalArgumentException{}
    private void method_notThrows() {}

    @Test
    void testThrowingMethod() throws NoSuchMethodException {
        final var method_throws = this.getClass().getDeclaredMethod("method_throws");
        Assertions.assertTrue(new MemberSelector().compile("throws ~ /IllegalArgumentException/").match(method_throws));
        final var method_notThrows = this.getClass().getDeclaredMethod("method_notThrows");
        Assertions.assertFalse(new MemberSelector().compile("throws ~ /IllegalArgumentException/").match(method_notThrows));
        Assertions.assertFalse(new MemberSelector().compile("throws ~ /NullPointerException/").match(method_throws));
    }


    static abstract class X {
        abstract int method_abstract();
    }
}

