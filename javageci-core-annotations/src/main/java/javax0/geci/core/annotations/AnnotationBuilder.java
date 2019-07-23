package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("annotationBuilder")
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationBuilder {

    String value() default "";
    String absolute() default "";
    String in() default "";
    String module() default "";
}
