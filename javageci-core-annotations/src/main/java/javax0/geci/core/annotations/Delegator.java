package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("delegator")
@Retention(RetentionPolicy.RUNTIME)
public @interface Delegator {

    String value() default "";
    String filter() default "";
    String methods() default "";
}
