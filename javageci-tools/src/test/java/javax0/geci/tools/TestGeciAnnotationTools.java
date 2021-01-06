package javax0.geci.tools;

import javax0.geci.annotations.Geci;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TestGeciAnnotationTools {
    @Retention(RetentionPolicy.RUNTIME)
    @Geci
    private @interface NoValue {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Geci
    private @interface NoValueOthers {
        String cuccooo() default "bird";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Geci
    private @interface WithValue {
        String value() default "";
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Geci
    private @interface Others {
        String value() default "bird";
        String cuccooo() default "bird";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Geci("OtherName")
    private @interface Renamed {
        String cuccooo() default "bird";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Renamed
    private @interface X {
        String value() default "";
    }

    @NoValue
    @WithValue("habakukk='1'")
    @Others(value = "fly", cuccooo = "flyingBird")
    private static final class Ops {

    }

    @NoValueOthers(cuccooo = "manu")
    @WithValue("habakukk")
    @Others(cuccooo = "flyingBird")
    private static final class Opsa {

    }

    @Renamed(cuccooo = "manu")
    @X
    private static final class Opsi {

    }

    @Test
    void test() {
        var annotations = Ops.class.getAnnotations();
        annotations = Opsi.class.getAnnotations();
        Assertions.assertEquals(2, annotations.length);
        Assertions.assertEquals("otherName cuccooo='manu'", GeciAnnotationTools.getValue(annotations[0]));
        Assertions.assertEquals("x", GeciAnnotationTools.getValue(annotations[1]));

    }
}
