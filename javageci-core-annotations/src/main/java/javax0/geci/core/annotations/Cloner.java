package javax0.geci.core.annotations;

import javax0.geci.annotations.Geci;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Geci("cloner")
@Retention(RetentionPolicy.RUNTIME)
public @interface Cloner {

    String value() default "";
    String cloneMethod() default "";
    String cloneMethodProtection() default "";
    String cloneWith() default "";
    String copyCallsSuper() default "";
    String copyMethod() default "";
    String copyMethodProtection() default "";
    String filter() default "";
    String superCopyMethod() default "";
}
