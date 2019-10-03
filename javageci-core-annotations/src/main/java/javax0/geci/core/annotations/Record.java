package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("record")
@Retention(RetentionPolicy.RUNTIME)
public @interface Record {

    String value() default "";
    String filter() default "";
}
