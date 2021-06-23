package javax0.geci.jamal_test.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This is a demonstration sample file that shows how you can generate unit test proxy inner class in a unit test.
 * <p>
 * The tested class is {@link javax0.geci.jamal_test.sample.SystemUnderTest}.
 * <p>
 * The test file {@link ManualTestSystemUnderTest} is the manual implementation of the same test containing a manually
 * crafted proxy inner class.
 * <p>
 * This class contains the same test, but the inner proxy class is generated using Java::Geci Jamal module. The Jamal
 * template for the generated code is contained in the comment for demonstration purposes. Usually such a complex
 * template should be separated and stored in a Jamal include file ({@code .jim}) and imported into the source code.
 * <p>
 * The macro template here is simplified and should not be used for professional application. The production version of
 * the unit test inner proxy class template is in the {@code unittestproxy.jim} file. The version here does not care
 * about the exceptions that the methods, may throw and would stupidly generate setters for {@code final} variables. The
 * production version does care about those situations.
 */
public class GeneratedTestSystemUnderTest {

    @Test
    void testCounter() throws Exception {
        final var sut = new SystemUnderTest();
        sut.setCounter(0);
        sut.increment();
        Assertions.assertEquals(1, sut.getCounter());
    }

    /*!jamal
    {%@import res:geci.jim%}\
    {%beginCode SystemUnderTest proxy generated%}
    private static class SystemUnderTest {
        private final javax0.geci.jamal_test.sample.SystemUnderTest sut = new javax0.geci.jamal_test.sample.SystemUnderTest();
    {%!#for [skipEmpty] ($name,$type,$args) in
            ({%#methods (class=javax0.geci.jamal_test.sample.SystemUnderTest selector=private format=$name|$type|$args)%}) =
        private $type $name({%`@argList $args%}) throws Exception {
            Method m = sut.getClass().getDeclaredMethod("$name"{%`#classList ,$args%});
            m.setAccessible(true);
            m.invoke(sut{%`#callArgs ,$args%});
            }
    %}
    {%!#for [skipEmpty] ($name,$type,$args) in
            ({%#methods (class=javax0.geci.jamal_test.sample.SystemUnderTest selector="!private & declaringClass -> ( ! canonicalName ~ /java.lang.Object/ )" format=$name|$type|$args)%}) =
        private $type $name({%`@argList $args%}) {
            {%`#ifNotVoid $type return %}sut.$name({%`#callArgs $args%});
            }
    %}
    {%!#for [skipEmpty] ($name,$type) in
            ({%#fields (class=javax0.geci.jamal_test.sample.SystemUnderTest selector=private format=$name|$type)%}) =
        private void {%setter=$name%}($type $name) throws Exception {
            Field f = sut.getClass().getDeclaredField("$name");
            f.setAccessible(true);
            f.set(sut,$name);
            }

        private $type {%getter/$name/$type%}() throws Exception {
            Field f = sut.getClass().getDeclaredField("$name");
            f.setAccessible(true);
            return ($type)f.get(sut);
            }
    %}
    {%!#for [skipEmpty] ($name,$type) in
            ({%#fields (class=javax0.geci.jamal_test.sample.SystemUnderTest selector=!private format=$name|$type)%}) =
        private void {%setter/$name%}($type $name) {
            sut.$name = $name;
            }

        private $type {%getter/$name/$type%}() {
            return sut.$name;
            }
        %}
    }
    {%endCode%}
     */
        //<editor-fold desc="SystemUnderTest proxy generated">
    private static class SystemUnderTest {
        private final javax0.geci.jamal_test.sample.SystemUnderTest sut = new javax0.geci.jamal_test.sample.SystemUnderTest();

        private void increment() throws Exception {
            Method m = sut.getClass().getDeclaredMethod("increment");
            m.setAccessible(true);
            m.invoke(sut);
            }


        private int count(int arg0) {
            return sut.count(arg0);
            }


        private void setCounter(int counter) throws Exception {
            Field f = sut.getClass().getDeclaredField("counter");
            f.setAccessible(true);
            f.set(sut,counter);
            }

        private int getCounter() throws Exception {
            Field f = sut.getClass().getDeclaredField("counter");
            f.setAccessible(true);
            return (int)f.get(sut);
            }


    }
    //</editor-fold>
    //__END__
}




