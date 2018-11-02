package javax0.geci.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Simply an annotation collection so that programs can have more than one {@code @Geci()}
 * annotation on some classes, fields etc. that need attention from multiple generators.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Gecis {
    Geci[] value();
}
