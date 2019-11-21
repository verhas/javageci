package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("iterate")
@Retention(RetentionPolicy.RUNTIME)
public @interface Iterate {

    String value() default "";
    String editorFoldLine() default "";
    String escapeLine() default "";
    String loopLine() default "";
    String sep1() default "";
    String sep1Line() default "";
    String sep2() default "";
    String sep2Line() default "";
    String skipLine() default "";
    String templateEndLine() default "";
    String templateLine() default "";
}
