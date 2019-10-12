package javax0.geci.core.annotations;

import javax0.geci.annotations.Geci;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Geci("mapper")
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {

    String value() default "";
    String factory() default "";
    String filter() default "";
}
