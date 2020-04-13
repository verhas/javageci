package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("fluent")
@Retention(RetentionPolicy.RUNTIME)
public @interface Fluent {

    String value() default "";
}
