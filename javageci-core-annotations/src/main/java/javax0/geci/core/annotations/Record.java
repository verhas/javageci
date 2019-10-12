package javax0.geci.core.annotations;

import javax0.geci.annotations.Geci;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Geci("record")
@Retention(RetentionPolicy.RUNTIME)
public @interface Record {

    String value() default "";
    String filter() default "";
}
