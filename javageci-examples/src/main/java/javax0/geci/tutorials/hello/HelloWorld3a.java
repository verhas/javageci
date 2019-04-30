package javax0.geci.tutorials.hello;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@HelloWorld3a.Geci(value = "HelloWorld3 id='hallo'", methodName = "hiyaHuya")
public class HelloWorld3a {
    //<editor-fold id="hallo">
    public static void hiyaHuya(){
        System.out.println("Hello, World");
    }
    //</editor-fold>

    @Retention(RetentionPolicy.RUNTIME)
    @interface Geci {
        String value();

        String methodName() default "hello";
    }
}
