package javax0.geci.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class TestJVM8Tools {

    @Test
    @DisplayName("Test that space() returns only spaces and as many as requested")
    void testSpaces(){
        for( int i = 0 ; i < 200 ; i ++ ){
            final var result = JVM8Tools.space(i);
            Assertions.assertEquals(i,result.length());
            for( int j = 0 ; j < result.length() ; j++ ) {
                Assertions.assertEquals(' ',result.charAt(j));
            }
        }
    }

    @Test
    @DisplayName("Test that the simulated getNestHost works")
    void testGetNestHost(){
        Assertions.assertEquals(Map.class,JVM8Tools.getNestHost(Map.Entry.class));
        Assertions.assertEquals(Map.class,JVM8Tools.jdk8_getNestHost(Map.Entry.class));
        Assertions.assertEquals(Map.class,JVM8Tools.getNestHost(Map.class));
        Assertions.assertEquals(Map.class,JVM8Tools.jdk8_getNestHost(Map.class));
    }

}
