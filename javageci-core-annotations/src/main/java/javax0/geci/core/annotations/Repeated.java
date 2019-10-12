package javax0.geci.core.annotations;

import javax0.geci.annotations.Geci;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Geci("repeated")
@Retention(RetentionPolicy.RUNTIME)
public @interface Repeated {

    String value() default "";
    String end() default "";
    String matchLine() default "";
    String start() default "";
    String templateEnd() default "";
    String templateStart() default "";
    String values() default "";
}
