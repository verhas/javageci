package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("jdocify")
@Retention(RetentionPolicy.RUNTIME)
public @interface Jdocify {

    String value() default "";
    String commentCODEEnd() default "";
    String commentCODEStart() default "";
}
