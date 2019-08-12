package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("accessor")
@Retention(RetentionPolicy.RUNTIME)
public @interface Accessor {

    String value() default "";
    String access() default "";
    String filter() default "";
    String getter() default "";
    String mnemonic() default "";
    String only() default "";
    String setter() default "";
}
