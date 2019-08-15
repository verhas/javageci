package javax0.geci.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax0.geci.annotations.Geci;

@Geci("templated")
@Retention(RetentionPolicy.RUNTIME)
public @interface Templated {

    String value() default "";
    String classFilter() default "";
    String fieldFilter() default "";
    String memberClassFilter() default "";
    String methodFilter() default "";
    String postprocess() default "";
    String postprocessClass() default "";
    String preprocess() default "";
    String preprocessClass() default "";
    String processClass() default "";
    String processClasses() default "";
    String processField() default "";
    String processFields() default "";
    String processMemberClass() default "";
    String processMethod() default "";
    String processMethods() default "";
    String selector() default "";
}
