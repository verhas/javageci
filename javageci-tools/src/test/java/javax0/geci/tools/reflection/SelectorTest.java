package javax0.geci.tools.reflection;

import javax0.geci.annotations.Generated;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.lang.reflect.Member;
import java.util.Properties;
import java.util.function.Function;

@SuppressWarnings("ALL")
class SelectorTest {

    private static final Member IGNORED_MEMBER = null;
    private static final Class[] NO_ARGS = null;
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
    @DisplayName("compiles empty string")
    void testEmptyString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Selector.compile("").match(IGNORED_MEMBER));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Selector.compile("  ").match(IGNORED_MEMBER));
    }

    @Test
    @DisplayName("compiles expression with daingling space")
    void testDainglingSpace() {
        Assertions.assertTrue(Selector.compile("true ").match(IGNORED_MEMBER));
        Assertions.assertTrue(Selector.compile("!false ").match(IGNORED_MEMBER));
    }

    @Test
    @DisplayName("throws IllegalArgumentException when we test something for 'blabla'")
    void testInvalidTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("blabla").match(IGNORED_MEMBER));
    }

    @Test
    @DisplayName("true and !false return true")
    void testTrue() {
        Assertions.assertTrue(Selector.compile("true").match(IGNORED_MEMBER));
        Assertions.assertTrue(Selector.compile("!false").match(IGNORED_MEMBER));
    }

    @Test
    @DisplayName("false and !true return false")
    void testFalse() {
        Assertions.assertFalse(Selector.compile("false").match(IGNORED_MEMBER));
        Assertions.assertFalse(Selector.compile("!true").match(IGNORED_MEMBER));
    }


    @Test
    @DisplayName("& has higher precedence than | and there can be parentheses")
    void testPrecedence() {
        Assertions.assertTrue(Selector.compile("true | false & false").match(IGNORED_MEMBER));
        Assertions.assertFalse(Selector.compile("(true | false) & false").match(IGNORED_MEMBER));
    }

    @Test
    @DisplayName("field with final is recognized")
    void testFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(Selector.compile("final").match(f));
    }

    public boolean equals(Object other) {
        return true;
    }

    @Test
    @DisplayName("Method that overrides method from superclass is recognized")
    void testOverrides1() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("equals", Object.class);
        Assertions.assertTrue(Selector.compile("overrides").match(f));
    }

    @Test
    @DisplayName("Method that does not override any method recognized")
    void testOverrides2(TestInfo info) throws NoSuchMethodException {
        final var f2 = info.getTestMethod().get();
        Assertions.assertFalse(Selector.compile("overrides").match(f2));
    }

    @Test
    @DisplayName("Method that overrides method from the superclass of the superclass is recognized")
    void testOverrides3() throws NoSuchMethodException {
        final var f3 = X.class.getDeclaredMethod("hashCode", NO_ARGS);
        Assertions.assertTrue(Selector.compile("overrides").match(f3));
    }

    @Test
    @DisplayName("Class simple name is matched")
    void testClassSimpleName() throws NoSuchFieldException {
        final var f = this.getClass();
        Assertions.assertTrue(Selector.compile("simpleName ~ /Test/").match(f));
    }

    @Test
    @DisplayName("Class canonical name is matched")
    void testClassCanonicalName() throws NoSuchFieldException {
        final var f = this.getClass();
        Assertions.assertTrue(Selector.compile("canonicalName ~ /reflection.*?Test/").match(f));
    }

    @Test
    @DisplayName("Class that does not extend anything extends Object")
    void testClassExtendsObject() throws NoSuchFieldException {
        final var f = this.getClass();
        Assertions.assertTrue(Selector.compile("extends ~ /java\\.lang\\.Object/").match(f));
    }

    @Test
    @DisplayName("Integer class extends Number")
    void testClassExtends() throws NoSuchFieldException {
        Assertions.assertTrue(Selector.compile("extends ~ /Number/").match(Integer.class));
    }

    @Test
    @DisplayName("field with annotation is recognized")
    void testFieldHasAnnotation() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(Selector.compile("annotation ~ /Generated/").match(f));
        Assertions.assertTrue(Selector.compile("annotation ~ /Generated$/").match(f));
        Assertions.assertFalse(Selector.compile("annotation ~ /^Generated/").match(f));
        Assertions.assertTrue(Selector.compile("annotation ~ /^javax0\\.geci\\.annotations\\.Generated$/").match(f));
    }

    @SuppressWarnings("SameReturnValue")
    private int z() {
        return 1;
    }

    @Test
    @DisplayName("return type can be checked for int and void")
    void testReturns() throws NoSuchMethodException {
        final var f1 = this.getClass().getDeclaredMethod("z");
        Assertions.assertTrue(Selector.compile("returns ~ /int/").match(f1));
        final var f2 = this.getClass().getDeclaredMethod("testReturns");
        Assertions.assertTrue(Selector.compile("returns ~ /void/").match(f2));
    }

    @Test
    @DisplayName("method with annotation is recognized")
    void testMethodHasAnnotation() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("testMethodHasAnnotation", (Class<?>[]) null);
        Assertions.assertTrue(Selector.compile("annotation ~ /Test/").match(f));
        Assertions.assertTrue(Selector.compile("annotation ~ /Test$/").match(f));
        Assertions.assertFalse(Selector.compile("annotation ~ /^Test/").match(f));
        Assertions.assertTrue(Selector.compile("annotation ~ /^org\\.junit\\.jupiter\\.api\\.Test$/").match(f));
    }


    @Test
    @DisplayName("non final field is recognized")
    void testNotFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertFalse(Selector.compile("final").match(f));
    }

    @Test
    @DisplayName("Testing a final field for !final is false")
    void testNegFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(Selector.compile("!final").match(f));
    }

    @Test
    @DisplayName("Non final field tested with !final is true")
    void testNegNotFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(Selector.compile("!final").match(f));
    }

    @Test
    void testPrivateAndFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(Selector.compile("final & private").match(f));
    }

    @Test
    void testNegPrivateAndFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertFalse(Selector.compile("!(final | private)").match(f));
        Assertions.assertFalse(Selector.compile("!final & !private").match(f));
    }

    @Test
    void testNegPrivateAndFinal2() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertTrue(Selector.compile("!final | private").match(f));
    }

    @Test
    void testPrivateOrFinal() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("j");
        Assertions.assertTrue(Selector.compile("final | private").match(f));
    }

    @Test
    void testPrivateField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_private");
        Assertions.assertTrue(Selector.compile("private").match(f));
    }

    @Test
    void testProtectedField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_protected");
        Assertions.assertTrue(Selector.compile("protected").match(f));
    }

    @Test
    void testPackageField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_package");
        Assertions.assertTrue(Selector.compile("package").match(f));
    }

    @Test
    void testPublicField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_public");
        Assertions.assertTrue(Selector.compile("public").match(f));
    }

    @Test
    void testFinalField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_final");
        Assertions.assertTrue(Selector.compile("final").match(f));
    }

    @Test
    void testTransientField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_transient");
        Assertions.assertTrue(Selector.compile("transient").match(f));
    }

    @Test
    void testVolatileField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_volatile");
        Assertions.assertTrue(Selector.compile("volatile").match(f));
    }

    @Test
    void testStaticField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("var_static");
        Assertions.assertTrue(Selector.compile("static").match(f));
    }

    @Test
    void testSynchronizedMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_synchronized");
        Assertions.assertTrue(Selector.compile("synchronized").match(f));
    }

    @Test
    void testAbstractMethod() throws NoSuchMethodException {
        final var f = X.class.getDeclaredMethod("method_abstract");
        Assertions.assertTrue(Selector.compile("abstract").match(f));
    }

    @Test
    void testStrictMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_strict");
        Assertions.assertTrue(Selector.compile("strict").match(f));
    }

    @Test
    void testSignatureMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("equals", Object.class);
        Assertions.assertTrue(Selector.compile("signature ~ /equals\\(Object\\s+arg1\\)").match(f));
    }

    @Test
    void testVarargMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_vararg", Object[].class);
        Assertions.assertTrue(Selector.compile("vararg").match(f));
    }

    @Test
    void testNotVarargMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_notVararg", Object[].class);
        Assertions.assertFalse(Selector.compile("vararg").match(f));
    }

    @Test
    void testNativeMethod() throws NoSuchMethodException {
        final var f = System.class.getDeclaredMethod("initProperties", Properties.class);
        Assertions.assertTrue(Selector.compile("native").match(f));
    }

    private void method_private() {
    }

    @Test
    void testPrivateMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_private");
        Assertions.assertTrue(Selector.compile("private").match(f));
    }

    protected void method_protected() {
    }

    @Test
    void testProtectedMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_protected");
        Assertions.assertTrue(Selector.compile("protected").match(f));
    }

    @Test
    void testPackageMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertTrue(Selector.compile("package").match(f));
    }

    public void method_public() {
    }

    @Test
    void testPublicMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_public");
        Assertions.assertTrue(Selector.compile("public").match(f));
    }

    final void method_final() {
    }

    @Test
    void testFinalMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_final");
        Assertions.assertTrue(Selector.compile("final").match(f));
    }

    @Test
    void testTransientMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("transient").match(f));
    }

    @Test
    void testVolatileMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("volatile").match(f));
    }

    @Test
    void testStaticMethod() throws NoSuchMethodException {
        final var f = this.getClass().getDeclaredMethod("method_static");
        Assertions.assertTrue(Selector.compile("static").match(f));
    }

    @Test
    void testSynchronizedField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("synchronized").match(f));
    }

    @Test
    void testAbstractField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("abstract").match(f));
    }

    @Test
    void testStrictField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("strict").match(f));
    }

    @Test
    void testVarargField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("vararg").match(f));
    }

    @Test
    void testNativeField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("native").match(f));
    }

    @Test
    void testThrowingField() throws NoSuchFieldException {
        final var f = this.getClass().getDeclaredField("i");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Selector.compile("throws ~ /IllegalArgumentException/").match(f));
    }

    @Test
    @DisplayName("A method that implements directly from interface is recognized")
    void testMethodImplementsInterfaceMethod() throws NoSuchMethodException {
        final var f = X.class.getDeclaredMethod("apply", Object.class);
        Assertions.assertTrue(Selector.compile("implements").match(f));
    }

    @Test
    @DisplayName("A method that implements transitively from some super interface is recognized")
    void testMethodImplementsTransitiveInterfaceMethod() throws NoSuchMethodException {
        final var f = Y.class.getDeclaredMethod("q", NO_ARGS);
        Assertions.assertTrue(Selector.compile("implements").match(f));
    }

    @Test
    @DisplayName("A method that does not implement is recognized")
    void testMethodDoeNotImplementsInterfaceMethod(TestInfo info) throws NoSuchMethodException {
        Assertions.assertFalse(Selector.compile("implements").match(info.getTestMethod().get()));
    }

    private void method_throws() throws IllegalArgumentException {
    }

    private void method_notThrows() {
    }

    @Test
    void testThrowingMethod() throws NoSuchMethodException {
        final var method_throws = this.getClass().getDeclaredMethod("method_throws");
        Assertions.assertTrue(Selector.compile("throws ~ /IllegalArgumentException/").match(method_throws));
        final var method_notThrows = this.getClass().getDeclaredMethod("method_notThrows");
        Assertions.assertFalse(Selector.compile("throws ~ /IllegalArgumentException/").match(method_notThrows));
        Assertions.assertFalse(Selector.compile("throws ~ /NullPointerException/").match(method_throws));
    }


    interface A {
        void q();
    }

    interface B extends A {
    }

    interface C extends A, B {
    }

    static class Y implements C {
        public void q() {
        }
    }

    static abstract class X extends SelectorTest implements Function {
        abstract int method_abstract();

        public Object apply(Object t) {
            return null;
        }

        public int hashCode() {
            return 0;
        }
    }
}

