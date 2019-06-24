package javax0.geci.annotations;

@Geci("builder")
public @interface Builder {
    String value() default "";
    String aggregatorMethod() default "add";
    String buildMethod() default "build";
    String builderFactoryMethod() default "builder";
    String builderName() default "Builder";
    String filter() default "private & !static & !final";
}
