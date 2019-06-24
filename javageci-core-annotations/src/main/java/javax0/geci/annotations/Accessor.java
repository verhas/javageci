package javax0.geci.annotations;

@Geci("accessor")
public @interface Accessor {
    String value() default "";
    String access() default "public";
    String filter() default "true";
    String getter() default "";
    String setter() default "";
    String only() default "";
}
