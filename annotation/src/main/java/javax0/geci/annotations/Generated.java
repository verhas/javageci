package javax0.geci.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used in the generated code. It is a good signal that the method, class, field etc. was generated
 * and the same time the generator can also decide using reflection if a certain method, class, field etc. needs to
 * be generated or not. If it does not exist or it exists but is annotated using the annotation {@link Generated}
 * then it has to be generated. In case it exists, but does not have this annotation then the programmer
 * provided an implementation for the specific method, class, field etc.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Generated {
}
