package javax0.geci.core.annotations;

import javax0.geci.annotations.Geci;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Geci("configBuilder")
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigBuilder {

    String value() default "";
    String buildMethod() default "";
    String builderFactoryMethod() default "";
    String builderName() default "";
    String configAccess() default "";
    String configurableMnemonic() default "";
    String filter() default "";
    String generateImplementedKeys() default "";
    String localConfigMethod() default "";
}
