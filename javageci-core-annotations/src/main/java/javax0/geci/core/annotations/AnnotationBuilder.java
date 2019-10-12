package javax0.geci.core.annotations;

import javax0.geci.annotations.Geci;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Geci("annotationBuilder")
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationBuilder {

    String value() default "";
    String absolute() default "";
    String in() default "";
    String set() default "";
}
