package javax0.geci.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used in the generated code. It is a good signal
 * that the method, class, field etc. was generated and the same time
 * the generator can also decide using reflection if a certain method,
 * class, field etc. needs to be generated or not. If it does not exist
 * or it exists but is annotated using the annotation {@code Generated}
 * then it has to be generated. In case it exists, but does not have
 * this annotation then the programmer provided an implementation for
 * the specific method, class, field etc.
 * <p>
 * Note that there is a {@code javax.annotation.Generated} annotation in
 * the JDK. That annotation has a {@code @Retention(value=SOURCE)}
 * retention scope, and this application needs the annotation kept till
 * RUNTIME, thus the annotation in the JDK is not suitable.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Generated {
    /**
     * Documentation string. It is recommended that the generator puts
     * its own mnemonic into this parameter.
     *
     * @return ""
     */
    String[] value() default "";
}
