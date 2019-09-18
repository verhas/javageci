package javax0.geci.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used by many code generators that work with Java::Geci. The classes, fields or other
 * Java language elements that the code generators consider to get information from are annotated with this
 * annotation and the parameter provides value for the code generators.
 * <p>
 * For example the setter/getter generator will look at the classes and in case they have a
 * {@code @Geci("accessor ...")} annotation it will generate the setters and the getters for the fields
 * of the class. Classes that do not have such an annotation will not be effected by the accessor code
 * generator.
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Gecis.class)
public @interface Geci {
    String[] value() default "";
}
