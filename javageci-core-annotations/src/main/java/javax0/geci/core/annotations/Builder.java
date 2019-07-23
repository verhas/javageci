package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("builder")
@Retention(RetentionPolicy.RUNTIME)
public @interface Builder {

    String value() default "";
    String aggregatorMethod() default "";
    String buildMethod() default "";
    String builderFactoryMethod() default "";
    String builderName() default "";
    String filter() default "";
}
