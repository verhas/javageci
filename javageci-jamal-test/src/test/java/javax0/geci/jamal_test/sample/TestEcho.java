package javax0.geci.jamal_test.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestEcho {


    /*!jamal
//<editor-fold desc="generated code">
{%@counter:define id=z%}
{%!@for $string in ("wups","","string with spaces in it", null)=
    @Test
    @DisplayName("Testing that echo({%@replace /$string/"/\"/%}) returns {%@replace /$string/"/\"/%}")
    void testEcho{%z%}(){
        final var sut = new Echo();
        Assertions.assertEquals($string, sut.echo($string));
    }
%}
//</editor-fold>
     */
//<editor-fold desc="generated code">


    @Test
    @DisplayName("Testing that echo(\"wups\") returns \"wups\"")
    void testEcho1(){
        final var sut = new Echo();
        Assertions.assertEquals("wups", sut.echo("wups"));
    }

    @Test
    @DisplayName("Testing that echo(\"\") returns \"\"")
    void testEcho2(){
        final var sut = new Echo();
        Assertions.assertEquals("", sut.echo(""));
    }

    @Test
    @DisplayName("Testing that echo(\"string with spaces in it\") returns \"string with spaces in it\"")
    void testEcho3(){
        final var sut = new Echo();
        Assertions.assertEquals("string with spaces in it", sut.echo("string with spaces in it"));
    }

    @Test
    @DisplayName("Testing that echo( null) returns  null")
    void testEcho4(){
        final var sut = new Echo();
        Assertions.assertEquals( null, sut.echo( null));
    }

//</editor-fold>
   //__END__
}












