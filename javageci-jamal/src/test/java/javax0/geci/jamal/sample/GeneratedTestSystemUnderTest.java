package javax0.geci.jamal.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GeneratedTestSystemUnderTest {

    @Test
    void testCounter() throws Exception {
        final var sut = new SystemUnderTest();
        sut.setCounter(0);
        sut.increment("");
        Assertions.assertEquals(1, sut.getCounter());
    }

    /*!jamal
    {%@import res:geci.jim%}\
    {%beginCode SystemUnderTest proxy generated%}
    private static class SystemUnderTest {
        private javax0.geci.jamal.sample.SystemUnderTest sut = new javax0.geci.jamal.sample.SystemUnderTest();
     {%!#for ($name,$type,$args,$exceptions) in
            ({%#methods
            {%class javax0.geci.jamal.sample.SystemUnderTest%}
            {%selector private %}
            {%format/$name|$type|$args|$exceptions%}
            %}) ={%@options skipForEmpty%}
            private $type $name({%`@argList $args%}) throws Exception {
                Method m = sut.getClass().getDeclaredMethod("$name"{%`#classList ,$args%});
                m.setAccessible(true);
                m.invoke(sut{%`#callArgs ,$args%});
                }
     %}
             {%!#for ($name,$type,$args,$exceptions) in
            ({%#methods
            {%class javax0.geci.jamal.sample.SystemUnderTest%}
            {%selector/ !private & declaringClass -> ( canonicalName ~ /javax0.geci.jamal.sample.SystemUnderTest/ )%}
            {%format/$name|$type|$args|$exceptions%}
            %}) ={%@options skipForEmpty%}
            private $type $name({%`@argList $args%}) {%`@if/$exceptions/throws %}$exceptions {
                {%`#ifNotVoid $type return %}sut.$name({%`#callArgs $args%});
                }
     %}
             {%!#for ($name,$type) in
            ({%#fields
            {%class javax0.geci.jamal.sample.SystemUnderTest%}
            {%selector/ private %}
            {%format/$name|$type%}
            %}) ={%@options skipForEmpty%}
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
             {%!#for ($name,$type) in
            ({%#fields
            {%class javax0.geci.jamal.sample.SystemUnderTest%}
            {%selector/ !private & declaringClass -> ( canonicalName ~ /javax0.geci.jamal.sample.SystemUnderTest/)%}
            {%format/$name|$type%}
            %}) ={%@options skipForEmpty%}
            private void {%setter/$name%}($type $name) {
                set.$name = $name;
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
        private javax0.geci.jamal.sample.SystemUnderTest sut = new javax0.geci.jamal.sample.SystemUnderTest();

            private void increment(String arg0) throws Exception {
                Method m = sut.getClass().getDeclaredMethod("increment",String.class);
                m.setAccessible(true);
                m.invoke(sut,arg0);
                }


            private int count(int arg0)  {
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
